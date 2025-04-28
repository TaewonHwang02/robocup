package common.actions;

import common.StateKeys;
import common.players.Player;
import common.Tuple;
import common.PlayerMath;

import java.awt.geom.Point2D;
import java.io.IOException;

/**
 * Run to the ball.
 * Used by: All
 */
public class MoveToBallAction extends GOAPAction {

    public MoveToBallAction() {
        super(false, 1);
        addPrecondition(StateKeys.ball_centered_in_FOV, true);

        addEffect(StateKeys.has_ball, true);
        addEffect(StateKeys.in_new_position, false);
    }

    @Override
    public void resetActionSpecifics() {

    }

    @Override
    public boolean checkProceduralPrecondition(Player agent) {
        Tuple[] balls = agent.getBallsCartesian();

        if (balls[1] == null){
            return false;
        }

        return true;
    }

    @Override
    public boolean executeAction(Player agent) {
        double x;
        double y;

        Tuple ballPos = agent.getBallsCartesian()[1];
        double ball_x = ballPos.getIParams()[1];
        double ball_y = ballPos.getIParams()[2];

        Tuple playerPos = agent.getPlayerPos();
        double player_x = playerPos.getIParams()[0];
        double player_y = playerPos.getIParams()[1];
        int[] init_position = PlayerMath.getStartPosition(agent.getNum(), agent.getSide());

        String type = agent.getType();
        int sign_player_y = (int) Math.signum(player_y);
        int sign_ball_y = (int) Math.signum(ball_y);

        while (!agent.getPitch().state.get(StateKeys.has_ball)) {
            // if player is the closest to ball
            if (agent.getIsClosest() && agent.goalStillValid()) {
                try {
                    agent.doSay("Going for the ball");
                    agent.doDash("30");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                agent.reader.read();

                if (!agent.getPitch().state.get(StateKeys.ball_centered_in_FOV)) {
                    return false;
                }

                // if player is not the closest to ball
            } else if (!agent.getIsClosest() && agent.goalStillValid()) {
                try {
                    switch (agent.getType()) {
                        case "defender": // dash to ball
                            x = ball_x;
                            y = ball_y;
                            agent.doDash("25",String.valueOf(PlayerMath.findAngleWithPoint(playerPos, x, y)));
                            break;

                        case "midfielder": // dash up/down to the ball
                            x = ball_x;
                            y = ball_y + sign_player_y * 10;

                            //Double distToBall = agent.getBallsPolar()[1].iParams[1];
                            if (agent.getBallsPolar()[1]!= null && agent.getBallsPolar()[1].iParams[1] >= 10) {
                                agent.doDash("20", String.valueOf(PlayerMath.findAngleWithPoint(playerPos, x, y)));
                            } else if (agent.getBallsPolar()[1]!= null && agent.getBallsPolar()[1].iParams[1] < 10){
                                agent.doDash("10");
                            }
                            break;

                        case "winger":
                            // Constrain the winger's movement to stay within their lateral boundaries
                            double xLimit = agent.getSide().equals("l") ? 52 : -52;
                            double target_x = Math.max(0, Math.min(xLimit, ball_x));
                            double target_y = Math.signum(player_y) * Math.max(10, Math.min(34, Math.abs(ball_y)));
                            agent.doDash("50", String.valueOf(PlayerMath.findAngleWithPoint(playerPos, target_x, target_y)));
                            break;
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                agent.reader.read();

                if (!agent.getPitch().state.get(StateKeys.ball_centered_in_FOV)) {
                    return false;
                }

            } else {
                return false;
            }
        }
        return true;
    }
}