package com.kim.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author : wengliuhu
 * @version : 0.1
 * @since : 2021/6/17 15:24
 * Describe：用于接口，类等
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface Server {
    /**
     * 要通讯的服务端的包名
     * @return
     */
    String[] serverPackageNames();
//
//    /**
//     *
//     * @return
//     */
//    String ipcUtilName();
}
