package model;

/**
 * The LootBox class represents a single loot box in the game world.
 * Loot boxes contain items or resources (e.g., health packs, ammunition)
 * that players can pick up.
 */
public class LootBox {

    /** The unique ID of this loot box. */
    private int id;

    /** The x-coordinate of the loot box's location. */
    private int x;

    /** The y-coordinate of the loot box's location. */
    private int y;

    /** The type of item contained in this loot box (e.g., "HealthPack", "Ammo"). */
    private String type;

    /** The quantity or amount of the resource contained in this loot box. */
    private int quantity;

    /**
     * Constructs a new LootBox with the specified properties.
     *
     * @param id       The unique ID for this loot box.
     * @param x        The x-coordinate of its position.
     * @param y        The y-coordinate of its position.
     * @param type     A string describing the type of item (e.g., "HealthPack").
     * @param quantity The amount or quantity of the item stored.
     */
    public LootBox(int id, int x, int y, String type, int quantity) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.type = type;
        this.quantity = quantity;
    }

    /**
     * Gets the unique ID of this loot box.
     *
     * @return An integer representing the loot box ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the x-coordinate of this loot box.
     *
     * @return The x-coordinate as an integer.
     */
    public int getX() {
        return x;
    }

    /**
     * Gets the y-coordinate of this loot box.
     *
     * @return The y-coordinate as an integer.
     */
    public int getY() {
        return y;
    }

    /**
     * Gets the type of item contained in this loot box.
     *
     * @return A string representing the item type (e.g., "HealthPack").
     */
    public String getType() {
        return type;
    }

    /**
     * Gets the quantity of the item contained in this loot box.
     *
     * @return The quantity of the item as an integer.
     */
    public int getQuantity() {
        return quantity;
    }
}
