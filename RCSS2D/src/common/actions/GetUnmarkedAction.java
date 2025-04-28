package common.actions;

import common.PlayerMath;
import common.StateKeys;
import common.Tuple;
import common.players.Player;

import java.awt.geom.Point2D;
import java.io.IOException;

/**
 * UNFINISHED: Gets away from surrounding players.
 * Used by: Attacker
 */
public class GetUnmarkedAction extends GOAPAction{
	public GetUnmarkedAction() {
        super(false, 1);
        addPrecondition(StateKeys.in_att_position, true);

        addEffect(StateKeys.oppo_blocking, false);
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
        for (int i=0; i<2; i++) {
	    	// Parameters: 0 is x, 1 is y, 2 is dist
	        try {
	            agent.doDash("30", String.valueOf(90));
	            agent.reader.read();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
        }
        return true;
    }
}
