import common.ConfigLoader;
import service.LoggerService;
import service.UdpSocket;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.logging.Logger;

import static util.DiagramPacket.getPacket;

/**
 * Listens on a fixed UDP port and forwards packets between the client and the server.
 */
public class IntermediateHost {
    private static final Logger logger = LoggerService.getLogger();
    private static final InetAddress SERVER_ADDRESS = ConfigLoader.getInstance().getServerReceiveAddress();
    private static final int SERVER_PORT = ConfigLoader.getInstance().getServerReceivePort();
    private static InetAddress clientAddress = null;
    private static int clientPort = 0;

    /**
     * Entry point of the intermediate host.
     *
     * @param args command-line arguments, not used
     */
    public static void main(String[] args) {
        logger.info("IntermediateHost start");
        int receivePort = ConfigLoader.getInstance().getIntermediateReceivePort();
        UdpSocket udpSocket = new UdpSocket(receivePort);
        System.out.println("Battle Royale Host started on port " + receivePort + "\n");

        while (true) {
            DatagramPacket packet = udpSocket.receive();
            if (packet == null) continue;

            InetAddress resAddress = packet.getAddress();
            int resPort = packet.getPort();

            String receive = new String(packet.getData(), 0, packet.getLength());
            String[] splitReceive = receive.split(":");

            if (splitReceive[0].equals("SHUTDOWN")) { //Not in used in server yet, but leave here for fun
                logger.info("IntermediateHost shutdown");
                System.out.println("IntermediateHost closed.");
                System.exit(0);
            }

            int toPort;
            InetAddress toAddress;
            if (resAddress.equals(SERVER_ADDRESS) && resPort == SERVER_PORT){ //if get from server
                logger.info("Received: " + Arrays.toString(splitReceive) + ", from Server(" + resAddress + ":" + resPort + ")");

                System.out.println("Host: received:\n" +
                        "From server: " + resAddress + "\n" +
                        "From server port: " + resPort + "\n" +
                        "Length: " + packet.getLength() + "\n" +
                        "Containing: " + receive + Arrays.toString(packet.getData()) + "\n");

                toAddress = clientAddress;
                toPort = clientPort;

                System.out.println("Host: sending response:\n" +
                        "To client: " + toAddress + "\n" +
                        "To client port: " + toPort + "\n" +
                        "Length: " + packet.getLength() + "\n" +
                        "Containing: " + receive + Arrays.toString(packet.getData()) + "\n");
            }
            else {//if get from client
                clientAddress = packet.getAddress();
                clientPort = packet.getPort();
                logger.info("Received: " + Arrays.toString(splitReceive) + ", from Client(" + resAddress + ":" + resPort + ")");

                System.out.println("Host: received:\n" +
                        "From client: " + resAddress + "\n" +
                        "From client port: " + resPort + "\n" +
                        "Length: " + packet.getLength() + "\n" +
                        "Containing: " + receive + Arrays.toString(packet.getData()) + "\n");

                toAddress = SERVER_ADDRESS;
                toPort = SERVER_PORT;

                System.out.println("Host: forwarded:\n" +
                        "To server: local" + toAddress + "\n" +
                        "To server port: " + toPort + "\n" +
                        "Length: " + packet.getLength() + "\n" +
                        "Containing: " + receive + Arrays.toString(packet.getData()) + "\n");
            }

            DatagramPacket intermediatePacket = getPacket(receive, toAddress, toPort);
            udpSocket.send(intermediatePacket);
        }
    }
}
