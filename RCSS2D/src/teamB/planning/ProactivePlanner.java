package teamB.planning;

import java.util.*;

/** Hard-coded micro-planner that maps a high-level label ➜ list of steps. */
public class ProactivePlanner {

    private static final Map<String,List<String>> TABLE = Map.of(
        // “attack”  →  go get the ball, dribble a bit, shoot when close
        "attack",          List.of("move_to_ball", "dribble_forward", "shoot_goal"),

        // “guard_carrier”  →  run near the player who has the ball and shadow
        "guard_carrier",   List.of("move_near_carrier", "shadow_opponent")
    );

    /** Return a *copy* so the caller can freely mutate the list.            */
    public List<String> makePlanFor(String goalLabel) {
        return new ArrayList<>( TABLE.getOrDefault(goalLabel, List.of()) );
    }
}
