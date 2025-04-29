import common.players.Team;    // the template AI module
import teamA.TeamMain;         // your custom Team-A module

public class teamtester {
    public static void main(String[] args) {
        // — Team A (1 ST, 3 MF, 2 WG, 4 Def, + GK) —
        TeamMain teamA = new TeamMain("TeamA");
        // side = "l", attackers=1, midfielders=3, wingers=2, defenders=4, haveGK=true
        teamA.createTeam("l", 1, 3, 2, 4, true);

        // — Team B (template AI, e.g. 1–2–1–6 + GK) —
        Team teamB = new Team("TeamB");
        teamB.createTeam("r", 1, 2, 1, 6, true);

        // kick off
        teamA.startTeam();
        teamB.startTeam();

        // wait until someone scores
        teamA.joinTeam();
        teamB.joinTeam();
    }
}
