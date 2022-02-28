package com.kim.ipc;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.kim.ipc.bean.BaseConnection;
import com.kim.ipc.bean.BinderConnection;
import com.kim.ipc.messenger.BaseIpcMessenger;
import com.kim.ipc.messenger.IMessageLisenter;
import com.kim.ipc.util.NetUtil;
import com.yanantec.ynbus.message.YnMessageManager;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static com.kim.ipc.Message.IPC_TYPE_MESSAGE;
import static com.kim.ipc.Message.IPC_TYPE_METHOD;
import static com.kim.ipc.Message.IPC_TYPE_REBACK;

/**
 * @author : wengliuhu
 * @version : 0.1
 * @since : 2021/7/6 15:11
 * Describe：默认的IPC方式为Binder
 */
public class BinderIpcMessenger extends BaseIpcMessenger {

    // 当前应用注册的监听IPC连接情况
//    protected Map<String, OnIpcConnectionLisenter> mAppIpcConnectionLisenters = new HashMap<>();
//    // 存储单设备多进程的Binder连接池
//    protected HashMap<String, BaseConnection> mServers = new HashMap<>();
//    // 注册供外部调用的方法
//    protected Map<String, Method> mOpenMethodMapmOpenMethodMap = new HashMap<>();
    Gson gson = new Gson();
    protected BinderIpcMessenger() {
    }

    private static class SingleHolder{
        private final static BinderIpcMessenger INSTANCE = new BinderIpcMessenger();
    }

    // <editor-fold defaultstate="collapsed" desc="用以给动态生成的IpcManager进行注入信息">
//    public static BinderIpcMessenger getInstance(@NonNull Application app){
//        IpcApp.getInstance().onCreate(app);
//        return SingleHolder.INSTANCE;
//    }

    public static BinderIpcMessenger getInstance(){
        return SingleHolder.INSTANCE;
    }

    @Override
    public void addConnection(BaseConnection connection) {

    }

    @Override
    public void removeConnection(BaseConnection connection) {

    }

