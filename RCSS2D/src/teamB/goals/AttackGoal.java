// ────────── File: src/teamB/goals/AttackGoal.java ──────────
package teamB.goals;

import common.goals.GOAPGoal;
import common.StateKeys;
import common.players.Player;

/**
 * High-level goal: capture the ball and advance toward the opponent goal.
 * The goal is considered satisfied when THIS player controls the ball
 * *and* is facing roughly toward the opponent goal.
 */
public class AttackGoal extends GOAPGoal {

    public AttackGoal() {
        /* ---- desired end-state for the planner ---------------- */
        addCondition(StateKeys.has_ball,              true);
        addCondition(StateKeys.ball_centered_in_FOV,  true);
    }

    /* =============== GOAPGoal interface ======================= */

    /** Valid when our team does *not* have possession. */
    @Override
    public boolean validitySpecifics(Player p) {
        Boolean weHave = p.getPitch().state.get(StateKeys.team_has_ball);
        return !Boolean.TRUE.equals(weHave);          // start attacking only if we’re chasing
    }

    /**
     * Priority: very high (1.0) when valid; “disabled” otherwise.
     * Lower numeric value ⇒ higher priority in GOAP selection.
     */
    @Override
    public double calculatePriority(Player p) {
        return validitySpecifics(p) ? 1.0 : Double.MAX_VALUE;
    }
}
