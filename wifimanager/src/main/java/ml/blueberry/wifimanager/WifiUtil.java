package ml.blueberry.wifimanager;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static android.net.wifi.WifiManager.WIFI_MODE_FULL_HIGH_PERF;

public class WifiUtil {
    // 定义WifiManager对象
    private WifiManager mWifiManager;
    // 定义WifiInfo对象
    private WifiInfo mWifiInfo;
    // 扫描出的网络连接列表
    private List<ScanResult> mScanResultList;
    // 网络连接列表
    private List<WifiConfiguration> mConfiguredNetworks;
    public List<MyWifiInfo> MyWifiInfoList;

    // 定义一个WifiLock
    private WifiManager.WifiLock mWifiLock;

    private static WifiUtil util;

    /**
     * 单例方法
     *
     * @param context
     * @return
     */
    public static WifiUtil getInstance(Context context) {
        if (util == null) {
            synchronized (WifiUtil.class) {
                if(util==null){
                    util = new WifiUtil(context);
                }
            }
        }
        return util;
    }

    // 构造器
    private WifiUtil(Context context) {
        // 取得WifiManager对象
        mWifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        // 取得WifiInfo对象
        mWifiInfo = mWifiManager.getConnectionInfo();
        MyWifiInfoList = new ArrayList<MyWifiInfo>();
    }

