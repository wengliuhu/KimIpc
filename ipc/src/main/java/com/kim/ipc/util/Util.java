package com.kim.ipc.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import io.reactivex.disposables.Disposable;

/**
 * @author : wengliuhu
 * @version : 0.1
 * @since : 2021/7/7 10:46
 * Describe：
 */
public class Util {
    /**
     * 关闭IO流
     * @param closeable
     */
    public static void close(Closeable closeable){
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean isEmpty(List list)
    {
        return list == null || list.size() == 0;
    }

    public static boolean isEmpty(byte[] bytes)
    {
        return bytes == null || bytes.length == 0;
    }

    public static boolean isEmpty(Object[] objects)
    {
        return objects == null || objects.length == 0;
    }


    public static boolean isEmpty(Map objects)
    {
        return objects == null || objects.size() == 0;
    }

    /**
     * [获取应用程序版本名称信息]
     * @param context
     * @return 当前应用的版本名称
     */
    public static  String getPackageName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.packageName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void dispose(Disposable disposable){
        if (disposable != null || !disposable.isDisposed()) disposable.dispose();
    }

}
