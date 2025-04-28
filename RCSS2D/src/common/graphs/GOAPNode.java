package common.graphs;

import common.actions.GOAPAction;
import common.StateKeys;

import java.util.*;

/**
 * Serves as a wrapper for GOAPActions to facilitate running A* over them.
 */
public class GOAPNode implements Comparable<GOAPNode>{

    private final HashMap<StateKeys, Boolean> currentState;
    private final HashSet<GOAPAction> availableActions;

    private final GOAPNode parent;
    //private ArrayList<GOAPNode> children;

    private final GOAPAction action;

    // some values needed for A*:
    //      - g : move function, cost of the path up until this point
    //      - h : heuristic function, approximation of distance from this node to the goal
    //      - f : cost function, g + h
    private final double f;
    private final double g;
    private final double h;



    public GOAPNode(GOAPAction action, GOAPNode parent, double g, HashMap<StateKeys, Boolean> currentState, HashSet<GOAPAction> availableActions, HashMap<StateKeys, Boolean> goalState){
        this.action = action;
        this.parent = parent;
        this.g = g;
        this.currentState = currentState;
        this.availableActions = availableActions;
        this.h = setValue(goalState);
        this.f = this.g + this.h;
    }

    /**
     * Calculate the heuristic value of this node.
     */
    public double setValue(HashMap<StateKeys, Boolean> goalState){

        double numEffects = 0;
        double numChanges = 0;
        for (Map.Entry<StateKeys, Boolean> entry : action.getEffects().entrySet()){
            numEffects ++;

            // what it means for an effect to have value:
            //      - it is needed to reach the goal state
            //      - the boolean value of the effect and of the goal is the same
            //      - the current state either does not have this key yet or this key is currently set to the wrong value
            if (goalState.containsKey(entry.getKey())
                    &&
                    (goalState.get(entry.getKey()) == entry.getValue())){
                numChanges ++;
            }
        }
        if (numChanges == 0){
            return Double.MAX_VALUE;
        }
        else {
            // numChanges/numEffects is the percentage of useful effects.
            // to have actions with higher value be first in a priority queue, we invert the division
            return numEffects/numChanges;
        }
    }

    /**
     * Build a sequence of nodes that takes us from the start node (and thus the start state) to the goal state
     * @param start The node containing our start state
     * @param goalState A HashMap representing our goal state. Defines which states must be true and which must be false.
     * @return the final node in the sequence
     */
    public static GOAPNode AStar(GOAPNode start, HashMap<StateKeys, Boolean> goalState){

        PriorityQueue<GOAPNode> open = new PriorityQueue<>();

        open.add(start);

        while (!open.isEmpty()){

            GOAPNode head = open.remove();
            if (satisfies(head.currentState, goalState)){
                // The goal has been found.
                // Its parent information should have been updated in a way that allows us
                //      to retrace our steps back to the start
                return head;
            }

            for (GOAPAction nextAction : head.availableActions){

                if (nextAction.checkPreconditions(head.currentState)){
                    // Action is usable given the state of the world. Try using it and applying its effects
                    HashMap<StateKeys, Boolean> nextState = nextAction.applyEffects(head.currentState);

                    // Exclude this action from the list of actions available in the next step (actions appear once in a plan)
                    HashSet<GOAPAction> nextActions = new HashSet<>(head.availableActions);
                    nextActions.remove(nextAction);

                    // Create node, and add it to the Queue
                    GOAPNode childNode = new GOAPNode(nextAction, head, head.g + 1, nextState, nextActions, goalState);
                    open.add(childNode);
                }
            }
        }

        // We have emptied the open list but we haven't found a way to satisfy the goal
        return null;
    }

    /**
     * @return true if the current HashMap contains all the entries of the goal HashMap (with the same values), false otherwise
     */
    public static boolean satisfies(HashMap<StateKeys, Boolean> current, HashMap<StateKeys, Boolean> goal){
        // current must have matching values to goal
        for (Map.Entry<StateKeys, Boolean> entry : goal.entrySet()){
            if (!current.containsKey(entry.getKey()) || current.get(entry.getKey()) != entry.getValue()){
                return false;
            }
        }
        return true;
    }

    /**
     * Reconstruct the path from start node to end node that was created during A* using the parent attribute.
     * @param end the node that contains the final action to take before satisfying the goal.
     * @return the sequence of actions to take to reach the end node.
     */
    public static LinkedList<GOAPAction> getPath(GOAPNode end){

        if (end == null){
            return null;
        }

        GOAPNode iter = end;
        LinkedList<GOAPAction> path = new LinkedList<>();

        while (iter.parent != null){
            path.addFirst(iter.action);
            iter = iter.parent;
        }
        path.addFirst(iter.action);
        return path;
    }

    @Override
    public int compareTo(GOAPNode o) {
        return Double.compare(this.f, o.f);
    }
}


