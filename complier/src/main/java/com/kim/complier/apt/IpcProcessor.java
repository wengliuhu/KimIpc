package com.kim.complier.apt;

import com.google.auto.service.AutoService;
import com.kim.annotation.IpcMethod;
import com.kim.annotation.Server;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.rmi.Remote;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

/**
 * @author : wengliuhu
 * @version : 0.1
 * @since : 2021/6/17 15:47
 * Describe：
 */
@AutoService(Processor.class)
public class IpcProcessor extends BaseProcessor{
    private boolean dealed;
    private AnnotationEntity annotationEntity;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (dealed) return false;
        try {
            filter(roundEnv);
            generateJavaFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        dealed = true;
        return super.process(annotations, roundEnv);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set set =  super.getSupportedAnnotationTypes();
        set.add(Server.class.getCanonicalName());
        set.add(IpcMethod.class.getCanonicalName());
        return set;
    }

    private void filter(RoundEnvironment roundEnv){
        Set<? extends Element> serverElements = roundEnv.getElementsAnnotatedWith(Server.class);
        Set<? extends Element> ipcMethodElements = roundEnv.getElementsAnnotatedWith(IpcMethod.class);
        Iterator<? extends Element> serverIterator = serverElements.iterator();
        annotationEntity = new AnnotationEntity();
        annotationEntity.thisPackageName = PACKAGE_NAME;
        while (serverIterator.hasNext()){
            Element element =  serverIterator.next();
            if (!(element instanceof TypeElement)) continue;
            TypeElement serverTypeElement = (TypeElement) element;
            String[] packageNames = serverTypeElement.getAnnotation(Server.class).serverPackageNames();
            if (packageNames != null && packageNames.length > 0){
                for (int i = 0; i < packageNames.length; i ++){
                    if (annotationEntity.serverApp.contains(packageNames[i])) continue;
                    annotationEntity.serverApp.add(packageNames[i]);
                }
            }
        }

        Iterator<? extends Element> ipcMethodIterator = ipcMethodElements.iterator();
        while (ipcMethodIterator.hasNext()){
            Element element =  ipcMethodIterator.next();
            if (!(element instanceof ExecutableElement)) continue;
            ExecutableElement ipcMethodExecutableElement = (ExecutableElement) element;
            String key = ipcMethodExecutableElement.getAnnotation(IpcMethod.class).key();
            if (key != null && key.trim().length() > 0){
                if (annotationEntity.registerMethods.get(key) != null){
                    mLogUtil.e("@IpcMethod has two same key on the method " + ipcMethodExecutableElement.getSimpleName().toString()
                            + " and " + annotationEntity.registerMethods.get(key));
                    return;
                }
                annotationEntity.registerMethods.put(key, ipcMethodExecutableElement.getEnclosingElement().toString() + "." +ipcMethodExecutableElement.getSimpleName());
            }
        }

    }

    private void generateJavaFile(){
        if (annotationEntity == null) return;
        // 先生成service
//        generateService();
        // 生成注册的方法的集合
        generateServiceConnections();

        //

    }

    private void generateServiceConnections(){
        final String ipcPackageName = "com.kim.ipc";

        TypeSpec.Builder builder =TypeSpec.classBuilder("BinderIpcManager");
        // 构建静态全局变量
        FieldSpec.Builder instanceFsB = FieldSpec.builder(ClassName.get(ipcPackageName, "BinderIpcManager"), "mInstance")
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC);

        FieldSpec.Builder ipcMessagerFsB = FieldSpec.builder(ClassName.get(ipcPackageName, "BinderIpcMessenger"), "ipcMessage")
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                .initializer("$T.getInstance()", ClassName.get("com.kim.ipc", "BinderIpcMessenger"));

        FieldSpec.Builder ipcMethodFsB = FieldSpec.builder(ClassName.get("com.kim.ipc", "IpcMethod"), "ipcMethod")
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                .initializer("$T.getInstance()", ClassName.get("com.kim.ipc", "IpcMethod"));

        // 构建类
        TypeSpec.Builder ipcManagerTsB = builder
                .addModifiers(Modifier.PUBLIC);

        // 生成构造函数
        MethodSpec.Builder ipcManagerMsB = MethodSpec.methodBuilder("BinderIpcManager")
                .addModifiers(Modifier.PRIVATE);

