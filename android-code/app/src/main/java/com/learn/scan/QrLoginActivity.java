package com.learn.scan;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.learn.scan.bean.RResult;
import com.learn.scan.utils.NetworkUtil;

import java.util.HashMap;

public class QrLoginActivity extends AppCompatActivity {
    private String qrcodeString;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrlogin);

        initData();
        login();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            handlerMessage(msg);
        }
    };

    public void handlerMessage(Message msg) {
        switch (msg.what) {
            case 100:
                RResult rResult = (RResult) msg.obj;
//
                Toast.makeText(this, rResult.getMsg(), Toast.LENGTH_SHORT).show();
                finish();
                break;
        }
    }

    private void initData() {
        Intent intent = getIntent();
        qrcodeString = intent.getStringExtra("qrcodeString");
    }

    private void login() {
        Button agreeBt = (Button) findViewById(R.id.qrcode_login_bt);
        agreeBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    @Override
                    public void run() {
                        Message message = Message.obtain(handler, 100);

                        HashMap<String, String> params = new HashMap<>();
                        params.put("user_id", "100");
                        params.put("token", "this is used to test!");
                        params.put("qrcode_string", qrcodeString);

                        String jsonString = NetworkUtil.doPost(
                                "http://192.168.0.105/scan-kit-test/api.php", params);
//                        System.out.println("json: " + jsonString);

                        RResult rResult = JSON.parseObject(jsonString, RResult.class);
                        message.obj = rResult;

//                        System.out.println("result: " + rResult);

                        message.sendToTarget();
                    }
                }.start();
            }
        });
    }
}
