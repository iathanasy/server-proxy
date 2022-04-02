package top.icss.serializer;

/**
 * @author cd.wang
 * @create 2022-04-01 10:58
 */
public interface Serializer<T> {

    byte JSON = 1, HESSIAN = 2;

    /**
     * 序列化算法
     * @return
     */
    byte getSerializerAlogrithm();

    /**
     * java 对象转换成二进制
     * @param t
     * @return
     */
    byte[] serializer(T t);

    /**
     * 二进制转换成 java 对象
     * @param data
     * @return
     */
    T deserializer(byte[] data, Class<T> clazz);
}
