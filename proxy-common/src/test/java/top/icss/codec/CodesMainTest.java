package top.icss.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LoggingHandler;
import top.icss.protocol.Message;
import top.icss.protocol.MessageType;

/**
 * @author cd.wang
 * @create 2022-04-01 9:52
 */
public class CodesMainTest {

    public static void main(String[] args) throws Exception {
        test();
//        test1();
    }

    /***
     * 正常数据包
     * @throws Exception
     */
    public static void test() throws Exception {
        EmbeddedChannel channel=new EmbeddedChannel(
                new LoggingHandler(),
                new MessageEncoder(),
                new MessageDecoder());
        Message record=new Message();
        record.setType(MessageType.AUTH);
        record.setData("Hello World".getBytes());
        channel.writeOutbound(record);

        ByteBuf buf= ByteBufAllocator.DEFAULT.buffer();
        new MessageEncoder().encode(null,record,buf);
        channel.writeInbound(buf);
    }


    /***
     * 模拟半包和粘包问题
     * @throws Exception
     */
    public static void test1() throws Exception {
        // EmbeddedChannel是netty专门针对ChannelHandler的单元测试而提供的类。可以通过这个类来测试channel输入入站和出站的实现
        EmbeddedChannel channel=new EmbeddedChannel(
                //解决粘包和半包问题
                new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,10,4,0,0),
                new LoggingHandler(),
                new MessageEncoder(),
                new MessageDecoder());
        Message msg=new Message();
        msg.setType(MessageType.AUTH);
        msg.setData("Hello World".getBytes());

        channel.writeOutbound(msg);

        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
        new MessageEncoder().encode(null,msg,buf);
        channel.writeInbound(buf);

        //*********模拟半包和粘包问题************//
        //todo 把一个包通过slice拆分成两个部分 目前有点问题
        ByteBuf bb1=buf.slice(0,7); //获取前面7个字节
        ByteBuf bb2=buf.slice(7,buf.readableBytes() - 7); //获取后面的字节
        bb1.retain();

        channel.writeInbound(bb1);
        channel.writeInbound(bb2);
    }

}
