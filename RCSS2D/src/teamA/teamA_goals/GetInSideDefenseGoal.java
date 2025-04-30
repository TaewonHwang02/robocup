package teamA.teamA_goals;

import common.StateKeys;
import common.goals.GOAPGoal;
import common.players.Player;

/** Goal: tuck inside to cover the flank when the opponents have the ball. */
public class GetInSideDefenseGoal extends GOAPGoal {

  public GetInSideDefenseGoal() {
    // end-state: we want in_side_defense == true
    addCondition(StateKeys.in_side_defense, true);
  }

  /** Only valid when _we_ do NOT have possession. */
  @Override
  public boolean validitySpecifics(Player agent) {
    Boolean weHave = agent.getPitch().state.get(StateKeys.team_has_ball);
    return weHave != null && !weHave;
  }

  /** Give this higher urgency than FocusOnBallGoal (1000). */
  @Override
  public double calculatePriority(Player agent) {
    // Bigger number = more urgent
    return 2000;
  }
}
