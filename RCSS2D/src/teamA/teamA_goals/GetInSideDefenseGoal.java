// File: src/teamA/goals/GetInFlankDefenseGoal.java
package teamA.teamA_goals;

import common.StateKeys;
import common.players.Player;
import common.goals.GOAPGoal;

/**
 * Goal: tuck inside to cover the flank when the opponents have the ball.
 */public class GetInSideDefenseGoal extends GOAPGoal {

    public GetInSideDefenseGoal() {
        // “end state” condition: we want inFlankDefense == true
        addCondition(StateKeys.in_side_defense, true);
    }

    /** Only valid when the other team controls the ball. */
    @Override
    public boolean validitySpecifics(Player agent) {
        Boolean theyHave = agent.getPitch().state.get(StateKeys.team_has_ball);
        return theyHave != null && theyHave;
    }

    /** Higher urgency when valid; otherwise this goal is effectively disabled. */
    @Override
    public double calculatePriority(Player agent) {
        return validitySpecifics(agent) ? 2.0 : Double.MAX_VALUE;
    }
}