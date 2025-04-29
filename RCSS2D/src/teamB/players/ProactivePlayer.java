package teamB.players;

import teamB.goals.*;
import teamB.planning.ProactivePlanner;
import java.util.*;

import common.players.Team;

/**
 * Minimal proactive agent:
 *  • keeps a FIFO queue of goals
 *  • plans once per goal
 *  • executes step-by-step, with tiny sleep to emulate cycle
 *  • interrupts on two toy events (ball lost / help call)
 */
public class ProactivePlayer implements Runnable {

    // ----------------------------------------------------------------–
    private final Queue<MetaGoal> goalQueue = new ArrayDeque<>();
    private final ProactivePlanner planner = new ProactivePlanner();

    private List<String> currentPlan = List.of();
    private int   planIndex = 0;
    private final String name;

    public ProactivePlayer(int num, String teamName, boolean visual, int x, int y, String side, Team teamRef) {
        super();
        this.name = "";
    }

    // ----------------------------------------------------------------–
    @Override public void run() {
        enqueueDefaultGoals();

        while (!goalQueue.isEmpty()) {
            MetaGoal g = goalQueue.peek();

            // plan once per goal
            if (planIndex == 0 || planIndex >= currentPlan.size()) {
                currentPlan = planner.makePlan(g);
                planIndex    = 0;
            }

            // execute one step
            String step = currentPlan.get(planIndex);
            executeStep(step);
            planIndex++;

            // finished?
            if (planIndex >= currentPlan.size()) {
                g.isCompleted();
                goalQueue.remove();
            }

            // ── interrupts (toy examples) ──
            if (ballLost())          { cancelAndPushFront(new AttackGoal()); }
            if (helpRequested())     { cancelAndPushFront(new GuardCarrierGoal()); }

            sleep(80);   // simulate 80 ms cycle
        }
        System.out.println(name + " finished all goals.");
    }

    // ----------------------------------------------------------------–
    /* ===== helper behaviour stubs ===== */

    private void enqueueDefaultGoals() {
        goalQueue.add(new AttackGoal());
    }

    private void executeStep(String s) {
        switch (s) {
            case "move_to_ball"         -> System.out.println(name + " dashes to ball");
            case "dribble_forward"   -> System.out.println(name + " dribbles");
            case "shoot_goal"        -> System.out.println(name + " shoots!");
            case "move_near_carrier" -> System.out.println(name + " guards carrier");
            case "shadow_opponent"   -> System.out.println(name + " shadows foe");
            case "tackle_if_close"   -> System.out.println(name + " tries tackle");
            default                  -> System.out.println(name + " ??? " + s);
        }
    }

    // toy interrupt flags (replace with real sensor checks)
    private boolean ballLost()      { return false; }
    private boolean helpRequested() { return false; }

    private void cancelAndPushFront(MetaGoal g) {
        if (!goalQueue.isEmpty()) goalQueue.peek().cancel();
        goalQueue.add(g);
        currentPlan = List.of(); planIndex = 0;
    }

    private static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }
}
