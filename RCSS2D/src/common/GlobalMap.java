package common;

import java.awt.geom.Point2D;
import java.util.*;
import javax.swing.JPanel;

//import jdk.nashorn.api.tree.ForInLoopTree;


@SuppressWarnings("serial")
/**
 * GlobalMap extending a JPanel, and holding information about the map from the Player's point of view.
 * Updates the state HashMap depending on the information parsed by LocalView.
 */
public class GlobalMap extends JPanel{

	private boolean visualOn = false;

	private Tuple playerPos;
	private final String teamId;
	private final String side;
	private double curTime;

	private static int scale = 8; //must be even
	private ArrayList<Tuple> teammates;
	private ArrayList<Tuple> opponents;
	private final ArrayList<ArrayList<Tuple[]>> gridMap;

	private Tuple ball; // The sParams are "time" at 0, "dist" at 1, and "dir" as 2. There are a few more params but they don't hold info relevant to the ball
	private Tuple old_ball;
	private Tuple goal;

	private final StateKeys[] hearStates = new StateKeys[]{StateKeys.before_kick_off, StateKeys.play_on, StateKeys.time_up, StateKeys.time_over,
			StateKeys.kick_off_l, StateKeys.kick_off_r, StateKeys.kick_in_l, StateKeys.kick_in_r,
			StateKeys.free_kick_l, StateKeys.free_kick_r, StateKeys.corner_kick_l, StateKeys.corner_kick_r, StateKeys.goal_kick_l,
			StateKeys.goal_kick_r, StateKeys.goal_l, StateKeys.goal_r, StateKeys.drop_ball, StateKeys.offside_l, StateKeys.offside_r,
			StateKeys.penalty_kick_l, StateKeys.penalty_kick_r, StateKeys.foul_charge_l, StateKeys.foul_charge_r, StateKeys.back_pass_l,
			StateKeys.back_pass_r, StateKeys.free_kick_fault_l, StateKeys.free_kick_fault_r, StateKeys.indirect_free_kick_l,
			StateKeys.indirect_free_kick_r, StateKeys.illegal_defense_l, StateKeys.illegal_defense_r};

	public final HashMap<StateKeys, Boolean> state = new HashMap<>();

	public GlobalMap(String teamid, String side) {
		this.teamId = teamid;
		this.side = side;

		playerPos = new Tuple("player", new String[]{"x", "y", "absDir"}, new double[]{0,0,0});
		ball = null;

		// States related to ball
		state.put(StateKeys.ball_in_FOV, false);
		state.put(StateKeys.ball_centered_in_FOV, false);
		state.put(StateKeys.has_ball, false);
		state.put(StateKeys.carrying_ball_fwd, false);

		// States related to teamates & Opponents
		state.put(StateKeys.oppo_goal_in_FOV, false);
		state.put(StateKeys.oppo_blocking, false);
		state.put(StateKeys.oppo_attacking, false);
		state.put(StateKeys.oppo_has_ball, false);
		state.put(StateKeys.dribble_mode, false);
		state.put(StateKeys.teammate_in_FOV, false);
		state.put(StateKeys.team_has_ball, false);

		// States related to positioning
		state.put(StateKeys.in_win_position, false);
		state.put(StateKeys.in_def_position, false);
		state.put(StateKeys.in_mid_position, false);
		state.put(StateKeys.in_att_position, false);
		state.put(StateKeys.aligned_with_ball, false);
		state.put(StateKeys.in_oppo_fourth, false);

		// Misc States
		state.put(StateKeys.kick_performed, false);
		state.put(StateKeys.move_allowed, true);
		state.put(StateKeys.in_new_position, true);

		// added
		state.put(StateKeys.designated_kick_off, false);
		state.put(StateKeys.in_side_defense,    false);
		state.put(StateKeys.in_support_position,false);
		state.put(StateKeys.in_support_winger,    false);  // NEW
		state.put(StateKeys.in_support_midfielder,false);
	

		for (StateKeys key: hearStates){
			state.put(key, false);
		}
		state.put(StateKeys.before_kick_off, true);

		gridMap = new ArrayList<ArrayList<Tuple[]>>();
		for (int i = 0; i < 21; i++) {
			gridMap.add(new ArrayList<Tuple[]>());
			for (int j = 0; j < 10; j++) {
				gridMap.get(i).add(new Tuple[24]);
			}
		}

		teammates = new ArrayList<Tuple>(10);
		opponents = new ArrayList<Tuple>(11);

	}


