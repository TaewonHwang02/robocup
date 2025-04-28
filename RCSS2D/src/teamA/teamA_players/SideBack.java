package teamA.teamA_players;
import common.StateKeys;
// add
import common.actions.*;
import common.goals.*;

//add
import teamA.teamA_actions.*;
//add
import teamA.teamA_goals.*;

import common.goals.ResetPositionsGoal;
import common.players.Player;
import common.players.Team;

public class SideBack extends Player {

    public SideBack(int num, String teamName, boolean vis, int xPos, int yPos, String side, Team team) {
        super(num, teamName, vis, xPos, yPos, false, side, team, "sideback");

        // GOALS
        addAvailableGoal(new FocusOnBallGoal());
        // add
        addAvailableGoal(new SupportWingerGoal());
        //add
        addAvailableGoal(new GetInSideDefenseGoal());
        addAvailableGoal(new ResetPositionsGoal());

        // ACTIONS
        addAvailableAction(new MoveToWingSupportAction());
        addAvailableAction(new MoveToDefPositionAction());
        addAvailableAction(new MoveToBallAction());
        addAvailableAction(new TeleportAction());
        addAvailableAction(new TackleAction());
        addAvailableAction(new CrossBallAction());
    }

    @Override
    public void specificRun() {
        while (!(getPitch().state.get(StateKeys.time_up) || getPitch().state.get(StateKeys.time_over))){
            planAndExecute();
        }
    }
}