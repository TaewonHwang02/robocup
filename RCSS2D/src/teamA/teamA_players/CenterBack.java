package teamA.teamA_players;


import common.StateKeys;
import common.actions.*;
import common.goals.*;
//add
//import common.goals.MarkStrongestAttackerGoal;
import common.goals.ClearBallGoal;
import common.goals.ResetPositionsGoal;
import common.players.Player;
import common.players.Team;

public class CenterBack extends Player {

    public CenterBack(int num, String teamName, boolean vis, int xPos, int yPos, String side, Team team) {
        super(num, teamName, vis, xPos, yPos, false, side, team, "centerback");

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
