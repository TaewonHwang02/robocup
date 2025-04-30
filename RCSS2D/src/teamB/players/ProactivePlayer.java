// ────────── File: src/teamB/players/ProactivePlayer.java ──────────
package teamB.players;

import common.players.Player;
import common.players.Team;

import teamB.planning.ProactivePlanner;
import teamB.actions.*;

import java.util.*;

/** Very small proactive agent that does NOT use MetaGoal.          */
public class ProactivePlayer extends Player {

    /* ------------- high-level goal queue (strings) --------------- */
    private final Deque<String> goalQ = new ArrayDeque<>();
    private final ProactivePlanner planner = new ProactivePlanner();

    private List<String> plan  = List.of();   // current low-level steps
    private int          stepI = 0;

    /* ------- small reusable Action helpers (real movement) ------- */
    private final MoveToBallAction move  = new MoveToBallAction();
    private final DribbleAction    drib  = new DribbleAction();
    private final ShootAction      shot  = new ShootAction();
    private final GuardAction      guard = new GuardAction();
    private final ShadowAction     shad  = new ShadowAction();

    /* ------------------------------------------------------------- */
    public ProactivePlayer(int num,String team,boolean vis,
                           int x,int y,String side,Team ref) {
        super(num, team, vis, x, y, /*gk*/false, side, ref, "proactive");
    }

    /* ============================================================= */
    @Override public void specificRun() {

        enqueueDefaultGoal();                // “attack”

        while (!goalQ.isEmpty()) {

            /* -------- create (or refresh) low-level plan -------- */
            String gLabel = goalQ.peekFirst();
            if (stepI == 0 || stepI >= plan.size()) {
                plan  = planner.makePlanFor(gLabel);
                stepI = 0;
            }

            /* ----------- perform next atomic step ------------- */
            performStep(plan.get(stepI));
            stepI++;

            /* -------------- goal satisfied? ------------------- */
            if (stepI >= plan.size()) {          // finished low-level plan
                goalQ.removeFirst();             // pop goal
            }

            /* -------------- toy interrupts ------------------- */
            if (ballLost())      pushFront("attack");
            if (helpRequested()) pushFront("guard_carrier");

            sleep(80);                            // ~80 ms cycle
        }
    }

    /* ============================================================= */
    private void enqueueDefaultGoal() { goalQ.addLast("attack"); }
    private void pushFront(String g){ goalQ.addFirst(g); plan=List.of(); stepI=0; }

    /* -- execute one low-level step label with real actions ------- */
    private void performStep(String s){
        try {
            switch (s) {
                case "move_to_ball"      -> move.perform(this);
                case "dribble_forward"   -> drib.perform(this);
                case "shoot_goal"        -> shot.perform(this);
                case "move_near_carrier" -> guard.perform(this);
                case "shadow_opponent"   -> shad.perform(this);
                default                  -> {}          // ignore unknown
            }
        } catch (Exception e){ e.printStackTrace(); }
    }

    /* -------------- stub interrupt conditions -------------------- */
    private boolean ballLost()      { return false; }
    private boolean helpRequested() { return false; }

    private static void sleep(long ms){
        try { Thread.sleep(ms); } catch (InterruptedException ignored){}
    }
}
