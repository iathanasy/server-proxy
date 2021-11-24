package top.icss.enums;

/**
 * @author cd.wang
 * @create 2021-11-22 15:48
 */
public enum EnumMessageType {
    AUTH(1),
    KEEPALIVE(2),
    CONNECTED(3),
    DISCONNECTED(4),
    DATA(5);

    private int code;

    EnumMessageType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static EnumMessageType valueOf(int code) throws Exception {
        for (EnumMessageType item : EnumMessageType.values()) {
            if (item.code == code) {
                return item;
            }
        }
        throw new Exception("MessageType code error: " + code);
    }
}
