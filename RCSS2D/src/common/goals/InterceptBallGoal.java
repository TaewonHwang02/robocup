package common.goals;

import common.StateKeys;
import common.Tuple;
import common.players.Player;
import common.PlayerMath;

/**
 * Run towards ball to gain control. To avoid clustering, only run if ball in same half on the field.
 * Used by: Midfielders, Winger.
 */
public class InterceptBallGoal extends GOAPGoal{

    public InterceptBallGoal() {
        addCondition(StateKeys.has_ball, true);
    }

    @Override
    public double calculatePriority(Player agent) {
/*  previous code:
        // parameters: 0 is time, 1 is distance, 2 is direction
        Tuple[] balls_polar = agent.getBallsPolar();
        double ball_dist = balls_polar[1].iParams[1];

        if (balls_polar[1] == null){
            return Double.MAX_VALUE;
        }

        else {
            // parameters: 0 is x, 1 is y, 2 is direction
            Tuple[] balls_cartesian = agent.getBallsCartesian();
            double ball_y = balls_cartesian[0].iParams[1];
            int sign_ball_y = (int) Math.signum(ball_y);

            // parameters: 0 is x, 1 is y
            int[] init_position = PlayerMath.getStartPosition(agent.getNum(), agent.getSide());
            double init_pos_y = init_position[1];
            int sign_init_pos_y = (int) Math.signum(init_pos_y);

	        if (sign_ball_y == sign_init_pos_y) {
	            return 2;
	        } else if (agent.getNum() == 1) {
	        	return 999;
	        } else {
	        	return Double.MAX_VALUE;
	        }
 */
        if (agent.getNum() != 1) {return 2;}
        else if (agent.getNum() == 1) {return 999;}
        else {return Double.MAX_VALUE;}
    }

    @Override
    public boolean validitySpecifics(Player agent) {
        /**
         *  Will only intercept if:
         *  1. Can Dash
         *  2. Team does not have ball
         *  3. Ball is in FOV
         */
        return agent.playerCanDash() &&
                !agent.getPitch().getState().get(StateKeys.team_has_ball) &&
                agent.getPitch().getState().get(StateKeys.ball_in_FOV);
    }
}
