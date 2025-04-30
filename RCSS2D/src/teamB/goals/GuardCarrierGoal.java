// ── File: src/teamB/goals/GuardCarrierGoal.java
package teamB.goals;

import common.goals.GOAPGoal;          // ← extend the right base class
import common.StateKeys;
import common.players.Player;

public class GuardCarrierGoal extends GOAPGoal {

    public GuardCarrierGoal() {
        // define the desired end-state for the planner
        addCondition(StateKeys.in_support_position, true);
    }

    /* --- GOAPGoal hooks --------------------------------------- */

    @Override                       // when should this goal be considered?
    public boolean validitySpecifics(Player p) {
        // valid while *our* team has the ball but THIS player is not the carrier
        Boolean weHave = p.getPitch().state.get(StateKeys.team_has_ball);
        Boolean iCarry = p.getPitch().state.get(StateKeys.carrying_ball_fwd);
        return Boolean.TRUE.equals(weHave) && !Boolean.TRUE.equals(iCarry);
    }

    @Override                       // lower number == higher priority
    public double calculatePriority(Player p) {
        return validitySpecifics(p) ? 2.0 : Double.MAX_VALUE;
    }
}
