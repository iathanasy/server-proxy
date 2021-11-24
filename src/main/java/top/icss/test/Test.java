package top.icss.test;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.MessageToMessageCodec;
import top.icss.enums.EnumMessageType;
import top.icss.packet.MessagePacket;
import top.icss.utils.JsonSerializer;

import java.util.List;

/**
 * @author cd.wang
 * @create 2021-11-19 10:55
 */
public class Test {
    public static void main(String[] args) {
        StudyPacket studyPacket = new StudyPacket("banxian", 18);
        MessagePacket messagePacket = studyPacket.create();

        EmbeddedChannel channel = new EmbeddedChannel(new LengthFieldPrepender(4), new MessageToMessageCodec<ByteBuf,MessagePacket>() {

            final int packetLen = 16;
            @Override
            protected void encode(ChannelHandlerContext ctx, MessagePacket msg, List<Object> out) throws Exception {
                ByteBuf buf = ctx.channel().alloc().ioBuffer();
                // out
                int len = msg.getLen();
                int type = msg.getType().getCode();
                long opaque = msg.getOpaque();
                byte[] body = msg.getBody();
                buf.writeInt(len);
                buf.writeInt(type);
                buf.writeLong(opaque);
                buf.writeBytes(body);
                out.add(buf);
            }

            @Override
            protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
                if (msg.readableBytes() < packetLen) {
                    return;
                }
                msg.markReaderIndex();

                MessagePacket packet = new MessagePacket();
                // in
                int len = msg.readInt();
                int type = msg.readInt();
                long opaque = msg.readLong();

                byte[] bytes = new byte[len];
                msg.readBytes(len);

                packet.setLen(len);
                packet.setType(EnumMessageType.valueOf(type));
                packet.setOpaque(opaque);
                packet.setBody(bytes);

                out.add(packet);
            }
        });

        // out
        channel.writeInbound(messagePacket);
        channel.finish();

        // in
        MessagePacket o = channel.readInbound();
        System.out.println(o);
        StudyPacket deserialize = JsonSerializer.deserialize(o.getBody(), StudyPacket.class);
        System.out.println(deserialize);

//        MessagePacket{opaque=1, type=1, len=71}
//        StudyPacket(id=a3123467-a2a2-4569-a314-c11fb96ed8fe, name=banxian, age=18)
    }
}
