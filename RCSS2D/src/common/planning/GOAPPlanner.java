package common.planning;

import common.actions.GOAPAction;
import common.actions.NullAction;
import common.StateKeys;
import common.goals.GOAPGoal;
import common.graphs.GOAPNode;
import common.players.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * Planner used in the GOAP system. The planner is given a GOAPGoal as well as a set of GOAPActions it can use, then
 * runs A* through them in order to get the sequence of actions to take that will satisfy the goal.
 */
public class GOAPPlanner {

    /**
     * Create the plan that will fulfill the goal.
     * @param goal the goal that must be met. The goal is met if at the end state of the plan fulfills all the conditions of the goal.
     * @param start_state the current state of the world, based on observations of the agent's environment.
     * @param agent the agent for which the plan is being created
     * @return the list of actions (first action to take is first in the list) that must be executed to meet the goal.
     */
    public LinkedList<GOAPAction> getPlan(GOAPGoal goal, HashMap<StateKeys, Boolean> start_state, Player agent){
        if (goal == null){
            return new LinkedList<>();
        }
        //System.out.println(start_state);
        HashMap<StateKeys, Boolean> end_state = goal.getNeededState();

        if (end_state.isEmpty()){
            System.err.println("ERROR: goal " + goal + " does not have a set end state");
            return new LinkedList<>();
        }

        // finding a plan
        HashSet<GOAPAction> possibleActions = new HashSet<>(); // subset of availableActions: what actions are feasible right now
        for (GOAPAction action : agent.getAvailableActions()){
            if (action.checkProceduralPrecondition(agent)){
                possibleActions.add(action);
            }
        }

        GOAPNode root = new GOAPNode(new NullAction(), null, 0, start_state, possibleActions, end_state);

        GOAPNode bestLeaf = GOAPNode.AStar(root, end_state);

        return GOAPNode.getPath(bestLeaf);
    }
}
