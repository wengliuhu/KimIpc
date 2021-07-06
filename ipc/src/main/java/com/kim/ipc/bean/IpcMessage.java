package com.kim.ipc.bean;

import java.io.Serializable;

/**
 * @author : wengliuhu
 * @version : 0.1
 * @since : 2021/6/24 14:06
 * Describe：
 */
public class IpcMessage implements Serializable {
    private String msgType;
    private String sender;
    private String msgKey;
    private String msgBody;

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMsgKey() {
        return msgKey;
    }

    public void setMsgKey(String msgKey) {
        this.msgKey = msgKey;
    }

    public String getMsgBody() {
        return msgBody;
    }

    public void setMsgBody(String msgBody) {
        this.msgBody = msgBody;
    }
}
