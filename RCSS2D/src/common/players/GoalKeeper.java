package common.players;

import common.StateKeys;
import common.actions.*;
import common.goals.FocusOnBallGoal;
import common.goals.InterceptBallGoal;
import common.goals.ResetPositionsGoal;

/**
 * Keeper of the goal.
 */
public class GoalKeeper extends Player {

    public GoalKeeper(int num, String teamName, boolean vis, int xPos, int yPos, String side, Team team) {
        super(num, teamName, vis, xPos, yPos, true, side, team,"gk");

        // create list of possible goals
        addAvailableGoal(new FocusOnBallGoal());
        addAvailableGoal(new InterceptBallGoal());
        addAvailableGoal(new ResetPositionsGoal());

        // create list of available actions
        addAvailableAction(new GetBallInFOVAction());
        addAvailableAction(new CenterBallInFOVAction());
        addAvailableAction(new CatchBallAction());
        addAvailableAction(new TeleportAction());
    }


    @Override
    public void specificRun() {
        while (!(getPitch().state.get(StateKeys.time_up) || getPitch().state.get(StateKeys.time_over))){
            planAndExecute();
        }
    }
}
