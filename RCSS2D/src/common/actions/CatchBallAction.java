package common.actions;

import common.StateKeys;
import common.Tuple;
import common.players.Player;

import java.io.IOException;

/**
 * Make the Player catch the ball. Reserved for the GoalKeeper.
 */
public class CatchBallAction extends GOAPAction{
    /*
     * An action reserved to the goalie. Play mode must be "play_on"
     * Goalie must be inside penalty area
     * Command to server is (catch DIR), where dir is an angle (from -180 to +180)
     * REMINDER: positive angle means anti-clockwise rotation, negative means clockwise rotation
     * See https://rcsoccersim.readthedocs.io/en/latest/soccerserver.html#catch-model 4.6.1 to see the catch area depending on DIR
     *
     */

    public CatchBallAction() {
        super(false, 1);
        addPrecondition(StateKeys.ball_centered_in_FOV, true); // could be just Ball_in_FOV though.

        addEffect(StateKeys.has_ball, true);
        addEffect(StateKeys.in_new_position, false);
    }

    @Override
    public void resetActionSpecifics() {}

    @Override
    public boolean checkProceduralPrecondition(Player agent) { return true;}

    @Override
    public boolean executeAction(Player agent) {
        agent.reader.read();
        // Parameters of ball in polar coordinates: 0 is time, 1 is dist, 2 is dir
        Tuple ball_polar = agent.getPitch().getBall();
        if (ball_polar == null){
            return false;
        }
        // Parameters of player position tuples: 0 is x position, 1 is y position, 2 is absolute direction
        Tuple playerPos = agent.getPlayerPos();

        // Parameters of ball in cartesian coordinates: 0 is time, 1 is x position, 2 is y position
        Tuple[] balls = agent.getBallsCartesian();

        double diff = balls[1].iParams[2] - playerPos.iParams[1];

        while (diff > 1 || diff < -1 ){
            if (agent.goalStillValid()) {
                // We should move only when the ball is close to us and when dashing doesn't take us outside the goal area
                if (ball_polar.iParams[1] > 40) {
                    return false;
                }
                if (ball_polar.iParams[1] < 2) {
                    try {
                        agent.doCatch(String.valueOf(ball_polar.iParams[2]));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return true;
                }
                // If we are not in the right position, we need to dash along a line parallel to the goalposts
                else if (diff > 0) {
                    // ball will be to the left of the player
                    if (playerPos.iParams[1] > 5) {
                        return false;
                    }
                    try {
                        agent.doDash("50", String.valueOf(90 - playerPos.iParams[2]));
                        agent.reader.read();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    // ball will be to the right
                    if (playerPos.iParams[1] < -5) {
                        return false;
                    }
                    try {
                        agent.doDash("50", String.valueOf(-90 - playerPos.iParams[2]));
                        agent.reader.read();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

                balls = agent.getBallsCartesian();
                if (balls[1] == null) {
                    return false;
                }
                ball_polar = agent.getPitch().getBall();
                playerPos = agent.getPlayerPos();


                diff = balls[1].iParams[2] - playerPos.iParams[1];
            }
            else {
                return false;
            }
        }


        if (ball_polar != null && ball_polar.iParams[1] < 2){
            try {
                agent.doCatch(String.valueOf(ball_polar.iParams[2]));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }
}
