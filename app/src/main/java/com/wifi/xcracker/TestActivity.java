package com.wifi.xcracker;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        int type=bundle.getInt("type");
        String SSID=bundle.getString("SSID");

        }
    }

