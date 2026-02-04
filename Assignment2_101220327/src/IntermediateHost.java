import common.ConfigLoader;
import service.LoggerService;
import service.UdpSocket;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.logging.Logger;

import static util.DiagramPacket.getPacket;

public class IntermediateHost {
    private static final Logger logger = LoggerService.getLogger();
    private static final InetAddress SERVER_ADDRESS = ConfigLoader.getInstance().getServerReceiveAddress();
    private static final int SERVER_PORT = ConfigLoader.getInstance().getServerReceivePort();
    private static InetAddress clientAddress = null, toAddress;
    private static int clientPort = 0, toPort;
    private static boolean intermediateHostOn = true;

    public static void main(String[] args) {
        logger.info("IntermediateHost start");
        int receivePort = ConfigLoader.getInstance().getIntermediateReceivePort();
        UdpSocket receiveSocket = new UdpSocket(receivePort);
        UdpSocket sendSocket = new UdpSocket();
        System.out.println("Battle Royale Host started on port " + receivePort + "\n");

        while (intermediateHostOn) {
            DatagramPacket packet = receiveSocket.receive();
            if (packet == null) continue;

            InetAddress resAddress = packet.getAddress();
            int resPort = packet.getPort();

            String receive = new String(packet.getData(), 0, packet.getLength());
            String[] splitReceive = receive.split(":");

            if (resAddress.equals(SERVER_ADDRESS) && resPort == SERVER_PORT){
                logger.info("Received: " + Arrays.toString(splitReceive) + ", from Server(" + resAddress + ":" + resPort + ")");

                System.out.println("Host: received:\n" +
                        "From server: " + resAddress + "\n" +
                        "From server port: " + resPort + "\n" +
                        "Length: " + packet.getLength() + "\n" +
                        "Containing: " + receive + "\n");

                toAddress = clientAddress;
                toPort = clientPort;

                System.out.println("Host: sending response:\n" +
                        "To client: " + toAddress + "\n" +
                        "To client port: " + toPort + "\n" +
                        "Length: " + packet.getLength() + "\n" +
                        "Containing: " + receive + "\n");
            }
            else {
                clientAddress = packet.getAddress();
                clientPort = packet.getPort();
                logger.info("Received: " + Arrays.toString(splitReceive) + ", from Client(" + resAddress + ":" + resPort + ")");

                System.out.println("Host: received:\n" +
                        "From client: " + resAddress + "\n" +
                        "From client port: " + resPort + "\n" +
                        "Length: " + packet.getLength() + "\n" +
                        "Containing: " + receive + "\n");

                toAddress = SERVER_ADDRESS;
                toPort = SERVER_PORT;

                System.out.println("Host: forwarded:\n" +
                        "To server: local" + toAddress + "\n" +
                        "To server port: " + toPort + "\n" +
                        "Length: " + packet.getLength() + "\n" +
                        "Containing: " + receive + "\n");
            }

            DatagramPacket intermediatePacket = getPacket(receive, toAddress, toPort);
            sendSocket.send(intermediatePacket);
        }
    }
}
