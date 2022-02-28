package com.kim.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author : wengliuhu
 * @version : 0.1
 * @since : 2021/7/6 14:17
 * Describe：
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface IpcConnectType {

    /**
     * IPC通讯的连接方式
     *  Multiple：多设备（采用UDP + TCP方式）
     *  Single:单设备（采用Binder方式连接）
     * @return
     */
    String connectType() default "Single";
}