        MethodSpec.Builder getInstanceMsB = MethodSpec.methodBuilder("getInstance")
//                .addParameter(ParameterSpec.builder(ClassName.get("android.app", "Application"), "application").build())
                .addStatement("if (mInstance == null){\n" +
                        " synchronized (BinderIpcManager.class){\n" +
                        " if (mInstance == null){\n" +
                        " mInstance = new BinderIpcManager();\n" +
                        "  }\n" +
                        " }\n" +
                        "}\n" +
                        " return mInstance")
                .returns(ClassName.get(ipcPackageName, "BinderIpcManager"))
                .addModifiers(Modifier.STATIC, Modifier.PUBLIC);
        // init()函数
        MethodSpec.Builder initMsB = MethodSpec.methodBuilder("init")
//                .addStatement("ipcMessage = $T.getInstance()", ClassName.get("com.kim.ipc", "IpcMessager"))
                .addStatement("ipcMessage.clearServerConnection()")
                .addModifiers(Modifier.PUBLIC)
                .addJavadoc("初始化参数");
        if (annotationEntity.serverApp != null && annotationEntity.serverApp.size() > 0){
            for (int i = 0; i < annotationEntity.serverApp.size(); i ++)
            {
                String serverPackageName = annotationEntity.serverApp.get(i);
                initMsB.addStatement("ipcMessage.addServerConnection($S,serviceConnection" + i + ")", annotationEntity.serverApp.get(i));
                // 添加全局静态变量
                FieldSpec.Builder f = FieldSpec.builder(String.class, serverPackageName.replace(".", "_").toUpperCase(), Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
                        .initializer("$S", serverPackageName);
                ipcManagerTsB.addField(f.build());
            }
        }
        if (annotationEntity.registerMethods != null && annotationEntity.registerMethods.size() > 0){
            for (Map.Entry<String, String> entry: annotationEntity.registerMethods.entrySet()) {
                initMsB.addStatement("ipcMethod.addIpcMethod($S, $S)", entry.getKey(), entry.getValue());
            }
        }

        // 全局变量ServiceConnection
        List<FieldSpec.Builder> connections = createConnections();
        Iterator<FieldSpec.Builder> connectionIterator = connections.iterator();
        ipcManagerTsB
                .addField(instanceFsB.build())
                .addField(ipcMessagerFsB.build())
                .addField(ipcMethodFsB.build())
                .addMethod(ipcManagerMsB.build())
                .addMethod(getInstanceMsB.build())
                .addMethod(initMsB.build());

        while (connectionIterator.hasNext()){
            ipcManagerTsB.addField(connectionIterator.next()
                    .addModifiers(Modifier.PROTECTED).build());
        }
        // 生成.java文件
        JavaFile javaFile = JavaFile.builder(ipcPackageName, ipcManagerTsB.build()).build();
        try
        {
            javaFile.writeTo(mFiler);
        } catch (Exception e)
        {
            e.printStackTrace();
            mMessager.printMessage(Diagnostic.Kind.ERROR, "写文件错误:" + e);
        }

    }

    private void generateService(){
        // 反射获取applicationId
        final String ipcPackageName = "com.kim.ipc";
        TypeSpec.Builder builder =TypeSpec.classBuilder("KimIpcService");
        // 全局变量
        // #1.       public Map<String, IMessageServer> listeners = new HashMap<>();
        ClassName hashMapClasssName = ClassName.get("java.util","HashMap");
        ClassName messageServerClassName = ClassName.get(ipcPackageName, "IMessageServer");
        TypeName messagerMapClassName = ParameterizedTypeName.get(hashMapClasssName, ClassName.get(String.class), messageServerClassName);
        FieldSpec messageServerFieldSpec = FieldSpec.builder(messagerMapClassName, "mServers", Modifier.PUBLIC)
                .initializer("new $T()", messagerMapClassName)
                .addJavadoc("缓存的服务端的binder")
                .build();

        // #2.     private IpcMessager ipcMessager = new IpcMessager();
        FieldSpec ipcMessagerFieldSpec = FieldSpec.builder(ClassName.get(ipcPackageName, "IpcMessager"), "mIpcMessager")
                .initializer("new IpcMessager()")
                .addModifiers(Modifier.PRIVATE)
                .addJavadoc("IPC包下 事件处理类")
                .build();

        // #3.      private static Map<String, ServiceConnection> serviceConnectionMap = new ArrayMap<>();
        ClassName connectionClassName = ClassName.get("android.content", "ServiceConnection");
        TypeName connectionMapClassName = ParameterizedTypeName.get(hashMapClasssName, ClassName.get(String.class), connectionClassName);
        FieldSpec connectionMapFieldSpec = FieldSpec.builder(connectionMapClassName, "mConnectionMap", Modifier.PRIVATE, Modifier.STATIC)
                .initializer("new $T()", connectionMapClassName)
                .addJavadoc("存储的服务端的连接 ServerConnection")
                .build();

        // #4   private final IMessageServer.Stub stub = new IMessageServer.Stub() {
        //        @Override
        //        public void sendMessage(String from, String msgType, String messageKey, String messageJsonString) throws RemoteException {
        //            try {
        //                ipcMessager.onReceiveMessage(from, msgType, messageKey, messageJsonString);
        //            } catch (Exception e) {
        //                e.printStackTrace();
        //            }
        //        }
        //    };
        ClassName stubClassName = ClassName.get("com.kim.ipc", "IMessageServer.Stub");
        // 重写的方法
        MethodSpec sendMessageMethodSpec = MethodSpec.methodBuilder("sendMessage")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterSpec.builder(String.class, "from").build())
                .addParameter(ParameterSpec.builder(String.class, "messageType").build())
                .addParameter(ParameterSpec.builder(String.class, "messageKey").build())
                .addParameter(ParameterSpec.builder(String.class, "messageJsonString").build())
                .addAnnotation(Override.class)
                .addStatement("try {\n" +
                        "mIpcMessager.onReceiveMessage(from, messageType, messageKey, messageJsonString);\n" +
                        "} catch ($T e) {\n" +
                        "e.printStackTrace();\n" +
                        "}", Exception.class)
                .addException(ClassName.get("android.os", "RemoteException"))
                .build();

