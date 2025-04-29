package teamB.goals;

import common.players.Player;

/** Generic meta-task for proactive planning. */
public abstract class MetaGoal implements Comparable<MetaGoal> {

    public enum Status {ACTIVE, COMPLETED, CANCELLED}

    protected Status status = Status.ACTIVE;

    public Status getStatus()        { return status; }
    public abstract boolean isSatisfied(Player agent);

    protected double priority = 1.0;   // lower = sooner

    /** Called by the player: should we drop this goal? */
    public boolean isCompleted()            { return status == Status.COMPLETED; }
    public void    cancel()                 { status = Status.CANCELLED; }

    /** Domain-specific score (e.g. distance to target). */
    public abstract int    score();

    /** Produce a *linearly ordered* list of abstract steps. */
    public abstract java.util.List<String> buildPlan();

    // ---- Comparable ----
    @Override public int compareTo(MetaGoal g) {
        return Double.compare(this.priority, g.priority);
    }
}
