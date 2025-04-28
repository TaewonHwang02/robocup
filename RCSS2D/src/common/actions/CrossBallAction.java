package common.actions;

import common.PlayerMath;
import common.StateKeys;
import common.Tuple;
import common.players.Player;

import java.awt.geom.Point2D;
import java.io.IOException;

/**
 * Similar to kiCrosses the BringBallGoal. Kicks the ball towards opponent's goal when ball is in winger's position range.
 * Used by: Winger.
 */

public class CrossBallAction extends GOAPAction {

    public CrossBallAction() {
        super(false, 1);
        addPrecondition(StateKeys.has_ball, true);

        addEffect(StateKeys.has_ball, false);
        addEffect(StateKeys.in_oppo_fourth, true);
        addEffect(StateKeys.kick_performed, true);
        addEffect(StateKeys.in_new_position, false);
    }

    @Override
    public void resetActionSpecifics() {

    }

    @Override
    public boolean checkProceduralPrecondition(Player agent) {
        // If ball out of sight OR opponent blocking,
        if (agent.getBallsCartesian()[1] == null || agent.getPitch().state.get(StateKeys.oppo_blocking)){
            return false;
        }

        // parameters: 0 is x, 1 is y, 2 is direction
        Tuple[] balls = agent.getBallsCartesian();
        double ball_x = balls[1].iParams[0];
        double ball_y = balls[1].iParams[1];

        // ball is in winger's range
        return balls != null && Math.abs(ball_x) <= 52 && Math.abs(ball_y) > 10 && Math.abs(ball_y) < 34;
    }

    @Override
    public boolean executeAction(Player agent) {
        try {
            Point2D.Double goalKnown = PlayerMath.getOppoGoal(agent);

            // agent.doSay("Crossing ball");
            agent.doKick("40", String.valueOf(PlayerMath.findAngleWithPoint(agent.getPlayerPos(), goalKnown.x, 0)));
            agent.reader.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}

