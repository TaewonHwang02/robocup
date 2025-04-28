// File: src/teamA/actions/MoveToWingSupportAction.java
package teamA.teamA_actions;

import common.PlayerMath;
import common.StateKeys;
import common.players.Player;
import common.actions.GOAPAction;

import java.io.IOException;

/**
 * MoveToWingSupportAction:
 *   SideBack overlaps the winger by running up the flank **when our team has possession**.
 */
public class MoveToWingSupportAction extends GOAPAction {

    public MoveToWingSupportAction() {
        // (canExecuteImmediately=false, cost=1)
        super(false, 1);

        // Only consider this action when *we* have the ball
        addPrecondition(StateKeys.team_has_ball, true);

        // After running, we'll be "in_support_position"
        addEffect(StateKeys.in_support_position, true);
    }

    @Override
    public void resetActionSpecifics() {
        // clear any per‐action state here (none needed)
    }

    @Override
    public boolean checkProceduralPrecondition(Player p) {
        // double‐check: we only want to overlap when we truly have possession
        Boolean weHave = p.getPitch().state.get(StateKeys.team_has_ball);
        return weHave != null && weHave;
    }

    @Override
    public boolean executeAction(Player p) {
        // compute the overlap target relative to our starting spot
        int[] base = PlayerMath.getStartPosition(p.getNum(), p.getSide());
        double targetX = base[0] + (p.getSide().equals("l") ? 10 : -10);
        double targetY = base[1];

        try {
            p.doMove(String.valueOf(targetX), String.valueOf(targetY));
            // if you want to wait for the server ack:
            p.reader.read();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
