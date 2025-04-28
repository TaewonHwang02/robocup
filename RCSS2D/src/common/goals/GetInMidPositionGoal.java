package common.goals;

import common.StateKeys;
import common.players.Player;

/**
 * Go to designated position.
 * Used by: Midfielder.
 */
public class GetInMidPositionGoal extends GOAPGoal{

    public GetInMidPositionGoal() {
        this.addCondition(StateKeys.in_mid_position, true);
        this.addCondition(StateKeys.before_kick_off, false);
    }

    @Override
    public double calculatePriority(Player agent) {
        return 4;
    }

    @Override
    public boolean validitySpecifics(Player agent) {
    	/**
    	 *  Will only go to Midfielder position if:
    	 *  1. Player can dash
    	 *  2. Team has the ball
    	 *  3. Player does not have ball (else CarryForward)
    	 *  4. Player not carrying ball forward
    	 */
    	return agent.playerCanDash() &&
    			agent.getPitch().getState().get(StateKeys.team_has_ball) &&
    			!agent.getPitch().getState().get(StateKeys.has_ball) &&
    			!agent.getPitch().getState().get(StateKeys.carrying_ball_fwd);
    }
}