package common.actions;

import common.StateKeys;
import common.players.Player;
import common.Tuple;
import common.PlayerMath;

import java.awt.geom.Point2D;
import java.io.IOException;

/**
 * Looks around to find teammates.
 * Used by: Attacker, Midfielder, Winger.
 */
public class ScanForTeammatesAction extends GOAPAction{

    public ScanForTeammatesAction() {
        super(false, 1);
        this.addPrecondition(StateKeys.teammate_in_FOV, false);
        this.addPrecondition(StateKeys.has_ball, true);
        
        this.addEffect(StateKeys.teammate_in_FOV, true);
        //this.addEffect(StateKeys.kick_performed, true);
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
        // perform 72 degree turns until we find a teammate that's close enough.
        // the field of view of the player is 90 degrees, but having the turn angle slightly smaller can help with finding the ball
        for (int i = 0; i < 5; i++){
            if (agent.getPitch().getState().get(StateKeys.teammate_in_FOV)){
            	Tuple teammate_in_FOV = agent.getPitch().getTeammates().get(0);
                double dist = teammate_in_FOV.getIParams()[1];
            	
                if (dist < 30) {
            		return true;
            	}
            }
            try {
                agent.doTurn("72");
                agent.reader.read();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return false;
    }
}
