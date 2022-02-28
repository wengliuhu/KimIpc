package com.kim.ipc;

/**
 * @author : wengliuhu
 * @version : 0.1
 * @since : 2021/6/23 16:30
 * Describe：
 */
public interface OnIpcLisenter {

    boolean onConected(String packageName);

    boolean onDisConected(String packageName);

//    void onMessageBack();

}
