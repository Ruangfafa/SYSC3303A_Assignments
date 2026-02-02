package model;

import java.util.ArrayList;
import java.util.List;

/**
 * The GameState class maintains the global state of a simple Battle Royale
 * environment, including lists of players and loot boxes. It provides methods
 * for adding players, moving them, processing loot pickups, and serializing
 * the entire game state for transmission.
 */
public class GameState {

    /** A list of all players currently in the game. */
    private List<Player> players;

    /** A list of all loot boxes currently placed in the game world. */
    private List<LootBox> lootBoxes;

    /** Used to assign unique IDs to new players. */
    private int nextPlayerId = 100;

    /** Used to assign unique IDs to new loot boxes. */
    private int nextLootBoxId = 200;

    /**
     * Constructs a new GameState with empty lists of players and loot boxes,
     * plus a couple of default loot boxes for demonstration.
     */
    public GameState() {
        players = new ArrayList<>();
        lootBoxes = new ArrayList<>();

        // Add some default loot boxes as an example.
        lootBoxes.add(new LootBox(nextLootBoxId++, 5, 5, "HealthPack", 1));
        lootBoxes.add(new LootBox(nextLootBoxId++, 10, 2, "Ammo", 5));
    }

    /**
     * Creates and adds a new player to the game state.
     *
     * @param name The name of the new player.
     * @return A Player object representing the newly added player.
     */
    public Player addNewPlayer(String name) {
        Player p = new Player(nextPlayerId++, 0, 0, 100, name);
        players.add(p);
        return p;
    }

    /**
     * Processes a request for a player to pick up a loot box, if they are
     * standing at the same position as the box.
     *
     * @param playerId The unique ID of the player attempting the pickup.
     * @param lootId   The unique ID of the loot box being picked up.
     * @return true if the pickup was successful, false otherwise.
     */
    public boolean processPickup(int playerId, int lootId) {
        Player player = getPlayerById(playerId);
        LootBox box = getLootBoxById(lootId);
        if (player == null || box == null) {
            return false;
        }
        // Check distance (e.g., if same position, allow pickup)
        if (player.getX() == box.getX() && player.getY() == box.getY()) {
            // Example: picking up a HealthPack increases HP
            if (box.getType().equalsIgnoreCase("HealthPack")) {
                player.setHealth(player.getHealth() + 20);
            }
            // Remove the loot from the game
            lootBoxes.remove(box);
            return true;
        }
        return false;
    }

    /**
     * Moves a player in the game state by adjusting their x and y coordinates.
     *
     * @param playerId The unique ID of the player to move.
     * @param dx       The change in the x-direction.
     * @param dy       The change in the y-direction.
     */
    public void movePlayer(int playerId, int dx, int dy) {
        Player p = getPlayerById(playerId);
        if (p != null) {
            p.setX(p.getX() + dx);
            p.setY(p.getY() + dy);
        }
    }

    /**
     * Converts the current game state (players and loot boxes) into a
     * simplified JSON-like string for transmission or debugging.
     *
     * Example format:
     * <pre>
     * PLAYERS=[(id,x,y,health,name),...];LOOT=[(id,x,y,type,quantity),...]
     * </pre>
     *
     * @return A string representing the serialized game state.
     */
    public String serialize() {
        StringBuilder sb = new StringBuilder();
        sb.append("PLAYERS=[");
        for (Player p : players) {
            sb.append("(")
                    .append(p.getId()).append(",")
                    .append(p.getX()).append(",")
                    .append(p.getY()).append(",")
                    .append(p.getHealth()).append(",")
                    .append(p.getName())
                    .append("),");
        }
        sb.append("];");

        sb.append("LOOT=[");
        for (LootBox lb : lootBoxes) {
            sb.append("(")
                    .append(lb.getId()).append(",")
                    .append(lb.getX()).append(",")
                    .append(lb.getY()).append(",")
                    .append(lb.getType()).append(",")
                    .append(lb.getQuantity())
                    .append("),");
        }
        sb.append("]");

        return sb.toString();
    }

    /**
     * Finds a player by their unique ID.
     *
     * @param id The ID of the player to find.
     * @return The matching Player object, or null if none is found.
     */
    private Player getPlayerById(int id) {
        for (Player p : players) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    /**
     * Finds a loot box by its unique ID.
     *
     * @param id The ID of the loot box to find.
     * @return The matching LootBox object, or null if none is found.
     */
    private LootBox getLootBoxById(int id) {
        for (LootBox lb : lootBoxes) {
            if (lb.getId() == id) {
                return lb;
            }
        }
        return null;
    }
}