	/**
	 * Updates global map with new localView
	 * @param view view
	 */
	public void update(LocalView view) {

		curTime = view.getTime();

		visualUpdate(view);
		bodyUpdate(view);
		hearingUpdate(view);

		if (old_ball != null) {
			positionUpdate();
		}
	}

	/**
	 * Updates all information based on visual sensor:
	 * Identifies and maps objects of interest in a list of items,
	 * Separates the flags from other players and balls
	 */
	public void visualUpdate(LocalView view) {
		ArrayList<Tuple> items = view.getItemsInView();
		Tuple[] flags = new Tuple[2];
		int index = 0;
		teammates.clear();
		opponents.clear();
		boolean ball_seen = false;
		boolean has_ball = false;
		boolean goal_seen = false;
		boolean teammate_seen = false;
		boolean opponent_seen = false;

		for (Tuple item : items) {
			// FIELDS RELEVANT TO THE BALL
			if (item.label.charAt(0) == 'b' || item.label.charAt(0) == 'B') {
				old_ball = ball;
				ball = item;
				ball_seen = true;

				if (item.iParams[1] < 0.7){
					has_ball = true;
					state.put(StateKeys.team_has_ball, true);
				}

				// determine if the ball is in the center of our field of view. We'll allow a small margin of error since dir is a floating point number
				//if (ball.getIParams()[2] < 0.1 && ball.getIParams()[2] > -0.1){
				if (ball.getIParams()[2] < 5 && ball.getIParams()[2] > -5){
					state.put(StateKeys.ball_centered_in_FOV, true);

				}
				else {
					state.put(StateKeys.ball_centered_in_FOV, false);
				}
			}

			// FIELDS RELEVANT TO OTHER PLAYERS
			else if (item.label.charAt(0) == 'p') {

				// CASE : Opponent player
				if (!item.label.contains(teamId)) {
					opponent_seen = true;
					opponents.add(item);

					// CASE: Player is attackin
					if (item.iParams[1] <= 15) {
						state.put(StateKeys.oppo_blocking, true);
					}

					else {
						state.put(StateKeys.oppo_blocking, false);
					}

					// CASE: Player is defending
					if (ball_seen && !has_ball && item.iParams[1] <= 12) {
						// CASE: If ball within reach
						if (ball.iParams[1] <= 12) {
							state.put(StateKeys.oppo_attacking, true);
						}
						// CASE: If ball not within reach
						else {
							state.put(StateKeys.oppo_attacking, false);
						}
					}
				}

				// CASE : Teammate player
				else {
					teammate_seen = true;
					teammates.add(item);
					state.put(StateKeys.teammate_in_FOV, true);
				}
			}

			// FIELS RELEVANT TO FLAGS/GOAL
			else if (index < 2 && (item.label.charAt(0) == 'f' || item.label.charAt(0) == 'g')) {
				flags[index] = item;
				index++;
				if (index == 2) {
					double[] new_pos = PlayerMath.locatePlayer(flags[0], flags[1]);

					if (new_pos != null) {
						playerPos.setIParams(new_pos);
					}

				}
			}
			else if ((item.label.equals("gl") && side.equals("r"))
					|| (item.label.equals("gr") && side.equals("l"))){
				goal = item;
				goal_seen = true;

				// UPDATE CURRENT FOURTH
				state.put(StateKeys.in_oppo_fourth, goal.getIParams()[1] < 20);
			}
		}

		// RESETS
		// ball wasn't seen during this cycle
		if (!ball_seen){
			if (ball != null){
				old_ball = ball;
			}
			ball = null;
			state.put(StateKeys.ball_centered_in_FOV, false);
		}
		state.put(StateKeys.ball_in_FOV, ball_seen);

		// opponent goal wasn't seen during this cycle
		if (!goal_seen){
			goal = null;
		}
		state.put(StateKeys.oppo_goal_in_FOV, goal_seen);
		state.put(StateKeys.has_ball, has_ball);

		// opponent wasn't seen during this cycle
		if (!opponent_seen) {
			state.put(StateKeys.oppo_goal_in_FOV, opponent_seen);
			state.put(StateKeys.oppo_blocking, opponent_seen);
			state.put(StateKeys.oppo_attacking, opponent_seen);
		}


		// no teammates wasn't seen during this cycle
		if (!teammate_seen) {
			state.put(StateKeys.teammate_in_FOV, teammate_seen);
		}

		state.put(StateKeys.kick_performed, false);
	}

