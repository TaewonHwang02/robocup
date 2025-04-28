package common.players;

import common.StateKeys;
import common.actions.*;
import common.goals.*;

/**
 * Attacker: Behaviour should be as follow
 * If team has ball: Go towards goal
 * If team does not have ball: Stay close to ball
 * Be available for pass
 * Scan for pass and pass to Mid if opponent blocking the way
 * Shoot to goal when possible
 */
public class Attacker extends Player {

    public Attacker(int num, String teamName, boolean vis, int xPos, int yPos, String side, Team team) {
        super(num, teamName, vis, xPos, yPos, false, side, team, "attacker");


        // GOALS
        addAvailableGoal(new FocusOnBallGoal());
        addAvailableGoal(new KickBallGoal());
        addAvailableGoal(new PassBallGoal());
        addAvailableGoal(new GetInAttPositionGoal());
        addAvailableGoal(new ResetPositionsGoal());

        // ACTIONS
        addAvailableAction(new GetBallInFOVAction());
        addAvailableAction(new GetOppoGoalInFOVAction());
        addAvailableAction(new CenterBallInFOVAction());
        addAvailableAction(new ScanForTeammatesAction());

        addAvailableAction(new MoveToBallAction());
        addAvailableAction(new MoveToAttPositionAction());
        addAvailableAction(new GetUnmarkedAction());
        addAvailableAction(new BringBallToGoalAction());
        addAvailableAction(new TeleportAction());

        addAvailableAction(new KickBallToGoalAction());
        addAvailableAction(new PassToTeammateAction());
    }


    @Override
    public void specificRun() {
        while (!(getPitch().state.get(StateKeys.time_up) || getPitch().state.get(StateKeys.time_over))){
            planAndExecute();
        }
    }
}