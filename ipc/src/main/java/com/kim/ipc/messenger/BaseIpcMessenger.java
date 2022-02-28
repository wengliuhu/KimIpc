package com.kim.ipc.messenger;

import android.text.TextUtils;

import androidx.lifecycle.LifecycleOwner;

import com.kim.ipc.OnIpcLisenter;
import com.kim.ipc.bean.BaseConnection;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author : wengliuhu
 * @version : 0.1
 * @since : 2021/7/12 10:27
 * Describe：
 */
public abstract class BaseIpcMessenger implements IIpcMessenger{
    // 手动注册监听，一直永久监听
    protected static volatile ConcurrentHashMap<String, IMessageLisenter> mMessageLisenters = new ConcurrentHashMap<>();

    // 存储单设备多进程的Binder连接池
    protected static ConcurrentHashMap<String, BaseConnection> mServers = new ConcurrentHashMap<>();

    protected static ConcurrentHashMap<String, OnIpcLisenter> mAppIpcConnectionLisenters = new ConcurrentHashMap<>();

//    // 注册供外部调用的方法
//    protected Map<String, Method> mOpenMethodMap = new HashMap<>();

    protected static volatile int msgId;

    @Override
    public boolean onConected(String packageName) {
        if (mAppIpcConnectionLisenters.containsKey(packageName)){
            return  mAppIpcConnectionLisenters.get(packageName).onConected(packageName);
        }
        return false;
    }

    @Override
    public boolean onDisConected(String packageName) {
        if (mAppIpcConnectionLisenters.containsKey(packageName)){
            return  mAppIpcConnectionLisenters.get(packageName).onDisConected(packageName);
        }
        return false;
    }

    /**
     * 监听指定包名的binder连接情况
     * @param packageName
     * @param lisenter
     */
    public void addIpcConnectionLisenter(String packageName, OnIpcLisenter lisenter){
        if (!mAppIpcConnectionLisenters.containsKey(packageName)) mAppIpcConnectionLisenters.put(packageName, lisenter);

    }

    /**
     * 移除监听指定包名的binder连接情况
     * @param packageName
     * @param lisenter
     */
    public void removeIpcConnectionLisenter(String packageName, OnIpcLisenter lisenter){
        if (mAppIpcConnectionLisenters.containsKey(packageName)) mAppIpcConnectionLisenters.remove(packageName);
    }

    public void addMessageLisenter(String key, IMessageLisenter lisenter){
        if (lisenter != null && !TextUtils.isEmpty(key)){
            mMessageLisenters.put(key, lisenter);
        }
    }

    public void removeMessageLisenter(String key){
        if (mMessageLisenters.contains(key)) mMessageLisenters.remove(key);
    }


}
