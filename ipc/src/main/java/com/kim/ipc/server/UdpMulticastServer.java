package com.kim.ipc.server;

import android.text.TextUtils;
import android.util.Log;

import com.kim.ipc.IpcApp;
import com.kim.ipc.bean.Constant;
import com.kim.ipc.bean.SocketConnection;
import com.kim.ipc.messenger.TcpIpcMessenger;
import com.kim.ipc.util.DataConvertUtil;
import com.kim.ipc.util.NetUtil;
import com.kim.ipc.util.Util;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @author : wengliuhu
 * @version : 0.1
 * @since : 2021/7/12 14:12
 * Describe：
 */
public class UdpMulticastServer extends Thread{
    private InetAddress group;
    private MulticastSocket socket;
    private volatile boolean isFinish;
    private Disposable disposable;
    int msgId;
    int buffLength = 512;
    @Override
    public void run() {
        super.run();
        try {
            group = InetAddress.getByName("224.5.6.7");
            if (!group.isMulticastAddress()){
                throw new RuntimeException("please use multicast ip 224.0.0.0 to 239.255.255.255 ");
            }
            System.out.println("组播服务端启动完成");
            socket = new MulticastSocket(Constant.UDP_PORT);
            //把组员加进来
            socket.joinGroup(group);

            Observable.interval(Constant.TIME_HEAT, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.computation())
                    .subscribe(new Observer<Long>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            disposable = d;
                        }

                        @Override
                        public void onNext(Long aLong) {
                            if (isFinish) Util.dispose(disposable);
                            sendHeart();
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });

            while (!isFinish) {
                //新建一个 package 来接受数据
                byte[] bytes = new byte[512];
                DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
                socket.receive(packet);
                Observable.just(packet)
                        .subscribeOn(Schedulers.computation())
                        .subscribe(new Observer<DatagramPacket>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(DatagramPacket datagramPacket) {
//                                String reciveStr = new String(datagramPacket.getData(), Charset.forName("GBK"));
                                // 同设备的不接收
                                if (TextUtils.equals(datagramPacket.getAddress().getHostAddress(), NetUtil.getIpAddress(IpcApp.getApp()))){
                                    return;
                                }
    //                                    // 解析报文
                                byte datas[] = datagramPacket.getData();
    //                                    Log.d("kim", "--接收心跳数据:" + DataConvertUtil.bytesToHexStr(datas));

                                // 第一个字节是长度，第2个字节为类型，第3、4个字节为msgId, 最后一个字节为校验位

                                // 长度包含：长度位 -》 最后的检验位之前（即不包含校验位）
                                int length = DataConvertUtil.byteToInt(datas[0]);
                                if (length >= buffLength) return;
                                int type = DataConvertUtil.byteToInt(datas[1]);
                                byte msgIdBytes[] = new byte[2];
                                System.arraycopy(datas, 2, msgIdBytes, 0, msgIdBytes.length);
                                int msgId = DataConvertUtil.bytesToInt(msgIdBytes, false);
                                byte realDatas[] = new byte[length];
                                System.arraycopy(datas, 0, realDatas, 0, realDatas.length);
                                // 异或校验
                                boolean checkResult = DataConvertUtil.checXorCode(realDatas, datas[length]);
                                if (checkResult){
                                    switch (type){
                                        case Constant.TYPE_HEART:
                                        {
                                            byte msgBytes[] = new byte[length - 6];
                                            byte portBytes[] = new byte[2];
                                            System.arraycopy(realDatas, 4, portBytes, 0, portBytes.length);
                                            System.arraycopy(realDatas, 6, msgBytes, 0, msgBytes.length);
                                            String packageName = new String(msgBytes, Charset.forName("GBK"));
                                            int port = DataConvertUtil.bytesToInt(portBytes, false);
//                                            String packageName = new String(DataConvertUtil.hex2byte(msgBytesStr[0].toUpperCase().replaceAll("0X", "")), Charset.forName("GBK"));
//                                            int tcpPort = DataConvertUtil.hex2byte(msgBytesStr[1].toUpperCase().replaceAll("0X", ""));
                                            Log.d("kim", "-------multicast port:" + datagramPacket.getPort());
                                            TcpIpcMessenger.getInstance().receiveHeart(packageName, datagramPacket.getAddress().getHostAddress() + ":" + port);
                                            break;
                                        }
                                    }

                                }
                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onComplete() {

                            }
                        });
//                String ip = packet.getAddress().getHostAddress();
//                int port = packet.getPort();
//                String msg = new String(bytes);
//                System.out.println("get client : " + ip + "\t port: " + port + "\tmsg: " + msg);
//                String receiveMsg = "hello "+msg;
//
//                byte[] buf = receiveMsg.getBytes();
//                DatagramPacket receivePacket = new DatagramPacket(buf,
//                        buf.length,
//                        packet.getAddress(),
//                        port);
//                socket.send(receivePacket);
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 发送心跳
     */
    private void sendHeart(){
        MulticastSocket socket = null;
        try {
            socket = new MulticastSocket();
            // 每个包名通过0xOO分隔，总共有packageNames.size() - 1个0x00;
            byte packageName[] = IpcApp.getApp().getPackageName().getBytes(Charset.forName("GBK"));
//            byte tcpIpPort[] = (NetUtil.getIpAddress(IpcApp.getApp()) + ":" +Constant.TCP_PORT).getBytes(Charset.forName("GBK"));
//            byte tcpIpPort[] = DataConvertUtil.bytesToInt();
            // 包长度1位 + 包类型1位 + 包ID2位 + TCP端口2位 + 包名数据 + 检验位1位；
            byte heartDataBytes[] = new byte[6 + packageName.length];
            // 创建首部
            int cusor = 0;
            // 包长度
            heartDataBytes[cusor ++] = (byte) (heartDataBytes.length);
            // 包类型
            heartDataBytes[cusor ++] = Constant.TYPE_HEART;
            // 包ID
            heartDataBytes[cusor ++] = (byte)((msgId >> 8) & 0xFF);
            heartDataBytes[cusor ++] = (byte)((msgId >> 0) & 0xFF);
            msgId ++;
            // TCP端口
            heartDataBytes[cusor ++] = (byte)((Constant.TCP_PORT >> 8) & 0xFF);
            heartDataBytes[cusor ++] = (byte)((Constant.TCP_PORT >> 0) & 0xFF);

            System.arraycopy(packageName, 0, heartDataBytes, cusor, packageName.length);
            cusor += packageName.length;
//            heartDataBytes[cusor++] = 0x00;
//            System.arraycopy(tcpIpPort, 0, heartDataBytes, cusor, tcpIpPort.length);

            // 计算数据部分的异或结果
            byte xor = DataConvertUtil.getXorCode(heartDataBytes);
            // 重新组包，在末尾增加校验位
            byte data[] = new byte[heartDataBytes.length + 1];
            System.arraycopy(heartDataBytes, 0, data, 0, heartDataBytes.length);
            data[data.length - 1] = xor;
            Log.d("kim", "--发送心跳数据:" + DataConvertUtil.bytesToHexStr(data));
            DatagramPacket packet = new DatagramPacket(data, data.length, group, Constant.UDP_PORT);
            socket.send(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            Util.close(socket);
        }

    }
}
