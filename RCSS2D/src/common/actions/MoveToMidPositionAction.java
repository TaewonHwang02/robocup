package common.actions;

import common.PlayerMath;
import common.StateKeys;
import common.Tuple;
import common.players.Player;

import java.awt.geom.Point2D;
import java.io.IOException;

/**
 * Make the player go towards its position. Midfielder is in position if aligned with ball on X axis and far from it.
 * Used by: Midfielder.
 */
public class MoveToMidPositionAction extends GOAPAction{

    public MoveToMidPositionAction() {
        super(false, 1);
        addPrecondition(StateKeys.in_mid_position, false);
        addPrecondition(StateKeys.oppo_goal_in_FOV, true);
        
        addEffect(StateKeys.in_mid_position, true);
        addEffect(StateKeys.in_new_position, false);
    }

    @Override
    public void resetActionSpecifics() {
    }

    @Override
    public boolean checkProceduralPrecondition(Player agent) {
    	return !agent.getPitch().getState().get(StateKeys.carrying_ball_fwd);
    }

    @Override
    public boolean executeAction(Player agent) {
    	for (int i = 0; i < 5; i++){
            if (agent.goalStillValid()) {
                try {
                	// Get Player Position: Move up if player is initially up on field, down otherwise
                	Tuple player_pos = agent.getPlayerPos();
                	int[] init_position = PlayerMath.getStartPosition(agent.getNum(), agent.getSide());
                	
                	double delta = 0;
                	if (init_position[1] > 0) {
                		delta = 20;
                	} else {
                		delta = -20;
                	}
                	
                	Point2D.Double goal_known = PlayerMath.getOppoGoal(agent);
	            	agent.doDash("30", String.valueOf(PlayerMath.findAngleWithPoint(agent.getPlayerPos(), goal_known.x, goal_known.y + delta)));
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