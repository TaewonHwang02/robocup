package teamA.teamA_goals;

import common.StateKeys;
import common.goals.GOAPGoal;
import common.players.Player;

// Sideback players move to wide area to defend
public class GetInSideDefenseGoal extends GOAPGoal {

  public GetInSideDefenseGoal() {

    addCondition(StateKeys.in_side_defense, true);
  }

  //  Only valid when _we_ do NOT have possession. 
  @Override
  public boolean validitySpecifics(Player agent) {
    Boolean weHave = agent.getPitch().state.get(StateKeys.team_has_ball);
    return weHave != null && !weHave;
  }

  // Give high priority 
  @Override
  public double calculatePriority(Player agent) {
    return 2000;
  }
}
