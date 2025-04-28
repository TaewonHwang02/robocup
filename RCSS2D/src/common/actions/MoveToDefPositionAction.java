package common.actions;

import common.PlayerMath;
import common.StateKeys;
import common.Tuple;
import common.players.Player;

import java.awt.geom.Point2D;
import java.io.IOException;

/**
 * Make the player go towards its position. Defender is in position if close to its goal AND aligned with ball.
 * Used by: Defender.
 */
public class MoveToDefPositionAction extends GOAPAction{

    public MoveToDefPositionAction() {
        super(false, 1);
        addPrecondition(StateKeys.in_def_position, false);
        addPrecondition(StateKeys.aligned_with_ball, true);
        addPrecondition(StateKeys.ball_centered_in_FOV, true);
        
        addEffect(StateKeys.in_def_position, true);
        addEffect(StateKeys.in_new_position, false);
    }

    @Override
    public void resetActionSpecifics() {
    }

    @Override
    public boolean checkProceduralPrecondition(Player agent) {
    	return agent.getPitch().getBall() != null;
    }

    @Override
    public boolean executeAction(Player agent) {
    	for (int i=0; i<10; i++) {
			if (agent.goalStillValid()) {
	        	// Try Moving backwards
	            try {
	            	Point2D.Double goal_known = PlayerMath.getTeamGoal(agent);
	            	agent.doDash("30", String.valueOf(PlayerMath.findAngleWithPoint(agent.getPlayerPos(), goal_known.x, goal_known.y)));
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	            
	            agent.reader.read();
	        }
	        
	        else {
	            return false;
	        }
    	}
        return true;
    }
}