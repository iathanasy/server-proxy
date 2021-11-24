package top.icss.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import top.icss.enums.EnumMessageType;
import top.icss.packet.MessagePacket;

import java.util.List;
import java.util.Objects;

/**
 * @author cd.wang
 * @create 2021-11-19 10:57
 */
public class MessageCodec extends MessageToMessageCodec<ByteBuf, MessagePacket> {

    final int packetLen = 20;

    @Override
    protected void encode(ChannelHandlerContext ctx, MessagePacket msg, List<Object> out) throws Exception {
        ByteBuf buf = ctx.channel().alloc().ioBuffer();
        // out
        int type = msg.getType().getCode();
        long opaque = msg.getOpaque();
        byte[] body = msg.getBody();
        byte[] data = msg.getData();
        int len = body.length;
        int lenData = 0;
        if(Objects.isNull(data)) {
            data = new byte[0];
        }else {
            lenData = data.length;
        }
        int newLen = len + lenData;
        buf.writeInt(newLen);
        buf.writeInt(type);
        buf.writeLong(opaque);
        buf.writeInt(lenData);
        buf.writeBytes(data);
        buf.writeBytes(body);
        out.add(buf);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        int readableBytes = msg.readableBytes();
        if (readableBytes < packetLen) {
            return;
        }
        msg.markReaderIndex();

        MessagePacket packet = new MessagePacket();
        // in
        int len = msg.readInt();
        int type = msg.readInt();
        long opaque = msg.readLong();
        int lenData = msg.readInt();
        int newLen = len - lenData;

        byte[] data = new byte[0];
        byte[] bytes = new byte[0];
        if(msg.isReadable()) {
            if (lenData > 0) {
                data = new byte[lenData];
                msg.readBytes(data);
            }

            if (newLen > 0) {
                bytes = new byte[newLen];
                msg.readBytes(bytes);
            }
        }
        packet.setLen(len);
        packet.setType(EnumMessageType.valueOf(type));
        packet.setOpaque(opaque);
        packet.setBody(bytes);
        packet.setData(data);

        out.add(packet);
    }
}
