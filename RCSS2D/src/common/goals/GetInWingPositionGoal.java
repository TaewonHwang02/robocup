package common.goals;

import common.StateKeys;
import common.players.Player;

/**
 * Go to designated position.
 * Used by: Winger.
 */

public class GetInWingPositionGoal extends GOAPGoal {

    public GetInWingPositionGoal() {
        this.addCondition(StateKeys.in_win_position, true);
        // this.addCondition(StateKeys.oppo_blocking, false);
        this.addCondition(StateKeys.before_kick_off, false);
    }

    @Override
    public double calculatePriority(Player agent) {
        return 4;
    }

    @Override
    public boolean validitySpecifics(Player agent) {
        return agent.playerCanDash();
    }

}