    // 打开WIFI
    public void openWifi() {
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }
    }

    // 关闭WIFI
    public void closeWifi() {
        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }
    }

    /**
     *getWifiState
     * @return  One of  WifiManager.WIFI_STATE_DISABLED ,
     *         WifiManager.WIFI_STATE_DISABLING,  WifiManager.WIFI_STATE_ENABLED,
     *         WifiManager.WIFI_STATE_ENABLING,  WifiManager.WIFI_STATE_UNKNOWN
     */
    public int getWifiState() {
        return mWifiManager.getWifiState();
    }
    public boolean isWiFiEnable(){
        return mWifiManager.isWifiEnabled();
    }

    // 锁定WifiLock
    public void acquireWifiLock() {
        if(mWifiLock!=null){
            mWifiLock.acquire();
        }
    }

    // 解锁WifiLock
    public void releaseWifiLock() {
        // 判断时候锁定
        if (mWifiLock.isHeld()) {
            mWifiLock.release();
        }
    }

    // 创建一个WifiLock
    public void createWifiLock(String lockName) {
        mWifiLock = mWifiManager.createWifiLock(WIFI_MODE_FULL_HIGH_PERF,lockName);
    }

    // 得到配置好的网络
    public List<WifiConfiguration> getConfiguration() {
        return mConfiguredNetworks;
    }

    /**
     *  连接已经配置过的wifi
     * @param networkId MyWifiInfo中的networkId，不能为-1
     */
    public void enableNetwork(int networkId) {
        // 连接配置好的指定ID的网络
        if(networkId!=-1){
            for(MyWifiInfo config:MyWifiInfoList){
                if(config.isEverConnected()){
                    mWifiManager.disableNetwork(config.getNetworkId());
                }
            }
            mWifiManager.enableNetwork(networkId, true);
        }else{
            Log.d("linglingwifi", "enableNetwork的wifiInfo是没有配置过的 ");
        }
    }

    public List<MyWifiInfo>  searchWifi() {
        mWifiManager.startScan();
        mScanResultList = mWifiManager.getScanResults();
        // 得到配置好的网络连接
        mConfiguredNetworks = mWifiManager.getConfiguredNetworks();
        Log.d("lingling", "重新获取 MyWifiInfoList ");
        MyWifiInfoList.clear();
        for(ScanResult sr:mScanResultList){
            Log.d("lingling", "ScanResult: "+sr.SSID+"  。 capabilities :"+sr.capabilities);
        }
        //MyWifiInfoList中添加配置过的wifi
        for(ScanResult sr:mScanResultList){
            for(WifiConfiguration config:mConfiguredNetworks) {
                Boolean isValidatedInternetAccess=false;
                try {
                    Field validatedInternetAccess = WifiConfiguration.class.getField("validatedInternetAccess");
                    isValidatedInternetAccess = (Boolean) validatedInternetAccess.get(config);
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                if(isValidatedInternetAccess){
                    if (("\"" + sr.SSID + "\"").equals(config.SSID)) {
                        int level=WifiManager.calculateSignalLevel(sr.level,5);
                        MyWifiInfo info=new MyWifiInfo(sr.SSID,sr.BSSID,level,sr.capabilities,true,config.networkId);
                        MyWifiInfoList.add(info);
                    }
                }
            }
        }
        //MyWifiInfoList中加入未配置过的wifi信息
        for(ScanResult sr:mScanResultList){
            boolean isExist=false;
            for(MyWifiInfo configuredInfo: MyWifiInfoList) {
                if(configuredInfo.getSSID().equals(sr.SSID)){
                    isExist=true;
                    break;
                }
            }
            if(!isExist){
                int level=WifiManager.calculateSignalLevel(sr.level,5);
                MyWifiInfo info=new MyWifiInfo(sr.SSID,sr.BSSID,level,sr.capabilities,false,-1);
                MyWifiInfoList.add(info);
            }
        }
        for(MyWifiInfo info:MyWifiInfoList){
            Log.d("linglingwifi", "WifiUtil  searchWifi: info"+info);
        }
        return MyWifiInfoList;
    }

    // 得到扫描结果
    // 得到网络列表
    public List<ScanResult> getScanResultList() {
        return mScanResultList;
    }

    // 查看扫描结果
    public StringBuilder lookUpScan() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < mScanResultList.size(); i++) {
            stringBuilder
                    .append("Index_" + new Integer(i + 1).toString() + ":");
            // 将ScanResult信息转换成一个字符串包
            // 其中把包括：BSSID、SSID、capabilities、frequency、level
            stringBuilder.append((mScanResultList.get(i)).toString());
            stringBuilder.append("/n");
        }
        return stringBuilder;
    }

    // 若当前连接到wifi，得到它的MAC地址
    public String getMacAddress() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();
    }

    // 若当前连接到wifi，得到它的BSSID
    public String getBSSID() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getBSSID();
    }

    // 若当前连接到wifi，得到它的IP地址
    public int getIPAddress() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
    }

    // 若当前连接到wifi，得到它的ID
    public int getNetworkId() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
    }

    // 若当前连接到wifi，得到它的所有信息
    public String getWifiInfo() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.toString();
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
    public int addWifiConnect(String SSID, String password, String type) {
        // 创建WiFi配置
        WifiConfiguration configuration = CreateWifiConfiguration(SSID, password, type);
        Log.d("linglingwifi", " 新配置的WifiConfiguration： "+configuration);
        // 添加WIFI网络
        //psw长度必须大于8，否则返回-1
        int networkId = mWifiManager.addNetwork(configuration);
        if (networkId == -1) {
            Log.d("linglingwifi", "添加网络失败 networkId为-1 ");
            return -1;
        }else {
            for(MyWifiInfo con:MyWifiInfoList){
                if(con.isEverConnected()){
                    mWifiManager.disableNetwork(con.getNetworkId());
                }
            }
            mWifiManager.enableNetwork(networkId,true);
            return networkId;
        }
    }

    /**
     * 断开WiFi连接
     *
     * @param networkId
     */
    public void disconnectWiFiNetWork(int networkId) {
        // 设置对应的wifi网络停用
        mWifiManager.disableNetwork(networkId);

        // 断开所有网络连接
        mWifiManager.disconnect();
    }

    /**
     * 创建WifiConfiguration
     * 三个安全性的排序为：WEP<WPA<WPA2。
     * WEP是Wired Equivalent Privacy的简称，有线等效保密（WEP）协议是对在两台设备间无线传输的数据进行加密的方式，
     * 用以防止非法用户窃听或侵入无线网络
     * WPA全名为Wi-Fi Protected Access，有WPA和WPA2两个标准，是一种保护无线电脑网络（Wi-Fi）安全的系统，
     * 它是应研究者在前一代的系统有线等效加密（WEP）中找到的几个严重的弱点而产生的
     * WPA是用来替代WEP的。WPA继承了WEP的基本原理而又弥补了WEP的缺点：WPA加强了生成加密密钥的算法，
     * 因此即便收集到分组信息并对其进行解析，也几乎无法计算出通用密钥；WPA中还增加了防止数据中途被篡改的功能和认证功能
     * WPA2是WPA的增强型版本，与WPA相比，WPA2新增了支持AES的加密方式
     *
     * @param SSID
     * @param password
     * @param type
     * @return
     **/
    public WifiConfiguration createWifiConfig(String SSID, String password, Data type) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";
       // config.SSID=SSID;

        if (type == Data.WIFI_CIPHER_NOPASS) {
            config.wepKeys[0] = "";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        } else if (type == Data.WIFI_CIPHER_WEP) {
            config.hiddenSSID = true;
            config.wepKeys[0] = "\"" + password + "\"";
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        } else if (type == Data.WIFI_CIPHER_WPA) {
            config.preSharedKey = "\"" + password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.status = WifiConfiguration.Status.ENABLED;
        } else if (type == Data.WIFI_CIPHER_WPA2) {
            config.preSharedKey = "\"" + password + "\"";
            config.hiddenSSID = false;
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

    /**
     * 创建新的wifiInfo的configuration
     * @param SSID MyWifiInfo的SSID
     * @param password 用户输入的密码，密码必须大于8位
     * @param type MyWifiInfo的Capabilities
     * @return
     */
    public WifiConfiguration CreateWifiConfiguration(String SSID, String password,String type) {
        deleteExistConfigurations(SSID);
        WifiConfiguration config = new WifiConfiguration();
        config.hiddenSSID = false;
        //config.SSID=SSID;
        config.SSID = "\"" + SSID + "\"";
        config.status = WifiConfiguration.Status.ENABLED;
        if (type.contains("WPA")) {
            Log.d("lingling", "CreateWifiConfiguration:  WPA");
            config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.preSharedKey = "\"".concat(password).concat("\"");
        } else if (type.contains("WEP")) {
            Log.d("lingling", "CreateWifiConfiguration:  WEP");
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
             config.wepKeys[0] = password;
            config.wepTxKeyIndex = 0;
        } else {
            Log.d("lingling", "CreateWifiConfiguration:  default");
            config.preSharedKey = null;
            config.wepKeys[0] = "\"" + "\"";
            config.wepTxKeyIndex = 0;
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedAuthAlgorithms.clear();
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        }
        return config;
    }

    // 删除之前这个wifi的configuration
    private void deleteExistConfigurations(String SSID) {
        if(mConfiguredNetworks != null){
            for (WifiConfiguration existingConfig : mConfiguredNetworks) {
                if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                    Log.d("linglingwifi", "之前配置过Configuration ，需要删掉, networkId:  "+existingConfig.networkId);
                    mWifiManager.removeNetwork(existingConfig.networkId);
                }
            }
        }
    }
        /**
         * 密码加密类型
         */
    public enum Data {
        WIFI_CIPHER_NOPASS(0), WIFI_CIPHER_WEP(1), WIFI_CIPHER_WPA(2), WIFI_CIPHER_WPA2(3);

        private final int value;

        //构造器默认也只能是private, 从而保证构造函数只能在内部使用
        Data(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

}
