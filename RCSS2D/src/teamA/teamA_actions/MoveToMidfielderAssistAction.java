package teamA.teamA_actions;

import common.PlayerMath;
import common.StateKeys;
import common.Tuple;
import common.actions.GOAPAction;
import common.players.Player;
import java.io.IOException;

// Action used by centerbacks to follow clostest midfielder. Trailing behind them by x meters,
// without crossing the center line

// Constructor.
// Only run if your team has the ball and you are not yet supporting a midfielder.
// in_support_midfielder meaning, the centerback is already in "supporting midfielder state"
// If completed, mark that you are now in_support_midfielder.

public class MoveToMidfielderAssistAction extends GOAPAction {
    // Reference to clostest midfielder
    private Player targetMid;
    // position of the midfielder
    private double  tx, ty;
    // flag that says whether the target position (tx, ty) has been successfully computed yet.
    private boolean computedTarget = false;

    public MoveToMidfielderAssistAction() {
        super(false, 1.2);
        addPrecondition(StateKeys.team_has_ball, true);
        addPrecondition(StateKeys.in_support_midfielder, false);
        addEffect(StateKeys.in_support_midfielder,  true);
    }
    // This is the main logic to decide if the action should run and where to go:
    @Override
    public boolean checkProceduralPrecondition(Player p) {
        // 1) find closest midfielder
        Player[] mates = p.getTeam().getPlayers();
        double best = Double.MAX_VALUE;
        targetMid = null;
        for (Player t : mates) {
            if (t != null && "midfielder".equals(t.getType())) {
                double d = PlayerMath.findDistanceWithPoint(
                    p.getPlayerPos(),
                    t.getPlayerPos().iParams[0],
                    t.getPlayerPos().iParams[1]
                );
                if (d < best) {
                    best = d;
                    targetMid = t;
                }
            }
        }
        // If no midfielder found, this action is not applicable
        if (targetMid == null) return false;

    
         // Get ball position
        Tuple[] balls = p.getPitch().getBallsCartesian();
        Tuple ball = balls.length > 1 && balls[1] != null ? balls[1] : balls[0];
        double bx = ball.iParams[0], by = ball.iParams[1];

        // Get midfielder position
        Tuple mp = targetMid.getPlayerPos();
        double mx = mp.iParams[0], my = mp.iParams[1];

        double vx = mx - bx, vy = my - by;
        double len = Math.hypot(vx, vy);
        if (len < 1e-3) {
            int[] home = PlayerMath.getStartPosition(p.getNum(), p.getSide());
            tx = home[0] - (p.getSide().equals("l") ? 7 : -7);
            ty = home[1];
        } else {
            tx = mx - 7 * (vx / len);
            ty = my - 7 * (vy / len);
        }

        // Prevent defenders passing the center line
        if (p.getSide().equals("l")) {
            tx = Math.min(tx, 0.0);
        } else {
            tx = Math.max(tx, 0.0);
        }

        computedTarget = true;
        return true;
    }

    @Override
    public boolean executeAction(Player p) {
        if (!computedTarget) return false;

        // Get current player position
        double px = p.getPlayerPos().iParams[0],
               py = p.getPlayerPos().iParams[1];
        // check if player reached target position
        if (Math.hypot(px - tx, py - ty) < 1.0) {
            p.getPitch().state.put(StateKeys.in_support_midfielder, true);
            return true;
        }
        // Dash up or down depending on Y position relative to target
        try {
            double dir = 0.0;
            if (py > ty) {
                // Too low, dash up field
                dir = 90;
            } else if (py < ty) {
                // Too high, dash down field
                dir = -90;
            }
            p.doDash("30", String.valueOf(dir));
        } catch(IOException e) {
            e.printStackTrace();  
            return false;
    }

        return false;
    }

    @Override
    public void resetActionSpecifics() {
        computedTarget = false;
        targetMid      = null;
    }
}
