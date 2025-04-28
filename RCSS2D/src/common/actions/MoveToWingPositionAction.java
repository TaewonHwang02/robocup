package common.actions;

import common.PlayerMath;
import common.StateKeys;
import common.Tuple;
import common.goals.GOAPGoal;
import common.players.Player;

import java.awt.geom.Point2D;
import java.io.IOException;

/**
 * Make the player go towards its position.
 * Used by: Winger.
 */

public class MoveToWingPositionAction extends GOAPAction {

    public MoveToWingPositionAction() {
        super(false, 1);
        addPrecondition(StateKeys.in_win_position, false);
        // addPrecondition(StateKeys.oppo_goal_in_FOV, true);

        addEffect(StateKeys.in_win_position, true);
        addEffect(StateKeys.in_new_position, false);
    }

    @Override
    public void resetActionSpecifics() {
    }

    @Override
    public boolean checkProceduralPrecondition(Player agent) {
        return !agent.getPitch().getState().get(StateKeys.carrying_ball_fwd) &&
                !agent.getPitch().state.get(StateKeys.in_win_position);
    }

    @Override
    public boolean executeAction(Player agent) {
        for (int i = 0; i < 20; i++){
            if (agent.goalStillValid()) {
                try {
                    // if the player is initially up on the field, move up across midpoint, down otherwise
                    Tuple player_pos = agent.getPlayerPos();
                    int init_y = PlayerMath.getStartPosition(agent.getNum(), agent.getSide())[1];
                    double xLimit = agent.getSide().equals("l") ? 52 : -52;
                    double yLimitStart = init_y > 0 ? 10 : -10;
                    double yLimitEnd = init_y > 0 ? 34 : -34;

                    double target_x = agent.getSide().equals("l") ?
                            Math.max(0, Math.min(xLimit, player_pos.getIParams()[0])) :
                            Math.min(0, Math.max(xLimit, player_pos.getIParams()[0]));
                    double target_y = (init_y > 0) ?
                            Math.max(yLimitStart, Math.min(yLimitEnd, player_pos.getIParams()[1])) :
                            Math.min(yLimitStart, Math.max(yLimitEnd, player_pos.getIParams()[1]));

                    agent.doDash("50", String.valueOf(PlayerMath.findAngleWithPoint(player_pos, target_x, target_y)));
                    agent.reader.read();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                agent.reader.read();
            }
            else {
                return false;
            }
        }
        return true;
    }
}
