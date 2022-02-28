package com.kim.ipc.server;

import android.text.TextUtils;
import android.util.ArrayMap;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kim.ipc.Message;
import com.kim.ipc.bean.Constant;
import com.kim.ipc.messenger.TcpIpcMessenger;
import com.kim.ipc.util.Util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @author : wengliuhu
 * @version : 0.1
 * @since : 2021/7/7 10:36
 * Describe：用于接收TCP数据
 */
public class TcpServer extends Thread{
    ServerSocket serverSocket;
    // 把TCP连接的
    private Map<String, Socket> socketMap = new ArrayMap<>();
    private Gson gson = new Gson();
    @Override
    public void run() {
        super.run();
        try {
            serverSocket = new ServerSocket(Constant.TCP_PORT);
            while (true){
                Socket socket = serverSocket.accept();
                Observable.just(socket)
                        .subscribeOn(Schedulers.computation())
                        .subscribe(new Observer<Socket>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(Socket socket) {
                                DataInputStream in;
                                DataOutputStream out;
                                try {
                                    in = new DataInputStream(socket.getInputStream());
                                    String msg = in.readUTF();
                                    out = new DataOutputStream(socket.getOutputStream());
                                    Message message = gson.fromJson(msg, new TypeToken<Message>(){}.getType());
                                    if (message != null){
                                        String reslut = TcpIpcMessenger.getInstance().onReciveMessage(message);
                                        if (!TextUtils.isEmpty(reslut)){
//                                            message.setMessage(reslut);
//                                            String msgJsonStr = gson.toJson(message);
//                                            out.write(reslut.getBytes("utf-8"));
                                            out.writeUTF(reslut);
                                            out.flush();
                                        }
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                }finally {
                                    Util.close(socket);
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                e.printStackTrace();
                            }

                            @Override
                            public void onComplete() {

                            }
                        });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
