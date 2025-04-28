package common;

import java.util.ArrayList;

/**
 * Local View specific to a time in the game running, about the see information from the server
 */
public class LocalView {
	
	private ArrayList<Tuple> itemsInView = null;
	private ArrayList<Tuple> bodySensorInfo = null;
	private final ArrayList<String[]> hearInfo = new ArrayList<>();

	private int time = -1;
	private boolean sightReady = false;
	private boolean bodyReady = false;

	// General form of a see message:
	//(see 0 ((f r t) 54.6 -38) ((f r b) 54.6 38) ((f g r b) 43.8 9) ((g r) 42.9 0) 
	//	((f g r t) 43.8 -9) ((f p r b) 33.4 37) ((f p r c) 26.6 0 0 0) ((f p r t) 33.4 -37) 
	//	((f t r 50) 56.3 -44) ((f b r 50) 56.3 44) ((f r 0) 47.9 0) ((f r t 10) 48.9 -12) 
	//	((f r t 20) 51.9 -23) ((f r t 30) 56.8 -32) ((f r b 10) 48.9 12) ((f r b 20) 51.9 23) 
	//	((f r b 30) 56.8 32) ((l r) 42.9 90))
//	(see 0 ((F) 0.4 47) ((f c t) 34.5 8) ((f t 0) 39.3 8) ((f t r 10) 40.4 22) 
//			((f t r 20) 44.3 35) ((f t r 30) 49.4 45) ((f t l 10) 40.4 -7) 
//			((f t l 20) 43.8 -19) ((f t l 30) 49.4 -30) ((f t l 40) 55.7 -38) 
//			((f t l 50) 63.4 -44) ((B) 0.4 47) ((l t) 34.5 -83))
//	(see 0 ((f c) 0.4 -15 0 0) ((f c b) 34.5 -9 0 0) ((f b 0) 39.3 -9) 
//			((f b r 10) 40.4 -23) ((f b r 20) 44.3 -36) ((f b l 10) 40.4 5) 
//			((f b l 20) 44.3 18) ((f b l 30) 49.4 29) ((f b l 40) 56.3 37) 
//			((f b l 50) 63.4 43) ((b) 0.4 -15 0 0) ((l b) 34.8 81))
//	(see 0 ((f c b) 4 -9 0 0) ((f b 0) 9 -9 0 0) ((f b l 10) 13.5 39 0 0) ((l b) 4.1 81))


	/**
	 * Parse through a "see" message from the server in order to isolate each object into a Tuple.
	 */
	public void parseSee(String message){
		// split whenever there is a ((
		String[] sep = message.split("\\(\\(");

		int temp_time = Integer.parseInt(sep[0].split(" ")[1]);
		/*
		if (time == -1 || time == temp_time){
			time = temp_time;
		}
		else {
			return;
		}*/
		time = temp_time;

		itemsInView = new ArrayList<>();

		for (int i = 1; i < sep.length; i++){

			// sep[i] is something of the type f c) 0.4 -15 0 0)
			// We must format this into a Tuple
			String[] tuple = sep[i].split("\\)");

			// build the label of the Tuple (first part of sep[i])
			StringBuilder sb = new StringBuilder();
			for (int j = 0; j < tuple[0].length(); j++){
				if (tuple[0].charAt(j) != ' '){
					sb.append(tuple[0].charAt(j));
				}
			}

			String[] sParams = {"time", "dist", "dir", "distChange", "dirChange", "bodyDir", "headDir"};
			double[] iParams = new double[7];
			iParams[0] = time;

			// go through the iParams (second part of sep[i])
			int k = 1;
			for (String s : tuple[1].split(" ")){
				// double-check that there are no letters in s
				if (!s.matches("[A-Za-z]+") && !s.equals("")) {
					iParams[k] = Double.parseDouble(s);
					k++;
					if (k >= 7){
						break;
					}
				}
			}
			if (k < 3){
				// The documentation says that it's possible for us to only get one parameter in some cases, but I never came across such a case.
				// If this ever happens, this warning will print. We would then need to modify some of our code, since a lot
				//      of it relies on having at least distance and direction.
				System.out.println("WARNING: parsed through " + (k-1) + " parameters. Need at least 2 for code to function properly");
			}

			itemsInView.add(new Tuple(sb.toString(), sParams, iParams));

		}
		sightReady = true;
	}

	/**
	 * Parse through a "sense_body" message from the server in order to isolate each object into a Tuple.
	 */
	public void parseBodySense(String message) {
		String[] sep = message.split("\\(");

		String[] sub_sep = sep[1].split(" ");
		int temp_time = Integer.parseInt(sub_sep[1]);
		/*
		if (time == -1 || time == temp_time){
			time = temp_time;
		}
		else {
			return;
		}*/
		time = temp_time;

		bodySensorInfo = new ArrayList<>();

		for (int i = 2; i < sep.length; i++){

			String[] sub = sep[i].split(" ");

			if (sub[0].equals("stamina")){
				double[] iParams = new double[3];
				iParams[0] = Double.parseDouble(sub[1]);
				iParams[1] = Double.parseDouble(sub[2]);
				// last element of sub contains a trailing ) that must be removed
				iParams[2] = Double.parseDouble(sub[3].substring(0, sub[3].length()-1));

				bodySensorInfo.add(new Tuple("stamina", new String[]{"stamina", "effort", "capacity"}, iParams));
			}
			else if (sub[0].equals("speed")){
				double[] iParams = new double[2];
				iParams[0] = Double.parseDouble(sub[1]);
				iParams[1] = Double.parseDouble(sub[2].substring(0, sub[2].length()-1));

				bodySensorInfo.add(new Tuple("speed", new String[]{"amount_of_speed", "dir_of_speed"}, iParams));
			}
		}
		bodyReady = true;

	}

	/**
	 * Parse through a "hear" message from the server in order to isolate each object into a Tuple.
	 */
	public void parseHear(String message) {
		// Message is always of the form (hear Time Sender Message)
		String[] sep = message.split(" ");
		String[] hear = new String[2];
		
		// CASE: Message heard is from ref or self
		if (sep[2].equals("referee") || sep[2].equals("self")) {
			hear[0] = sep[2];
			hear[1] = sep[3];
		}
		
		else {
			// CASE: Message heard is from opponent
			if (sep[3].equals("opp")) {
				hear[0] = sep[3];
				hear[1] = sep[4];
			}
			// CASE: Message heard is from teammate
			else {
				hear[0] = sep[3];
				hear[1] = sep[5];
			}
		}
		

		hearInfo.add(hear);
	}

	/**
	 * @return the items seen
	 */
	public ArrayList<Tuple> getItemsInView() {
		return itemsInView;
	}

	/**
	 * @return body information
	 */
	public ArrayList<Tuple> getBodySensorInfo() {
		return bodySensorInfo;
	}

	/**
	 * @return the messages that were heard
	 */
	public ArrayList<String[]> getHearInfo() {
		return hearInfo;
	}

	/**
	 * @return the time this local view represents
	 */
	public int getTime() {
		return time;
	}

	/**
	 * @return true if visual and body sensor information have been filled in, false otherwise
	 */
	public boolean allInfoReady(){
		return (sightReady && bodyReady);
	}

}
