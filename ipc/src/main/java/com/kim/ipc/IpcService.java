package com.kim.ipc;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import java.lang.Exception;
import java.lang.Override;
import java.lang.String;
import java.util.HashMap;

public class IpcService extends Service {

    /**
     * IPC包下 事件处理类 */
    private IpcMessager mIpcMessager = IpcMessager.getInstance();

    private final IMessageServer.Stub stub = new IMessageServer.Stub() {
        @Override
        public void sendMessage(String from, String messageType, String messageKey,
                                String messageJsonString) throws RemoteException {
            try {
                mIpcMessager.onReceiveMessage(from, messageType, messageKey, messageJsonString);
            } catch (Exception e) {
                e.printStackTrace();
            };
        }
    };

//    private ServiceConnection serviceConnection0 = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            mServers.put(name.toString(), IMessageServer.Stub.asInterface(service));
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            mServers.remove(name.toString());
//        }
//    };
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        mConnectionMap.clear();
//        mConnectionMap.put("com.kim.kimipc", serviceConnection0);
//    }

    @Override
    public IBinder onBind(Intent intent) {
        return stub;
    }


}
