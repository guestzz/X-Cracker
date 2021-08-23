package com.wifi.xcracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class ChooseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);

        //接收ScanActivity传来的加密方式和ssid
        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();

        String ssid=bundle.getString("SSID");
        int type=bundle.getInt("type");

        Button button3=(Button) findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1=new Intent(ChooseActivity.this,CrackActivity.class);
                Bundle bundle1=new Bundle();
                bundle1.putCharSequence("ssid",ssid);
                bundle1.putInt("type",type);
                bundle1.putInt("method",1);
                intent1.putExtras(bundle1);
                startActivity(intent1);
            }
        });

        Button button4=(Button) findViewById(R.id.button4);
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2=new Intent(ChooseActivity.this,CrackActivity.class);
                Bundle bundle2=new Bundle();
                bundle2.putCharSequence("ssid",ssid);
                bundle2.putInt("type",type);
                bundle2.putInt("method",2);
                intent2.putExtras(bundle2);
                startActivity(intent2);
            }
        });

        Button button5=(Button) findViewById(R.id.button5);
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent3=new Intent(ChooseActivity.this,InputActivity.class);
                Bundle bundle3=new Bundle();
                bundle3.putCharSequence("ssid",ssid);
                bundle3.putInt("type",type);
                bundle3.putInt("method",3);
                intent3.putExtras(bundle3);
                startActivity(intent3);
            }
        });
    }
}