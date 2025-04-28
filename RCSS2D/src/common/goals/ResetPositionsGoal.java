package common.goals;

import common.StateKeys;
import common.Tuple;
import common.players.Player;

/**
 * Make the agent move to some specific position.
 * Used by: All.
 */
public class ResetPositionsGoal extends GOAPGoal{

    public ResetPositionsGoal() {
        addCondition(StateKeys.in_new_position, true);
    }

    @Override
    public double calculatePriority(Player agent) {
        return 1;
    }

    @Override
    public boolean validitySpecifics(Player agent) {
        Tuple ball = agent.getPitch().getBall();
        return agent.getPitch().state.get(StateKeys.move_allowed) || (ball != null && ball.iParams[1] > 60);
    }
}
