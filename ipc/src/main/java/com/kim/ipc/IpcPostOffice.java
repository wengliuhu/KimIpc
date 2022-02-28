package com.kim.ipc;

import com.google.gson.Gson;
import com.kim.ipc.messenger.IIpcMessenger;
import com.kim.ipc.messenger.TcpIpcMessenger;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : wengliuhu
 * @version : 0.1
 * @since : 2021/7/13 14:09
 * Describe：IPC邮局，用于管理所有类型的信使
 */
public class IpcPostOffice {
    private List<IIpcMessenger> messengers;
    protected IpcPostOffice() {
        messengers = new ArrayList<>();
    }

    private static class SingleHolder{
        private final static IpcPostOffice INSTANCE = new IpcPostOffice();
    }

    public static IpcPostOffice getInstance(){
        return SingleHolder.INSTANCE;
    }

    public void registMessenger(IIpcMessenger messenger){
        if (!messengers.contains(messenger)) messengers.add(messenger);
    }

    public void unRegistMessenger(IIpcMessenger messenger){
        if (messengers.contains(messenger)) messengers.remove(messenger);
    }
}
