package common.actions;

import common.PlayerMath;
import common.StateKeys;
import common.Tuple;
import common.players.Player;

import java.awt.geom.Point2D;
import java.io.IOException;

/**
 * Similar to BringBallGoal. Kicks the ball towards point above or below opponent's goal in order to avoid clustering. 
 * Used by: Midfielder
 */
public class CarryBallForwardAction extends GOAPAction{

    public CarryBallForwardAction() {
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
        // Parameters of ball: 0 is time, 1 is x position, 2 is y position
        Tuple[] balls = agent.getBallsCartesian();
        
        // If ball out of sight OR opponent blocking,
        if (balls[1] == null || agent.getPitch().state.get(StateKeys.oppo_blocking)){
            return false;
        }

        return ((agent.getSide().equals("r") && balls[1].iParams[1] > -30) ||
                (agent.getSide().equals("l") && balls[1].iParams[1] < 30));
    }

    @Override
    public boolean executeAction(Player agent) {
        // Parameters: 0 is time, 1 is dist, 2 is dir
        Tuple goal_seen = agent.getPitch().getGoal();
        
        double dist_goal = Integer.MAX_VALUE;
        if (goal_seen != null) {
        	dist_goal = goal_seen.iParams[1];
        }

        try {
        	agent.doSay("have_b");
        	agent.doSay("carry_b");
        	
            if (dist_goal < 15) {
            	agent.doKick("20", String.valueOf(goal_seen.iParams[2]));
            } 
            
            else {
                Tuple playerPos = agent.getPlayerPos();
                int[] init_position = PlayerMath.getStartPosition(agent.getNum(), agent.getSide());
            	
            	double delta = 0;
            	if (init_position[1] > 0) {
            		delta = 20;
            	} else {
            		delta = -20;
            	}
                
               	// approximate current direction to goal
                Point2D.Double goal_known = PlayerMath.getOppoGoal(agent);
                agent.doKick("20", String.valueOf(PlayerMath.findAngleWithPoint(agent.getPlayerPos(), goal_known.x, goal_known.y + delta)));
            }
            agent.reader.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}