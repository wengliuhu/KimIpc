//package com.kim.ipc.server;
//
//import android.app.Application;
//import android.text.TextUtils;
//
//import com.kim.ipc.bean.Constant;
//import com.kim.ipc.BinderIpcMessenger;
//import com.kim.ipc.util.DataConvertUtil;
//import com.kim.ipc.util.NetUtil;
//import com.kim.ipc.util.Util;
//
//import java.io.IOException;
//import java.net.DatagramPacket;
//import java.net.DatagramSocket;
//import java.net.InetAddress;
//import java.net.SocketException;
//import java.nio.charset.Charset;
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//
//import io.reactivex.Observable;
//import io.reactivex.Observer;
//import io.reactivex.disposables.Disposable;
//import io.reactivex.schedulers.Schedulers;
//
///**
// * @author : wengliuhu
// * @version : 0.1
// * @since : 2021/7/6 14:58
// * Describe：UDP用来判断心跳和获得各设备的IP地址
// */
//public class UdpBroadcastServer extends Thread{
//    DatagramSocket socket;
//    String myIp;
//    private Application app;
//    int buffLength = 512;
//    int msgId = 0;
//
//    public UdpBroadcastServer(Application app) {
//        this.app = app;
//    }
//
//    @Override
//    public void run() {
//        super.run();
//        // 开启定时的心跳发送
//        startHeart();
//        try {
//            socket = new DatagramSocket(Constant.UDP_PORT);
//            while (true){
//                //2.创建一个 udp 的数据包
//                byte[] buf = new byte[buffLength];
//                DatagramPacket packet = new DatagramPacket(buf, buf.length);
//                try {
//                    socket.receive(packet);
//                    Observable.just(packet)
//                            .subscribeOn(Schedulers.computation())
//                            .subscribe(new Observer<DatagramPacket>() {
//                                @Override
//                                public void onSubscribe(Disposable d) {
//
//                                }
//
//                                @Override
//                                public void onNext(DatagramPacket datagramPacket) {
////                                    String reciveStr = new String(datagramPacket.getData(), Charset.forName("GBK"));
////                                    Log.d("kim", "------IpcReciveThread  ip:" + datagramPacket.getAddress().getHostAddress());
//////                                        Log.d("kim", "------IpcReciveThread  port:" + datagramPacket.getAddress().get());
////                                    Log.d("kim", "------IpcReciveThread  reciveStr:" + reciveStr);
//                                    // 同设备的不接收
//                                    if (TextUtils.equals(datagramPacket.getAddress().getHostAddress(), NetUtil.getIpAddress(app))){
//                                        return;
//                                    }
////                                    // 解析报文
//                                    byte datas[] = datagramPacket.getData();
////                                    Log.d("kim", "--接收心跳数据:" + DataConvertUtil.bytesToHexStr(datas));
//
////                                    // 第一个字节是长度，第2个字节为类型，第3、4个字节为msgId, 最后一个字节为校验位
//
//                                    // 长度包含：长度位 -》 最后的检验位之前（即不包含校验位）
//                                    int length = DataConvertUtil.byteToInt(datas[0]);
//                                    if (length >= buffLength) return;
//                                    int type = DataConvertUtil.byteToInt(datas[1]);
//                                    byte msgIdBytes[] = new byte[2];
//                                    System.arraycopy(datas, 2, msgIdBytes, 0, msgIdBytes.length);
//                                    int msgId = DataConvertUtil.bytesToInt(msgIdBytes, false);
//                                    byte realDatas[] = new byte[length];
//                                    System.arraycopy(datas, 0, realDatas, 0, realDatas.length);
//                                    // 异或校验
//                                    boolean checkResult = DataConvertUtil.checXorCode(realDatas, datas[length]);
//                                    if (checkResult){
//                                        switch (type){
//                                            case Constant.TYPE_HEART:
//                                            {
//                                                byte msgBytes[] = new byte[length - 4];
//                                                System.arraycopy(realDatas, 4, msgBytes, 0, msgBytes.length);
//                                                //
//                                                String hexStr = DataConvertUtil.bytesToHexStr(msgBytes);
//                                                String packageNames[] = hexStr.split("0X00");
//                                                List<String> packageNameList = new ArrayList<>();
//                                                for (int i = 0; i < packageNames.length; i ++){
//                                                    byte packagenameBytes[] = DataConvertUtil.hex2byte(packageNames[i].toUpperCase().replaceAll("0X", ""));
////                                                    Log.d("kim", "--接收包名："  + DataConvertUtil.bytesToHexStr(packagenameBytes));
//
//                                                    String packageName = new String(packagenameBytes, Charset.forName("GBK"));
//                                                    if (!TextUtils.isEmpty(packageName)) packageNameList.add(packageName);
//                                                }
//                                                BinderIpcMessenger.getInstance().receiveHeart(packageNameList, datagramPacket.getAddress().getHostAddress());
//                                                break;
//                                            }
//                                        }
//
//                                    }
//                                }
//
//                                @Override
//                                public void onError(Throwable e) {
//
//                                }
//
//                                @Override
//                                public void onComplete() {
//
//                                }
//                            });
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        } catch (SocketException e) {
//            e.printStackTrace();
//        }finally {
//            if (socket != null) socket.close();
//        }
//    }
//
//    private void startHeart(){
//        Observable.interval(Constant.TIME_HEAT, TimeUnit.MILLISECONDS)
//                .subscribeOn(Schedulers.computation())
//                .subscribe(new Observer<Long>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//
//                    }
//
//                    @Override
//                    public void onNext(Long aLong) {
//                        DatagramSocket socket;
//                        try {
//                            socket = new DatagramSocket();
//                            // 获得本设备Binder的包名
//                            List<String> packageNames = BinderIpcMessenger.getInstance().getBinderPackageNames();
//                            packageNames.add(Util.getPackageName(app));
//                            List<byte[]> byteList = new ArrayList<>();
//                            Iterator<String> iterator = packageNames.iterator();
//                            int msgSize = 0;
//                            // 把包名的byte数组存储起来, 并计算总长度
//                            while (iterator.hasNext()){
//                                String packageName = iterator.next();
//                                if (TextUtils.isEmpty(packageName)) continue;
//                                byte[] packageBytes = packageName.getBytes(Charset.forName("GBK"));
////                                Log.d("kim", "--发送的包名："  + DataConvertUtil.bytesToHexStr(packageBytes));
//                                byteList.add(packageBytes);
//                                msgSize += (packageBytes.length);
//                            }
//                            // 每个包名通过0xOO分隔，总共有packageNames.size() - 1个0x00;
//                            byte heartDataBytes[] = new byte[msgSize + 4 + packageNames.size() - 1];
//                            // 创建首部
//                            heartDataBytes[0] = (byte) (heartDataBytes.length);
//                            heartDataBytes[1] = Constant.TYPE_HEART;
//                            heartDataBytes[2] = (byte)((msgId >> 8) & 0xFF);
//                            heartDataBytes[3] = (byte)((msgId >> 0) & 0xFF);
//                            msgId ++;
//                            // 增加包名的byte数组
//                            int position = 4;
//                            for (int i = 0; i < byteList.size(); i ++){
//                                byte[] packageBytes = byteList.get(i);
//                                System.arraycopy(packageBytes, 0, heartDataBytes, position, packageBytes.length);
//                                if (i != byteList.size() - 1){
//                                    position += packageBytes.length;
//                                    heartDataBytes[position++] = 0x00;
//                                }
//                            }
//                            // 计算数据部分的异或结果
//                            byte xor = DataConvertUtil.getXorCode(heartDataBytes);
//                            // 重新组包，在末尾增加校验位
//                            byte data[] = new byte[heartDataBytes.length + 1];
//                            System.arraycopy(heartDataBytes, 0, data, 0, heartDataBytes.length);
//                            data[data.length - 1] = xor;
////                            Log.d("kim", "--发送心跳数据:" + DataConvertUtil.bytesToHexStr(data));
//                            DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName("255.255.255.255"), Constant.UDP_PORT);
//                            socket.send(packet);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                    @Override
//                    public void onError( Throwable e) {
//                        e.printStackTrace();
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });
//    }
//
//}