	/**
	 * Updates all information based on body sensor:
	 * Doesn't do anything for now
	 */
	private void bodyUpdate(LocalView view) {
	}

	/**
	 * Updates all information based on hearing sensor:
	 * Changes the play mode
	 */
	private void hearingUpdate(LocalView view) {
		for (String[] packet : view.getHearInfo()){
			// CASE: Player hear referee command
			if (packet[0].equals("referee") && !packet[1].startsWith("drop_ball")) {

				if (state.get(StateKeys.designated_kick_off) && side.equals("l")) {
					// temporary, to monitor what is being heard
					System.out.println(view.getTime() + ": " + packet[1]);
				}

				// set the play state to whatever was declared by the referee. Also resets the previous state to false
				for (StateKeys key : hearStates) {
					state.put(key, packet[1].startsWith(key.name()));
				}

				if (packet[1].startsWith("goal_l")
						|| packet[1].startsWith("goal_r")
						//|| packet[1].startsWith("play_on")
						|| packet[1].startsWith("before_kick_off")){
					state.put(StateKeys.move_allowed, true);
					// state.put(StateKeys.in_new_position, true);
				} else {
					state.put(StateKeys.move_allowed, false);
					state.put(StateKeys.in_new_position, false);
				}

				state.put(StateKeys.team_has_ball, false);
				state.put(StateKeys.oppo_has_ball, false);
			}

			// CASE: Player hears other player
			else {
				// CASE : Player hears 'opp', meaning opponent have the ball
				if (packet[0].equals("opp")) {
					state.put(StateKeys.team_has_ball, false);
					state.put(StateKeys.oppo_has_ball, true);
					state.put(StateKeys.has_ball, false);
					state.put(StateKeys.carrying_ball_fwd, false);
				}
				// CASE : Player hears 'our' or self, meaning teammates or self have the ball
				else {
					// CASE : Player hears have_b
					if (packet[1].startsWith("\"have_b\"")) {
						// CASE : Player hears self
						if (packet[0].equals("self")) {
							//System.out.println("I HAS BALL");
							state.put(StateKeys.has_ball, true);

							// CASE : Player hears team
						} else {
							///System.out.println("TEAM HAS BALL");
							state.put(StateKeys.has_ball, false);
						}
						state.put(StateKeys.team_has_ball, true);
						state.put(StateKeys.oppo_has_ball, false);
					}

					// CASE : Player hears carry_b
					else if (packet[1].startsWith("\"carry_b\"")) {
						if (packet[0].equals("self")) {
							//System.out.println("I CARRY BALL");
							state.put(StateKeys.carrying_ball_fwd, true);
						} else {
							state.put(StateKeys.carrying_ball_fwd, false);
						}
						state.put(StateKeys.team_has_ball, true);
						state.put(StateKeys.oppo_has_ball, false);
					}
				}
			}
		}
	}

