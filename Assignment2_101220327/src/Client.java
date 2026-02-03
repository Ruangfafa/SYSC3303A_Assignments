import common.ConfigLoader;
import service.LoggerService;
import service.UdpSocket;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;

import static common.CSVReader.readFireEvents;
import static java.lang.Thread.sleep;
import static util.DiagramPacket.getPacket;

public class Client {
    private static final Logger logger = LoggerService.getLogger();
    private static final InetAddress INTERMEDIATE_ADDRESS = ConfigLoader.getInstance().getIntermediateReceiveAddress();
    private static final int INTERMEDIATE_PORT = ConfigLoader.getInstance().getIntermediateReceivePort();

    public static void main(String[] args) {
        logger.info("Client Start");
        ArrayList<String> requests = readFireEvents("sample_client_input.csv");
        UdpSocket udpSocket = new UdpSocket();
        DatagramPacket packet;

        while (!requests.isEmpty()) {
            String request = requests.removeFirst();

            packet = getPacket(request,INTERMEDIATE_ADDRESS, INTERMEDIATE_PORT);
            udpSocket.send(packet);

            packet = udpSocket.receive();
            String[] receive = new String(packet.getData(),0,packet.getLength()).split(":");
            logger.info("Received: " + Arrays.toString(receive) + ", from Server(" + request + ")");

            processCommand(receive);
        }
    }

    private static void processCommand(String[] receive) {
        try {
            sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
