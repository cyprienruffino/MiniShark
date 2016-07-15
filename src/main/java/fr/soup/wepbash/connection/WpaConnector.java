package fr.soup.wepbash.connection;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import java.util.List;

/**
 * Created by cyprien on 15/07/16.
 */
public class WpaConnector implements WifiConnector{
    private String ssid;
    private String key;
    private Context context;

    public WpaConnector(String ssid, String key, Context context){
        this.ssid=ssid;
        this.key=key;
        this.context=context;
    }

    public void connect(){
        WifiConfiguration conf= new WifiConfiguration();
        conf.SSID = "\"" + ssid + "\"";
        conf.preSharedKey = "\""+ key +"\"";
        WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        wifiManager.addNetwork(conf);
        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for( WifiConfiguration i : list ) {
            if(i.SSID != null && i.SSID.equals("\"" + ssid + "\"")) {
                wifiManager.disconnect();
                wifiManager.enableNetwork(i.networkId, true);
                wifiManager.reconnect();
                break;
            }
        }
    }
}
