package teamB.planning;

import teamB.goals.MetaGoal;
import java.util.List;

/** The “planner” is trivial: just return the goal’s own plan. */
public class ProactivePlanner {

    public List<String> makePlan(MetaGoal g) {
        return g.buildPlan();
    }
}
