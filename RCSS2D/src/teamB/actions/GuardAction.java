package teamB.actions;

import common.players.Player;

public class GuardAction {
    public boolean perform(Player p) throws Exception {
        p.doDash("60");
        return true;
    }
}
