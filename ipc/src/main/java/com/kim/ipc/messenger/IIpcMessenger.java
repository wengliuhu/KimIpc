package com.kim.ipc.messenger;

import com.kim.ipc.Message;
import com.kim.ipc.bean.BaseConnection;

/**
 * @author : wengliuhu
 * @version : 0.1
 * @since : 2021/7/12 9:46
 * Describe：IPC消息传递信使接口
 */
public interface IIpcMessenger {
    /**
     * 接收消息
     * @param message
     * @return 为空会null即为不回复消息（异步时）
     */
    String onReciveMessage(Message message);

    /**
     * 发送消息
     * @param message
     * @return msgId
     */
    int sendMessage(Message message);

    /**
     * 加入连接
     * @param connection
     */
    void addConnection(BaseConnection connection);

    /**
     * 移除连接
     * @param connection
     */
    void removeConnection(BaseConnection connection);

    /**
     * 连接成功
     * @param packageName
     * @return
     */
    boolean onConected(String packageName);

    /**
     * 断开连接
     * @param packageName
     * @return
     */
    boolean onDisConected(String packageName);
}
