import common.ConfigLoader;
import common.enums.CommandType;
import common.enums.RespondType;
import service.LoggerService;
import service.UdpSocket;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Logger;

import static java.lang.Thread.sleep;
import static util.DiagramPacket.getPacket;

public class Client {
    private static final Logger logger = LoggerService.getLogger();
    private static final InetAddress INTERMEDIATE_ADDRESS = ConfigLoader.getInstance().getIntermediateReceiveAddress();
    private static final int INTERMEDIATE_PORT = ConfigLoader.getInstance().getIntermediateReceivePort();
    private static final Scanner sc = new Scanner(System.in);
    private static int playerId;
    private static DatagramPacket packet;
    private static final UdpSocket udpSocket = new UdpSocket();
    private static String request;

    public static void main(String[] args) {
        logger.info("Client Start");

        playerJoin();

        while (true) {
            String command = nextRequest();
            if (command == null) continue;
            if (command.equals("QUIT")) {
                System.out.println("Client closed.");
                System.exit(0);
            }

            packet = getPacket(command, INTERMEDIATE_ADDRESS, INTERMEDIATE_PORT);
            udpSocket.send(packet);


            System.out.println("\nClient: sent: \n" +
                    "To host: local" + INTERMEDIATE_ADDRESS + "\n" +
                    "To host prot: " + INTERMEDIATE_PORT + "\n" +
                    "Length: " + packet.getLength() + "\n" +
                    "Containing: " + command + "\n");

            packet = udpSocket.receive();
            processReceive();
        }
    }

    private static String nextRequest() {
        request = sc.nextLine();
        String[] splitRequest = request.split(" ");
        CommandType commandType = CommandType.fromString(splitRequest[0]);
        switch (commandType) {
            case MOVE ->
            {
                if (splitRequest.length < 3) {
                    logger.warning("MOVE command missing arguments");
                    return null;
                }
                return "MOVE:" + playerId + ":" + splitRequest[1] + ":" + splitRequest[2];
            }
            case PICKUP ->
            {
                if (splitRequest.length < 2) {
                    logger.warning("PICKUP command missing arguments");
                    return null;
                }
                return "PICKUP:" + playerId + ":" + splitRequest[1];
            }
            case STATE, QUIT -> {
                return commandType.toString();
            }
            case null, default -> {return null;}
        }
    }

    private static void playerJoin() {
        System.out.print("Client started. Socket on random port.\n" +
                "Enter your player name: ");
        String request = "JOIN:" + sc.nextLine();

        packet = getPacket(request,INTERMEDIATE_ADDRESS, INTERMEDIATE_PORT);
        udpSocket.send(packet);

        System.out.println("\nClient: sent: \n" +
                "To host: local" + INTERMEDIATE_ADDRESS + "\n" +
                "To host prot: " + INTERMEDIATE_PORT + "\n" +
                "Length: " + packet.getLength() + "\n" +
                "Containing: " + request + "\n");

        packet = udpSocket.receive();
        processReceive();
    }

    private static void processReceive() {
        String receive = new String(packet.getData(), 0, packet.getLength());
        String[] splitReceive = receive.split(":");
        logger.info("Received: " + Arrays.toString(splitReceive) + ", from Server(" + request + ")");
        System.out.println("Client: received: \n" +
                "From host: " + packet.getAddress() + "\n" +
                "From host port: " + packet.getPort() + "\n" +
                "Length: " + packet.getLength() + "\n" +
                "Containing: " + receive);
        RespondType respondType = RespondType.valueOf(splitReceive[0]);
        switch (respondType) {
            case JOINED ->  {
                if (splitReceive.length < 2) {
                    logger.warning("JOIN command missing arguments");
                    return;
                }
                playerId =  Integer.parseInt(splitReceive[1]);
                System.out.println("Joined game with playerId = " + playerId);
            }

            case MOVE_OK ->
                System.out.println("Server: MOVE_OK");

            case PICKUP_OK ->
                System.out.println("Server: PICKUP_OK");

            case PICKUP_FAIL ->
                System.out.println("Server: PICKUP_FAIL");

            case STATE ->
                System.out.println("Game State: " + splitReceive[1]);
        }
        System.out.println("Commands: MOVE dx dy | PICKUP lootId | STATE | QUIT");
    }
}
