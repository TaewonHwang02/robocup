// ────────── File: src/teamB/planning/ProactivePlanner.java ──────────
package teamB.planning;

import java.util.*;

public class ProactivePlanner {

    /** Return a naïve fixed sequence of step-labels for a goal label. */
    public List<String> makePlanFor(String goal){
        return switch(goal){
            case "attack"         -> List.of("move_to_ball",
                                              "dribble_forward",
                                              "shoot_goal");
            case "guard_carrier"  -> List.of("move_near_carrier",
                                              "shadow_opponent");
            default               -> List.of();
        };
    }
}
