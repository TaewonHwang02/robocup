package common;

import common.players.Player;

import java.awt.geom.Point2D;
import java.util.Hashtable;

/**
 * Holds some static read-only information and provides math-related functions.
 */
public class PlayerMath {

    private static final Hashtable<String, Point2D.Double> flags = initFlagHash();

    /**
     * Finds position of the player based on the two flags passed as input
     * Source of formula : https://www.101computing.net/cell-phone-trilateration-algorithm/
     * @param item1 first flag
     * @param item2 second flag
     */
    public static double[] locatePlayer(Tuple item1, Tuple item2){

        //System.out.println("flag1: " + item1.label + ", flag2: " + item2.label);
        double dist1 = item1.getIParams()[1];
        double dist2 = item2.getIParams()[1];
        double dir1 = item1.getIParams()[2];
        double dir2 = item2.getIParams()[2];

        //grab flags from hashtable of flag coordinates
        Point2D.Double point1 = flags.get(item1.getLabel().toLowerCase());
        Point2D.Double point2 = flags.get(item2.getLabel().toLowerCase());

        //solves for intersection of circles
        //finds midpoint between circle centers
        double dx = point1.getX() - point2.getX();
        double dy = point1.getY() - point2.getY();

        //distance between circle centers
        double r = Math.sqrt(dx * dx + dy * dy);
        double r2 = r*r;
        double r4 = r2*r2;

        double i = (dist1 * dist1 - dist2 * dist2) / (2 * r2);
        double r2r2 = (dist1 * dist1 - dist2 * dist2);

        double j = Math.sqrt(2 * (dist1 * dist1 + dist2 * dist2) / r2 - (r2r2 * r2r2) / r4 - 1);

        double fx = (point1.getX()+point2.getX()) / 2 + i * (point2.getX() - point1.getX());
        double gx = j * (point2.getY() - point1.getY()) / 2;
        double fy = (point1.getY()+point2.getY()) / 2 + i * (point2.getY() - point1.getY());
        double gy = j * (point1.getX() - point2.getX()) / 2;

        /*
            The calculations above sometimes result in NaN. It is unclear why these occur,
                since we always do floating point division and it is almost impossible
                for anything here to evaluate to exactly zero. This code can either be left
                as-is, or the function could be modified to use some other calculations.
        */
        if (Double.isNaN(fx) || Double.isNaN(gx) || Double.isNaN(fy) || Double.isNaN(gy)){
            return null;
        }
        double[] iparams = new double[3];

        //choose proper intersection dependent on which flag is to the left/right
        if (dir1 < dir2) {
            iparams[0] = fx - gx;
            iparams[1] = fy - gy;
        } else {
            iparams[0] = fx + gx;
            iparams[1] = fy + gy;
        }

        // calculate absolute direction using one of the flags
        double theta = Math.abs(Math.toDegrees(
                Math.atan((point1.getY() - iparams[1]) / (point1.getX() - iparams[0]))));

        if (point1.x - iparams[0] > 0){
            if (point1.y - iparams[1] > 0){
                iparams[2] = theta - dir1;
            }
            else {
                iparams[2] = - theta - dir1;
            }

        }
        else {

            if (point1.y - iparams[1] > 0){
                iparams[2] = 180 - theta - dir1;
            }
            else {
                iparams[2] = -180 + theta - dir1; // notice that this is -(180 - theta) - dir1
            }
        }

        if (iparams[2] < -180){
            iparams[2] = 360 + iparams[2];
        }
        else if (iparams[2] > 180){
            iparams[2] = -360 + iparams[2];
        }

        //System.out.println("x: " + iparams[0] + " y: " + iparams[1] + " dir: " + iparams[2]);
        return iparams;
    }

    /**
     * Determine how much power we should use in our dash to move some distance, given the stamina we have.
     * The calculations are taken from the manual, section 4.4: https://rcsoccersim.readthedocs.io/en/latest/soccerserver.html#movement-models
     * @param dist the distance we want to move
     * @param stamina how much stamina the player currently has
     */
    public static double findPowerToMove(double dist, double stamina){
        // TODO: make this function take into account stamina, effort, capacity
        return dist / 0.006;
    }

