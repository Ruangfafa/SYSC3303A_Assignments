import common.ConfigLoader;
import service.LoggerService;
import service.Udp;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.logging.Logger;

import static util.DiagramPacket.decodePacketBytes;
import static util.DiagramPacket.getPacket;

public class IntermediateHost {
    private static final Logger logger = LoggerService.getLogger();
    private static final InetAddress SERVER_ADDRESS = ConfigLoader.getInstance().getServerReceiveAddress();
    private static final int SERVER_PORT = ConfigLoader.getInstance().getServerReceivePort();

    public static void main(String[] args) {
        logger.info("IntermediateHost start");
        int port = ConfigLoader.getInstance().getIntermediateReceivePort();
        Udp udp = new Udp(port);

        while (true) {
            DatagramPacket packet = udp.receive();
            if (packet == null) continue;

            InetAddress resAddress = packet.getAddress();
            int resPort = packet.getPort();

            if (resAddress == SERVER_ADDRESS && resPort == SERVER_PORT){
                DatagramPacket clientPacket = decodePacketBytes(packet.getData());
                String[] receive = new String(clientPacket.getData(), 0, clientPacket.getLength()).split(":");
                logger.info("Received: " + Arrays.toString(receive) + ", from Server(" + resAddress + ":" + resPort + ")");
            }
            else {
                String[] receive = new String(packet.getData(), 0, packet.getLength()).split(":");
                logger.info("Received: " + Arrays.toString(receive) + ", from Client(" + resAddress + ":" + resPort + ")");

                DatagramPacket intermediatePacket = getPacket(packet, SERVER_ADDRESS, SERVER_PORT);
                udp.send(intermediatePacket);
            }
        }
    }
}
