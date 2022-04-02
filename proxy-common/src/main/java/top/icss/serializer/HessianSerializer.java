package top.icss.serializer;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author cd.wang
 * @create 2022-04-01 10:58
 */
@Slf4j
public class HessianSerializer<T> implements Serializer<T>{

    @Override
    public byte getSerializerAlogrithm() {
        return HESSIAN;
    }

    @Override
    public byte[] serializer(T o) {
        ByteArrayOutputStream bos=new ByteArrayOutputStream(); //表示输出到内存的实现
        HessianOutput ho=new HessianOutput(bos);
        try {
            ho.writeObject(o);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("HessianSerializer serializer err： {}", e.getMessage());
        }
        return bos.toByteArray();
    }

    @Override
    public T deserializer(byte[] data, Class<T> clazz) {
        ByteArrayInputStream bis=new ByteArrayInputStream(data);
        HessianInput hi=new HessianInput(bis);
        try {
            return (T) hi.readObject(clazz);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("HessianSerializer deserializer err： {}", e.getMessage());
        }
        return null;
    }
}
