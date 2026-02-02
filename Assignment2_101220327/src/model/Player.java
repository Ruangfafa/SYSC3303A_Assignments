package model;

/**
 * The Player class represents a single player's state within the game,
 * including their unique ID, position (x,y), health, and a display name.
 */
public class Player {

    /** The unique ID of this player. */
    private int id;

    /** The x-coordinate of the player's position in the game world. */
    private int x;

    /** The y-coordinate of the player's position in the game world. */
    private int y;

    /** The current health of this player. */
    private int health;

    /** The display name of this player. */
    private String name;

    /**
     * Constructs a new Player with the given properties.
     *
     * @param id     The unique ID for this player.
     * @param x      The initial x-coordinate of the player.
     * @param y      The initial y-coordinate of the player.
     * @param health The initial health value of the player.
     * @param name   The display name for this player.
     */
    public Player(int id, int x, int y, int health, String name) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.health = health;
        this.name = name;
    }

    /**
     * Gets the unique ID of this player.
     *
     * @return An integer representing the player's ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the player's x-coordinate.
     *
     * @return The x-coordinate of the player's position.
     */
    public int getX() {
        return x;
    }

    /**
     * Gets the player's y-coordinate.
     *
     * @return The y-coordinate of the player's position.
     */
    public int getY() {
        return y;
    }

    /**
     * Gets the current health of this player.
     *
     * @return The player's health as an integer.
     */
    public int getHealth() {
        return health;
    }

    /**
     * Gets the display name of this player.
     *
     * @return A String representing the player's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the x-coordinate of the player's position.
     *
     * @param x The new x-coordinate.
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Sets the y-coordinate of the player's position.
     *
     * @param y The new y-coordinate.
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Updates the player's health to the specified value.
     *
     * @param health The new health value.
     */
    public void setHealth(int health) {
        this.health = health;
    }
}