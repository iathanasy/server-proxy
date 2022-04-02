package top.icss.serializer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author cd.wang
 * @create 2022-04-02 11:03
 */
public class SerializerFactory {

    private static final Map<Byte, Serializer> serializerMap;

    static {
        serializerMap = new HashMap<>();
        serializerMap.put(Serializer.JSON, new JsonSerializer());
        serializerMap.put(Serializer.HESSIAN, new HessianSerializer());
    }

    public static Serializer getSerializer(byte key){
        return serializerMap.get(key);
    }
}
