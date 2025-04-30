// ───────────────── AlignWithBallAction.java ─────────────────
package teamB.actions;

import common.PlayerMath;
import common.StateKeys;
import common.Tuple;
import common.players.Player;
import common.actions.GOAPAction;

public class AlignWithBallAction extends GOAPAction {

    public AlignWithBallAction() {
        super(false, 1);  // cost=1, no-range needed
        addPrecondition(StateKeys.ball_in_FOV, true);
        addEffect(StateKeys.ball_centered_in_FOV, true);
    }

    @Override public void resetActionSpecifics() {}   // nothing special

    @Override public boolean checkProceduralPrecondition(Player agent) {
        return agent.getPitch().state.get(StateKeys.ball_in_FOV);
    }

    @Override public boolean executeAction(Player agent) {
        Tuple ball = agent.getPitch().getBallsCartesian()[1];   // ← ONLY CHANGE
        if (ball == null) return false;                         // can’t see ball
        double bx = ball.getIParams()[1];
        double by = ball.getIParams()[2];
        double dir = PlayerMath.getDirection(agent, bx, by);
        try { agent.doTurn(String.valueOf(dir)); } catch (Exception ignored) {}
        return true;   // finished in one cycle
    }
}
