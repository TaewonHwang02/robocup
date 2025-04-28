package common.players;

import java.util.LinkedList;

import common.StateKeys;
import common.actions.*;
import common.goals.*;

/**
 * Defender: Behaviour should be as follow
 * Stay behind to defend goal
 * Keep ball in sight
 * Keep reasonable distance with the ball
 * Tackle if ball get too close to goal
 */
public class Defender extends Player {

    public Defender(int num, String teamName, boolean vis, int xPos, int yPos, String side, Team team) {
        super(num, teamName, vis, xPos, yPos, false, side, team, "defender");

        // GOALS
        addAvailableGoal(new FocusOnBallGoal());
        addAvailableGoal(new ResetPositionsGoal());
        addAvailableGoal(new GetInDefPositionGoal());
        addAvailableGoal(new ClearBallGoal());

        // ACTIONS
        addAvailableAction(new GetBallInFOVAction());
        addAvailableAction(new CenterBallInFOVAction());

        addAvailableAction(new MoveToDefPositionAction());
        addAvailableAction(new AlignWithBallAction());
        addAvailableAction(new MoveToBallAction());
        addAvailableAction(new TeleportAction());

        addAvailableAction(new TackleAction());
    }

    @Override
    public void specificRun() {
        while (!(getPitch().state.get(StateKeys.time_up) || getPitch().state.get(StateKeys.time_over))){
            planAndExecute();
        }
    }
}