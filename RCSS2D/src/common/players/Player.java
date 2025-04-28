package common.players;

import common.*;
import common.actions.GOAPAction;
import common.goals.GOAPGoal;
import common.planning.GOAPPlanner;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

//import jdk.nashorn.api.tree.ForInLoopTree;
//comment
//import tags.Tuple;

/**
 * Abstract class for players. Takes care of all of the basics, like connecting to the server,
 * then leaves the definition of availability of the GOAPActions or GOAPGoals up to the subclasses.
 */
public abstract class Player implements Runnable, GOAPAgent {

	// Player info
	private final int num; // number associated with this agent. Linked to spot in array kept in team
	private final String teamName;
	private final String side;
	private final String type;

	boolean gk; // is goalkeeper
	private Team team;
	private boolean isClosest = false;

	private GlobalMap pitch;

	// Communication
	private int port;
	private InetAddress IPAddress;
	private DatagramSocket clientSocket;
	public FromServer2 reader;


	// GOAP
	private final GOAPPlanner planner = new GOAPPlanner();
	private final HashSet<GOAPGoal> availableGoals = new HashSet<>();
	private GOAPGoal currentGoal = null;
	private final HashSet<GOAPAction> availableActions = new HashSet<>();
	private LinkedList<GOAPAction> currentPlan = null;

	// Left over from previous work, no longer used
	private positionEst posEst;
	boolean visual;
	boolean playing = false;

	/**
	 * Standard Instance. Shows map by default
	 * This is a temporary constructor. It will no longer be useful as development progresses.
	 * @param num player number
	 * @param team team name
	 */
	public Player(int num, String team, String type) {
		this.num = num;
		this.teamName = team;
		this.type = type;
		visual = true;
		this.side = "l";

		// For Global Map JPanel to work and estimating position from the server input.
		pitch = new GlobalMap(teamName, side);
		posEst = new positionEst();
		setupComms();

		// position player
		try {
			doAction("move", "-10 0");
		} catch (IOException e) {
			e.printStackTrace();
		}


	}

	/**
	 * Allows for map visual to be disabled
	 * This is a temporary constructor. It will no longer be useful as development progresses.
	 * @param num player number
	 * @param team team name
	 * @param vis visual on or no
	 */
	public Player(int num, String team, String type, boolean vis) {
		this(num, team, type);
		visual = vis;
	}

