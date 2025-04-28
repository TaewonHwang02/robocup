package common.actions;

import common.StateKeys;
import common.Tuple;
import common.players.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Abstract class representing an Action in the GOAP system. The GOAPPlanner uses actions in order
 * to figure out a way to achieve a goal. The sequence of actions returned by the planner is then
 * passed to a GOAPAgent for execution.
 */
public abstract class GOAPAction {

    private final HashMap<StateKeys, Boolean> preconditions = new HashMap<StateKeys, Boolean>(); // what must be true for the action to be selected
    private final HashMap<StateKeys, Boolean> effects = new HashMap<StateKeys, Boolean>(); // what will change if the action was selected

    private boolean inRange; // is the agent in range to perform the action
    private final boolean needRange; // does the agent need to be in range to perform the action

    private Tuple target; // what will be influenced by the action

    private double cost;

    public GOAPAction(boolean needRange, double cost){
        this.needRange = needRange;
        inRange = !needRange;

        this.cost = cost;
    }

    /// ~~~ ACTION RESETS ~~~ ///

    /** General reset of an Action **/
    public void resetAction(){
        inRange = !needRange;
        resetActionSpecifics();
    }

    /** Reset specific to an Action. Can be empty **/
    public abstract void resetActionSpecifics();


    /// ~~~ ~~~ ///

    /**
     * Determine whether this condition can run based on the current world state. Often consists of finding a target for the action
     * @return true if a target was found (or if one isn't needed), false otherwise
     * @param agent the agent executing the action
     */
    public abstract boolean checkProceduralPrecondition(Player agent);

    /**
     * Makes the player start executing the action.
     * @return true if the Action successfully finished execution, false otherwise
     * @param agent the agent executing the action
     */
    public abstract boolean executeAction(Player agent);

    /// ~~~ PRECONDITION AND EFFECT GETTERS, MODIFIERS (adders, removers) ~~~ ///

    /** Get the preconditions of this Action (what must be true in the world for this action to be used) **/
    public HashMap<StateKeys, Boolean> getPreconditions() {
        return preconditions;
    }

    /** Add a precondition for this action **/
    public void addPrecondition(StateKeys condition, boolean value){
        preconditions.put(condition, value);
    }

    /** Remove a precondition for this action **/
    public void removePrecondition(String condition){
        preconditions.remove(condition);
    }

    /** Get the effects of this Action (what this action changes in the world) **/
    public HashMap<StateKeys, Boolean> getEffects() {
        return effects;
    }

    /** Add an effect for this action **/
    public void addEffect(StateKeys condition, boolean value){
        effects.put(condition, value);
    }

    /** Remove an effect for this action **/
    public void removeEffect(String condition){
        effects.remove(condition);
    }

    public double getCost() {
        return cost;
    }

    /// ~~~ FUNCTIONS THAT CHECK THE PRECONDITIONS / EFFECTS WITH ANOTHER HASHMAP ~~~ ///

    /**
     * Apply the effects of this action to the state passed as parameter.
     * @return the modified state
     */
    public HashMap<StateKeys, Boolean> applyEffects(HashMap<StateKeys, Boolean> currentState){
        HashMap<StateKeys, Boolean> nextState = new HashMap<StateKeys, Boolean>(currentState);

        for (Map.Entry<StateKeys, Boolean> entry : effects.entrySet()){
            nextState.put(entry.getKey(), entry.getValue());
        }
        return nextState;
    }

    /**
     * @return true if the state passed as parameter satisfies this action's preconditions, false otherwise
     */
    public boolean checkPreconditions(HashMap<StateKeys, Boolean> currentState){

        // the current state needs to satisfy all preconditions: boolean values must match
        for (Map.Entry<StateKeys, Boolean> entry : preconditions.entrySet()){
            if (!currentState.containsKey(entry.getKey()) || currentState.get(entry.getKey()) != entry.getValue()){
                return false;
            }
        }
        return true;
    }

    /**
     * @return true if the state passed as parameter satisfies this action's effects, false otherwise
     */
    public boolean checkEffects(HashMap<StateKeys, Boolean> currentState){
        for (Map.Entry<StateKeys, Boolean> entry : effects.entrySet()){
            if (!currentState.containsKey(entry.getKey()) || currentState.get(entry.getKey()) != entry.getValue()){
                return false;
            }
        }
        return true;
    }

}
