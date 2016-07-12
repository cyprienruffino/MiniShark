package fr.soup.wepbash;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cyprien on 12/07/16.
 */
public class WiFiDiscovery{

    private ArrayList<String> resultsString;
    private List<ScanResult> results;
    private WifiManager wifi;
    private Context sourceContext;
    private DiscoveryCallback callback;

    public WiFiDiscovery(Context sourceContext, DiscoveryCallback callback){
        this.wifi = (WifiManager) sourceContext.getSystemService(Context.WIFI_SERVICE);
        this.sourceContext=sourceContext;
        this.callback=callback;
    }
    public void discover(){

        if (wifi.isWifiEnabled() == false)
        {
            Toast.makeText(sourceContext.getApplicationContext(), "WiFi is disabled... making it enabled", Toast.LENGTH_LONG).show();
            wifi.setWifiEnabled(true);
        }

        sourceContext.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent intent) {
                Log.wtf("hello", "hello");
                results = wifi.getScanResults();
                resultsString = new ArrayList<>();

                for (ScanResult result : results) {
                    if (result.capabilities.contains("WEP")) {
                        resultsString.add("" + result.SSID + " [WEP]");
                    } else if (result.capabilities.contains("EAP")) {
                        resultsString.add("" + result.SSID + " [EAP]");
                    } else if (result.capabilities.contains("WPS")) {
                        resultsString.add("" + result.SSID + " [WPS]");
                    }
                }
                callback.callback(sourceContext, resultsString);
                sourceContext.unregisterReceiver(this);
            }
        }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        wifi.startScan();
    }
}
