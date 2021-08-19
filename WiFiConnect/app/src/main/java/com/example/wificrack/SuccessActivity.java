package com.example.wificrack;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class SuccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);

        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();

        String ssid=bundle.getString("ssid");
        String password=bundle.getString("password");

        TextView myTextView1 = (TextView) findViewById(R.id.successInfo);
        myTextView1.setText("破解成功！");

        TextView myTextView2 = (TextView) findViewById(R.id.ssid);
        myTextView2.setText("WIFI名称："+ssid);

        TextView myTextView3 = (TextView) findViewById(R.id.password);
        myTextView3.setText("WIFI密码："+password);

    }
}