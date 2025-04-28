package common.goals;

import common.StateKeys;
import common.players.Player;

/**
 * Go to designated position.
 * Used by: Defender.
 */
public class GetInDefPositionGoal extends GOAPGoal{

    public GetInDefPositionGoal() {
        this.addCondition(StateKeys.in_def_position, true);
        this.addCondition(StateKeys.aligned_with_ball, true);
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