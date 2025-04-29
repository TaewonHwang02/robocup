// File: src/teamA/TeamMain.java
package teamA;

import common.players.Attacker;
import common.players.GoalKeeper;
import common.players.Midfielder;
import common.players.Team;
import common.players.Winger;
import teamA.teamA_players.CenterBack;
import teamA.teamA_players.SideBack;

public class TeamMain extends Team {
    public TeamMain(String name) {
        super(name);
    }

    /**
     * same signature as template, but uses customHome(...) for every player
     */
    @Override
    public void createTeam(
        String side,
        int num_attackers,
        int num_midfielders,
        int num_wingers,
        int num_defenders,
        boolean have_gk
    ) {
        int jersey = 0;

        // ── Goalkeeper (0) ──
        if (have_gk) {
            int[] home = getHomePosition(jersey, side);
            players[jersey] = new GoalKeeper(jersey, name, false, home[0], home[1], side, this);
            threads[jersey] = new Thread(players[jersey]);
            jersey++;
        }

        // ── Defenders (1–4) ── first two = SideBack, next two = CenterBack
        for (int i = 1; i <= num_defenders; i++, jersey++) {
            int[] home = getHomePosition(jersey, side);
            if (i <= 2) {
                players[jersey] = new SideBack(jersey, name, false, home[0], home[1], side, this);
            } else {
                players[jersey] = new CenterBack(jersey, name, false, home[0], home[1], side, this);
            }
            threads[jersey] = new Thread(players[jersey]);
        }

        // ── Midfielders (next slots) ──
        for (int i = 0; i < num_midfielders; i++, jersey++) {
            int[] home = getHomePosition(jersey, side);
            players[jersey] = new Midfielder(jersey, name, false, home[0], home[1], side, this);
            threads[jersey] = new Thread(players[jersey]);
        }

        // ── Wingers ──
        for (int i = 0; i < num_wingers; i++, jersey++) {
            int[] home = getHomePosition(jersey, side);
            players[jersey] = new Winger(jersey, name, false, home[0], home[1], side, this);
            threads[jersey] = new Thread(players[jersey]);
        }

        // ── Attackers ──
        for (int i = 0; i < num_attackers; i++, jersey++) {
            int[] home = getHomePosition(jersey, side);
            players[jersey] = new Attacker(jersey, name, false, home[0], home[1], side, this);
            threads[jersey] = new Thread(players[jersey]);
        }
    }

    /**
     * Team‐A formation:
     * returns [x,y] for each jersey number.
     */
    private int[] getHomePosition(int jersey, String side) {
        int x, y;
        switch (jersey) {
            case 0:  x = -50; y =   0; break;  // GK
            case 1:  x = -35; y =  25; break;  // SideBack #1
            case 2:  x = -35; y = -25; break;  // SideBack #2
            case 3:  x = -40; y =  10; break;  // CenterBack #1
            case 4:  x = -40; y = -10; break;  // CenterBack #2
            case 5:  x = -25; y =   0; break;  // Mid #1
            case 6:  x = -30; y =  15; break;  // Mid #2
            case 7:  x = -30; y = -15; break;  // Mid #3
            case 8:  x = -15; y =  30; break;  // Winger #1
            case 9:  x = -15; y = -30; break;  // Winger #2
            case 10: x =  -5; y =   0; break;  // Striker
            default: x =    0; y =   0; break;
        }
        // flip Y on the right side:
        if ("r".equals(side)) {
            y = -y;
        }
        return new int[]{x, y};
    }

    /** test harness */
    public static void main(String[] args) {
        String side = (args.length>0 && args[0].equals("r")) ? "r" : "l";
        TeamMain teamA = new TeamMain("TeamA");
        teamA.createTeam(side, 1, 3, 2, 4, true);
        teamA.startTeam();
        teamA.joinTeam();
    }
}
