package common.goals;

import common.StateKeys;
import common.Tuple;
import common.players.Player;

/**
 * Attempt to score by performing a kick.
 * Used by: Attacker, Winger
 */
public class KickBallGoal extends GOAPGoal{

    public KickBallGoal() {
        this.addCondition(StateKeys.kick_performed, true);
    }

    @Override
    public double calculatePriority(Player agent) {

        // parameters: 0 is time, 1 is distance, 2 is direction
        Tuple[] balls = agent.getBallsPolar();
        if (balls[1] == null){
            return Double.MAX_VALUE;
        }
        
        else {
        	// parameters: 0 is x, 1 is y, 2 is direction
            Tuple[] balls_cartesian = agent.getBallsCartesian();
            double ball_x = balls_cartesian[1].iParams[0];
            
            // parameters: 0 is x, 1 is y
            Tuple player_pos = agent.getPlayerPos();
            double player_x = player_pos.getIParams()[0];
            
            boolean ball_in_front = (ball_x > player_x && agent.getSide().equals("l")) ||
            						(ball_x < player_x && agent.getSide().equals("r"));

	        if (balls[1].iParams[1] <= 10 || 
	        		(!agent.getPitch().getState().get(StateKeys.oppo_blocking) && ball_in_front) ) {
	            return 2;
	        }
        }
        return Double.MAX_VALUE;
    }

    @Override
    public boolean validitySpecifics(Player agent) {
    	return agent.playerCanDash();
    }
}
