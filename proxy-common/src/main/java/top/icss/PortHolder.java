package top.icss;

import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author cd.wang
 * @create 2022-04-02 15:03
 */
public class PortHolder {

    private static final Map<String, List<Integer>> holder;
    private static final List<Integer> ports;

    static {
        holder = new ConcurrentHashMap<>();
        ports = new ArrayList<>();
    }

    public static List<Integer> get(String key) {
        return holder.get(key);
    }

    public static void put(String key, int port) {
        ports.add(port);
        holder.put(key, ports);
    }

    public static List<Integer> getPorts(){
        return null;
    }

}
