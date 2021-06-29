1.在Project的gradle文件中添加远程仓库地址
```groovy
allprojects {
    repositories {
        // 添加maven中央仓库
        mavenCentral()
    }
}

```
###### 2.添加依赖包并同步：

```groovy
	// 注解依赖包 
   	implementation "io.github.wengliuhu:KimIpc_annotation:1.0"
   	// IPC库依赖包
    implementation "io.github.wengliuhu:KimIpc_ipc:1.0"
    // apt动态编译包
    annotationProcessor "io.github.wengliuhu:KimIpc_complier:1.0"
    
```
###### 3.添加需要交互的进程的包名（任意类上添加注解都可以，为了方便查找，可以定义一个类IpcUtil）：

```groovy
// "com.kim.app2"和 "com.kim.ipcapp3"是用以创建这两个包名的进程下的客户端代码，即 "com.kim.app2"和 "com.kim.ipcapp3"为服务端。
@Server(serverPackageNames = {"com.kim.app2", "com.kim.ipcapp3", "com.kim.kimipc"})
```
###### 4.给开放给其他进程调用的静态方法添加注解：

```groovy
	// 注意：
	// 1.此方法必须为静态方法，目前只支持有且只有一个string类型的参数；
	// 2.如果有返回类型，必须为String类型，如无返回类型，可以写为void类型。
	@IpcMethod(key = "gotoSecondActivity")
    public static String gotoSecondActivity(String msg){
        if (topActivity != null);
        Intent intent = new Intent(topActivity, SecondActivity.class);
        topActivity.startActivity(intent);
        return "准备跳转第二个界面";
    }
```
###### 5.开始编译生成IpcManager.java类（也可以点击Make Project来生成IpcManager.java）：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210629101213124.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlbmdsaXVodQ==,size_16,color_FFFFFF,t_70#pic_center)

编译后在此目录下即可找到编译出来的IpcManager类：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210629101404773.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlbmdsaXVodQ==,size_16,color_FFFFFF,t_70#pic_center)
###### 6.初始化IPC:

```java
 IpcManager.getInstance().init();
```
###### 7.启动需要监听的进程（此处开放给用户选择是为了可能当前界面只关注特定的进程消息）：

```java
// bindServiceForever(String packageName)为一直开启包名为packageName的进程的IPC
// bindService(Context context, String packageName)为只开启当前Activity的，后续有可能会断开连接

//此处为开启包名为IpcManager.COM_KIM_APP2（COM_KIM_APP2为注解@Server注册的包名的大写）的Service（即开启该包名所在进程的服务端）
IpcMessager.getInstance(getApplication()).bindServiceForever(IpcManager.COM_KIM_APP2);
IpcMessager.getInstance(getApplication()).bindServiceForever(IpcManager.COM_KIM_IPCAPP3);
```

###### 8.发送消息：

```java
// IpcMessager下的方法：
// 发送调用服务端注册的方法，methodName为@IpcMethod注解的key
send2Method(String methodName, String params）
// 发送给指定包名所在进程，调用该进程的注册方法
send2Method(String packageName, String methodName, String params)
// 发送消息
sendMessage(String key, String value)
// 发送给指定进程消息
sendMessage(String packageName, String key, String value)
```
###### 9.监听其他进程发送的消息

```java
IpcMessager.getInstance(getApplication()).addMessageLisenter(new IMessageLisenter() {
            @Override
            public void onMessage(String key, String value) {
            // 此处线程是跑在Binder线程池里面的，Binder方式是异步的。如果要在主线程需要切换
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        StringBuilder stringBuilder = new StringBuilder(receiveTv.getText());
//                        stringBuilder.append("接收到信息:" +value + "\n");
//                        receiveTv.setText(stringBuilder.toString());
//                    }
//                });
            }
        });
```
