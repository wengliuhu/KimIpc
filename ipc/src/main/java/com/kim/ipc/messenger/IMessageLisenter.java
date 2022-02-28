package com.kim.ipc.messenger;

import com.kim.ipc.Message;

/**
 * @author : wengliuhu
 * @version : 0.1
 * @since : 2021/6/24 10:47
 * Describe：
 */
public interface IMessageLisenter {
    void onMessage(Message message);
}
