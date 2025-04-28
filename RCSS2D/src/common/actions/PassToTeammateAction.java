package common.actions;

import common.StateKeys;
import common.Tuple;
import common.players.Player;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Pass ball to teammate to avoid pressure.
 * Used by: Attacker, Midfielder, Winger.
 */
public class PassToTeammateAction extends GOAPAction {

    public PassToTeammateAction() {
        super(false, 1);
        addPrecondition(StateKeys.has_ball, true);
        addPrecondition(StateKeys.oppo_blocking, true);
        //addPrecondition(StateKeys.in_oppo_fourth, true);
        addPrecondition(StateKeys.teammate_in_FOV, true);

        addEffect(StateKeys.has_ball, false);
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
        // Parameters: 0 is time, 1 is dist, 2 is dir
        ArrayList<Tuple> teammates_in_FOV = agent.getPitch().getTeammates();
        Tuple teammate_to_pass = teammates_in_FOV.get(0);
        double dist = 2 * teammate_to_pass.getIParams()[1];
        double dir = teammate_to_pass.getIParams()[2];

        try {
            //agent.doSay("have_b");
            agent.doKick(String.valueOf(dist), String.valueOf(dir));
            agent.reader.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}
