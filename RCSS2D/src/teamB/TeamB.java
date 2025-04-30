// ────────── File: src/teamB/TeamB.java ──────────
package teamB;

import common.StateKeys;
import common.players.Team;
import teamB.players.ProactivePlayer;

public class TeamB extends Team {

    public TeamB(String name) { super(name); }

    @Override
    public void createTeam(String side,
                           int numAttack,
                           int numMid,
                           int numWing,
                           int numDef,
                           boolean haveGk) {

        int jersey = 0;

        /* ---------------- Goal-keeper ---------------- */
        if (haveGk) {
            int[] h = home(jersey, side);
            players[jersey] = new ProactivePlayer(jersey, name, false, h[0], h[1], side, this);
            threads[jersey] = new Thread(players[jersey]);
            jersey++;
        }

        /* --------------- Field players --------------- */
        int total = numAttack + numMid + numWing + numDef;
        for (int i = 0; i < total; i++, jersey++) {
            int[] h = home(jersey, side);
            players[jersey] = new ProactivePlayer(jersey, name, false, h[0], h[1], side, this);
            threads[jersey] = new Thread(players[jersey]);
        }

        /* ------------- (optional) kick-off flag -------- */
        if (players[0] != null) {
            players[0].getPitch().state.put(StateKeys.designated_kick_off, true);
        }
    }

    /* very small 4-4-2 style home formation */
    private static int[] home(int j, String side) {
        int x, y;
        switch (j) {
            case 0 -> { x = -50; y =   0; } // GK
            case 1 -> { x = -40; y =  20; }
            case 2 -> { x = -40; y = -20; }
            case 3 -> { x = -30; y =  10; }
            case 4 -> { x = -30; y = -10; }
            case 5 -> { x = -15; y =  30; } // winger L
            case 6 -> { x = -15; y = -30; } // winger R
            case 7 -> { x = -10; y =  15; } // CM
            case 8 -> { x = -10; y = -15; } // CM
            case 9 -> { x =  -5; y =   8; } // striker L
            case 10-> { x =  -5; y =  -8; } // striker R
            default -> { x = 0; y = 0; }
        }
        if ("r".equals(side)) y = -y;
        return new int[]{x, y};
    }
}
