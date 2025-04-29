package teamB.goals;

import java.util.List;

import common.players.Player;

/** Defend the teammate who currently has possession. */
public class GuardCarrierGoal extends MetaGoal {

    public GuardCarrierGoal() { this.priority = 1.5; }

    @Override public int score() { return 0; }

    @Override public List<String> buildPlan() {
        return java.util.List.of(
            "move_near_carrier",
            "shadow_opponent",
            "tackle_if_close"
        );
    }

    @Override
    public boolean isSatisfied(Player agent) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isSatisfied'");
    }
}
