package com.kim.complier.apt.util;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;

/**
 * @author : wengliuhu
 * @version : 0.1
 * @since : 2021/2/3
 * Describe:
 */
public class LogUtil
{

    private Messager messager;

    public LogUtil(Messager messager) {
        this.messager = messager;
    }

    public void e(String msg) {
        messager.printMessage(Diagnostic.Kind.ERROR, Consts.PREFIX_OF_LOGGER + "<---- " + msg + " ---->");
    }

    public void d(String msg) {
        messager.printMessage(Diagnostic.Kind.NOTE, Consts.PREFIX_OF_LOGGER + "<---- " + msg + " ---->");
    }

    public void w(String msg) {
        messager.printMessage(Diagnostic.Kind.WARNING, Consts.PREFIX_OF_LOGGER + "<---- " + msg + " ---->");
    }

    public void e(Throwable error) {
        if (null != error) {
            messager.printMessage(Diagnostic.Kind.ERROR, Consts.PREFIX_OF_LOGGER + "<----  An exception is encountered, [" + error.getMessage() + "]" + "\n" + formatStackTrace(error.getStackTrace()) + " ---->");
        }
    }

    private String formatStackTrace(StackTraceElement[] stackTrace) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : stackTrace) {
            sb.append("    at ").append(element.toString());
            sb.append("\n");
        }
        return sb.toString();
    }
}
