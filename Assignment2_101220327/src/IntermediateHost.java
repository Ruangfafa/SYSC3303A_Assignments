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

    public static void main(String[] args) {
        logger.info("IntermediateHost start");
        int receivePort = ConfigLoader.getInstance().getIntermediateReceivePort();
        InetAddress clientAddress = null, toAddress;
        int clientPort = 0, toPort;
        UdpSocket receiveSocket = new UdpSocket(receivePort);
        UdpSocket sendSocket = new UdpSocket();

        while (true) {
            DatagramPacket packet = receiveSocket.receive();
            if (packet == null) continue;

            InetAddress resAddress = packet.getAddress();
            int resPort = packet.getPort();

            String receive = new String(packet.getData(), 0, packet.getLength());
            String[] splitReceive = receive.split(":");

            if (resAddress.equals(SERVER_ADDRESS) && resPort == SERVER_PORT){
                logger.info("Received: " + Arrays.toString(splitReceive) + ", from Server(" + resAddress + ":" + resPort + ")");

                toAddress = clientAddress;
                toPort = clientPort;
            }
            else {
                clientAddress = packet.getAddress();
                clientPort = packet.getPort();
                logger.info("Received: " + Arrays.toString(splitReceive) + ", from Client(" + resAddress + ":" + resPort + ")");

                toAddress = SERVER_ADDRESS;
                toPort = SERVER_PORT;
            }

            DatagramPacket intermediatePacket = getPacket(receive, toAddress, toPort);
            sendSocket.send(intermediatePacket);
        }
    }
}
