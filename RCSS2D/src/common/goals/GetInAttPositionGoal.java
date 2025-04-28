package common.goals;

import common.StateKeys;
import common.players.Player;

/**
 * Go to designated position.
 * Used by: Attacker.
 */
public class GetInAttPositionGoal extends GOAPGoal{

    public GetInAttPositionGoal() {
        this.addCondition(StateKeys.in_att_position, true);
        this.addCondition(StateKeys.oppo_blocking, false);
        this.addCondition(StateKeys.before_kick_off, false);
    }

    @Override
    public double calculatePriority(Player agent) {
        return 3;
    }

    @Override
    public boolean validitySpecifics(Player agent) {
    	return agent.playerCanDash();
    }
}