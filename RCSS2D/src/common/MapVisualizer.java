package common;

import javax.swing.*;

/**
 * class for visualizing the global map in the JPanel
 */
public class MapVisualizer {

    private GlobalMap pitch;
    public JFrame f;

    /**
     * constructor, taking the global map
     * @param pitch
     */
    public MapVisualizer(GlobalMap pitch) {
    	this.pitch = pitch;
        f = new JFrame("MapVisualizer");
    }

    /**
     * creates the visualizer screen
     */
    public void createVisualizer() {
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setBounds(0, 0, 1050, 680);
        pitch.setFocusable(true);
        pitch.grabFocus();
        f.add(pitch);
        f.setVisible(true);
    }

}
