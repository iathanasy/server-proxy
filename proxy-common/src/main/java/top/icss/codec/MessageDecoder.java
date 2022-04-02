package top.icss.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;
import top.icss.protocol.Message;
import top.icss.protocol.MessageType;
import top.icss.serializer.SerializerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 解码器
 * @author cd.wang
 * @create 2022-04-01 9:49
 */
@Slf4j
public class MessageDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int readableBytes = in.readableBytes();
        if (readableBytes < 10) {
            return;
        }
        in.markReaderIndex();

        // in
        int len = in.readInt();
        byte type = in.readByte();
        byte serializer = in.readByte();
        int lenData = in.readInt();
        int newLen = len - lenData;

        byte[] data = new byte[0];
        byte[] serialize = new byte[0];

        if(in.isReadable()) {
            if (lenData > 0) {
                data = new byte[lenData];
                in.readBytes(data);
            }

            if (newLen > 0) {
                serialize = new byte[newLen];
                in.readBytes(serialize);
            }
        }

        Message message = new Message();
        message.setType(MessageType.valueOf(type));
        message.setAttachment((Map<String, Object>) SerializerFactory.getSerializer(serializer).deserializer(serialize, HashMap.class));
        message.setData(data);
        out.add(message);
    }
}
