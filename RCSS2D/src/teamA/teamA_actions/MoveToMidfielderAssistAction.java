package teamA.teamA_actions;

import common.PlayerMath;
import common.StateKeys;
import common.Tuple;
import common.actions.GOAPAction;
import common.players.Player;
import java.io.IOException;

/**
 * Trail the nearest midfielder by hanging back ~7 m behind them,
 * but never cross the center line (x=0).
 */
public class MoveToMidfielderAssistAction extends GOAPAction {
    private Player targetMid;
    private double  tx, ty;
    private boolean computedTarget = false;

    public MoveToMidfielderAssistAction() {
        super(false, 1.2);

        // —— drop the default “see & align with ball” requirements
        removePrecondition(StateKeys.ball_in_FOV);
        removePrecondition(StateKeys.aligned_with_ball);

        addPrecondition(StateKeys.team_has_ball,        true);
        addPrecondition(StateKeys.in_support_midfielder, false);
        addEffect(     StateKeys.in_support_midfielder,  true);
    }

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
        if (targetMid == null) return false;

        // 2) compute the “7m behind” spot
        Tuple[] balls = p.getPitch().getBallsCartesian();
        Tuple ball = balls.length > 1 && balls[1] != null ? balls[1] : balls[0];
        double bx = ball.iParams[0], by = ball.iParams[1];
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

        // 3) clamp at the center line
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
        double px = p.getPlayerPos().iParams[0],
               py = p.getPlayerPos().iParams[1];
        if (Math.hypot(px - tx, py - ty) < 1.0) {
            p.getPitch().state.put(StateKeys.in_support_midfielder, true);
            return true;
        }
        double dir = Math.toDegrees(Math.atan2(ty - py, tx - px));
        try {
            p.doDash("100", String.format("%.1f", dir));
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
