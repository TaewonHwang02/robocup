package teamA.teamA_actions;

import common.PlayerMath;
import common.StateKeys;
import common.Tuple;
import common.actions.GOAPAction;
import common.players.Player;
import java.io.IOException;

/**
 * When our team has the ball, tuck in behind the nearest winger
 * by maintaining a 5 m cushion (and never need to see/align with the ball).
 */
public class MoveToWingerAssistAction extends GOAPAction {
    private Player targetWinger;
    private double tx, ty;
    private boolean computedTarget = false;

    public MoveToWingerAssistAction() {
        super(false, 1.0);

        addPrecondition(StateKeys.team_has_ball,     true);
        addPrecondition(StateKeys.in_support_winger, false);
        addEffect(     StateKeys.in_support_winger,  true);
    }

    @Override
    public boolean checkProceduralPrecondition(Player p) {
        Player[] mates = p.getTeam().getPlayers();
        double bestDist = Double.MAX_VALUE;
        targetWinger = null;

        Tuple me = p.getPlayerPos();
        for (Player t : mates) {
            if (t != null && "winger".equals(t.getType())) {
                Tuple wp = t.getPlayerPos();
                double d = PlayerMath.findDistanceWithPoint(
                    me,
                    wp.iParams[0], wp.iParams[1]
                );
                if (d < bestDist) {
                    bestDist = d;
                    targetWinger = t;
                }
            }
        }
        if (targetWinger == null) return false;

        // compute 5m behind that winger
        Tuple wp = targetWinger.getPlayerPos();
        double wx = wp.iParams[0], wy = wp.iParams[1];
        double offset = p.getSide().equals("l") ? -5 : +5;
        tx = wx + offset;
        ty = wy;

        computedTarget = true;
        return true;
    }

    @Override
    public boolean executeAction(Player p) {
        if (!computedTarget) return false;
    
        Tuple pos = p.getPlayerPos();
        if (pos == null || pos.iParams.length < 2) return false;
    
        double myY = pos.iParams[1];
    
        // If we’re more than 1m below the target, dash up (90°)
        if (myY < ty - 1.0) {
            try {
                p.doDash("100", "90");
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        // If we’re more than 1m above the target, dash down (-90°)
        else if (myY > ty + 1.0) {
            try {
                p.doDash("100", "-90");
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        // Otherwise, we’re in position
        else {
            p.getPitch().state.put(StateKeys.in_support_winger, true);
            return true;
        }
    
        return false;
    }
    

    @Override
    public void resetActionSpecifics() {
        computedTarget = false;
        targetWinger   = null;
    }
}
