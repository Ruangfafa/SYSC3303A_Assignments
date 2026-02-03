import common.ConfigLoader;
import model.GameState;
import service.LoggerService;
import service.Udp;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.logging.Logger;

import static service.ServerAlgorithm.processCommand;
import static util.DiagramPacket.decodePacketBytes;
import static util.DiagramPacket.getPacket;

public class Server {
    private static final Logger logger = LoggerService.getLogger();

    public static void main(String[] args) {
        logger.info("Server start");
        int port = ConfigLoader.getInstance().getServerReceivePort();
        Udp udp = new Udp(port);
        GameState gameState = new GameState();

        while (true) {
            DatagramPacket intermediatePacket = udp.receive();
            if (intermediatePacket == null) continue;

            InetAddress intermediateAddress = intermediatePacket.getAddress();
            int intermediatePort = intermediatePacket.getPort();
            byte[] intermediatePayloadByte = Arrays.copyOf(intermediatePacket.getData(), intermediatePacket.getLength());

            DatagramPacket clientPacket = decodePacketBytes(intermediatePayloadByte);
            InetAddress clientAddress = clientPacket.getAddress();
            int clientPort =  clientPacket.getPort();
            String[] receive = new String(clientPacket.getData(),0,clientPacket.getLength()).split(":");

            logger.info("Received: " + Arrays.toString(receive) + "From: " + clientAddress + ":" + clientPort + " [Intermediate: " + intermediateAddress + ":" + intermediatePort + "]");

            String response = processCommand(gameState, receive);

            DatagramPacket respondClientPacket = getPacket(response, clientAddress, clientPort);
            DatagramPacket respondIntermediatePacket = getPacket(respondClientPacket, intermediateAddress, intermediatePort);
            udp.send(respondIntermediatePacket);
        }
    }


}
