package common.players;

import common.StateKeys;
import common.actions.*;
import common.goals.*;

/**
 * Midfielder: Behaviour should be as follow
 * Stay close to ball without chasing it
 * Be available for pass
 * Scan for pass and pass to Att if possible
 * Carry ball forward if pass not possible
 */
public class Midfielder extends Player {

    public Midfielder(int num, String teamName, boolean vis, int xPos, int yPos, String side, Team team) {
        super(num, teamName, vis, xPos, yPos, false, side, team,"midfielder");

        // GOALS
        addAvailableGoal(new FocusOnBallGoal());
        addAvailableGoal(new ResetPositionsGoal());
        addAvailableGoal(new GetInMidPositionGoal());
        addAvailableGoal(new PassBallGoal());
        addAvailableGoal(new InterceptBallGoal());
        
        // ACTIONS
        addAvailableAction(new GetBallInFOVAction());
        addAvailableAction(new CenterBallInFOVAction());
        addAvailableAction(new GetOppoGoalInFOVAction());
        addAvailableAction(new ScanForTeammatesAction());
        
        addAvailableAction(new MoveToMidPositionAction());
        addAvailableAction(new MoveToBallAction());
        addAvailableAction(new CarryBallForwardAction());
        addAvailableAction(new TeleportAction());
        
        addAvailableAction(new PassToTeammateAction());
    }


    @Override
    public void specificRun() {
        while (!(getPitch().state.get(StateKeys.time_up) || getPitch().state.get(StateKeys.time_over))){
            planAndExecute();
        }
    }
}