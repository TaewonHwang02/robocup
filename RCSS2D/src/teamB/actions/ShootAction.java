package teamB.actions;

import common.players.Player;

public class ShootAction {
    public boolean perform(Player p) throws Exception {
        var gPos = common.PlayerMath.getOppoGoal(p);
        double dir = common.PlayerMath.getDirection(p, gPos.x, gPos.y);
        p.doKick("100", Double.toString(dir));
        return true;
    }
}
