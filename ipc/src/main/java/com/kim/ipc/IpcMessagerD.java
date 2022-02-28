//package com.kim.ipc;
//
//
//import android.app.Application;
//import android.content.Context;
//import android.content.Intent;
//import android.content.ServiceConnection;
//import android.util.Log;
//
//import androidx.annotation.NonNull;
//
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Method;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.ConcurrentLinkedDeque;
//
//
///**
// * @author : wengliuhu
// * @version : 0.1
// * @since : 2021/6/17 16:06
// * Describe：
// */
//public class IpcMessager {
//    //用于直接调用当前进程的静态方法
//    private static final String IPC_TYPE_METHOD = "IPC_TYPE_METHOD";
//    // 用于传输普通消息
//    private static final String IPC_TYPE_MESSAGE = "IPC_TYPE_MESSAGE";
//    // 用于接收回复信息
//    private static final String IPC_TYPE_REBACK = "IPC_TYPE_REBACK";
//    // 存储包名和对应应用的binder
//    private volatile HashMap<String, IMessageServer> mServers = new HashMap<>();
//    /**
//     * 存储的服务端的连接 ServerConnection */
//    private static HashMap<String, ServiceConnection> mConnectionMap = new HashMap<String, ServiceConnection>();
//    private Application mApp;
//    // 当前应用注册的监听IPC连接情况
//    private Map<String, OnIpcConnectionLisenter> mAppIpcConnectionLisenters = new HashMap<>();
//    private volatile List<IMessageLisenter> mMessageLisenters = new ArrayList<>();
//    // 一直监听的服务端包名(断开连接时再次拉起)
//    private volatile List<String> mBindForeverPackageName = new ArrayList<>();
//    // 注册供外部调用的方法
//    private Map<String, Method> mOpenMethodMap = new HashMap<>();
//    protected IpcMessager() {
//    }
//
//    private static class SingleHolder{
//        private final static IpcMessager INSTANCE = new IpcMessager();
//    }
//
//    // <editor-fold defaultstate="collapsed" desc="用以给动态生成的IpcManager进行注入信息">
//    public static IpcMessager getInstance(@NonNull Application app){
//        SingleHolder.INSTANCE.mApp = app;
//        return SingleHolder.INSTANCE;
//    }
//
//    protected static IpcMessager getInstance(){
//        return SingleHolder.INSTANCE;
//    }
//
//    /**
//     * IpcManager调用的添加连接上的binder
//     * @param packageName
//     * @param messageServer
//     */
//    protected void addMessageServer(String packageName, @NonNull IMessageServer messageServer){
//        if (messageServer == null) return;
//        mServers.put(packageName, messageServer);
//        Log.d("kim", "-------------------Conected:--------" + packageName);
//        if (mAppIpcConnectionLisenters.containsKey(packageName)){
//            mAppIpcConnectionLisenters.get(packageName).onConected(packageName);
//        }
//    }
//
//    /**
//     * IpcManager调用的移除断开的binder
//     * @param packageName
//     */
//    protected void removeMessageServer(String packageName){
//        IMessageServer messageServer = mServers.get(packageName);
//        if (messageServer == null) return;
//        mServers.remove(packageName);
//        Log.d("kim", "-------------------onDisConected:--------" + packageName);
//
//        if (mAppIpcConnectionLisenters.containsKey(packageName)){
//            mAppIpcConnectionLisenters.get(packageName).onConected(packageName);
//        }
//
//        if (mBindForeverPackageName.contains(packageName)){
//            bindServiceForever(packageName);
//        }
//    }
//
//    /**
//     * 缓存，IpcManager 根据注解的包名生成的ServiceConnection
//     * @param packageName
//     * @param connection
//     */
//    protected void addServerConnection(String packageName, ServiceConnection connection){
//        mConnectionMap.put(packageName, connection);
//    }
//
//    /**
//     * IpcManager 清空ServiceConnection缓存
//     */
//    protected void clearServerConnection(){
//        mConnectionMap.clear();
//    }
//
//    /**
//     * IpcManager 移除ServiceConnection缓存
//     * @param packageName
//     */
//    protected void removeServerConnection(String packageName){
//        if (mConnectionMap.containsKey(packageName)) mConnectionMap.remove(packageName);
//    }
//
//    protected void addIpcMethod(String key, String methodAllName){
//        try {
//            if (!mOpenMethodMap.containsKey(key)) {
//                String methodName = methodAllName.substring(methodAllName.lastIndexOf(".") + 1);
//                Class<?> aClass = Class.forName(methodAllName.substring(0, methodAllName.lastIndexOf(".")));
//                Method method = aClass.getMethod(methodName, String.class);
////                Class returnType = method.getReturnType();
//                mOpenMethodMap.put(key, method);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//    }
//    // </editor-fold>
//
//
//    /**
//     * 监听指定包名的binder连接情况
//     * @param packageName
//     * @param lisenter
//     */
//    public void addIpcConnectionLisenter(String packageName, OnIpcConnectionLisenter lisenter){
//        if (!mAppIpcConnectionLisenters.containsKey(packageName)) mAppIpcConnectionLisenters.put(packageName, lisenter);
//
//    }
//
//    /**
//     * 移除监听指定包名的binder连接情况
//     * @param packageName
//     * @param lisenter
//     */
//    public void removeIpcConnectionLisenter(String packageName, OnIpcConnectionLisenter lisenter){
//        if (mAppIpcConnectionLisenters.containsKey(packageName)) mAppIpcConnectionLisenters.remove(packageName);
//
//    }
//
//    /**
//     * 注册监听，接收其他进程发过来的Message
//     * @param lisenter
//     */
//    public void addMessageLisenter(IMessageLisenter lisenter){
//        if (!mMessageLisenters.contains(lisenter) && lisenter != null) mMessageLisenters.add(lisenter);
//        Log.d("kim", "--------addMessageLisenter-----package:" + mApp.getPackageName() + ";///mMessageLisenters" + mMessageLisenters);
//
//    }
//
//    /**
//     * 移除监听其他进程发过来的Message
//     * @param lisenter
//     */
//    public void removeMessageLisenter(IMessageLisenter lisenter){
//        if (mMessageLisenters.contains(lisenter)) mMessageLisenters.remove(lisenter);
//    }
//
//    /**
//     * 发送调用对应进程方法的消息
//     * @param methodName
//     * @param params
//     * @throws Exception
//     */
//    public boolean send2Method(String packageName, String methodName, String params) throws Exception {
//        boolean result = false;
//        if (mServers.containsKey(packageName)) {
//            mServers.get(packageName).sendMessage(mApp.getPackageName(), IPC_TYPE_METHOD, methodName, params);
//            result = true;
//        }
//        return result;
//
//    }
//
//    /**
//     * 发送调用对应进程方法的消息
//     * @param methodName
//     * @param params
//     * @throws Exception
//     */
//    public boolean send2Method(String methodName, String params) throws Exception {
//        boolean result = false;
//        if (mServers.size() <= 0) return result;
//        for (Map.Entry<String, IMessageServer> entry : mServers.entrySet()){
//            IMessageServer listener = entry.getValue();
//            if (listener == null){
//                mServers.remove(entry);
//                continue;
//            }
//            listener.sendMessage(mApp.getPackageName(), IPC_TYPE_METHOD, methodName, params);
//            result =  true;
//        }
//        return result;
//
//    }
//
//    /**
//     * 发送普通消息
//     * @param key
//     * @param value
//     * @throws Exception
//     */
//    public boolean sendMessage(String packageName, String key, String value) throws Exception {
//        boolean result = false;
//        if (mServers.containsKey(packageName)) {
//            mServers.get(packageName).sendMessage(mApp.getPackageName(), IPC_TYPE_MESSAGE, key, value);
//            result = true;
//        }
//        return result;
//    }
//
//
//    /**
//     * 发送普通消息
//     * @param key
//     * @param value
//     * @throws Exception
//     */
//    public boolean sendMessage(String key, String value) throws Exception {
//        boolean result = false;
//        if (mServers.size() <= 0) return result;
//        for (Map.Entry<String, IMessageServer> entry : mServers.entrySet()){
//            IMessageServer listener = entry.getValue();
//            if (listener == null){
//                mServers.remove(entry);
//                continue;
//            }
//            listener.sendMessage(mApp.getPackageName(), IPC_TYPE_MESSAGE, key, value);
//            result = true;
//        }
//        return result;
//    }
//
//    /**
//     * 启动服务端的binder */
//    public  void bindServiceForever(String packageName) {
//        ServiceConnection serviceConnection = mConnectionMap.get(packageName);
//        if (serviceConnection == null) return;
//        Intent intent = new Intent();
//        intent.setAction("com.kim.ipc");
//        intent.setPackage(packageName);
//        mApp.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
//        if (!mBindForeverPackageName.contains(packageName)) mBindForeverPackageName.add(packageName);
//    }
//
//    /**
//     * 启动服务端的binder */
//    public  void bindService(Context context, String packageName) {
//        ServiceConnection serviceConnection = mConnectionMap.get(packageName);
//        if (serviceConnection == null) return;
//        Intent intent = new Intent();
//        intent.setAction("com.kim.ipc");
//        intent.setPackage(packageName);
//        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
//    }
//
//    /**
//     * 用以处理接收的消息
//     * @param from
//     * @param msgType
//     * @param msgKey
//     * @param msgBody
//     */
//    protected void onReceiveMessage(String from, String msgType, String msgKey, String msgBody){
//        Log.d("kimConected", "--------onReceiveMessage:from:" + from + ";message:" + msgBody);
//
//        try {
//            switch (msgType){
//                case IPC_TYPE_MESSAGE:
//                {
//                    Iterator<IMessageLisenter> iterator = mMessageLisenters.iterator();
//                    while (iterator.hasNext()){
//                        iterator.next().onMessage(msgKey, msgBody);
//                    }
//                    break;
//                }
//
//                case IPC_TYPE_METHOD:
//                {
//                    if (!mOpenMethodMap.containsKey(msgKey)) break;
//                    Method method = mOpenMethodMap.get(msgKey);
//                    Object Obj = method.invoke(null, msgBody);
//                    if (Obj instanceof String){
//                        sendMessage(from, msgKey, (String) Obj);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}
