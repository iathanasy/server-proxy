
package top.icss.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * json serialize or deserialize
 */
@Slf4j
public class JsonSerializer<T> implements Serializer<T> {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte getSerializerAlogrithm() {
        return JSON;
    }

    @Override
    public byte[] serializer(T t) {
        String json = "";
        try {
            json = objectMapper.writeValueAsString(t);
        } catch (JsonProcessingException e) {
            log.error("JsonSerializer serializer err： {}", e.getMessage());
        }
        return json.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public T deserializer(byte[] data, Class<T> clazz) {
        String json = new String(data, StandardCharsets.UTF_8);
        try {
            return (T) objectMapper.readValue(json, clazz);
        } catch (IOException e) {
            log.error("JsonSerializer deserializer err： {}", e.getMessage());
            return null;
        }
    }
}
