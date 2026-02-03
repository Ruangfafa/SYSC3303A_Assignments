package service;

import common.enums.CommandType;
import model.GameState;

import java.util.logging.Logger;

public class ServerAlgorithm {
    private static final Logger logger = LoggerService.getLogger();

    public static String processCommand(GameState gameState, String[] receive) {
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
