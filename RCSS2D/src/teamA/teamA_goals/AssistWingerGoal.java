package teamA.teamA_goals;

import common.StateKeys;
import common.goals.GOAPGoal;
import common.players.Player;

public class AssistWingerGoal extends GOAPGoal {
    public AssistWingerGoal() {
        addCondition(StateKeys.in_support_winger, true);
    }
    @Override
    public boolean validitySpecifics(Player agent) {
        Boolean weHave = agent.getPitch().state.get(StateKeys.team_has_ball);
        return (weHave != null && weHave) && agent.playerCanDash();
    }
    @Override
    public double calculatePriority(Player agent) {
        return 1.2; // fairly high priority when valid
    }
}