        TypeSpec  stubTypeSpec  = TypeSpec.anonymousClassBuilder("")
                .addSuperinterface(stubClassName)
                .addMethod(sendMessageMethodSpec)
                .build();
        FieldSpec stubFieldSpec = FieldSpec.builder(stubClassName, "stub")
                .initializer("$L", stubTypeSpec)
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                .build();

        // #5    @Override
        //    public IBinder onBind(Intent intent) {
        //        return stub;
        //    }
        MethodSpec onBindMs = MethodSpec.methodBuilder("onBind")
                .addAnnotation(Override.class)
                .addParameter(ParameterSpec.builder(ClassName.get("android.content", "Intent"), "intent").build())
                .addStatement("return stub")
                .returns(ClassName.get("android.os", "IBinder"))
                .addModifiers(Modifier.PUBLIC)
                .build();

        // #6   @Override
        //    public void onCreate() {
        //        super.onCreate();
        //        serviceConnectionMap.clear();
        //        serviceConnectionMap.put("packageName1", serviceConnection1);
        //    }
        MethodSpec.Builder onCreateMsB = MethodSpec.methodBuilder("onCreate")
                .addAnnotation(Override.class)
                .addStatement("super.onCreate()")
                .addStatement("mConnectionMap.clear()")
                .addModifiers(Modifier.PUBLIC);
        if (annotationEntity.serverApp != null && annotationEntity.serverApp.size() > 0){
            for (int i = 0; i < annotationEntity.serverApp.size(); i ++){
                onCreateMsB.addStatement("mConnectionMap.put($S, " + "serviceConnection" + i + ")", annotationEntity.serverApp.get(i));
            }
        }

        // #6   private ServiceConnection serviceConnection1 = new ServiceConnection() {
        //        @Override
        //        public void onServiceConnected(ComponentName name, IBinder service) {
        //            mServers.put(name.toString(), IMessageServer.Stub.asInterface(service));
        //            Log.d(TAG, "-----onServiceConnected--" + service);
        //        }
        //
        //        @Override
        //        public void onServiceDisconnected(ComponentName name) {
        //            mServers.remove(name.toString());
        //            Log.d(TAG, "-----onServiceDisconnected--" + name.getClassName());
        //        }
        //    };
        List<FieldSpec.Builder> connections = createConnections();

        // 构建bindService方法
        MethodSpec bindServiceMethodSpec = MethodSpec.methodBuilder("bindService")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(ParameterSpec.builder(ClassName.get("android.content", "Context"), "context").build())
                .addParameter(ParameterSpec.builder(String.class, "packageName").build())
                .addStatement("ServiceConnection serviceConnection = mConnectionMap.get(packageName)")
                .addStatement("if (serviceConnection == null) return")
                .addStatement("$T intent = new Intent()", ClassName.get("android.content", "Intent"))
                .addStatement("intent.setAction($S)", ipcPackageName)
                .addStatement("intent.setPackage(packageName)")
                .addStatement(" context.bindService(intent, mConnectionMap.get(packageName), Context.BIND_AUTO_CREATE)")
                .addJavadoc("启动服务端的binder")
                .build();

