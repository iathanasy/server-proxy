package top.icss.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;
import top.icss.protocol.Message;
import top.icss.serializer.Serializer;
import top.icss.serializer.SerializerFactory;

import java.util.Map;
import java.util.Objects;

/**
 * 编码器
 * @author cd.wang
 * @create 2022-04-01 9:43
 */
@Slf4j
public class MessageEncoder extends MessageToByteEncoder<Message> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        // out
        byte type = msg.getType().getCode();
        byte serializer = msg.getSerializer();
        Map<String, Object> attachment = msg.getAttachment();
        byte[] serialize = SerializerFactory.getSerializer(serializer).serializer(attachment);
        byte[] data = msg.getData();

        int len = serialize.length;
        int lenData = 0;

        if(Objects.isNull(data)) {
            data = new byte[0];
        }else {
            lenData = data.length;
        }

        int newLen = len + lenData;
        out.writeInt(newLen); // 4
        out.writeByte(type);  // 1
        out.writeByte(serializer);  // 1
        out.writeInt(lenData);// 4
        out.writeBytes(data);
        out.writeBytes(serialize);
    }
}
