package common.actions;

import common.StateKeys;
import common.Tuple;
import common.players.Player;

import java.io.IOException;

/**
 * Makes the player look directly at the ball.
 * Used by: All
 */
public class CenterBallInFOVAction extends GOAPAction{

    public CenterBallInFOVAction() {
        super(false, 1);
        addPrecondition(StateKeys.ball_in_FOV, true);
        addPrecondition(StateKeys.ball_centered_in_FOV, false);
        
        addEffect(StateKeys.ball_centered_in_FOV, true);
        // although it's not guaranteed, performing this action might make the player lose the goal
        addEffect(StateKeys.oppo_goal_in_FOV, false);
    }

    @Override
    public void resetActionSpecifics() {

    }

    @Override
    public boolean checkProceduralPrecondition(Player agent) {
        return true;
    }

    @Override
    public boolean executeAction(Player agent) {

        // turn the player by however many angles are indicated by its global map
        Tuple ball = agent.getPitch().getBall();
        if (ball != null) {
            try {
                agent.doTurn(String.valueOf(ball.getIParams()[2]));
                Thread.sleep(150);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }
}
