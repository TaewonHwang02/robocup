package common;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * Non thread option for processing information from the server, where we have the read method to be used by the player, in a while loop
 */
public class FromServer2{

    private final GlobalMap pitch;
    private final DatagramSocket clientSocket;

    public FromServer2 (GlobalMap pitch, DatagramSocket clientSocket) {
        this.pitch = pitch;
        this.clientSocket = clientSocket;
    }

    /**
     * Receive the information from the server and parse it
     */
    public void read() {
        if (clientSocket != null) {
            LocalView view = new LocalView();

            byte[] receiveData = new byte[1024];
            while (!view.allInfoReady()) {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                try {
                    clientSocket.receive(receivePacket);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                String modifiedSentence = new String(receivePacket.getData());
                if (modifiedSentence.startsWith("(see")) {
                    view.parseSee(modifiedSentence);
                }
                else if (modifiedSentence.startsWith("(sense_body")){
                    view.parseBodySense(modifiedSentence);
                }
                else if (modifiedSentence.startsWith("(hear")){
                    //System.out.println(modifiedSentence);
                    /*
                        NOTE: Hearing does not necessarily happen at every single cycle,
                            meaning that if the server sends a see, then a sense, and finally a hear,
                            then the hear will only be processed during the next cycle.
                    */
                    view.parseHear(modifiedSentence);
                }
            }
            pitch.update(view);
        }
    }
}