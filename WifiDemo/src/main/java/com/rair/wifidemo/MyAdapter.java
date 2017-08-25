package com.rair.wifidemo;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ml.blueberry.wifimanager.MyWifiInfo;
import ml.blueberry.wifimanager.WifiUtil;

import static com.rair.wifidemo.R.id.networkId;

public class MyAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater inflater;
    public List<MyWifiInfo> datas;
    private WifiUtil mWifiutil;

    private AlertDialog alert = null;
    private AlertDialog.Builder builder = null;

    public MyAdapter(Context context, List<MyWifiInfo> datas) {
        this.mContext = context;
        inflater = LayoutInflater.from(mContext);
        this.datas=new ArrayList<MyWifiInfo>();
        this.datas = datas;
        mWifiutil = WifiUtil.getInstance(context);
        //初始化Builder
        builder = new AlertDialog.Builder(context);
    }

    @Override
    public int getCount() {
        if(datas!=null){
            return datas.size();
        }else {
            return 0;
        }
    }

    @Override
    public MyWifiInfo getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item, null);
        }
        TextView mName = (TextView) convertView.findViewById(R.id.name);
        mName.setText(datas.get(position).getSSID());

        TextView mAddress = (TextView) convertView.findViewById(R.id.address);
        mAddress.setText(datas.get(position).getBSSID());

        TextView mNetWorkId = (TextView) convertView.findViewById(networkId);
        mNetWorkId.setText(datas.get(position).getNetworkId()+" ");
        TextView mLevel = (TextView) convertView.findViewById(R.id.level);
        mLevel.setText(datas.get(position).getLevel()+" ");

        Button connBtn = (Button) convertView.findViewById(R.id.connBtn);

        connBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(datas.get(position).getNetworkId()!=-1){
                    //已有配置，直接连接
                    mWifiutil.enableNetwork(datas.get(position).getNetworkId());
                }else{
                    //弹出对话框，填入密码，后点击连接，创建configuration，再连接
                    View view_alert = inflater.inflate(R.layout.alert_dialog, null,false);
                    TextView wifiSSID = (TextView) view_alert.findViewById(R.id.wifi_ssid);
                    wifiSSID.setText(datas.get(position).getSSID());
                    final EditText wifiPASS = (EditText) view_alert.findViewById(R.id.wifi_password);
                    wifiPASS.setText("");

                    view_alert.findViewById(R.id.btn_cancle).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alert.dismiss();
                        }
                    });
                    view_alert.findViewById(R.id.btn_connect).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int  flag= mWifiutil.addWifiConnect(datas.get(position).getSSID(),wifiPASS.getText().toString(), datas.get(position).getCapabilities());
                            if (flag == -1) {
                                Toast.makeText(mContext, "添加 Config 失败", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(mContext, "添加 Config 成功", Toast.LENGTH_SHORT).show();
                                alert.dismiss();
                            }
                        }
                    });
                    builder.setView(view_alert);
                    alert = builder.create();
                    alert.show();
                }
            }
        });
        return convertView;
    }

}
