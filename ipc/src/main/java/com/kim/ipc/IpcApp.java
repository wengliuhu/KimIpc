package com.kim.ipc;

import android.app.Application;

/**
 * @author : wengliuhu
 * @version : 0.1
 * @since : 2021/7/7 16:32
 * Describe：
 */
public class IpcApp {
    private static Application  mApp;
    private static IpcApp mInstance;

    private IpcApp(){

    }

    public static IpcApp getInstance(){
        if (mInstance == null){
            synchronized (IpcApp.class){
                if (mInstance == null) mInstance = new IpcApp();
            }
        }
        return mInstance;
    }

    public void onCreate(Application application){
        mApp = application;
    }

    public static Application getApp() {
        return mApp;
    }
}