	/**
	 * Updates all states based on the position of the players:
	 * Detects distance with ball and position of field to set flags accordingly
	 */
	private void positionUpdate() {
		// Relevant informations about player and ball
		Point2D.Double pos = calcPos(playerPos);
		double dist_ball;
		Tuple ball_pos;

		// If ball not seen this cycle, use last remembered position of ball
		if (ball != null) {
			dist_ball = ball.getIParams()[1];
			ball_pos = getBallsCartesian()[1];

		} else {
			dist_ball = old_ball.getIParams()[1];
			ball_pos = getBallsCartesian()[0];

		}

		double ball_pos_x = ball_pos.getIParams()[1];
		double ball_pos_y = ball_pos.getIParams()[2];

		// Updaye StateKeys depending on player's side
		if (side.equals("l")) {
			double def_range_start = -45.0;
			double def_range_end = def_range_start - (def_range_start - ball_pos_x)/2;
			double oppo_fourth_start = 26;

			// UPDATE DEF POSITION
			// Defender needs to be close to its goal, and aligned with the ball to tackle it
			boolean in_def_pos = (pos.x > def_range_start) && (pos.x < def_range_end);
			state.put(StateKeys.in_def_position, in_def_pos);

			// UPDATE ATT POSITION
			boolean in_att_pos = (pos.x > 30.0);
			state.put(StateKeys.in_att_position, in_att_pos);

			// UPDATE WIN POSITION
			boolean in_win_pos = (pos.x > 0 && (pos.y > 10 || pos.y < -10));
			state.put(StateKeys.in_win_position, in_win_pos);

		} else {
			double def_range_start = 45.0;
			double def_range_end = def_range_start - (def_range_start - ball_pos_x)/2;
			double oppo_fourth_start = -26;

			// UPDATE DEF POSITION
			// Defender needs to be close to its goal, and aligned with the ball to tackle it
			boolean in_def_pos = (pos.x < def_range_start) && (pos.x > def_range_end);
			state.put(StateKeys.in_def_position, in_def_pos);

			// UPDATE ATT POSITION
			boolean in_att_pos = (pos.x < -30.0);
			state.put(StateKeys.in_att_position, in_att_pos);

			// UPDATE WIN POSITION
			boolean in_win_pos = (pos.x < 0 && (pos.y > 10 || pos.y < -10));
			state.put(StateKeys.in_win_position, in_win_pos);
		}

		// Defender needs to be aligned with the ball to better defend against opponents
		boolean aligned_with_ball = (Math.abs(pos.y) <= Math.abs(ball_pos_y+2/2));
		state.put(StateKeys.aligned_with_ball, aligned_with_ball);

		// UPDATE MID POSIITION
		boolean in_mid_pos = (pos.x <= ball_pos_x + 5) && (pos.x >= ball_pos_x - 5) && (dist_ball > 15);
		state.put(StateKeys.in_mid_position, in_mid_pos);
	}

	/**
	 * calculates the position of an object as a 2DVector Point
	 * @param item item
	 * @return position
	 */
	public Point2D.Double calcPos(Tuple item) {
		double dist = item.iParams[1];
		double dir = item.iParams[2] + playerPos.getIParams()[2];
		double x = playerPos.getIParams()[0] + Math.cos(Math.toRadians(dir)) * dist;
		double y = playerPos.getIParams()[1] + Math.sin(Math.toRadians(dir)) * dist;
		//System.out.println(""+ x + ", " + y);
		return new Point2D.Double(x, y);
	}

	/**
	 * Player position in the field
	 * @return player position
	 */
	public Tuple getPlayerPos() {
		return playerPos;
	}

	/**
	 * gets the current time
	 * @return timethi
	 */
	public double getCurTime() {
		return curTime;
	}

	/**
	 * gets the ball as seen by the player in the last frame (in polar coordinates)
	 * @return ball tuple
	 */
	public Tuple getBall() {
		return ball;
	}

	/**
	 * gets the opponent's goal as seen by the player in the last frame (in polar coordinates)
	 * @return goal tuple
	 */
	public Tuple getGoal() {
		return goal;
	}

	/**
	 * get the latest info on the balls (the last two times it was seen), in cartesian coordinates
	 * @return old_ball (previous info about the ball) and ball (latest info about the ball)
	 */
	public Tuple[] getBallsCartesian(){

		Tuple old_ball_cartesian = null;
		Tuple ball_cartesian = null;
		if (old_ball != null){
			Point2D.Double old_ball_pos = calcPos(old_ball);
			old_ball_cartesian = new Tuple("old_ball", new String[]{"time", "x", "y"}, new double[]{old_ball.getIParams()[0], old_ball_pos.x, old_ball_pos.y});

		}
		if (ball != null){
			Point2D.Double ball_pos = calcPos(ball);
			ball_cartesian = new Tuple("ball", new String[]{"time", "x", "y"}, new double[]{ball.getIParams()[0], ball_pos.x, ball_pos.y});

		}


		return new Tuple[]{old_ball_cartesian, ball_cartesian};
	}

	/**
	 * get the latest info on the balls (the last two times it was seen), in polar coordinates
	 * @return old_ball (previous info about the ball) and ball (latest info about the ball)
	 */
	public Tuple[] getBallsPolar(){
		return new Tuple[]{old_ball, ball};
	}

	/**
	 * Get the current state of the world as seen by the player.
	 * @return a copy of the state hashmap
	 */
	public HashMap<StateKeys, Boolean> getState(){
		return new HashMap<>(state);
	}

	/**
	 * Get infos on teammates in vision
	 */
	public ArrayList<Tuple> getTeammates() {
		return teammates;
	}

	/**
	 * Get infos on opponents in vision
	 */
	public ArrayList<Tuple> getOpponents() {
		return opponents;
	}
}
