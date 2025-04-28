package teamA.teamA_goals;

import common.StateKeys;
import common.players.Player;
import common.goals.GOAPGoal;
/**
 * Goal: when our team has possession, move up to support the winger.
 */
public class SupportWingerGoal extends GOAPGoal {

    public SupportWingerGoal() {
        // Tell the planner that we want in_support_position == true
        addCondition(StateKeys.in_support_position, true);
    }

    /**
     * Only valid when our team has possession of the ball.
     */
    @Override
    public boolean validitySpecifics(Player agent) {
        Boolean weHave = agent.getPitch().state.get(StateKeys.team_has_ball);
        return weHave != null && weHave;
    }

    /**
     * Give this goal a moderate priority when valid, otherwise disable it.
     */
    @Override
    public double calculatePriority(Player agent) {
        return validitySpecifics(agent) ? 1.5 : Double.MAX_VALUE;
    }
}