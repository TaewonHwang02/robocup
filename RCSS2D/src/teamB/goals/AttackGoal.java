package teamB.goals;

import java.util.List;
import java.util.ListIterator;

import common.players.Player;

/** Simple “go score” goal: go to ball → dribble → shoot. */
public class AttackGoal extends MetaGoal {

    public AttackGoal() { this.priority = 1.0; }

    @Override public int score() {             // smaller score ⇒ closer to done
        return 0;                              // stub for now
    }

    @Override public List<String> buildPlan() {
        return java.util.List.of(
            "goto_ball",
            "dribble_forward",
            "shoot_goal"
        );
    }

    @Override
    public boolean isSatisfied(Player agent) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isSatisfied'");
    }
}
