// File: RCSS2D/src/teamA/teamA_actions/MoveToSideDefenseAction.java
package teamA.teamA_actions;

import common.PlayerMath;
import common.StateKeys;
import common.Tuple;
import common.actions.GOAPAction;
import common.players.Player;
import java.io.IOException;

/**
 * Tucks the SideBack into its flank‐defence slot whenever it's not already there,
 * but only if the ball is safely far away.
 */
public class MoveToSideDefenseAction extends GOAPAction {

    // flank target coordinates
    private double tx, ty;
    private boolean computedTarget = false;

    // don't tuck in if the ball is closer than this (meters)
    private static final double BALL_DANGER_RADIUS = 20.0;

    public MoveToSideDefenseAction() {
        // (running=false, cost=1.0)
        super(false, 1.0);

        // only run if we're not already in_side_defense
        addPrecondition(StateKeys.in_side_defense, false);
        // when done, we'll have in_side_defense==true
        addEffect(StateKeys.in_side_defense, true);
    }

    /**
     * Compute the exact slot we need to cover, once per plan—
     * but bail out early if the ball is too close or data is missing.
     */
    @Override
    public boolean checkProceduralPrecondition(Player p) {
        Boolean inSlot = p.getPitch().state.get(StateKeys.in_side_defense);
        if (inSlot != null && inSlot) {
            return false;
        }

        // 1) Defensive: verify ball is available and safe
        Tuple[] balls = p.getBallsCartesian();
        if (balls == null || balls.length <= 1 || balls[1] == null || balls[1].iParams.length < 2) {
            return false;
        }

        Tuple ballT = balls[1];
        double bx = ballT.iParams[0];
        double by = ballT.iParams[1];

        Tuple posT = p.getPlayerPos();
        if (posT == null || posT.iParams.length < 2) {
            return false;
        }

        double px = posT.iParams[0];
        double py = posT.iParams[1];

        if (Math.hypot(bx - px, by - py) < BALL_DANGER_RADIUS) {
            return false;
        }

        // 2) Compute target slot: home + 5m inward
        int jersey = p.getNum();
        int[] home = PlayerMath.getStartPosition(jersey, p.getSide());
        double dx = p.getSide().equals("l") ? +5 : -5;
        tx = home[0] + dx;
        ty = home[1];
        computedTarget = true;
        return true;
    }

    /**
     * Move each tick until we arrive; mark state when complete.
     */
    @Override
    public boolean executeAction(Player p) {
        if (!computedTarget) {
            return false;
        }

        Tuple posT = p.getPlayerPos();
        if (posT == null || posT.iParams.length < 2) {
            return false;
        }

        double px = posT.iParams[0];
        double py = posT.iParams[1];
        double dist = Math.hypot(px - tx, py - ty);

        if (dist < 1.0) {
            p.getPitch().state.put(StateKeys.in_side_defense, true);
            return true;
        }

        // Issue move command
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
