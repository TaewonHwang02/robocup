package teamB.actions;

import common.PlayerMath;
import common.Tuple;
import common.players.Player;

public class MoveToBallAction {

    /**
     * @return true when we have arrived at the ball, false otherwise
     */
    public boolean perform(Player agent) {

        // latest ball info (cartesian) = index 1
        Tuple ball = agent.getPitch().getBallsCartesian()[1];
        if (ball == null) {                     // not in FOV → scan
            try { agent.doTurn("30"); } catch (Exception ignored) {}
            return false;
        }

        // agent position & orientation
        Tuple self = agent.getPitch().getPlayerPos();
        double dir = PlayerMath.findAngleWithPoint(self,
                                                   ball.iParams[1],   // ball.x
                                                   ball.iParams[2]);  // ball.y
        double dist = PlayerMath.findDistanceWithPoint(self,
                                                       ball.iParams[1],
                                                       ball.iParams[2]);

        try {
            agent.doTurn(String.valueOf(dir));          // face the ball
            if (dist > 1.0) {                           // still far → dash
                agent.doDash("100");
                return false;                           // keep moving
            }
        } catch (Exception ignored) {}

        return true;    // within 1 m ⇒ “arrived”
    }
}
