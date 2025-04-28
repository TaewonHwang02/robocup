package common.actions;

import common.PlayerMath;
import common.StateKeys;
import common.Tuple;
import common.players.Player;

import java.awt.geom.Point2D;
import java.io.IOException;

/**
 * Make the player go towards its position. Attacker is in position if pos.x > 30 (or < -30)
 * Used by: Attacker.
 */
public class MoveToAttPositionAction extends GOAPAction{

    public MoveToAttPositionAction() {
        super(false, 1);
        addPrecondition(StateKeys.in_att_position, false);
        addPrecondition(StateKeys.oppo_goal_in_FOV, true);
        
        addEffect(StateKeys.in_att_position, true);
        addEffect(StateKeys.in_new_position, false);
    }

    @Override
    public void resetActionSpecifics() {
    }

    @Override
    public boolean checkProceduralPrecondition(Player agent) {
    	return agent.getPitch().state.get(StateKeys.team_has_ball) &&
    			!agent.getPitch().state.get(StateKeys.in_att_position);
    }

    @Override
    public boolean executeAction(Player agent) {
        for (int i = 0; i < 3; i++){
            if (agent.goalStillValid()) {
                try {
	            	agent.doDash("30");
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