package top.icss.test;
import lombok.Data;
import top.icss.enums.EnumMessageType;
import top.icss.packet.MessagePacket;
import top.icss.utils.JsonSerializer;

import java.util.UUID;

/**
 * @author cd.wang
 * @create 2021-11-19 10:09
 */
@Data
public class StudyPacket{
    private String id;
    private String name;
    private int age;

    public StudyPacket(String id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public StudyPacket(String name, int age) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.age = age;
    }

    public StudyPacket() {
    }

    public MessagePacket create(){
        MessagePacket packet = new MessagePacket();
        byte[] serialize = JsonSerializer.serialize(this);
        packet.setType(EnumMessageType.KEEPALIVE);
        packet.setLen(serialize.length);
        packet.setBody(serialize);
        return packet;
    }
}
