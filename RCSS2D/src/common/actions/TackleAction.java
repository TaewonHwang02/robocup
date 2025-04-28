package common.actions;

import common.StateKeys;
import common.Tuple;
import common.players.Player;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Intercept ball from incoming foe by tackling.
 * Used by: Defender.
 */
public class TackleAction extends GOAPAction {
	
	public TackleAction() {
		super(false, 1);
        addPrecondition(StateKeys.has_ball, true);
        addPrecondition(StateKeys.oppo_attacking, true);
        addPrecondition(StateKeys.ball_in_FOV, true);

        addEffect(StateKeys.has_ball, false);
        addEffect(StateKeys.oppo_attacking, false);
        addEffect(StateKeys.kick_performed, true);
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
        if (agent.goalStillValid()) {
            try {
            	agent.doSay("tackle_b");
            	agent.doTackle("15");
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            return true;
        }
        else {
            return false;
        }
        
    }
}