import common.ConfigLoader;
import common.enums.CommandType;
import model.GameState;
import service.LoggerService;
import service.Udp;

import java.net.DatagramPacket;
import java.util.Arrays;
import java.util.logging.Logger;

public class ServerApplication {
    private static final Logger logger = LoggerService.getLogger(ConfigLoader.getInstance().ifLogOutput());

    public static void main(String[] args) {
        logger.info("ServerApplication start");
        Udp udp = new Udp();
        GameState gameState = new GameState();

        while (true) {
            DatagramPacket packet = udp.receive();
            if (packet == null) continue;

            String[] receive = new String(packet.getData(), 0, packet.getLength()).split(":");
            logger.info("Received: " + Arrays.toString(receive));

            String response = algorithm(gameState, receive);
            udp.send(response, packet);
        }
    }

    private static String algorithm(GameState gameState, String[] receive) {
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

                String name = receive[1];
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
