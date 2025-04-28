package common.actions;

import common.StateKeys;
import common.players.Player;

import java.io.IOException;

/**
 * Finds the ball on the field.
 * Used by: All
 */
public class GetBallInFOVAction extends GOAPAction{

    public GetBallInFOVAction() {
        super(false, 1);
        this.addPrecondition(StateKeys.ball_in_FOV, false);
        this.addEffect(StateKeys.ball_in_FOV, true);
        // although it's not guaranteed, performing this action might make the player lose the goal
        this.addEffect(StateKeys.oppo_goal_in_FOV, false);
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
        // perform 72 degree turns until we find the ball.
        // the field of view of the player is 90 degrees, but having the turn angle slightly smaller can help with finding the ball
        for (int i = 0; i < 5; i++){

            if (agent.getPitch().getState().get(StateKeys.ball_in_FOV)){
                return true;
            }
            try {
                agent.doTurn("72");
                agent.reader.read();
            } catch (IOException e) {
                e.printStackTrace();
            }



        }
        return false;
    }
}