        ClassName serviceCn = ClassName.get("android.app", "Service");
        // 构建类
        TypeSpec.Builder ipcServiceTsB = builder
                .addField(messageServerFieldSpec)
                .addField(ipcMessagerFieldSpec)
                .addField(connectionMapFieldSpec)
                .addField(stubFieldSpec)
                .addMethod(onCreateMsB.build())
                .addMethod(onBindMs)
                .addMethod(bindServiceMethodSpec)
                .superclass(serviceCn)
                .addModifiers(Modifier.PUBLIC);

        Iterator<FieldSpec.Builder> connectionIterator = connections.iterator();
        while (connectionIterator.hasNext()){
            ipcServiceTsB.addField(connectionIterator.next().build());
        }

        // 超类

        // 生成.java文件
        JavaFile javaFile = JavaFile.builder(ipcPackageName, ipcServiceTsB.build()).build();
        try
        {
            javaFile.writeTo(mFiler);
        } catch (Exception e)
        {
            e.printStackTrace();
            mMessager.printMessage(Diagnostic.Kind.ERROR, "写文件错误:" + e);
        }
    }

    private List<FieldSpec.Builder> createConnections(){
        List<FieldSpec.Builder> connections = new ArrayList<>();
        if (annotationEntity.serverApp != null && annotationEntity.serverApp.size() > 0){
            for (int i = 0; i < annotationEntity.serverApp.size(); i ++)
            {
                String packageName = annotationEntity.serverApp.get(i);
                // 继承的接口
                ClassName serviceConnectionClassName = ClassName.get("android.content", "ServiceConnection");
                // 方法参数
                ParameterSpec componentNameParameterSpec = ParameterSpec.builder(ClassName.get("android.content", "ComponentName"), "name").build();
                ParameterSpec binderParameterSpec = ParameterSpec.builder(ClassName.get("android.os", "IBinder"), "service").build();
                // 重写的方法
                MethodSpec onServiceConnectedMethodSpec = MethodSpec.methodBuilder("onServiceConnected")
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(componentNameParameterSpec)
                        .addParameter(binderParameterSpec)
                        .addAnnotation(Override.class)
//                        .addStatement("IpcMessager ipcMessage = $T.getInstance()", ClassName.get("com.kim.ipc", "IpcMessager"))
                        .addStatement("ipcMessage.addMessageServer($S, IMessageServer.Stub.asInterface(service))", packageName)
//                        .addStatement("mServers.put(name.toString(), IMessageServer.Stub.asInterface(service))")
                        .build();
                MethodSpec onServiceDisconnectedMethodSpec = MethodSpec.methodBuilder("onServiceDisconnected")
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(componentNameParameterSpec)
//                        .addStatement("IpcMessager ipcMessage = $T.getInstance()", ClassName.get("com.kim.ipc", "IpcMessager"))
                        .addStatement("ipcMessage.removeMessageServer($S)", packageName)
//                        .addStatement("mServers.remove(name.toString())")
                        .addAnnotation(Override.class)
                        .build();
                MethodSpec onBindingDiedMethodSpec = MethodSpec.methodBuilder("onBindingDied")
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(componentNameParameterSpec)
//                        .addStatement("IpcMessager ipcMessage = $T.getInstance()", ClassName.get("com.kim.ipc", "IpcMessager"))
                        .addStatement("ipcMessage.removeMessageServer($S)", packageName)
//                        .addStatement("mServers.remove(name.toString())")
                        .addAnnotation(Override.class)
                        .build();
                TypeSpec  serviceConnectionTypeSpec  = TypeSpec.anonymousClassBuilder("")
                        .addSuperinterface(serviceConnectionClassName)
                        .addMethod(onServiceConnectedMethodSpec)
                        .addMethod(onServiceDisconnectedMethodSpec)
                        .addMethod(onBindingDiedMethodSpec)
                        .build();
                FieldSpec.Builder onServiceDisconnectedFieldSpec = FieldSpec.builder(serviceConnectionClassName,  "serviceConnection" + i)
                        .initializer("$L", serviceConnectionTypeSpec)
                        .addJavadoc("包名为：“" + packageName + "” 对应的serviceConnection");
                connections.add(onServiceDisconnectedFieldSpec);
            }

        }
        return connections;
    }

    private class AnnotationEntity{
        String thisPackageName = "com.kim.ipc";
        // 注册的方法
        Map<String, String> registerMethods = new HashMap<>();
        // 作为服务端的app包名
        List<String> serverApp = new ArrayList<>();
    }
}
