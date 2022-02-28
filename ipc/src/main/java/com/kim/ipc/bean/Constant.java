package com.kim.ipc.bean;

/**
 * @author : wengliuhu
 * @version : 0.1
 * @since : 2021/7/6 14:42
 * Describe：
 */
public interface Constant {
    /**
     * UDP服务端口
     */
    int UDP_PORT = 8088;
    /**
     * TCP服务端口
     */
    int TCP_PORT = 8089;

    /**
     * 报文类型
     */
    int TYPE_HEART = 1;

    /**
     * 判断存活的时间
     */
    int TIME_ALIVE = 60 * 1000;

    /**
     * UDP心跳间隔
     */
    int TIME_HEAT = 20 * 1000;

}
