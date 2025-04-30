package teamB.actions;

import common.players.Player;

public class DribbleAction {
    public boolean perform(Player p) throws Exception {
        p.doKick("10","0");   // small kick straight ahead
        p.doDash("70");
        return true;
    }
}
