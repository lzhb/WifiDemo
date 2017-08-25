package ml.blueberry.wifimanager;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/8/22.
 */

public class WifiManager {
    private static Context mContext;
    private static WifiUtil mUtil;

    /**
     * 使用WifiManager前，必须传入context
     * @param context 上下文
     */
    public static void setContext(Context context){
        mContext=context.getApplicationContext();
        mUtil=WifiUtil.getInstance(mContext);
    }
    public static void openWifi(){
        mUtil.openWifi();
    }
    public static void closeWifi(){
        mUtil.closeWifi();
    }

    /**
     * 搜索Wifi
     * @return MyWifiInfo的列表的Gson。列表前面为有配置过的wifi
     */
    public static String searchWifi(){
        MyWifiInfos myInfos=new MyWifiInfos();
        myInfos.myWifiInfos.addAll(mUtil.searchWifi());
        Log.d("linglingwifi:" , "searchWifi: MyWifiInfos的gson : "+ new Gson().toJson(myInfos));
        return new Gson().toJson(myInfos);
    }
    /**
     *  连接已经配置过的wifi
     * @param networkId MyWifiInfo中的networkId，不能为-1
     */
    public static void enableNetwork(int networkId){
        mUtil.enableNetwork(networkId);
    }
    /**
     * 添加WiFi网络
     *
     * @param SSID MyWifiInfo的SSID
     * @param password 用户输入的密码，密码必须大于8位
     * @param type MyWifiInfo的Capabilities
     *
     * @return int 为-1，代表添加网络失败。 否则返回新配置的WifiConfiguration的networkId
     */
    public static  int addWifiConnect(String SSID, String password, String type){
        return mUtil.addWifiConnect(SSID, password, type);
    }

    /**
     * 创建wifi锁
     * 防止手机锁屏后，若无应用使用wifi，系统会自动断开wifi，节省电量
     * @param lockName 锁的名称
     */
    public static void createWifiLock(String lockName){
        mUtil.createWifiLock(lockName);
    }

    /**
     * 锁定WifiLock
     * 锁定后，须记的release
     */
    public static void acquireWifiLock(){
        mUtil.acquireWifiLock();
    }

    /**
     * 解锁WifiLock
     */
    public static void releaseWifiLock(){
        mUtil.releaseWifiLock();
    }

}
class MyWifiInfos{
    public List<MyWifiInfo> myWifiInfos=new ArrayList<>();
}
