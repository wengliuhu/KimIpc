package com.kim.app2;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.kim.annotation.IpcMethod;
import com.kim.ipc.BuildConfig;
import com.kim.ipc.BinderIpcManager;

/**
 * @author : wengliuhu
 * @version : 0.1
 * @since : 2021/6/24 14:54
 * Describe：
 */
public class BaseApplication extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        Log.d("Conected", "------------BaseApplication---attachBaseContext-:" );

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("Conected", "------------BaseApplication---onCreate-:" );
        BinderIpcManager.getInstance().init();
    }
    @IpcMethod(key = "name")
    public static String appName(String msg){
        return BuildConfig.APPLICATION_ID;
    }
}
