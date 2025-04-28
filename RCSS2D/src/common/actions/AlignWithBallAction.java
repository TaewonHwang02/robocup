package common.actions;

import common.PlayerMath;
import common.StateKeys;
import common.Tuple;
import common.players.Player;

import java.awt.geom.Point2D;
import java.io.IOException;

/**
 * Make the player align with the ball on the Y axis
 * Used by: Defender
 */
public class AlignWithBallAction extends GOAPAction{

    public AlignWithBallAction() {
        super(false, 1);
        addPrecondition(StateKeys.aligned_with_ball, false);
        addPrecondition(StateKeys.ball_centered_in_FOV, true);
        
        addEffect(StateKeys.aligned_with_ball, true);
        addEffect(StateKeys.in_new_position, false);
    }

    @Override
    public void resetActionSpecifics() {
    }

    @Override
    public boolean checkProceduralPrecondition(Player agent) {
    	return true;
    }

    @Override
    public boolean executeAction(Player agent) {
    	while (!agent.getPitch().state.get(StateKeys.aligned_with_ball)){
			if (agent.goalStillValid()) {
	        	// Try Aligning with ball
	            try {
	            	// Parameters of ball in cartesian coordinates: 0 is time, 1 is x position, 2 is y position
	            	Tuple ball_pos = agent.getPitch().getBallsCartesian()[1];
	            	double ball_pos_y = ball_pos.getIParams()[2];
	            	
	            	// Parameters of player position tuples: 0 is x position, 1 is y position, 2 is absolute direction
	            	Tuple player_pos = agent.getPlayerPos();
	            	double player_pos_x = player_pos.getIParams()[0];
	            	double player_pos_y = player_pos.getIParams()[1];
	            	double player_dir = player_pos.getIParams()[2];
	            	
	            	// If player too up the field, dash down
	            	if (player_pos_y < ball_pos_y) {
	            		double down = 90 - player_dir;
	            		agent.doDash("30", String.valueOf(down));
	            	}
	            	
	            	// If player too low on the field, dash up
	            	else if (player_pos_y > ball_pos_y) {
	            		double up = -90 - player_dir;
	            		agent.doDash("30", String.valueOf(up));
	            	}
	            	
	            	// Else, end action
	            	else {
	            		return true;
	            	}
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	            
	            agent.reader.read();
                if (!agent.getPitch().state.get(StateKeys.ball_centered_in_FOV)) {
                    return false;
                }
	        }
	        
	        else {
	            return false;
	        }
    	}
        return true;
    }
}