    /**
     * 用以处理接收的消息
     */
    @Override
    public String onReciveMessage(Message message) {
        Log.d("kimConected", "--------onReceiveMessage:from:" + message.getFromAPP() + ";message:" + message.getMessage());
//        String reslut = "";
//        // 判断是否为被设备本进程的消息
//        String fromDevice = message.getFromDevice();
//        String nowDevice = NetUtil.getIpAddress(IpcApp.getApp());
//        // 非本设备发送的消息，需要判断是否需要传递给其他进程（binder方式发送的消息，fromDevice为空）
//        if (!TextUtils.isEmpty(fromDevice) && !TextUtils.equals(nowDevice, fromDevice)){
//            // 目的进程为空，则需要传递给所有进程
//            transitMessage(message);
//        }
//        // 是当前进程的，直接处理
//        if (TextUtils.isEmpty(message.getToApp()) || TextUtils.equals(message.getToApp(), IpcApp.getApp().getPackageName())){
//            reslut = dealMessage(message);
//        }

        try {
            switch (message.getType()){
                // 回复的消息
                case IPC_TYPE_REBACK:
                case IPC_TYPE_MESSAGE:
                {
                    Iterator<IMessageLisenter> iterator = mMessageLisenters.values().iterator();
                    if (TextUtils.isEmpty(message.getMessage())){
                        YnMessageManager.getInstance().sendEmptyMessage(message.getMessageKey());
                    }else {
                        try {
                            Object object = message.getMessage();
                            if (!TextUtils.equals(message.getMessageClass(), String.class.toString())){
                                Class cl = Class.forName(message.getMessageClass());
                                object = gson.fromJson(message.getMessage(), cl);
                            }
                            YnMessageManager.getInstance().sendMessage(message.getMessageKey(), object);
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();
                        }
                    }
                    while (iterator.hasNext()){
                        iterator.next().onMessage(message);
                    }
                    break;
                }

                case IPC_TYPE_METHOD:
                {
                    Method method = IpcMethod.getInstance().getIpcMethod(message.getMessageKey());
                    if (method == null) break;
                    Object Obj = method.invoke(null, message.getMessage());
                    rebackMessage(message, Obj);
                    break;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 发送普通消息
     * @param message
     * @throws Exception
     */
    @Override
    public int sendMessage(Message message){
        message.setMessageId(msgId ++);
        message.setFromAPP(IpcApp.getApp().getPackageName());
        // 目标包名为空，即发向所有包名
        for (Map.Entry<String, BaseConnection> entry : mServers.entrySet()) {
            if (entry.getValue() instanceof BinderConnection)
                entry.getValue().sendMessage(message);
        }
        return message.getMessageId();
    }


    /**
     * IpcManager调用的添加连接上的binder
     * @param packageName
     * @param messageServer
     */
    protected void addMessageServer(String packageName, @NonNull IMessageServer messageServer){
        if (messageServer == null && !mServers.containsKey(packageName)) return;
        BaseConnection connection = mServers.get(packageName);
        if (!(connection instanceof BinderConnection)) return;
        ((BinderConnection) connection).setServer(messageServer);
        connection.setAlive(true);
        mServers.put(packageName, connection);
        Log.d("kim", "-------------------Conected:--------" + packageName);
        onConected(packageName);
    }

    /**
     * IpcManager调用的移除断开的binder
     * @param packageName
     */
    protected void removeMessageServer(String packageName){
        if (!mServers.containsKey(packageName)) return;
        BaseConnection connection = mServers.get(packageName);
        if (!(connection instanceof BinderConnection)) return;
        ((BinderConnection) connection).setServer(null);
        connection.setAlive(false);
        Log.d("kim", "-------------------onDisConected:--------" + packageName);

        onDisConected(packageName);
        if (((BinderConnection) connection).isConnectForever()){
            bindServiceForever(packageName);
        }
    }

    /**
     * 缓存，IpcManager 根据注解的包名生成的ServiceConnection
     * @param packageName
     * @param connection
     */
    protected void addServerConnection(String packageName, ServiceConnection connection){
        BinderConnection connection1 = new BinderConnection();
        connection1.setServiceConnection(connection);
        mServers.put(packageName, connection1);
    }

    /**
     * IpcManager 清空ServiceConnection缓存
     */
    protected void clearServerConnection(){
        Set<Map.Entry<String, BaseConnection>> baseConnections = mServers.entrySet();
        Iterator<Map.Entry<String, BaseConnection>> entryIterator = baseConnections.iterator();
        while (entryIterator.hasNext()){
            Map.Entry<String, BaseConnection> item = entryIterator.next();
            if (item.getValue() instanceof BinderConnection){
                entryIterator.remove();
            }
        }
    }
    // </editor-fold>

    /**
     * 获取本设备通讯的包名
     * @return
     */
//    protected List<String> getBinderPackageNames(){
//        List<String> list = new ArrayList<>();
//        Iterator<Map.Entry<String, BaseConnection>> iterator = mServers.entrySet().iterator();
//        while (iterator.hasNext()){
//            Map.Entry<String, BaseConnection> entry = iterator.next();
//            BaseConnection connection = entry.getValue();
//            if (connection instanceof BinderConnection && connection.isAlive()){
//                list.add(entry.getKey());
//            }
//        }
//        return list;
//    }

    /**
     * 发送调用对应进程方法的消息
     * @param packageName 接收的进程的包名
     * @param msgKey    方法注册的key
     * @param msg       方法参数
     * @throws Exception
     */
    public void send2Method(String packageName, String msgKey, Object msg) throws Exception {
        Message message = new Message();
        message.setMessage(gson.toJson(msg));
        message.setToApp(packageName);
        message.setMessageKey(msgKey);
        message.setType(IPC_TYPE_METHOD);
        message.setMessageClass(msgKey.getClass().getName());
        sendMessage(message);
    }

    /**
     * 发送调用对应进程方法的消息,所有在通讯中的进程都调用(包括本设备和其他设备上的进程)
     * @param msgKey
     * @param message
     * @throws Exception
     */
    public void send2Method(String msgKey, Object message) throws Exception {
        send2Method("", msgKey, message);
    }


    /**
     * 发送普通消息
     * @param packageName
     * @param key
     * @param messageJsonString
     * @throws Exception
     */
    public void sendMessage(String packageName, String key, Object messageJsonString) throws Exception {
        Message message = new Message();
        message.setMessage(gson.toJson(messageJsonString));
        message.setMessageKey(key);
        message.setToApp(packageName);
        message.setType(IPC_TYPE_MESSAGE);
        message.setMessageClass(messageJsonString.getClass().getName());
        sendMessage(message);
    }

    /**
     * 发送普通消息
     * @param key
     * @param message
     * @throws Exception
     */
    public void sendMessage(String key, Object message) throws Exception {
        sendMessage("", key, message);
    }

    /**
     * 回复消息
     * @param oldMsg
     * @param rebackMessage
     * @throws Exception
     */
    private void rebackMessage(Message oldMsg, Object rebackMessage){
        if (mServers.containsKey(oldMsg.getFromAPP())){
            Message backMessage = new Message.Builder()
                    .message(gson.toJson(rebackMessage))
                    .messageClass(rebackMessage.getClass().getName())
                    .messageId(oldMsg.getMessageId())
                    .messageKey(oldMsg.getMessageKey())
                    .type(oldMsg.getType())
                    .toApp(oldMsg.getFromAPP())
                    .toDevices(oldMsg.getFromDevice())
                    .fromAPP(IpcApp.getApp().getPackageName())
                    .fromDevice(NetUtil.getIpAddress(IpcApp.getApp()))
                    .build();
            mServers.get(oldMsg.getFromAPP()).sendMessage(backMessage);
        }
    }

    /**
     * 启动服务端的binder */
    public  void bindServiceForever(String packageName) {
        if (!mServers.containsKey(packageName)) return;
        BaseConnection connection = mServers.get(packageName);
        if (!(connection instanceof BinderConnection)) return;
        ServiceConnection serviceConnection = ((BinderConnection) connection).getServiceConnection();
        if (serviceConnection == null) return;
        Intent intent = new Intent();
        intent.setAction("com.kim.ipc");
        intent.setPackage(packageName);
        IpcApp.getApp().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        ((BinderConnection) connection).setConnectForever(true);
    }

    /**
     * 启动服务端的binder */
    public  void bindService(Context context, String packageName) {
        if (!mServers.containsKey(packageName)) return;
        BaseConnection connection = mServers.get(packageName);
        if (!(connection instanceof BinderConnection)) return;
        ServiceConnection serviceConnection = ((BinderConnection) connection).getServiceConnection();
        if (serviceConnection == null) return;
        Intent intent = new Intent();
        intent.setAction("com.kim.ipc");
        intent.setPackage(packageName);
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }
}
