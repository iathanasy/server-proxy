package top.icss.protocol;

import lombok.Data;
import top.icss.serializer.Serializer;
import java.util.HashMap;
import java.util.Map;

/**
 * @author cd.wang
 * @create 2022-04-01 9:42
 */
@Data
public class Message {

    private MessageType type; //消息类型： 占1个字节
    private byte serializer; //序列化
    private Map<String, Object> attachment;  // 附加参数
    private byte[] data;

    public Message() {
        this.attachment = new HashMap<>();
        this.data = emptyData();
        this.serializer = Serializer.HESSIAN;
    }

    public void addAttachment(String key, String value) {
        this.attachment.put(key, value);
    }

    public void setChannelId(String channelId){
        this.attachment.put("channelId", channelId);
    }

    public String getChannelId(){
        return (String) this.attachment.get("channelId");
    }

    public void setUserName(String userName){
        this.attachment.put("username", userName);
    }
    public String getUserName(){
        return (String) this.attachment.get("username");
    }

    public void setPassWord(String password){
        this.attachment.put("password", password);
    }
    public String getPassWord(){
        return (String) this.attachment.get("password");
    }

    public void setPort(int port){
        this.attachment.put("port", port);
    }
    public int getPort(){
        return (int) this.attachment.get("port");
    }

    public void setSuccess(boolean success){
        this.attachment.put("success", success);
    }
    public boolean getSuccess(){
        return (boolean) this.attachment.get("success");
    }
    public void setReason(String reason){
        this.attachment.put("reason", reason);
    }
    public String getReason(){
        return (String) this.attachment.get("reason");
    }

    public static Message create(){
        Message message = new Message();
        message.setType(MessageType.KEEPALIVE);
        return message;
    }

    public static Message build(MessageType messageType){
        Message message = new Message();
        message.setType(messageType);
        return message;
    }

    public static Message build(String channelId, MessageType messageType){
        Message message = new Message();
        message.setType(messageType);
        message.setChannelId(channelId);
        return message;
    }

    public static Message build(String channelId, MessageType messageType, byte serializer){
        Message message = new Message();
        message.setType(messageType);
        message.setChannelId(channelId);
        message.setSerializer(serializer);
        return message;
    }

    public static Message build(String channelId, MessageType messageType, byte[] data){
        Message message = new Message();
        message.setType(messageType);
        message.setChannelId(channelId);
        message.setData(data);
        return message;
    }

    public static Message build(String channelId, MessageType messageType, byte[] data, byte serializer){
        Message message = new Message();
        message.setType(messageType);
        message.setChannelId(channelId);
        message.setSerializer(serializer);
        message.setData(data);
        return message;
    }

    public static byte[] emptyData() {
        return new byte[0];
    }

}
