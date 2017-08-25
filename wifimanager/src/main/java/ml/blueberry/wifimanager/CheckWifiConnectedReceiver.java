package ml.blueberry.wifimanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiManager;
import android.widget.Toast;

/**
 * Created by Administrator on 2017/8/21.
 */

public class CheckWifiConnectedReceiver extends BroadcastReceiver {
    private static final String TAG = "lingcheck";

    @Override
    public void onReceive(Context context, Intent intent) {
    if (intent.getAction().equals(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)) {
            int linkWifiResult = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, 123);
            if (linkWifiResult == WifiManager.ERROR_AUTHENTICATING) {
                Toast.makeText(context, "密码错误", Toast.LENGTH_SHORT).show();
//                UnityPlayer.UnitySendMessage("AndroidMsgReceiver","WifiConnectResult","false");
            }
            SupplicantState state =intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
            if(state==SupplicantState.COMPLETED){
                Toast.makeText(context, "连接成功", Toast.LENGTH_SHORT).show();
//                UnityPlayer.UnitySendMessage("AndroidMsgReceiver","WifiConnectResult","true");
            }
        }
    }
}
