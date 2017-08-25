package ml.blueberry.wifimanager;

/**
 * Created by Administrator on 2017/8/21.
 */

public class MyWifiInfo {
    private String SSID="";                 //wifi名称
    private String BSSID="";                //wifi的mac
    private int level=0;                    //wifi的信号强度，范围在0-4
    private String capabilities="";         //wifi的加密方式
    private boolean isEverConnected=false;  //该wifi之前是否有连接上的配置
    private int networkId=-1;               //若有配置，则该配置的networkId

    public MyWifiInfo() {
        this.SSID = "";
        this.BSSID = "";
        this.level = 0;
        this.capabilities = "";
        this.isEverConnected = false;
        this.networkId = -1;
    }

    public MyWifiInfo(String SSID, String BSSID, int level, String capabilities, boolean isEverConnected, int networkId) {
        this.SSID = SSID;
        this.BSSID = BSSID;
        this.level = level;
        this.capabilities = capabilities;
        this.isEverConnected = isEverConnected;
        this.networkId = networkId;
    }

    public String getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(String capabilities) {
        this.capabilities = capabilities;
    }

    public String getBSSID() {
        return BSSID;
    }

    public void setBSSID(String BSSID) {
        this.BSSID = BSSID;
    }

    public String getSSID() {
        return SSID;
    }

    public void setSSID(String SSID) {
        this.SSID = SSID;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean isEverConnected() {
        return isEverConnected;
    }

    public void setEverConnected(boolean everConnected) {
        isEverConnected = everConnected;
    }

    public int getNetworkId() {
        return networkId;
    }

    public void setNetworkId(int networkId) {
        this.networkId = networkId;
    }

    @Override
    public String toString() {
        return "MyWifiInfo{" +
                "SSID='" + SSID + '\'' +
                ", BSSID='" + BSSID + '\'' +
                ", level=" + level +
                ", isEverConnected=" + isEverConnected +
                ", networkId=" + networkId +
                ", capabilities='" + capabilities + '\'' +
                '}';
    }
}
