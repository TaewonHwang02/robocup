package common.actions;

import common.StateKeys;
import common.players.Player;
import common.PlayerMath;
import common.Tuple;

import java.awt.geom.Point2D;
import java.io.IOException;

/**
 * Find the opponent's goal.
 * Used by: Attacker, Midfielder, Winger.
 */
public class GetOppoGoalInFOVAction extends GOAPAction{

    public GetOppoGoalInFOVAction() {
        super(false, 1);
        this.addEffect(StateKeys.oppo_goal_in_FOV, true);
    }

    @Override
    public void resetActionSpecifics() {

    }

    @Override
    public boolean checkProceduralPrecondition(Player agent) {
        return !agent.getPitch().getState().get(StateKeys.oppo_goal_in_FOV);
    }

    @Override
    public boolean executeAction(Player agent) {
    	if (agent.goalStillValid()) {
    		try {
    			Tuple player_pos = agent.getPlayerPos();
	    		Point2D.Double goal_known = PlayerMath.getOppoGoal(agent);
	    		
	        	double oppo_goal_dir = PlayerMath.findAngleWithPoint(player_pos, goal_known.x, goal_known.y);
	        	agent.doTurn(String.valueOf(oppo_goal_dir));
	        			
	        	return true;
    		} catch (IOException e) {
                e.printStackTrace();
    		}
    	}
        return false;
    }
}