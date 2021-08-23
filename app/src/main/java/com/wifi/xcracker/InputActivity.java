package com.wifi.xcracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class InputActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        //接收ChooseActivity传来的ssid,type,method
        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();

        String ssid=bundle.getString("ssid");
        int type=bundle.getInt("type");
        int method=bundle.getInt("method");
        System.out.println("Input Activity ssid: "+ssid);
        System.out.println("Input Activity type: "+type);
        System.out.println("Input Activity method: "+method);

        Button button=(Button) findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input_message=((TextView) findViewById(R.id.input)).getText().toString();
                Intent intent5=new Intent(InputActivity.this,CrackActivity.class);
                Bundle bundle5=new Bundle();
                bundle5.putCharSequence("input_message",input_message);
                bundle5.putCharSequence("ssid",ssid);
                bundle5.putInt("type",type);
                bundle5.putInt("method",method);
                intent5.putExtras(bundle5);
                startActivity(intent5);
            }
        });
    }
}