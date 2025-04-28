package common.actions;

import common.PlayerMath;
import common.StateKeys;
import common.Tuple;
import common.players.Player;

import java.awt.geom.Point2D;
import java.io.IOException;

/**
 * Kicks the ball towards opponent's goal
 * Used by: Attacker
 */
public class BringBallToGoalAction extends GOAPAction{

    public BringBallToGoalAction() {
        super(false, 1);
        addPrecondition(StateKeys.has_ball, true);

        addEffect(StateKeys.has_ball, false);
        addEffect(StateKeys.in_oppo_fourth, true);
        addEffect(StateKeys.kick_performed, true);
        addEffect(StateKeys.in_new_position, false);
    }

    @Override
    public void resetActionSpecifics() {

    }

    @Override
    public boolean checkProceduralPrecondition(Player agent) {
        return !agent.getPitch().getState().get(StateKeys.team_has_ball) &&
        		!agent.getPitch().getState().get(StateKeys.has_ball);
    }

    @Override
    public boolean executeAction(Player agent) {
        // Parameters: 0 is time, 1 is dist, 2 is dir
        Tuple goal_seen = agent.getPitch().getGoal();


        try {
        	agent.doSay("have_b");
            if (goal_seen == null) {
                // approximate current direction to goal
                Point2D.Double goal_known = PlayerMath.getOppoGoal(agent);
                agent.doKick("20", String.valueOf(PlayerMath.findAngleWithPoint(agent.getPlayerPos(), goal_known.x, goal_known.y)));
            }
            else {
                agent.doKick("20", String.valueOf(goal_seen.iParams[2]));
            }
            agent.reader.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}

