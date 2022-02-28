package com.kim.ipc.messenger;

import android.os.SystemClock;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.kim.ipc.IpcApp;
import com.kim.ipc.IpcMethod;
import com.kim.ipc.Message;
import com.kim.ipc.bean.BaseConnection;
import com.kim.ipc.bean.BinderConnection;
import com.kim.ipc.bean.Constant;
import com.kim.ipc.bean.SocketConnection;
import com.kim.ipc.util.NetUtil;
import com.yanantec.ynbus.message.YnMessageManager;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;

import static com.kim.ipc.Message.IPC_TYPE_MESSAGE;
import static com.kim.ipc.Message.IPC_TYPE_METHOD;
import static com.kim.ipc.Message.IPC_TYPE_REBACK;

/**
 * @author : wengliuhu
 * @version : 0.1
 * @since : 2021/7/12 10:09
 * Describe：
 */
public class TcpIpcMessenger extends BaseIpcMessenger {
    private Gson gson;
    protected TcpIpcMessenger() {
        gson = new Gson();
    }

    private static class SingleHolder{
        private final static TcpIpcMessenger INSTANCE = new TcpIpcMessenger();
    }

    public static TcpIpcMessenger getInstance(){
        return SingleHolder.INSTANCE;
    }


    @Override
    public String onReciveMessage(Message message) {
        String result = "";

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
                    // Tcp直接返回结果
                    Message backMessage = new Message.Builder()
                            .message(gson.toJson(Obj))
                            .messageClass(Obj.getClass().getName())
                            .messageId(msgId ++)
                            .messageKey(message.getMessageKey())
                            .type(IPC_TYPE_REBACK)
                            .toApp(message.getFromAPP())
                            .toDevices(message.getFromDevice())
                            .fromAPP(IpcApp.getApp().getPackageName())
                            .fromDevice(NetUtil.getIpAddress(IpcApp.getApp()))
                            .build();
//                    rebackMessage(message, Obj);
                    result = gson.toJson(backMessage);
                    break;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public int sendMessage(Message message) {
        message.setMessageId(msgId ++);
        message.setFromAPP(IpcApp.getApp().getPackageName());
        message.setFromDevice(NetUtil.getIpAddress(IpcApp.getApp()));
        // 目标包名为空，即发向所有包名
        for (Map.Entry<String, BaseConnection> entry : mServers.entrySet()) {
            if (entry.getValue() instanceof SocketConnection)
                entry.getValue().sendMessage(message);
        }
        return message.getMessageId();
    }

    @Override
    public void addConnection(BaseConnection connection) {

    }

    @Override
    public void removeConnection(BaseConnection connection) {

    }


    /**
     * 接收UDP心跳数据
     * @param packageName
     * @param appId
     */
    public void receiveHeart(String packageName, String appId){
        if (!mServers.containsKey(appId)){
            SocketConnection socketConnection = new SocketConnection();
            socketConnection.setAppId(appId);
            socketConnection.setPackageName(packageName);
            socketConnection.setLastHeartTime(SystemClock.uptimeMillis());
            mServers.put(appId, socketConnection);
        }else {
            BaseConnection connection = mServers.get(appId);
            if (!(connection instanceof SocketConnection)) return;
            ((SocketConnection) connection).setLastHeartTime(SystemClock.uptimeMillis());
            ((SocketConnection) connection).setPackageName(packageName);
        }

    }
}