    /**
     * Get the angle of a point with respect to the center of a player's field of view.
     * This function is equivalent to getDirection. If this function appears to return incorrect angles, try
     * using the other one.
     * @param playerPos Tuple with the x position, y position, and absolute direction of the player
     * @param point_x x position of the point
     * @param point_y y position of the point
     */
    public static double findAngleWithPoint(Tuple playerPos, double point_x, double point_y){
        double theta = Math.abs(Math.toDegrees(
                Math.atan((playerPos.iParams[1] - point_y) / (playerPos.iParams[0] - point_x))));

        double ret_angle;
        if (point_x - playerPos.iParams[0] > 0){
            if (point_y - playerPos.iParams[1] > 0){
                ret_angle = theta - playerPos.iParams[2];
            }
            else {
                ret_angle = - theta - playerPos.iParams[2];
            }

        }
        else {
            if (point_y - playerPos.iParams[1] > 0){
                ret_angle = 180 - theta - playerPos.iParams[2];
            }
            else {
                ret_angle = -180 + theta - playerPos.iParams[2]; // notice that this is -(180 - theta) - dir1
            }
        }

        if (ret_angle > 180){
            return ret_angle - 360;
        }
        else if (ret_angle < -180){
            return ret_angle + 360;
        }
        return ret_angle;

    }

    /**
     * Calculates the Euclidean distance between a player's position and a given point.
     * @param playerPos The current position of the player.
     * @param point_x The x-coordinate of the target point.
     * @param point_y The y-coordinate of the target point.
     * @return The distance between the player's position and the point.
     */
    public static double findDistanceWithPoint(Tuple playerPos, double point_x, double point_y){
        double diffX = playerPos.getIParams()[0] - point_x;
        double diffY = playerPos.getIParams()[1] - point_y;
        return Math.sqrt(diffX * diffX + diffY * diffY);
    }

    /**
     * Get the direction from the player to a point, to be able to turn towards the position.
     * This function is equivalent to findAngleWithPoint. If this function appears to return incorrect angles, try
     * using the other one.
     * @param player the player
     * @param x x position of the point
     * @param y y position of the point
     */
    public static double getDirection(Player player, double x, double y){
        double x2 = player.getPitch().getPlayerPos().iParams[0];
        double y2 = player.getPitch().getPlayerPos().iParams[1];
        return Math.toDegrees(Math.atan((y-y2)/(x-x2))) - player.getPitch().getPlayerPos().iParams[2];
    }

    /**
     * Get the absolute poistion of opposing goal.
     * @param player
     * @return point_x and point_y of opposing goal
     */
    public static Point2D.Double getOppoGoal(Player player){
        if (player.getSide().equals("l")){
            return flags.get("gr");
        }
        return flags.get("gl");
    }

    /**
     * Get the absolute poistion of team goal.
     * @param player
     * @return point_x and point_y of team goal
     */
    public static Point2D.Double getTeamGoal(Player player) {
        if (player.getSide().equals("l")){
            return flags.get("gl");
        }
        return flags.get("gr");
    }

    /**
     * @return the starting position of the player (the position it should teleport to)
     */
    public static int[] getStartPosition(int i, String side){
        int [] x = switch (i) {
            case 0 -> new int[]{-50, 0};
            case 1 -> new int[]{-10, 0};
            case 2 -> new int[]{-20, -20};
            case 3 -> new int[]{-20, 0};
            case 4 -> new int[]{-20, 20};
            case 5 -> new int[]{-30, -10};
            case 6 -> new int[]{-30, 10};
            case 7 -> new int[]{-30, 30};
            case 8 -> new int[]{-30, -30};
            case 9 -> new int[]{-1, -10};
            case 10 -> new int[]{-1, 10};
            default -> new int[2];
        };

        if (side.equals("r")){
            x[1] = -x[1];
        }
        return x;
    }

