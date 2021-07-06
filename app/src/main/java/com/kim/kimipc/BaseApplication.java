package com.kim.kimipc;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kim.annotation.IpcMethod;

/**
 * @author : wengliuhu
 * @version : 0.1
 * @since : 2021/6/24 14:54
 * Describe：
 */
public class BaseApplication extends Application {
    private static Activity topActivity;
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        Log.d("Conected", "------------BaseApplication---attachBaseContext-:" );

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("Conected", "------------BaseApplication---onCreate-:" );
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
                topActivity = activity;
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {

            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {

            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {

            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {

            }
        });

    }

    @IpcMethod(key = "name")
    public static String appName(String msg){
        return BuildConfig.APPLICATION_ID;
    }

    @IpcMethod(key = "gotoSecondActivity")
    public static String gotoSecondActivity(String msg){
        if (topActivity != null);
        Intent intent = new Intent(topActivity, SecondActivity.class);
        topActivity.startActivity(intent);
        return "准备跳转第二个界面";
    }
}
