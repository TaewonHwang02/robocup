// ───────────────── BringBallToGoalAction.java ─────────────────
package teamB.actions;

import common.StateKeys;
import common.Tuple;
import common.players.Player;
import common.actions.GOAPAction;
import common.PlayerMath;
import java.awt.geom.Point2D;


public class BringBallToGoalAction extends GOAPAction {

    public BringBallToGoalAction() {
        super(true, 2);                 // must be close to ball
        addPrecondition(StateKeys.has_ball, true);
        addEffect(StateKeys.carrying_ball_fwd, true);
    }

    @Override public void resetActionSpecifics() {}

    @Override public boolean checkProceduralPrecondition(Player agent) {
        return agent.getPitch().state.get(StateKeys.has_ball);
    }

    @Override public boolean executeAction(Player agent) {
        Point2D.Double goal = PlayerMath.getOppoGoal(agent);            // absolute (x,y)
        Tuple ball = agent.getPitch().getBallsCartesian()[1];  // ← ONLY CHANGE
        if (ball == null) return false;

        double dir = PlayerMath.findAngleWithPoint(ball, goal.x, goal.y);
        try {
            agent.doKick("40", String.valueOf(dir));           // soft dribble kick
        } catch (Exception ignored) {}
        return true;
    }
}
