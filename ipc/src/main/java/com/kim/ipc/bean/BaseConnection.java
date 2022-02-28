package com.kim.ipc.bean;

import com.kim.ipc.Message;

/**
 * @author : wengliuhu
 * @version : 0.1
 * @since : 2021/7/6 14:23
 * Describe：
 */
public abstract class BaseConnection {
    // 进程唯一的标识
    protected String appId;
    protected boolean isAlive;
//    private Application app;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    public abstract void sendMessage(Message message);

//    protected Application getApp() {
//        return app;
//    }
//
//    public void setApp(Application app) {
//        this.app = app;
//    }
}
