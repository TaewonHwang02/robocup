// ────────── File: src/teamB/players/ProactivePlayer.java ──────────
package teamB.players;

import common.StateKeys;
import common.players.Player;
import common.players.Team;


import java.io.IOException;

public class ProactivePlayer extends Player {

    public ProactivePlayer(int num, String team,
                           boolean vis, int x, int y,
                           String side,  Team ref) {
        super(num, team, vis, x, y, /*gk*/false, side, ref, "proactive");

        // register only the common goals & actions you want
        addAvailableGoal(new common.goals.ResetPositionsGoal());
        addAvailableGoal(new common.goals.FocusOnBallGoal());
        addAvailableGoal(new common.goals.InterceptBallGoal());
        addAvailableGoal(new common.goals.KickBallGoal());

        addAvailableAction(new common.actions.GetBallInFOVAction());
        addAvailableAction(new common.actions.CenterBallInFOVAction());
        addAvailableAction(new common.actions.MoveToBallAction());
        addAvailableAction(new common.actions.CatchBallAction());
        addAvailableAction(new common.actions.BringBallToGoalAction());
        addAvailableAction(new common.actions.KickBallToGoalAction());
        addAvailableAction(new common.actions.CrossBallAction());
        addAvailableAction(new common.actions.MoveToDefPositionAction());
    }

    @Override
    public void specificRun() {
        // Main loop: read server, then let GOAP pick the highest-priority valid goal each cycle
        while (!(Boolean.TRUE.equals(getPitch().state.get(StateKeys.time_up))
              || Boolean.TRUE.equals(getPitch().state.get(StateKeys.time_over))))
        {
            // 1) pull in the latest world state
            reader.read();

            // 2) if you can’t even see the ball, spin & retry
            if (!Boolean.TRUE.equals(getPitch().state.get(StateKeys.ball_in_FOV))) {
                try { doTurn("30"); } catch (IOException ignored) {}
            }
            else {
                // 3) otherwise hand off to the GOAP core
                planAndExecute();
            }

            // 4) throttle to ~80 ms per cycle
            try { Thread.sleep(80); } catch (InterruptedException ignored) {}
        }
    }
}
