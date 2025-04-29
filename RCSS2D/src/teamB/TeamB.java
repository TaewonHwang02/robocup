package teamB;

import common.players.Team;
import teamB.players.ProactivePlayer;

public class TeamB extends Team {

    public TeamB(String name) {
        super(name);
    }

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

        // — No fancy roles yet, just ProactivePlayer for everyone —
        if (have_gk) {
            int[] home = getHomePosition(jersey, side);
            players[jersey] = new ProactivePlayer(jersey, name, false, home[0], home[1], side, this);
            threads[jersey] = new Thread(players[jersey]);
            jersey++;
        }

        int totalFieldPlayers = num_attackers + num_midfielders + num_wingers + num_defenders;

        for (int i = 0; i < totalFieldPlayers; i++, jersey++) {
            int[] home = getHomePosition(jersey, side);
            players[jersey] = new ProactivePlayer(jersey, name, false, home[0], home[1], side, this);
            threads[jersey] = new Thread(players[jersey]);
        }
    }

    private int[] getHomePosition(int jersey, String side) {
        int x, y;
        switch (jersey) {
            case 0:  x = -50; y =   0; break;  // GK
            case 1:  x = -40; y =  20; break;  // Defender
            case 2:  x = -40; y = -20; break;
            case 3:  x = -30; y =  10; break;
            case 4:  x = -30; y = -10; break;
            case 5:  x = -20; y =  30; break;  // Winger
            case 6:  x = -20; y = -30; break;
            case 7:  x = -10; y =   0; break;  // Midfielders
            case 8:  x = -10; y =  20; break;
            case 9:  x = -10; y = -20; break;
            case 10: x =   0; y =   0; break;  // Striker
            default: x = 0; y = 0; break;
        }
        if ("r".equals(side)) {
            y = -y;  // Flip side
        }
        return new int[]{x, y};
    }
    
}
