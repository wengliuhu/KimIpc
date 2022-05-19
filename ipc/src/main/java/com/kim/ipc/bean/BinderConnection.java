package com.kim.ipc.bean;

import android.content.ServiceConnection;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.kim.ipc.IMessageServer;
import com.kim.ipc.IpcApp;
import com.kim.ipc.Message;

/**
 * @author : wengliuhu
 * @version : 0.1
 * @since : 2021/7/6 14:25
 * Describe：
 */
public class BinderConnection extends BaseConnection{
    protected ServiceConnection serviceConnection;
    protected IMessageServer server;
    protected boolean connectForever;

    public ServiceConnection getServiceConnection() {
        return serviceConnection;
    }

    public void setServiceConnection(ServiceConnection serviceConnection) {
        this.serviceConnection = serviceConnection;
    }

    public IMessageServer getServer() {
        return server;
    }

    public void setServer(IMessageServer server) {
        this.server = server;
        Log.d("connecttion", "---------setServer-----" + server + "/////" + IpcApp.getApp().getPackageName());
    }

    public boolean isConnectForever() {
        return connectForever;
    }

    public void setConnectForever(boolean connectForever) {
        this.connectForever = connectForever;
    }

    @Override
    public boolean isAlive() {
        return server != null;
    }

    @Override
    public void sendMessage(Message message) {
        if (!isAlive() || !getServer().asBinder().isBinderAlive()) return;
        // 非当前连接的，不发送
        if (!TextUtils.isEmpty(message.getToApp()) && !TextUtils.equals(message.getToApp(), getAppId())) return;
        try {
            getServer().sendMsg(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
