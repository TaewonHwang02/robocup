package common.goals;

import common.StateKeys;
import common.Tuple;
import common.players.Player;

/**
 * Relieve pressure by performing a pass.
 * Used by: Attacker, Midfielder, Winger.
 */
public class PassBallGoal extends GOAPGoal{

    public PassBallGoal() {
        this.addCondition(StateKeys.kick_performed, true);
    }

    @Override
    public double calculatePriority(Player agent) {
        return 3;
    }

    @Override
    public boolean validitySpecifics(Player agent) {
    	/**
    	 *  Will only pass if:
    	 *  1. Can Dash
    	 *  2. Has Ball OR Is carrying ball fwd
    	 */
        return agent.playerCanDash() &&
        		(agent.getPitch().getState().get(StateKeys.has_ball) || agent.getPitch().getState().get(StateKeys.carrying_ball_fwd));
    }
}