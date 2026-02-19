import common.ConfigLoader;
import common.enums.CommandType;
import model.GameState;
import service.LoggerService;
import service.UdpSocket;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.logging.Logger;

import static util.DiagramPacket.getPacket;

/**
 *The server receives UDP packets, parses commands, updates the game state, and sends responses back through the intermediate host.
 */
public class Server {
    private static final Logger logger = LoggerService.getLogger();
    private static final InetAddress INTERMEDIATE_ADDRESS = ConfigLoader.getInstance().getIntermediateReceiveAddress();
    private static final int INTERMEDIATE_PORT = ConfigLoader.getInstance().getIntermediateReceivePort();

    /**
     * Entry point of the server application.
     *
     * @param args command-line arguments, not used
     */
    public static void main(String[] args) {
        logger.info("Server start");
        int port = ConfigLoader.getInstance().getServerReceivePort();
        UdpSocket udpSocket = new UdpSocket(port);
        GameState gameState = new GameState();
        System.out.println("Battle Royale Server started on port " + port + "\n");

        while (true) {
            DatagramPacket intermediatePacket = udpSocket.receive();
            if (intermediatePacket == null) continue;

            InetAddress intermediateAddress = intermediatePacket.getAddress();
            int intermediatePort = intermediatePacket.getPort();
            String receive = new String(intermediatePacket.getData(),0,intermediatePacket.getLength());
            String[] splitReceive = receive.split(":");

            logger.info("Received: " + Arrays.toString(splitReceive) + "From: [Intermediate: " + intermediateAddress + ":" + intermediatePort + "]");
            System.out.println("Server: received:\n" +
                    "From host: " + intermediateAddress + "\n" +
                    "From host port: " + intermediatePort + "\n" +
                    "Length: " + intermediatePacket.getLength() + "\n" +
                    "Containing: " + receive + Arrays.toString(intermediatePacket.getData()) + "\n");

            String response = processCommand(gameState, splitReceive);

            DatagramPacket respondIntermediatePacket = getPacket(response, INTERMEDIATE_ADDRESS, INTERMEDIATE_PORT);
            System.out.println("Server: sending response:\n" +
                    "To host: " + INTERMEDIATE_ADDRESS + "\n" +
                    "To host port: " + INTERMEDIATE_PORT + "\n" +
                    "Length: " + respondIntermediatePacket.getLength() + "\n" +
                    "Containing: " + response + Arrays.toString(respondIntermediatePacket.getData()) + "\n");

            udpSocket.sendBack(respondIntermediatePacket);
        }
    }

    /**
     * Processes a single command received from a client.
     * This method parses the command type, validates arguments, updates the game state when necessary, and returns a protocol-compliant response string.
     *
     * @param gameState the shared game state instance
     * @param receive the split command received from the client
     * @return response string to be sent back to the client
     */
    private static String processCommand(GameState gameState, String[] receive) {
        if (receive.length == 0 || receive[0].isEmpty()) {
            return "ERROR:UNSUPPORTED_COMMAND";
        }

        CommandType commandType = CommandType.fromString(receive[0]);
        switch (commandType) {
            case JOIN -> {
                if (receive.length < 2) {
                    logger.warning("JOIN command missing arguments");
                    return "ERROR:BAD_JOIN";
                }

                String name = receive[1].length() > 16 ? receive[1].substring(0, 16) : receive[1];
                int id = gameState.addNewPlayer(name).getId();
                return "JOINED:" + id;
            }

            case MOVE -> {
                if (receive.length < 4) {
                    logger.warning("MOVE command missing arguments");
                    return "ERROR:BAD_MOVE";
                }

                int playerId = Integer.parseInt(receive[1]);
                int dx = Integer.parseInt(receive[2]);
                int dy = Integer.parseInt(receive[3]);

                gameState.movePlayer(playerId, dx, dy);
                return "MOVE_OK";
            }

            case PICKUP -> {
                if (receive.length < 3) {
                    logger.warning("PICKUP command missing arguments");
                    return "ERROR:BAD_PICKUP";
                }

                int playerId = Integer.parseInt(receive[1]);
                int lootId = Integer.parseInt(receive[2]);

                boolean success = gameState.processPickup(playerId, lootId);
                return success ? "PICKUP_OK" : "PICKUP_FAIL";
            }

            case STATE -> {
                return "STATE:" + gameState.serialize();
            }

            case QUIT -> {
                return "BYE";
            }

            case null, default -> {
                return "ERROR:UNSUPPORTED_COMMAND";
            }
        }
    }

}
