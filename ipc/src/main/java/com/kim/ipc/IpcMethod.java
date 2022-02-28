package com.kim.ipc;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : wengliuhu
 * @version : 0.1
 * @since : 2021/7/12 11:22
 * Describe：注册的供其他进程调用的方法
 */
public class IpcMethod {
    private Map<String, Method> mOpenMethodMap;

    private IpcMethod() {
        mOpenMethodMap = new HashMap<>();
    }

    private static class SingleHolder{
        private final static IpcMethod INSTANCE = new IpcMethod();
    }

    // <editor-fold defaultstate="collapsed" desc="用以给动态生成的IpcManager进行注入信息">
//    public static BinderIpcMessenger getInstance(@NonNull Application app){
//        IpcApp.getInstance().onCreate(app);
//        return SingleHolder.INSTANCE;
//    }

    public static IpcMethod getInstance(){
        return SingleHolder.INSTANCE;
    }


    protected void addIpcMethod(String key, String methodAllName){
        try {
            if (!mOpenMethodMap.containsKey(key)) {
                String methodName = methodAllName.substring(methodAllName.lastIndexOf(".") + 1);
                Class<?> aClass = Class.forName(methodAllName.substring(0, methodAllName.lastIndexOf(".")));
                Method method = aClass.getMethod(methodName, String.class);
                mOpenMethodMap.put(key, method);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Method getIpcMethod(String key){
        try {
            if (mOpenMethodMap.containsKey(key)) {
                return mOpenMethodMap.get(key);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
