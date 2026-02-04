package common.enums;

public enum RespondType {
    JOINED,
    MOVE_OK,
    PICKUP_OK,
    PICKUP_FAIL,
    STATE,
    BYE,
    ERROR;

    public static CommandType fromString(String s) {
        try {
            return CommandType.valueOf(s.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
