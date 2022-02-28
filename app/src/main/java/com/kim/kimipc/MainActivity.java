package com.kim.kimipc;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.kim.annotation.Server;
import com.kim.bean.Student;
import com.kim.ipc.BinderIpcManager;
import com.kim.ipc.Message;
import com.kim.ipc.OnIpcLisenter;
import com.kim.ipc.BinderIpcMessenger;
import com.kim.ipc.messenger.BaseIpcMessenger;
import com.kim.ipc.messenger.IMessageLisenter;
import com.kim.ipc.messenger.TcpIpcMessenger;
import com.kim.ipc.server.TcpServer;
import com.kim.ipc.server.UdpMulticastServer;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

//@Server(serverPackageNames = {"com.kim.app2", "com.kim.ipcapp3", "com.kim.kimipc"})
@Server(serverPackageNames = {"com.kim.app2"})
public class MainActivity extends AppCompatActivity {
    private final String TAG = "kimipc111";
    private EditText sendEt;
    private TextView receiveTv;
    private Button sendBtn, nameBtn, secondBtn, btn_speak;
    int i = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sendEt = findViewById(R.id.et);
        receiveTv = findViewById(R.id.tv);
        sendBtn = findViewById(R.id.btn);
        nameBtn = findViewById(R.id.btn2);
        secondBtn = findViewById(R.id.btn3);
        btn_speak = findViewById(R.id.btn_speak);

        BinderIpcMessenger.getInstance().addIpcConnectionLisenter(BinderIpcManager.COM_KIM_APP2, new OnIpcLisenter() {
            @Override
            public boolean onConected(String packageName) {
                Toast.makeText(MainActivity.this, BinderIpcManager.COM_KIM_APP2 + "连接成功!", Toast.LENGTH_LONG).show();
                return false;
            }

            @Override
            public boolean onDisConected(String packageName) {
                Toast.makeText(MainActivity.this, BinderIpcManager.COM_KIM_APP2 + "断开连接!", Toast.LENGTH_LONG).show();
                Log.d(TAG, "-------------------onDisConected:--------" + packageName);
                return false;
            }
        });
        BinderIpcMessenger.getInstance().bindServiceForever(BinderIpcManager.COM_KIM_APP2);
//        IpcMessager.getInstance(getApplication()).bindServiceForever(IpcManager.COM_KIM_IPCAPP3);
//        IpcMessager.getInstance(getApplication()).bindServiceForever(IpcManager.COM_KIM_KIMIPC);
        BinderIpcMessenger.getInstance().addMessageLisenter("all", new IMessageLisenter() {

            @Override
            public void onMessage(Message message) {

            }
        });
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (TextUtils.isEmpty(sendEt.getText().toString())) return;
                    Student student = new Student();
                    student.setName("小王");
                    student.setAge(12);
                    BinderIpcMessenger.getInstance().sendMessage("key", student);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        nameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    Message message = new Message.Builder()
                            .message("你好")
                            .messageKey("name")
                            .type(Message.IPC_TYPE_METHOD)
                            .messageClass(String.class.getName())
                            .fromAPP(getApplication().getPackageName())
                            .toApp(getApplication().getPackageName())
                            .build();
                    final int msgId = TcpIpcMessenger.getInstance().sendMessage(message);
                    TcpIpcMessenger.getInstance().addMessageLisenter("name", new IMessageLisenter() {
                        @Override
                        public void onMessage(Message message) {
                            // 接收消息
                            if (message.getMessageBackId() == msgId){
                                Log.d("kim", "----------message back------:" + message.getMessage());
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        });

        secondBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    BinderIpcMessenger.getInstance().send2Method("gotoSecondActivity", "你好", String.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        TtsSpeaker.getInstance().init(this);
        btn_speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i++;
                TtsSpeaker.getInstance().addMessageFlush("[p1000] 放大" + i + "倍");
            }
        });

//        new UdpBroadcastServer(getApplication()).start();
        new UdpMulticastServer().start();
        new TcpServer().start();
    }


}