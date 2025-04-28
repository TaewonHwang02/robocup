package common.players;

import common.StateKeys;
import common.actions.*;
import common.goals.*;

public class Winger extends Player {
    public Winger(int num, String teamName, boolean vis, int xPos, int yPos, String side, Team team) {
        super(num, teamName, vis, xPos, yPos, false, side, team, "winger");

        // GOALS
        addAvailableGoal(new FocusOnBallGoal());
        addAvailableGoal(new PassBallGoal());
        addAvailableGoal(new GetInWingPositionGoal());
        addAvailableGoal(new ResetPositionsGoal());
        addAvailableGoal(new KickBallGoal());
        addAvailableGoal(new InterceptBallGoal());

        // ACTIONS
        addAvailableAction(new GetBallInFOVAction());
        addAvailableAction(new GetOppoGoalInFOVAction());
        addAvailableAction(new CenterBallInFOVAction());
        addAvailableAction(new ScanForTeammatesAction());

        addAvailableAction(new MoveToBallAction());
        addAvailableAction(new MoveToWingPositionAction());
        addAvailableAction(new TeleportAction());
        addAvailableAction(new CrossBallAction());

        addAvailableAction(new KickBallToGoalAction());
        addAvailableAction(new PassToTeammateAction());
    }


    @Override
    public void specificRun() {
        while (!(getPitch().state.get(StateKeys.time_up) || getPitch().state.get(StateKeys.time_over))) {
            planAndExecute();
        }
    }
}
