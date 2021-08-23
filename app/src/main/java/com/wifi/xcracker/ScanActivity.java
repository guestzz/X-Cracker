package com.wifi.xcracker;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.TreeMap;

import com.wifi.xcracker.task.HttpListener;
import com.wifi.xcracker.task.Request;
import com.wifi.xcracker.task.RequestExecutor;
import com.wifi.xcracker.task.RequestMethod;
import com.wifi.xcracker.task.Response;
import com.wifi.xcracker.util.Logger;
import static com.wifi.xcracker.WifiAdmin.isWifi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ScanActivity extends AppCompatActivity {
    private WifiManager mWifiManager;
    private Handler mMainHandler;
    private boolean mHasPermission;
    private boolean isconnect=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        isconnect=isWifi(this);

        //registerBroadcastReceiver();

        mWifiManager = (WifiManager)getApplicationContext().getSystemService(WIFI_SERVICE);
        mMainHandler = new Handler();

        findChildViews();

        configChildViews();

        mHasPermission = checkPermission();
        if (!mHasPermission) {
            requestPermission();
        }
    }

    Button mOpenWifiButton;
    Button mGetWifiInfoButton;
    RecyclerView mWifiInfoRecyclerView;

    private void findChildViews() {
        mOpenWifiButton = (Button)findViewById(R.id.open_wifi);
        mGetWifiInfoButton = (Button) findViewById(R.id.get_wifi_info);
        mWifiInfoRecyclerView = (RecyclerView) findViewById(R.id.wifi_info_detail);
    }

    private Runnable mMainRunnable = new Runnable() {
        @Override
        public void run() {
            if (mWifiManager.isWifiEnabled()) {
                mGetWifiInfoButton.setEnabled(true);
            } else {
                mMainHandler.postDelayed(mMainRunnable, 1000);
            }
        }
    };

    private List<ScanResult> mScanResultList;

    private void configChildViews() {
        mOpenWifiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mWifiManager.isWifiEnabled()) {
                    mWifiManager.setWifiEnabled(true);
                    mMainHandler.post(mMainRunnable);
                }
            }
        });

        mGetWifiInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mWifiManager.isWifiEnabled()) {
                    mScanResultList = mWifiManager.getScanResults();
                    sortList(mScanResultList);
                    mWifiInfoRecyclerView.getAdapter().notifyDataSetChanged();
                }
            }
        });

        mWifiInfoRecyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mWifiInfoRecyclerView.setAdapter(new ScanActivity.ScanResultAdapter());
    }

    private void sortList(List<ScanResult> list) {
        TreeMap<String, ScanResult> map = new TreeMap<>();
        for (ScanResult scanResult : list) {
            map.put(scanResult.SSID, scanResult);
        }
        list.clear();
        list.addAll(map.values());
    }

    private class ScanResultViewHolder extends RecyclerView.ViewHolder {
        private View mView;
        private TextView mWifiName;
        private TextView mWifiLevel;

        ScanResultViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            mWifiName = (TextView) itemView.findViewById(R.id.ssid);
            mWifiLevel = (TextView) itemView.findViewById(R.id.level);
        }

        void bindScanResult(final ScanResult scanResult) {
            mWifiName.setText(
                    getString(R.string.scan_wifi_name, "" + scanResult.SSID));
            mWifiLevel.setText(
                    getString(R.string.scan_wifi_level, "" + scanResult.level));


        //选中wifi后的执行
            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String encryption = scanResult.capabilities;
                    int type=0;
                    if (encryption != null) {
                        if (encryption.contains("WPA") || encryption.contains("wpa")) {
                            type=2;
                            Log.d("judgeEncryption", " wpa 方式加密");
                            //Constants.wifiEncription = 3;

                        } else if (encryption.contains("WEP") || encryption.contains("wep")) {
                            type=1;
                            Log.d( "judgeEncryption", " wep 方式加密");
                            //Constants.wifiEncription = 2;

                        }
                    } else {
                        type=0;
                        Log.d( "judgeEncryption", " 没有加密");
                        //Constants.wifiEncription = 1;
                    }

                    if (isconnect)
                    {
                        requestGet(scanResult.SSID,type);
                    }
                    else{
                        Intent intent=new Intent(ScanActivity.this,ChooseActivity.class);
                        Bundle bundle=new Bundle();
                        bundle.putInt("type",type);
                        bundle.putCharSequence("SSID",scanResult.SSID);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                }
            });
        }
    }

    private static final int WIFICIPHER_NOPASS = 0;
    private static final int WIFICIPHER_WEP = 1;
    private static final int WIFICIPHER_WPA = 2;

    private WifiConfiguration createWifiConfig(String ssid, String password, int type) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + ssid + "\"";

        WifiConfiguration tempConfig = isExist(ssid);
        if(tempConfig != null) {
            mWifiManager.removeNetwork(tempConfig.networkId);
        }

        if(type == WIFICIPHER_NOPASS) {
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        } else if(type == WIFICIPHER_WEP) {
            config.hiddenSSID = true;
            config.wepKeys[0]= "\""+password+"\"";
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        } else if(type == WIFICIPHER_WPA) {
            config.preSharedKey = "\""+password+"\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }

        return config;
    }

    private WifiConfiguration isExist(String ssid) {
        List<WifiConfiguration> configs = mWifiManager.getConfiguredNetworks();

        for (WifiConfiguration config : configs) {
            if (config.SSID.equals("\""+ssid+"\"")) {
                return config;
            }
        }
        return null;
    }

    private class ScanResultAdapter extends RecyclerView.Adapter<ScanActivity.ScanResultViewHolder> {
        @Override
        public ScanActivity.ScanResultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getApplicationContext())
                    .inflate(R.layout.item_scan_result, parent, false);

            return new ScanActivity.ScanResultViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ScanActivity.ScanResultViewHolder holder, int position) {
            if (mScanResultList != null) {
                holder.bindScanResult(mScanResultList.get(position));
            }
        }

        @Override
        public int getItemCount() {
            if (mScanResultList == null) {
                return 0;
            } else {
                return mScanResultList.size();
            }
        }
    }

    private static final String[] NEEDED_PERMISSIONS = new String[] {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
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

    @Override
    public void onResume() {
        super.onResume();
        if (mWifiManager.isWifiEnabled() && mHasPermission) {
            mGetWifiInfoButton.setEnabled(true);
        } else {
            mGetWifiInfoButton.setEnabled(false);
            if (mScanResultList != null) {
                mScanResultList.clear();
                mWifiInfoRecyclerView.getAdapter().notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean hasAllPermission = true;

        if (requestCode == PERMISSION_REQUEST_CODE) {
            for (int i : grantResults) {
                if (i != PackageManager.PERMISSION_GRANTED) {
                    hasAllPermission = false;
                    break;
                }
            }

            if (hasAllPermission) {
                mHasPermission = true;
            } else {
                mHasPermission = false;
                Toast.makeText(
                        this, "Need More Permission",
                        Toast.LENGTH_LONG)
                        .show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //unregisterBroadcastReceiver();
    }

//    private BroadcastReceiver mBroadcastReceiver;
//    private void registerBroadcastReceiver() {
//        mBroadcastReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                int state = intent.getIntExtra("wifi_state", 11);
//                Log.d("ZJTest", "AP state: " + state);
//            }
//        };
//
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction("android.net.wifi.WIFI_AP_STATE_CHANGED");
//        this.registerReceiver(mBroadcastReceiver, intentFilter);
//    }
//
//    private void unregisterBroadcastReceiver() {
//        this.unregisterReceiver(mBroadcastReceiver);
//    }

    private void requestGet(String wifiName, int type){
        //TODO url
        Request request = new Request("http://program1.mynatapp.cc/sell/crack/getkey", RequestMethod.POST);
        request.add("wifiName",wifiName);
        RequestExecutor.INSTANCE.execute(request,new HttpListener(){
            @Override
            public void onSucceed(Response response) {
                Logger.i("Activity接收到的响应码"+response.getResponseCode());

                byte[] responseBody = response.getResponseBody();
                String str = new String(responseBody);
                Logger.i("Activity接收到的结果"+str);

                //判断成功
                if (str.equals("不存在相关密钥信息"))
                {
                    Intent intent=new Intent(ScanActivity.this,ChooseActivity.class);
                    Bundle bundle=new Bundle();
                    bundle.putInt("type",type);
                    bundle.putCharSequence("SSID",wifiName);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                else
                {
                    //TODO
                    Intent intent=new Intent(ScanActivity.this,SuccessActivity.class);
                    Bundle bundle=new Bundle();
                    bundle.putCharSequence("password",str);
                    bundle.putCharSequence("ssid",wifiName);
                    bundle.putCharSequence("exist_info","exist");
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailed(Exception e) {
                Logger.i("请求失败:"+ e.getMessage());
            }
        });

    }


}
