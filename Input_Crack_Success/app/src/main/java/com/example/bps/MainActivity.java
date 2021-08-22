package com.example.bps;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;

import static com.example.bps.Correlation_Coefficient.getPearsonCorrelationScore;
import static com.example.bps.GenerateFile.generate_file;
import static com.example.bps.non_zero.non_zero;
import static com.example.bps.AverageDistance.AverageDistance;
import static java.lang.Float.NaN;
import static java.lang.Float.POSITIVE_INFINITY;
import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {
    public static String PcapAddr="/sdcard/temp/555.pcap";
    public static String TxtAddr="/sdcard/temp/Triple.txt";
    public static String VideoAddr="/storage/emulated/0/DCIM/Camera/VID_20210723_201945.mp4";
    private long starttime=0;
    private long endtime=0;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.myFrame);

        //第一步，进入root权限抓包
        try {
            Process process = Runtime.getRuntime().exec("su");
            InputStreamReader in = new InputStreamReader(process.getInputStream());
            DataOutputStream out = new DataOutputStream(process.getOutputStream());
            BufferedReader br = new BufferedReader(in);
            out.writeBytes("nexutil -m2 \n");
            out.writeBytes("LD_PRELOAD=libnexmon.so tcpdump -n -i wlan0 -c 50000 -w /sdcard/temp/555.pcap \n");
            out.writeBytes("exit \n");
            System.out.println("Capturing...");
            out.flush();
            String line;
            while ((line = br.readLine()) != null){
                Log.i("output==>",line);
            }
            br.close();
            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Capture Done!");


        //第二步：选取视频，获取视频的bps
        Videobps videobps=new Videobps();
        videobps.CalVideoBps(getExternalCacheDir().toString());
        double []VideoBps;
        VideoBps=videobps.getBps();
        videobps.displaybps();

        starttime =System.currentTimeMillis();

        //第三步，解析pcap报文生成Triple文件
        FirstTest firstTest=new FirstTest();
        try {
            firstTest.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //第四步，读取文件，获得pcap报文的bps
        CalPacpBps calPacpBps=new CalPacpBps();
        try {
            calPacpBps.readfile();
            calPacpBps.printbps();
        } catch (IOException e) {
            e.printStackTrace();
        }
        HashMap<String, double[]> PacpBps = calPacpBps.getBps();

        //第五步，计算相似度生成特征矩阵
        int finalsize=Math.min(calPacpBps.getPcap_record_time(),videobps.getDuration());
        HashMap<String,double[]> Similarity=new HashMap<>();
        for(String key: PacpBps.keySet()){
            double []temp=new double[3];
            temp[0]=getPearsonCorrelationScore(VideoBps,PacpBps.get(key),finalsize);
            temp[1]=non_zero(PacpBps.get(key),finalsize);
            temp[2]=AverageDistance(VideoBps,PacpBps.get(key),finalsize);
            Similarity.put(key,temp);
        }

        //第六步，生成文件准备传输至服务器
        try {
            generate_file(Similarity);
        } catch (IOException e) {
            e.printStackTrace();
        }
        endtime =System.currentTimeMillis();
        System.out.println("程序运行时间: "+(double)(endtime-starttime)/1000+"s");
  }
}


