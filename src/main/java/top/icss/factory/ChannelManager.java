package top.icss.factory;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.concurrent.ConcurrentHashMap;


/**
 * @author cd.wang
 * @create 2021-11-19 11:30
 */
public class ChannelManager {
    // Server
    public final static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    public final static ConcurrentHashMap<String, Channel> clientChannel = new ConcurrentHashMap<>();
    public final static AttributeKey<String> CLIENT_NAME = AttributeKey.newInstance("clientName");

    // Client
    public final static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    public final static ConcurrentHashMap<String, Channel> channelMap = new ConcurrentHashMap<>();
    public final static AttributeKey<Channel> CHANNEL_ID = AttributeKey.newInstance("channelId");
    public final static AttributeKey<String> CHANNEL_ID_STR = AttributeKey.newInstance("channelIdStr");
}
