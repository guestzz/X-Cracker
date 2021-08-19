package com.example.wificrack;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;

import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

import static com.example.wificrack.WifiAdmin.isWifi;
import static java.lang.Thread.sleep;

public class CrackActivity extends AppCompatActivity {

    private boolean mHasPermission;
    private Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() {
        public void run() {
            //前端显示GIF动图
            GifImageView gifImageView1 = (GifImageView) findViewById(R.id.gifcalculate);
            GifDrawable gifDrawable = null;
            try {
                gifDrawable = new GifDrawable(getResources(), R.drawable.calculating);
            } catch (IOException e) {
                e.printStackTrace();
            }
            gifImageView1.setImageDrawable(gifDrawable);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crack);
        mHasPermission = checkPermission();
        if (!mHasPermission) {
            requestPermission();
        }

        //GIF线程
        mHandler.post(mRunnable);

        //破解WIFI密码
        try {
            String ssid="东南炮王506";
            String password="a506yyds";
            if (ConnectWifi(ssid,password,2)) {
                Intent intent = new Intent(CrackActivity.this, SuccessActivity.class);
                Bundle bundle=new Bundle();
                bundle.putCharSequence("ssid",ssid);
                bundle.putCharSequence("password",password);
                intent.putExtras(bundle);
                startActivity(intent);

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private boolean ConnectWifi(String ssid, String password, int type) throws InterruptedException {
        WifiAdmin wifiAdmin = new WifiAdmin(this);
        //wifiAdmin.openWifi();
        wifiAdmin.addNetwork(wifiAdmin.CreateWifiInfo(ssid, password, type));
        sleep(1500);
        return isWifi(CrackActivity.this);
    }

    private static final String[] NEEDED_PERMISSIONS = new String[] {
            Manifest.permission.CHANGE_NETWORK_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CHANGE_WIFI_MULTICAST_STATE
    };

    private boolean checkPermission() {
        for (String permission : NEEDED_PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }

    private static final int PERMISSION_REQUEST_CODE = 0;

    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
                NEEDED_PERMISSIONS, PERMISSION_REQUEST_CODE);
    }
}

