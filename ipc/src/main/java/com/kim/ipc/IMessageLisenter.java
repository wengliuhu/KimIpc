package com.kim.ipc;

/**
 * @author : wengliuhu
 * @version : 0.1
 * @since : 2021/6/24 10:47
 * Describe：
 */
public interface IMessageLisenter {
    void onMessage(String key, String value);
}