	/**
	 * Allows for map visual to be disabled and to position player where you want at initiation
	 * and let us decide if the player is a goalkeeper or not
	 * Allows for map visual to be disabled
	 * @param num player number
	 * @param team team name
	 * @param vis visual on or no
	 */
	public Player(int num, String teamName, boolean vis, int xPos, int yPos, Boolean gk, String side, Team team, String type) {
		this.gk = gk;
		this.num = num;
		this.teamName = teamName;
		this.type = type;
		visual = true;
		this.team = team;
		this.side = side;

		// For Global Map JPanel to work and estimating position from the server input.
		pitch = new GlobalMap(teamName, side);
		posEst = new positionEst();

		setupComms();

		visual = vis;

		try {
			doAction("move", "" + xPos + " " + yPos);
			if (side.equals("r")){
				// need to wait to send another command, otherwise server won't accept it
				Thread.sleep(100);
				doAction("turn", "180");
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}


	}

	/**
	 * Setup code that creates all the links to the server. Do not call outside of constructors.
	 */
	private void setupComms(){
		//  Send first the init command to let server know a new player is connecting
		try {
			IPAddress = InetAddress.getByName("localhost");
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}

		// Setup socket
		try {
			clientSocket = new DatagramSocket();
		} catch (SocketException e1) {
			e1.printStackTrace();
		}
		byte[] sendData;
		byte[] receiveData = new byte[1024];


		if (gk) {
			sendData = ("(init " + teamName + " (version 15) (goalie)) ").getBytes();
		} else {
			sendData = ("(init " + teamName + " (version 15)) ").getBytes();
		}

		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 6000);
		try {
			clientSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// after sending the init command to the server,
		// specifying the team, the player will receive its own port to communicate
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		try {
			clientSocket.receive(receivePacket);
		} catch (IOException e) {
			e.printStackTrace();
		}

		IPAddress = receivePacket.getAddress(); // new IP address unique to this player
		port = receivePacket.getPort(); // new port unique to this player

		String modifiedSentence = new String(receivePacket.getData());
		System.out.println("First packet received : " + modifiedSentence); // receiving the confirmation with side and uniformnumber

		// receiving the rest of the data, server_param, player_param, player_type
		for (int i = 0; i < 20; i++) {
			receivePacket = new DatagramPacket(receiveData, receiveData.length);
			try {
				clientSocket.receive(receivePacket);
			} catch (IOException e) {
				e.printStackTrace();
			}

			modifiedSentence = new String(receivePacket.getData());
		}
	}


	public Tuple[] getBallsCartesian(){
		return pitch.getBallsCartesian();
	}

	public Tuple[] getBallsPolar() {return pitch.getBallsPolar();}

	public Tuple getPlayerPos(){
		return pitch.getPlayerPos();
	}

	/**
	 * Determine whether a player can currently dash.
	 * A player can dash when the "move" command is banned, when play is on,
	 * 		or when we are in a situation where a kick is required and this player is the designated kicker.
	 * @return true if the player can dash, false otherwise.
	 */
	public boolean playerCanDash(){
		if (!pitch.state.get(StateKeys.move_allowed)){
			return  (pitch.state.get(StateKeys.play_on))
					||
					(pitch.state.get(StateKeys.designated_kick_off) && side.equals("l") &&
							(pitch.state.get(StateKeys.kick_off_l)
									|| pitch.state.get(StateKeys.free_kick_l)
									|| pitch.state.get(StateKeys.kick_in_l)
									|| pitch.state.get(StateKeys.corner_kick_l)
									|| pitch.state.get(StateKeys.indirect_free_kick_l)
							)
					)
					||
					(pitch.state.get(StateKeys.designated_kick_off) && side.equals("r") &&
							(pitch.state.get(StateKeys.kick_off_r)
									|| pitch.state.get(StateKeys.free_kick_r)
									|| pitch.state.get(StateKeys.kick_in_r)
									|| pitch.state.get(StateKeys.corner_kick_r)
									|| pitch.state.get(StateKeys.indirect_free_kick_r)
							)
					);
		}
		return false;
	}

	public void run() {
		playing = true;

		//Start Receiving from rcss server
		reader = new FromServer2(pitch, clientSocket);
		reader.read();
		// server will change the information
		// in the pitch, which player will be able to read


		//create field visualizer
		// was a thread before but does not need to be one, doesn't make sense to have an overhead here
		if (visual) {
			MapVisualizer mapper = new MapVisualizer(pitch);
			mapper.createVisualizer();
		}

		specificRun();
	}

	public abstract void specificRun();


	@Override
	public boolean goalStillValid() {
		return currentGoal.isValid(this);
	}

	@Override
	public GOAPGoal getNewGoal() {

		GOAPGoal new_goal = null;
		double min_priority = Integer.MAX_VALUE;

		for (GOAPGoal g : availableGoals){
			if (g.isValid(this) && g.calculatePriority(this) < min_priority){
				new_goal = g;
				min_priority = g.calculatePriority(this);
			}
		}

		return new_goal;
	}

	@Override
	public void planAndExecute() {

		currentGoal = getNewGoal();
		reader.read();
		LinkedList<GOAPAction> plan = planner.getPlan(currentGoal, this.getPitch().getState(), this);

		if (plan != null) {

			PlayerMath.updateClosestPlayerToBall(this);

			if (num==1 && side.equals("l")) {
				toStr(side, num, plan);
				positionToStr();
				stateToStr();
				teammatesToStr();
			}

			for (GOAPAction action : plan) {

				if (goalStillValid()) {
					reader.read();

					// If Action cannot be executed, break
					if (!action.executeAction(this)) {
						break;
					}

				} else {
					break;
				}

			}
		}

		// If plan is null, no plan satisfies current goal. Ban it and try again.
		else {
			GOAPGoal bannedGoal = currentGoal;
			availableGoals.remove(currentGoal);
			planAndExecute();
			availableGoals.add(bannedGoal);
		}
	}

	/**
	 * The following 5 methods are used for debugging. They will print relevant informations regarding players on field.
	 * Call them in the PlanAndExecute function
	 */
	public void stateToStr() {
		System.out.println("STATE");
		System.out.println(this.getPitch().state);
		System.out.println();
	}

	public void toStr(String side, int num, LinkedList<GOAPAction> plan) {
		System.out.println("Player " + num + "; side " + side + "; type " + type);
		System.out.println("Current Goal: " + currentGoal);
		System.out.println("Available Goals: " + availableGoals);
		System.out.println("Available Actions: " + availableActions);
		System.out.println("Current Plan: " + plan);
		System.out.println("IsClosest: " + isClosest);
		System.out.println();
	}

	public void teammatesToStr() {
		System.out.println("Teammates in vision :" + this.getPitch().getTeammates());
		System.out.println();
	}

	public void opponentsToStr() {
		System.out.println("Opponents in vision :" + this.getPitch().getOpponents());
		System.out.println();
	}

	public void positionToStr() {
		GlobalMap pitch = getPitch();
		System.out.println(pitch.getPlayerPos().toString());
		if (pitch.getBallsCartesian()[1] != null ) {
			System.out.println(pitch.getBallsCartesian()[1].toString());
			System.out.println(pitch.getBallsPolar()[1].toString());
		}
	}

	/**
	 * Execute a catch command, as described in section 4.6.1:
	 * https://rcsoccersim.readthedocs.io/en/latest/soccerserver.html?highlight=player#catch-model
	 */
	public void doCatch(String angle) throws IOException {
		doAction("catch", angle);
	}

	/**
	 * Execute a dash command, as described in section 4.6.2:
	 * https://rcsoccersim.readthedocs.io/en/latest/soccerserver.html?highlight=player#dash-model
	 */
	public void doDash(String power, String direction) throws IOException {
		doAction("dash", power + " " + direction);
	}

	/**
	 * Execute a dash command, as described in section 4.6.2, but without a direction:
	 * https://rcsoccersim.readthedocs.io/en/latest/soccerserver.html?highlight=player#dash-model
	 */
	public void doDash(String power) throws IOException {
		doAction("dash", power);
	}

	/**
	 * Execute a kick command, as described in section 4.6.3:
	 * https://rcsoccersim.readthedocs.io/en/latest/soccerserver.html?highlight=player#kick-model
	 */
	public void doKick(String power, String direction) throws IOException {
		doAction("kick", power + " " + direction);
	}

	/**
	 * Execute a kick command, as described in section 4.6.3, but without a direction:
	 * https://rcsoccersim.readthedocs.io/en/latest/soccerserver.html?highlight=player#kick-model
	 */
	public void doKick(String power) throws IOException {
		doAction("kick", power);
	}

	/**
	 * Execute a move command, as described in section 4.6.4:
	 * https://rcsoccersim.readthedocs.io/en/latest/soccerserver.html?highlight=player#move-model
	 */
	public void doMove(String x, String y) throws IOException {
		doAction("move", x + " " + y);
	}

	/**
	 * Execute a say command, as described in section 4.6.5:
	 * https://rcsoccersim.readthedocs.io/en/latest/soccerserver.html?highlight=player#say-model
	 */
	public void doSay(String msg) throws IOException {
		doAction("say", msg);
	}

	/**
	 * Execute a tackle command, as described in section 4.6.6:
	 * https://rcsoccersim.readthedocs.io/en/latest/soccerserver.html?highlight=player#tackle-model
	 */
	public void doTackle(String powerOrAngle) throws IOException {
		doAction("tackle", powerOrAngle);
	}

	/**
	 * Execute a turn command, as described in section 4.6.8:
	 * https://rcsoccersim.readthedocs.io/en/latest/soccerserver.html?highlight=player#turn-model
	 */
	public void doTurn(String angle) throws IOException {
		doAction("turn", angle);
	}

	/**
	 * Execute a turn command, as described in section 4.6.9:
	 * https://rcsoccersim.readthedocs.io/en/latest/soccerserver.html?highlight=player#turnneck-model
	 */
	public void doTurnNeck(String angle) throws IOException {
		doAction("turn_neck", angle);
	}

	/**
	 * sends action to rcssserver
	 * @param command action wanted
	 * @param param string containing param or params
	 */
	private void doAction(String command, String param) throws IOException {
		byte[] sendData;
		sendData = ("(" + command + " " + param + ")").getBytes();
		DatagramPacket receivePacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
		clientSocket.send(receivePacket);
	}

	/// ~~~ SOME GETTERS AND SETTERS ~~~ ///


	public int getNum() {
		return num;
	}

	public String getSide() {
		return side;
	}

	public GOAPGoal getCurrentGoal() {
		return currentGoal;
	}

	public HashSet<GOAPAction> getAvailableActions() {
		return availableActions;
	}

	public LinkedList<GOAPAction> getCurrentPlan(){
		return currentPlan;
	}

	public HashSet<GOAPGoal> getAvailableGoals() {
		return availableGoals;
	}

	public boolean getIsClosest() {
		return isClosest;
	}

	public Team getTeam() {
		return team;
	}

	public String getType(){
		return type;
	}


	public void setCurrentGoal(GOAPGoal currentGoal) {
		this.currentGoal = currentGoal;
	}

	public void setCurrentPlan(LinkedList<GOAPAction> currentPlan) {
		this.currentPlan = currentPlan;
	}

	public void addAvailableAction(GOAPAction action){
		availableActions.add(action);
	}

	public void addAvailableGoal(GOAPGoal goal){
		availableGoals.add(goal);
	}

	public GlobalMap getPitch() {
		return pitch;
	}

	public void setIsClosest(boolean isClosest) {
		this.isClosest = isClosest;
	}
}
