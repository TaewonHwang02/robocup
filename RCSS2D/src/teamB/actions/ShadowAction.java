package teamB.actions;

import common.players.Player;

public class ShadowAction {
    public boolean perform(Player p) throws Exception {
        p.doDash("40");      // stay near opponent (dummy)
        return true;
    }
}
