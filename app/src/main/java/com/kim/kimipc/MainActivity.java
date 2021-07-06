package com.kim.kimipc;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kim.annotation.Server;
import com.kim.ipc.IMessageLisenter;
import com.kim.ipc.IpcManager;
import com.kim.ipc.IpcMessager;
import com.kim.ipc.OnIpcConnectionLisenter;

//@Server(serverPackageNames = {"com.kim.app2", "com.kim.ipcapp3", "com.kim.kimipc"})
@Server(serverPackageNames = {"com.kim.app2"})
public class MainActivity extends AppCompatActivity {
    private final String TAG = "kimipc111";
    private EditText sendEt;
    private TextView receiveTv;
    private Button sendBtn, nameBtn, secondBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sendEt = findViewById(R.id.et);
        receiveTv = findViewById(R.id.tv);
        sendBtn = findViewById(R.id.btn);
        nameBtn = findViewById(R.id.btn2);
        secondBtn = findViewById(R.id.btn3);

        IpcMessager.getInstance(getApplication()).addIpcConnectionLisenter(IpcManager.COM_KIM_APP2, new OnIpcConnectionLisenter() {
            @Override
            public boolean onConected(String packageName) {
                Toast.makeText(MainActivity.this, IpcManager.COM_KIM_APP2 + "连接成功!", Toast.LENGTH_LONG).show();
                return false;
            }

            @Override
            public boolean onDisConected(String packageName) {
                Toast.makeText(MainActivity.this, IpcManager.COM_KIM_APP2 + "断开连接!", Toast.LENGTH_LONG).show();
                Log.d(TAG, "-------------------onDisConected:--------" + packageName);
                return false;
            }
        });
        IpcMessager.getInstance(getApplication()).bindServiceForever(IpcManager.COM_KIM_APP2);
//        IpcMessager.getInstance(getApplication()).bindServiceForever(IpcManager.COM_KIM_IPCAPP3);
//        IpcMessager.getInstance(getApplication()).bindServiceForever(IpcManager.COM_KIM_KIMIPC);
        IpcMessager.getInstance(getApplication()).addMessageLisenter(new IMessageLisenter() {
            @Override
            public void onMessage(String key, String value) {
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
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (TextUtils.isEmpty(sendEt.getText().toString())) return;
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i < 1000; i ++){
                        stringBuilder.append("0123456789**********0123456789**********0123456789**********0123456789**********0123456789**********");
                        stringBuilder.append("0123456789**********0123456789**********0123456789**********0123456789**********0123456789**********");
                        stringBuilder.append("0123456789**********0123456789**********0123456789**********0123456789**********0123456789**********");
                        stringBuilder.append("0123456789**********0123456789**********0123456789**********0123456789**********0123456789**********");
                        stringBuilder.append("0123456789**********0123456789**********0123456789**********0123456789**********0123456789**********");
                        stringBuilder.append("0123456789**********0123456789**********0123456789**********0123456789**********0123456789**********");
                        stringBuilder.append("0123456789**********0123456789**********0123456789**********0123456789**********0123456789**********");
                        stringBuilder.append("0123456789**********0123456789**********0123456789**********0123456789**********0123456789**********");
                        stringBuilder.append("0123456789**********0123456789**********0123456789**********0123456789**********0123456789**********");
                        stringBuilder.append("0123456789**********0123456789**********0123456789**********0123456789**********0123456789**********");
                    }
                    IpcMessager.getInstance(getApplication()).sendMessage("key", stringBuilder.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        nameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    IpcMessager.getInstance(getApplication()).send2Method("name", "你好");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        secondBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    IpcMessager.getInstance(getApplication()).send2Method("gotoSecondActivity", "你好");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}