package common.enums;

public enum CommandType {
    JOIN,
    MOVE,
    PICKUP,
    STATE,
    QUIT;

    public static CommandType fromString(String s) {
        try {
            return CommandType.valueOf(s.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
