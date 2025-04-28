package common.players;

import common.actions.GOAPAction;
import common.goals.GOAPGoal;

import java.util.*;

/**
 * Agent used in the GOAP system. The agent chooses some goal it must reach, and uses the plan made for it by a planner to do so.
 */
public interface GOAPAgent {

    /**
     * Checks whether the goal the agent is currently working on is still valid.
     * @return true if the goal is valid, false otherwise.
     */
    public boolean goalStillValid();

    /**
     * @return new goal to follow and plan for.
     */
    public GOAPGoal getNewGoal();

    /**
     * Executes the latest plan.
     */
    public void planAndExecute();

}
