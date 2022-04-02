package top.icss;

import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author cd.wang
 * @create 2022-04-01 9:35
 */
public class ChannelHolder {

    private static final Map<String, Channel> holder;

    static {
        holder = new ConcurrentHashMap<>();
    }

    public static Channel get(String key) {
        return holder.get(key);
    }

    public static void put(String key, Channel channel) {
        holder.put(key, channel);
    }

    public static void remove(String key) {
        holder.remove(key);
    }

    public static void removeByChannel(Channel channel) {
        holder.forEach((k, v) -> {
            if (v.equals(channel)) {
                remove(k);
            }
        });
    }
}
