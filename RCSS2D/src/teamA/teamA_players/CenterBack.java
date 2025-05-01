package teamA.teamA_players;
import common.StateKeys;
import common.actions.*;
import common.goals.*;
import common.players.Player;
import common.players.Team;
import teamA.teamA_actions.MoveToMidfielderAssistAction;
import teamA.teamA_goals.AssistMidfielderGoal;

public class CenterBack extends Player {

    public CenterBack(int num, String teamName, boolean vis,
                      int xPos, int yPos, String side, Team team) {
        super(num, teamName, vis, xPos, yPos, false, side, team, "centerback");

        // — GOALS —
        // 1) chase any loose ball immediately
        addAvailableGoal(new FocusOnBallGoal());

        // 2) fall back into position
        addAvailableGoal(new ResetPositionsGoal());
        addAvailableGoal(new GetInDefPositionGoal());

        // 3) if you’ve got it, clear it
        addAvailableGoal(new ClearBallGoal());

        // 4) otherwise support your midfielders
        addAvailableGoal(new AssistMidfielderGoal());

        // — ACTIONS —
        addAvailableAction(new GetBallInFOVAction());
        addAvailableAction(new CenterBallInFOVAction());
        addAvailableAction(new MoveToDefPositionAction());
        addAvailableAction(new AlignWithBallAction());
        addAvailableAction(new MoveToBallAction());
        addAvailableAction(new TeleportAction());
        addAvailableAction(new TackleAction());
        addAvailableAction(new MoveToMidfielderAssistAction());
    }

    @Override
    public void specificRun() {
        while (!(getPitch().state.get(StateKeys.time_up)
              || getPitch().state.get(StateKeys.time_over))) {
            planAndExecute();
        }
    }
}