package com.kim.kimipc;

import android.app.Activity;
import android.app.Application;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;

import com.kim.ipc.IpcApp;
import com.kim.ipc.BinderIpcManager;
import com.yanantec.ynbus.YnContentProvider;
import com.yanantec.ynbus.annotation.handler.YnBusAnnotationHandler;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


/**
 * @author : wengliuhu
 * @version : 0.1
 * @since : 2021/6/24 14:52
 * Describe：
 */
public class App1ContentProvider extends ContentProvider {
    @Override
    public boolean onCreate() {
        Log.d("kim", "-----------App1ContentProvider----------");
        Application app = (Application)this.getContext().getApplicationContext();
        BinderIpcManager.getInstance().init();
        app.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
                if (activity instanceof LifecycleOwner) {
                    injectBus((LifecycleOwner)activity);
                }

            }

            public void onActivityStarted(@NonNull Activity activity) {
            }

            public void onActivityResumed(@NonNull Activity activity) {
            }

            public void onActivityPaused(@NonNull Activity activity) {
            }

            public void onActivityStopped(@NonNull Activity activity) {
            }

            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
            }

            public void onActivityDestroyed(@NonNull Activity activity) {
            }
        });
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    private void injectBus(LifecycleOwner lifecycleOwner) {
        Method[] methods = lifecycleOwner.getClass().getDeclaredMethods();
        if (methods != null && methods.length != 0) {
            List<Method> allMethods = new ArrayList();
            allMethods.addAll(Arrays.asList(methods));
            if (lifecycleOwner.getClass().getSuperclass() != null) {
                Method[] superMethods = lifecycleOwner.getClass().getSuperclass().getDeclaredMethods();
                if (superMethods != null && superMethods.length != 0) {
                    allMethods.addAll(Arrays.asList(superMethods));
                }
            }

            YnBusAnnotationHandler handler = new YnBusAnnotationHandler();
            Iterator var5 = allMethods.iterator();

            while(true) {
                Method method;
                Annotation[] annotations;
                do {
                    do {
                        if (!var5.hasNext()) {
                            handler.handleMethodAnnotation(lifecycleOwner);
                            return;
                        }

                        method = (Method)var5.next();
                        annotations = method.getAnnotations();
                    } while(annotations == null);
                } while(annotations.length == 0);

                Annotation[] var8 = annotations;
                int var9 = annotations.length;

                for(int var10 = 0; var10 < var9; ++var10) {
                    Annotation annotation = var8[var10];
                    handler.collectMethodAnnotation(method, annotation);
                }
            }
        }
    }
}
