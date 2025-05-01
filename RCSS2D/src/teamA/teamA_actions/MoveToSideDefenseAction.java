// File: RCSS2D/src/teamA/teamA_actions/MoveToSideDefenseAction.java
package teamA.teamA_actions;

import common.PlayerMath;
import common.StateKeys;
import common.Tuple;
import common.actions.GOAPAction;
import common.players.Player;
import java.io.IOException;

/**
 * Simplified: Always send the SideBack to a defensive slot,
 * ignoring ball proximity. Helps reinforce defensive area.
 */
public class MoveToSideDefenseAction extends GOAPAction {

    // target destination 
    private double tx, ty;

    // inidicating reached or not
    private boolean computedTarget = false;

    public MoveToSideDefenseAction() {
        super(false, 1.0);
        // precondition
        addPrecondition(StateKeys.in_side_defense, false);
        // after the act
        addEffect(StateKeys.in_side_defense, true);
    }

    @Override
    public boolean checkProceduralPrecondition(Player p) {
        // if already in the side defense position, do nothing 
        Boolean inSlot = p.getPitch().state.get(StateKeys.in_side_defense);
        if (inSlot != null && inSlot) {
            return false;
        }

        // Always move to defensive slot: home + 5 meters inward
        // close to teams defensive area
        int jersey = p.getNum();
        int[] home = PlayerMath.getStartPosition(jersey, p.getSide());
        double dx = p.getSide().equals("l") ? +5 : -5;
        tx = home[0] + dx;
        ty = home[1];

        computedTarget = true;
        return true;
    }
    // executing adction
    @Override
    public boolean executeAction(Player p) {

        // checking if checkProceduralPrecondition actual works.
        if (!computedTarget) return false;

        //player position
        Tuple posT = p.getPlayerPos();
        if (posT == null || posT.iParams.length < 2) return false;

        // computing current position Euclidean distance
        double px = posT.iParams[0];
        double py = posT.iParams[1];
        double dist = Math.hypot(px - tx, py - ty);

        // if player is close with target, its done.
        if (dist < 1.0) {
            p.getPitch().state.put(StateKeys.in_side_defense, true);
            return true;
        }
        //  Otherwise, if we’re still far from the target, issue a move command to go there.
        try {
            p.doMove(String.valueOf(tx), String.valueOf(ty));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }
    @Override
    public void resetActionSpecifics() {
        computedTarget = false;
    }
}