    /**
     * generates hashtable of flags. The values are based off the manual (section 4.3.2):
     * https://rcsoccersim.readthedocs.io/en/latest/soccerserver.html#vision-sensor-model
     * Note that (0,0) is the center of the field, x increases to the right and y increases downwards
     */
    private static Hashtable<String, Point2D.Double> initFlagHash() {
        Hashtable<String, Point2D.Double> flags = new Hashtable<>(51);

        // flags on the perimeter of the field
        flags.put("ftl50", new Point2D.Double(-50, -39));
        flags.put("ftl40", new Point2D.Double(-40, -39));
        flags.put("ftl30", new Point2D.Double(-30, -39));
        flags.put("ftl20", new Point2D.Double(-20, -39));
        flags.put("ftl10", new Point2D.Double(-10, -39));
        flags.put("ft0", new Point2D.Double(0, -39));
        flags.put("ftr10", new Point2D.Double(10, -39));
        flags.put("ftr20", new Point2D.Double(20, -39));
        flags.put("ftr30", new Point2D.Double(30, -39));
        flags.put("ftr40", new Point2D.Double(40, -39));
        flags.put("ftr50", new Point2D.Double(50, -39));
        flags.put("fbl50", new Point2D.Double(-50, 39));
        flags.put("fbl40", new Point2D.Double(-40, 39));
        flags.put("fbl30", new Point2D.Double(-30, 39));
        flags.put("fbl20", new Point2D.Double(-20, 39));
        flags.put("fbl10", new Point2D.Double(-10, 39));
        flags.put("fb0", new Point2D.Double(0, 39));
        flags.put("fbr10", new Point2D.Double(10, 39));
        flags.put("fbr20", new Point2D.Double(20, 39));
        flags.put("fbr30", new Point2D.Double(30, 39));
        flags.put("fbr40", new Point2D.Double(40, 39));
        flags.put("fbr50", new Point2D.Double(50, 39));
        flags.put("frt30", new Point2D.Double(57.5, -30));
        flags.put("frt20", new Point2D.Double(57.5, -20));
        flags.put("frt10", new Point2D.Double(57.5, -10));
        flags.put("fr0", new Point2D.Double(57.5, 0));
        flags.put("frb10", new Point2D.Double(57.5, 10));
        flags.put("frb20", new Point2D.Double(57.5, 20));
        flags.put("frb30", new Point2D.Double(57.5, 30));
        flags.put("flt30", new Point2D.Double(-57.5, -30));
        flags.put("flt20", new Point2D.Double(-57.5, -20));
        flags.put("flt10", new Point2D.Double(-57.5, -10));
        flags.put("fl0", new Point2D.Double(-57.5, 0));
        flags.put("flb10", new Point2D.Double(-57.5, 10));
        flags.put("flb20", new Point2D.Double(-57.5, 20));
        flags.put("flb30", new Point2D.Double(-57.5, 30));

        // flags on the center line
        flags.put("fct", new Point2D.Double(0, -34));
        flags.put("fc", new Point2D.Double(0, 0));
        flags.put("fcb", new Point2D.Double(0, 34));

        // flags for the left goal
        flags.put("fglt", new Point2D.Double(-52.5, -7.01));
        flags.put("gl", new Point2D.Double(-52.5, 0));
        flags.put("fglb", new Point2D.Double(-52.5, 7.01));

        // flags for the left penalty area
        flags.put("fplt", new Point2D.Double(-52.5 + 16.5, -20.16));
        flags.put("fplc", new Point2D.Double(-52.5 + 16.5, 0));
        flags.put("fplb", new Point2D.Double(-52.5 + 16.5, 20.16));

        // flags for left corners of the field
        flags.put("flt", new Point2D.Double(-52.5, -34));
        flags.put("flb", new Point2D.Double(-52.5, 34));

        // flags for the right goal
        flags.put("fgrt", new Point2D.Double(52.5, -7.01));
        flags.put("gr", new Point2D.Double(52.5, 0));
        flags.put("fgrb", new Point2D.Double(52.5, 7.01));

        // flags for the right penalty area
        flags.put("fprt", new Point2D.Double(52.5 - 16.5, -20.16));
        flags.put("fprc", new Point2D.Double(52.5 - 16.5, 0));
        flags.put("fprb", new Point2D.Double(52.5 - 16.5, 20.16));

        // flags for the right corners of the field
        flags.put("frt", new Point2D.Double(52.5, -34));
        flags.put("frb", new Point2D.Double(52.5, 34));

        return flags;
    }

    /**
     * update isClosest flag for closest player
     */
    public static void updateClosestPlayerToBall(Player player) {

        Player closestPlayer = null;
        double minDistance = Double.MAX_VALUE;

        Tuple[] balls_polar;
        double distance;

        for (Player agent : player.getTeam().getPlayers()) {
            if (agent != null && agent.getBallsPolar()[1] != null) {
                balls_polar = agent.getBallsPolar();
                distance = balls_polar[1].iParams[1];

                if (distance < minDistance) {
                    minDistance = distance;
                    closestPlayer = agent;
                }
            }
        }

        // update isClosest
        for (Player agent : player.getTeam().getPlayers()) {
            if (agent != null) {
                agent.setIsClosest(agent == closestPlayer);
            }
        }
    }
}
