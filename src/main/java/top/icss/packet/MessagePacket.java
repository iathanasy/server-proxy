package top.icss.packet;

import lombok.Data;
import top.icss.enums.EnumMessageType;

import java.util.concurrent.atomic.AtomicLong;

/**
 *  byte     1字节
 *  short    2字节
 *  char     2字节（C语言中是1字节）可以存储一个汉字
 *  int      4字节
 *  long     8字节
 *  float    4字节
 *  double   8字节
 *  boolean  false/true(理论上占用1bit,1/8字节，实际处理按1byte处理)
 *
 *   协议
 *   数据长度   协议类型   opaque   数据
 *    4         4        8        N字
 * @author cd.wang
 * @create 2021-11-17 19:31
 */
@Data
public class MessagePacket {
    private static final AtomicLong REQUEST_ID = new AtomicLong(1);
    private long opaque;
    private EnumMessageType type;
    private int len;
    private byte[] body;
    private byte[] data;

    public MessagePacket() {
        this.opaque = REQUEST_ID.getAndIncrement();
    }

    @Override
    public String toString() {
        return "MessagePacket{" +
                "opaque=" + opaque +
                ", type=" + type +
                ", len=" + len +
                '}';
    }
}
