package common.actions;

import common.PlayerMath;
import common.StateKeys;
import common.players.Player;

import java.io.IOException;

/**
 * Teleports the player to its start position using the move command.
 * Used by: All.
 */
public class TeleportAction extends GOAPAction{

    public TeleportAction() {
        super(false, 5);

        addEffect(StateKeys.in_new_position, true);
    }

    @Override
    public void resetActionSpecifics() {

    }

    @Override
    public boolean checkProceduralPrecondition(Player agent) {
        return agent.getPitch().state.get(StateKeys.move_allowed);
    }

    @Override
    public boolean executeAction(Player agent) {
        agent.reader.read();
        int[] position = PlayerMath.getStartPosition(agent.getNum(), agent.getSide());
        try {
            agent.doMove(String.valueOf(position[0]), String.valueOf(position[1]));
            agent.reader.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        agent.getPitch().state.put(StateKeys.in_new_position, true);
        return true;
    }
}
