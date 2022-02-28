package com.kim.ipc.bean;

import android.os.SystemClock;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kim.ipc.Message;
import com.kim.ipc.messenger.TcpIpcMessenger;
import com.kim.ipc.util.Util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @author : wengliuhu
 * @version : 0.1
 * @since : 2021/7/6 14:25
 * Describe：
 */
public class SocketConnection extends BaseConnection{
    // Ip + 端口地址，即作为设备的appId
//    protected String ip;
    // 自开机起的事件使用 systemClock
    private long lastHeartTime;
    // 该设备Binder方式连接的包名
//    private List<String> packageNames = new ArrayList<>();
    private String packageName;

    @Override
    public boolean isAlive() {
        return SystemClock.uptimeMillis() - lastHeartTime <= Constant.TIME_ALIVE;
    }

    @Override
    public void sendMessage(Message message) {
        if (TextUtils.isEmpty(getAppId()) || !isAlive()) return;
        // 如果目标设备和目标进程都为空，即所有连接都需要发送；
        // 如果两者有其一不为空，即要判断是否匹配当前连接
        // 目标设备非所有设备（不为空，空即发送所有设备）, 非当前设备的连接不发送
        if (!TextUtils.isEmpty(message.getToDevices()) && !TextUtils.equals(message.getToDevices(), getAppId())){
            return;
        }
        // 目标进程不为空，但非当前连接的进程
        if (!TextUtils.isEmpty(message.getToApp()) && !TextUtils.equals(message.getToApp(), getPackageName())){
            return;
        }
        Observable.just(1)
                .subscribeOn(Schedulers.computation())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe( Disposable d) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        send(message);
                    }

                    @Override
                    public void onError( Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
//
//        if (Looper.getMainLooper() == Looper.myLooper()){
//           throw new RuntimeException("can not run in main thread!");
//        }else {
//        }
    }

    private void send(Message message){
        Socket socket = null;
        try {
            String ips[] = getAppId().split(":");
            if (ips.length != 2) return;
            socket = new Socket(ips[0], Integer.valueOf(ips[1]));
            socket.setSoTimeout(30 * 1000);
            DataOutputStream out = new DataOutputStream(
                    socket.getOutputStream());
            Gson gson = new Gson();
            String msg = gson.toJson(message);
            String str = new BufferedReader(new InputStreamReader(
                    new ByteArrayInputStream(msg.getBytes())))
                    .readLine();
            out.writeUTF(str);
            out.flush();
            DataInputStream input = new DataInputStream(
                    socket.getInputStream());
            String resultBack = input.readUTF();
            if (TextUtils.isEmpty(resultBack)) return ;
            Message resultMsg = gson.fromJson(resultBack, new TypeToken<Message>(){}.getType());
            TcpIpcMessenger.getInstance().onReciveMessage(resultMsg);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            Util.close(socket);
        }
    }

    public void setLastHeartTime(long lastHeartTime) {
        this.lastHeartTime = lastHeartTime;
    }

//    public List<String> getPackageNames() {
//        return packageNames;
//    }
//
//    public void setPackageNames(List<String> packageNames) {
//        this.packageNames.clear();
//        if (packageNames == null || packageNames.size() <= 0) return;
//        this.packageNames.addAll(packageNames);
//    }
//
//    @Override
//    public String getAppId() {
//        return getIp();
//    }
//
//    @Override
//    public void setAppId(String appId) {
//        setIp(appId);
//    }

//    private String getIp() {
//        return ip;
//    }
//
//    private void setIp(String ip) {
//        this.ip = ip;
//    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
