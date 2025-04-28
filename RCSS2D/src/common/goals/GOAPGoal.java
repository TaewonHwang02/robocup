package common.goals;

import common.StateKeys;
import common.players.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Abstract class representing a Goal in the GOAP system. A GOAPPlanner tries to fulfill the conditions of a goal
 * using one or more GOAPActions.
 */
public abstract class GOAPGoal implements Comparable<GOAPGoal>{

    private double priority;
    private final HashMap<StateKeys, Boolean> neededState = new HashMap<>(); // state of the world needed to reach this goal


    /**
     * Get the validity of this goal. A goal isn't valid if all of its conditions are already satisfied. More conditions for
     * validity can be defined using the validitySpecifics() function.
     * @return whether the goal is valid or not
     */
    public final boolean isValid(Player agent) {
        return !isSatisfied(agent) && validitySpecifics(agent);
    }

    /**
     * @return true if the goal is satisfied, false otherwise
     */
    private boolean isSatisfied(Player agent){
        for (Map.Entry<StateKeys, Boolean> key : neededState.entrySet()){
            if (!agent.getPitch().state.containsKey(key.getKey()) || agent.getPitch().state.get(key.getKey()) != key.getValue()){
                return false;
            }
        }
        return true;
    }

    /**
     * Getter method for neededState
     */
    public HashMap<StateKeys, Boolean> getNeededState(){
        return neededState;
    }

    /**
     * Add a key-value pair to neededState
     * @param key key
     * @param value value
     */
    public void addCondition(StateKeys key, Boolean value) { 
    	neededState.put(key, value); 
    }

    public void setPriority(Player agent){
        priority = calculatePriority(agent);
    }
    /**
     * Calculates the priority attribute of this goal.
     */
    public abstract double calculatePriority(Player agent);

    /**
     * Add specifics on how to determine the validity of the goal (whether it should be currently considered as a goal or not).
     * This function should not be used directly. Please use isValid() instead.
     * @return true is goal is valid, false otherwise
     */
    public abstract boolean validitySpecifics(Player agent);

    @Override
    public int compareTo(GOAPGoal o) {
        return Double.compare(this.priority, o.priority);
    }
}
