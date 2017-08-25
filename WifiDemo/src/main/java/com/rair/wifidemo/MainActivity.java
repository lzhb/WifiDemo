package com.rair.wifidemo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ml.blueberry.wifimanager.MyWifiInfo;
import ml.blueberry.wifimanager.WifiUtil;

public class MainActivity extends AppCompatActivity {
    private ListView listView;
    private WifiUtil mWifiUtil;
    private List<MyWifiInfo> MyWifiInfoList;

    private MyAdapter myAdapter;

    @SuppressLint("WifiManagerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWifiUtil = WifiUtil.getInstance(this);

        MyWifiInfoList = new ArrayList<MyWifiInfo>();
        listView = (ListView) findViewById(R.id.listView);
        myAdapter = new MyAdapter(this, MyWifiInfoList);
        listView.setAdapter(myAdapter);

        // 创建wifi锁
        mWifiUtil.createWifiLock("wifiLock");
        // 锁定WifiLock
        mWifiUtil.acquireWifiLock();

        requirePermission();
    }

    private void requirePermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                Toast.makeText(this, "请给我权限", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        202);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        202);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 202: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "得到了权限", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "没有权限", Toast.LENGTH_SHORT).show();
                }
            }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWifiUtil.releaseWifiLock();
    }


    public void openWiFi(View view) {
        mWifiUtil.openWifi();
    }

    public void closeWiFi(View view) {
        mWifiUtil.closeWifi();

    }

    public void getWiFiInfo(View view) {
        mWifiUtil.getWifiInfo();
    }

    /**
     * 搜索WiFi
     *
     * @param view
     */
    public void searchWiFi(View view) {
        MyWifiInfoList= mWifiUtil.searchWifi();
        for(MyWifiInfo info:MyWifiInfoList){
            Log.d("linglingwifi", "MainActivity searchWifi: info"+info);
        }
        myAdapter.datas=MyWifiInfoList;
        myAdapter.notifyDataSetChanged();
    }


}
