package com.kim.app2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.kim.annotation.Server;
import com.kim.bean.Student;
import com.kim.ipc.Message;
import com.kim.ipc.BinderIpcManager;
import com.kim.ipc.BinderIpcMessenger;
import com.kim.ipc.messenger.IMessageLisenter;
import com.kim.ipc.server.TcpServer;
import com.kim.ipc.server.UdpMulticastServer;
import com.yanantec.ynbus.annotation.OnMessage;

//@Server(serverPackageNames = {"com.kim.kimipc", "com.kim.ipcapp3"})
@Server(serverPackageNames = {"com.kim.kimipc"})
public class MainActivity extends AppCompatActivity {
    private final String TAG = "kimipc2222";
    private EditText sendEt;
    private TextView receiveTv;
    private Button sendBtn, nameBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sendEt = findViewById(R.id.et);
        receiveTv = findViewById(R.id.tv);
        sendBtn = findViewById(R.id.btn);
        nameBtn = findViewById(R.id.btn2);

//        IpcMessager.getInstance(getApplication()).addIpcConnectionLisenter(IpcManager.COM_KIM_IPCAPP3, new OnIpcConnectionLisenter() {
//            @Override
//            public boolean onConected(String packageName) {
//                Toast.makeText(MainActivity.this, IpcManager.COM_KIM_IPCAPP3 + "连接成功!", Toast.LENGTH_LONG).show();
//                return false;
//            }
//
//            @Override
//            public boolean onDisConected(String packageName) {
//                Toast.makeText(MainActivity.this, IpcManager.COM_KIM_IPCAPP3 + "断开连接!", Toast.LENGTH_LONG).show();
//                Log.d(TAG, "-------------------onDisConected:--------" + packageName);
//                return false;
//            }
//        });
        BinderIpcMessenger.getInstance().bindServiceForever(BinderIpcManager.COM_KIM_KIMIPC);
//        IpcMessager.getInstance(getApplication()).bindServiceForever(IpcManager.COM_KIM_IPCAPP3);
        BinderIpcMessenger.getInstance().addMessageLisenter(new IMessageLisenter() {
            @Override
            public void onMessage(Message message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        StringBuilder stringBuilder = new StringBuilder(receiveTv.getText());
                        stringBuilder.append("接收到信息:" +message.getMessage() + "\n");
                        receiveTv.setText(stringBuilder.toString());
                    }
                });
            }
        });
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (TextUtils.isEmpty(sendEt.getText().toString())) return;
                    BinderIpcMessenger.getInstance().sendMessage("key", sendEt.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        nameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    BinderIpcMessenger.getInstance().send2Method("name", "你好");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

//        new UdpBroadcastServer(getApplication()).start();
        new UdpMulticastServer().start();
        new TcpServer().start();
    }

    @OnMessage(value = "key", always = true)
    public void onGetStudent(Student student){
        Log.d("kimAPP2", "----onGetStudent---" + student.getName());
    }
}