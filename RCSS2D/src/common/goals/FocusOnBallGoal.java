package common.goals;

import common.StateKeys;
import common.players.Player;

/**
 * Find the ball and focus on it.
 * Used by: All.
 */
public class FocusOnBallGoal extends GOAPGoal{
    // A very general goal to perform if nothing else is available

    public FocusOnBallGoal() {
        this.addCondition(StateKeys.ball_centered_in_FOV, true);
    }

    @Override
    public double calculatePriority(Player agent) {
        return 1000;
    }

    @Override
    public boolean validitySpecifics(Player agent) {
        return true;
    }
}
