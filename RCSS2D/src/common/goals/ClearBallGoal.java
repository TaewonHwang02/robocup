package common.goals;

import common.StateKeys;
import common.players.Player;

/**
 * Clear the ball if foe is attacking.
 * Used by: Defender.
 */
public class ClearBallGoal extends GOAPGoal{

    public ClearBallGoal() {
        addCondition(StateKeys.has_ball, false);
        addCondition(StateKeys.oppo_attacking, false);
    }

    @Override
    public double calculatePriority(Player agent) {

    	// If Opposition is attacking
        if (agent.getPitch().state.get(StateKeys.oppo_attacking)) {
            return 2;
        }
        return Double.MAX_VALUE;
    }

    @Override
    public boolean validitySpecifics(Player agent) {
        return agent.playerCanDash();
    }
}