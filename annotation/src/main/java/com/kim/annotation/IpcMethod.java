package com.kim.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author : wengliuhu
 * @version : 0.1
 * @since : 2021/6/17 15:33
 * Describe：用以开发给接口调用的方法
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface IpcMethod {
    String key();
}
