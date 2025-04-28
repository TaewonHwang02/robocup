package common.actions;

import common.players.Player;

/**
 * Do nothing. This is an action used to represent the fact that the goal has been reached.
 * Used by: All.
 */
public class NullAction extends GOAPAction{

    public NullAction() {
        super(false, 0);
    }

    @Override
    public void resetActionSpecifics() {}

    @Override
    public boolean checkProceduralPrecondition(Player agent) {
        return true;
    }

    @Override
    public boolean executeAction(Player agent) {
        return true;
    }
}
