package top.icss.protocol;

/**
 * @author cd.wang
 * @create 2022-04-01 9:40
 */
public enum MessageType {
    AUTH_RES((byte)0),
    AUTH((byte)1),
    KEEPALIVE((byte)2),
    CONNECTED((byte)3),
    DISCONNECTED((byte)4),
    DATA((byte)5);

    private byte code;

    MessageType(byte code) {
        this.code = code;
    }

    public byte getCode() {
        return code;
    }

    public static MessageType valueOf(byte code) throws Exception {
        for (MessageType item : MessageType.values()) {
            if (item.code == code) {
                return item;
            }
        }
        throw new Exception("MessageType code error: " + code);
    }
}
