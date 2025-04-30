package teamA.teamA_players;
import common.StateKeys;
import common.actions.*;
import common.goals.*;
import common.players.Player;
import common.players.Team;
import teamA.teamA_actions.MoveToSideDefenseAction;
import teamA.teamA_actions.MoveToWingerAssistAction;
import teamA.teamA_goals.AssistWingerGoal;
import teamA.teamA_goals.GetInSideDefenseGoal;

public class SideBack extends Player {

    public SideBack(int num, String teamName, boolean vis, int xPos, int yPos, String side, Team team) {
        super(num, teamName, vis, xPos, yPos, false, side, team, "sideback");

        // GOALS
        addAvailableGoal(new FocusOnBallGoal());
        // add
        //add
        addAvailableGoal(new GetInSideDefenseGoal());
        addAvailableGoal(new ResetPositionsGoal());
        addAvailableGoal(new AssistWingerGoal());
        addAvailableGoal(new FocusOnBallGoal());

        // ACTIONS
        addAvailableAction(new GetBallInFOVAction());       
        addAvailableAction(new CenterBallInFOVAction());    
        addAvailableAction(new MoveToBallAction());
        addAvailableAction(new MoveToSideDefenseAction());
        //addAvailableAction(new TeleportAction());
        addAvailableAction(new TackleAction());
        addAvailableAction(new CrossBallAction());
        addAvailableAction(new MoveToWingerAssistAction());
    }

    @Override
    public void specificRun() {
        while (!(getPitch().state.get(StateKeys.time_up)
            || getPitch().state.get(StateKeys.time_over))) {
            

            planAndExecute();
        }
}

}