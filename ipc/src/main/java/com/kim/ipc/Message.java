package com.kim.ipc;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author : wengliuhu
 * @version : 0.1
 * @since : 2021/7/6 16:10
 * Describe：
 */
public class Message implements Parcelable, Cloneable{
    //用于直接调用当前进程的静态方法
    public static final String IPC_TYPE_METHOD = "IPC_TYPE_METHOD";
    // 用于传输普通消息
    public static final String IPC_TYPE_MESSAGE = "IPC_TYPE_MESSAGE";
    // 用于接收回复信息
    public static final String IPC_TYPE_REBACK = "IPC_TYPE_REBACK";
    private String fromAPP;
    private String toApp;
    private String type;
    // 为jsonString类型
    private String message;
    private String messageKey;
    private int messageId;
    private String fromDevice;
    private String toDevices;
    // message类的全路径
    private String messageClass;
    // 通道：binder、tcp、Udp(组播)
    private String channal;
    private int messageBackId;

    protected Message(Parcel reply) {
        readFromParcel(reply);
    }

    public Message() {
    }

    public static class Builder{
        private Message message;

        public Builder() {
            message = new Message();
        }

        public Message build(){
            return message;
        }

        public Builder fromAPP(String fromApp){
            message.setFromAPP(fromApp);
            return this;
        }

        public Builder toApp(String toApp){
            message.setToApp(toApp);
            return this;
        }

        public Builder type(String type){
            message.setType(type);
            return this;
        }

        public Builder message(String messageStr){
            message.setMessage(messageStr);
            return this;
        }

        public Builder messageKey(String messageKey){
            message.setMessageKey(messageKey);
            return this;
        }

        public Builder messageId(int messageId){
            message.setMessageId(messageId);
            return this;
        }

        public Builder fromDevice(String fromDevice){
            message.setFromDevice(fromDevice);
            return this;
        }

        public Builder toDevices(String toDevices){
            message.setToDevices(toDevices);
            return this;
        }

        public Builder messageClass(String messageClass){
            message.setMessageClass(messageClass);
            return this;
        }

        public Builder channal(String channal){
            message.setChannal(channal);
            return this;
        }

        public Builder messageBackId(int messageBackId){
            message.setMessageBackId(messageBackId);
            return this;
        }
    }

    public static final Creator<Message> CREATOR = new Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fromAPP);
        dest.writeString(toApp);
        dest.writeString(type);
        dest.writeString(message);
        dest.writeString(messageKey);
        dest.writeInt(messageId);
        dest.writeString(fromDevice);
        dest.writeString(toDevices);
        dest.writeString(messageClass);
        dest.writeString(channal);
        dest.writeInt(messageBackId);
    }

    public void readFromParcel(Parcel reply) {
        fromAPP =reply.readString();
        toApp =reply.readString();
        type = reply.readString();
        message = reply.readString();
        messageKey = reply.readString();
        messageId = reply.readInt();
        fromDevice = reply.readString();
        toDevices = reply.readString();
        messageClass = reply.readString();
        channal = reply.readString();
        messageBackId = reply.readInt();
    }

    public String getFromAPP() {
        return fromAPP;
    }

    public void setFromAPP(String fromAPP) {
        this.fromAPP = fromAPP;
    }

    public String getToApp() {
        return toApp;
    }

    public void setToApp(String toApp) {
        this.toApp = toApp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public String getFromDevice() {
        return fromDevice;
    }

    public void setFromDevice(String fromDevice) {
        this.fromDevice = fromDevice;
    }

    public String getToDevices() {
        return toDevices;
    }

    public void setToDevices(String toDevices) {
        this.toDevices = toDevices;
    }

    public String getMessageClass() {
        return messageClass;
    }

    public void setMessageClass(String messageClass) {
        this.messageClass = messageClass;
    }

    public String getChannal() {
        return channal;
    }

    public void setChannal(String channal) {
        this.channal = channal;
    }

    public int getMessageBackId() {
        return messageBackId;
    }

    public void setMessageBackId(int messageBackId) {
        this.messageBackId = messageBackId;
    }
}
