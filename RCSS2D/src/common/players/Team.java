package common.players;

import common.PlayerMath;
import common.StateKeys;

/**
 * Class for managing all the players at once. Also provides a way for players of a same team to pool information and communicate.
 */
public class Team {

    protected  final String name;
    protected final Player[] players = new Player[11];
    protected final Thread[] threads = new Thread[11];

    public Team(String name){
        this.name = name;
    }

    /**
     * Create a team of players, as well as each player's thread. Note that this is a temporary method, and later on this should be changed to have
     * num_active_players as always 10 and have_gk as always true.
     * There must be at least one player (it could be just a goalkeeper though).
     * @param side the side of the team (either l or r)
     * @param num_active_players how many players we want on the team (can be no more than 10)
     * @param have_gk specify whether a goalkeeper is desired.
     */
    public void createTeam(String side, int num_attackers, int num_midfielders, int num_wingers, int num_defenders, boolean have_gk){
        assert num_attackers + num_midfielders + num_wingers + num_defenders <= 10;
        assert num_attackers + num_midfielders + num_wingers + num_defenders >= 1 || have_gk;

        //ADD ATTACKERS TO TEAM
        for (int i = 1; i <= num_attackers; i++){
            System.out.println("ADDING ATT");
            int[] position = PlayerMath.getStartPosition(i, side);
            players[i] = new Attacker(i, name, false, position[0], position[1], side, this);
            threads[i] = new Thread(players[i]);

            players[i].getPitch().state.put(StateKeys.designated_kick_off, i == 1);
        }

        // ADD MIDFIELDERS TO TEAM
        for (int i = num_attackers+1; i <= num_attackers+num_midfielders; i++){
            System.out.println("ADDING MID");
            int[] position = PlayerMath.getStartPosition(i, side);
            players[i] = new Midfielder(i, name, false, position[0], position[1], side, this);
            threads[i] = new Thread(players[i]);

            players[i].getPitch().state.put(StateKeys.designated_kick_off, i == 1);
        }

        // ADD WINGERS TO TEAM
        for (int i = num_attackers+num_midfielders+1; i <= num_attackers+num_midfielders+num_wingers; i++){
            System.out.println("ADDING WIN");
            int[] position = PlayerMath.getStartPosition(i, side);
            players[i] = new Winger(i, name, false, position[0], position[1], side, this);
            threads[i] = new Thread(players[i]);

            players[i].getPitch().state.put(StateKeys.designated_kick_off, i == 1);
        }

        // ADD DEFENDERS TO TEAM
        for (int i = num_attackers+num_midfielders+num_wingers+1; i <= num_attackers+num_midfielders+num_wingers+num_defenders; i++){
            System.out.println("ADDING DEF");
            int[] position = PlayerMath.getStartPosition(i, side);
            players[i] = new Defender(i, name, false, position[0], position[1], side, this);
            threads[i] = new Thread(players[i]);

            players[i].getPitch().state.put(StateKeys.designated_kick_off, i == 1);
        }

        // ADD GK TO TEAM
        if (have_gk) {
            int[] position = PlayerMath.getStartPosition(0, side);
            players[0] = new GoalKeeper(0, name, false, position[0], position[1], side, this);
            //players[0] = new GoalKeeper(0, name, false, 3, 9, side, this);
            threads[0] = new Thread(players[0]);

            players[0].getPitch().state.put(StateKeys.designated_kick_off, false);
        }
    }

    /**
     * Start every player thread.
     */
    public void startTeam(){
        for (Thread t : threads){
            if (t != null){
                t.start();
            }
        }
    }

    /**
     * Wait for every player thread to finish.
     */
    public void joinTeam(){
        for (Thread t : threads){
            if (t != null){
                try {
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public Player[] getPlayers() {
        return players;
    }
}
