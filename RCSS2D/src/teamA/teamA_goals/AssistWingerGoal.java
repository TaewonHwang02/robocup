package teamA.teamA_goals;

import common.StateKeys;
import common.goals.GOAPGoal;
import common.players.Player;

public class AssistWingerGoal extends GOAPGoal {
    public AssistWingerGoal() {
        // end‐state: we want our sideback in assist position
        addCondition(StateKeys.in_support_winger, true);
    }
    @Override
    public boolean validitySpecifics(Player agent) {
        // only when *our* team has possession
        Boolean weHave = agent.getPitch().state.get(StateKeys.team_has_ball);
        return weHave != null && weHave;
    }
    @Override
    public double calculatePriority(Player agent) {
        return 1.2; // fairly high priority when valid
    }
}
