    package teamA.teamA_goals;

    import common.StateKeys;
    import common.goals.GOAPGoal;
    import common.players.Player;


    //TEAM-A specific goal: once our team has possession, move up to support
    // the nearest midfielder and mark in_support_midfielder==true.

    public class AssistMidfielderGoal extends GOAPGoal {

        public AssistMidfielderGoal() {
            // We only consider this goal when:
            //   team_has_ball == true  AND  in_support_midfielder == false
            addCondition(StateKeys.team_has_ball,         true);
            addCondition(StateKeys.in_support_midfielder, false);
        }

        @Override
        public boolean validitySpecifics(Player agent) {
            Boolean weHave = agent.getPitch().state.get(StateKeys.team_has_ball);
            return (weHave != null && weHave) && agent.playerCanDash();
        }
        @Override
        public double calculatePriority(Player agent) {

            return 2.0;
        }
    }
