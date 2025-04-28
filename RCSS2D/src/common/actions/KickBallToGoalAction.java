package common.actions;

import common.PlayerMath;
import common.StateKeys;
import common.Tuple;
import common.players.Player;

import java.awt.geom.Point2D;
import java.io.IOException;

/**
 * Make the player do a strong kick towards the goal.
 * Used by: Attacker, Winger.
 */
public class KickBallToGoalAction extends GOAPAction{

    public KickBallToGoalAction() {
        super(false, 1);
        addPrecondition(StateKeys.has_ball, true);
        //addPrecondition(StateKeys.oppo_goal_in_FOV, false);

        addEffect(StateKeys.has_ball, false);
        addEffect(StateKeys.kick_performed, true);
        addEffect(StateKeys.in_new_position, false);
    }

    @Override
    public void resetActionSpecifics() {

    }

    @Override
    public boolean checkProceduralPrecondition(Player agent) {
        // A max power kick makes the ball travel about 20 meters in one cycle.
        // Goals are situated 52 meters away from (0,0) on the x-axis.

        // Parameters of ball: 0 is time, 1 is x position, 2 is y position
        Tuple[] balls = agent.getBallsCartesian();

        if (balls[1] == null){
            return false;
        }

        return ((agent.getSide().equals("r") && balls[1].iParams[1] <= -30) ||
                (agent.getSide().equals("l") && balls[1].iParams[1] >= 30)) ||
                (agent.getPitch().state.get(StateKeys.designated_kick_off) && agent.getSide().equals("l") && agent.getPitch().state.get(StateKeys.kick_off_l)) ||
                (agent.getPitch().state.get(StateKeys.designated_kick_off) && agent.getSide().equals("r") && agent.getPitch().state.get(StateKeys.kick_off_r));
    }

    @Override
    public boolean executeAction(Player agent) {
        // Parameters: 0 is time, 1 is dist, 2 is dir
        Tuple goal_seen = agent.getPitch().getGoal();

        try {
            if (goal_seen == null) {
                // approximate current direction to goal
                Point2D.Double goal_known = PlayerMath.getOppoGoal(agent);
                agent.doKick("40", String.valueOf(PlayerMath.findAngleWithPoint(agent.getPlayerPos(), goal_known.x, goal_known.y)));
            }
            else {
                agent.doKick("40", String.valueOf(goal_seen.iParams[2]));
            }
            agent.reader.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}
