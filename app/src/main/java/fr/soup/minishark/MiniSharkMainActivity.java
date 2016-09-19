package fr.soup.minishark;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

/**
 * Created by cyprien on 08/07/16.
 */
public class MiniSharkMainActivity extends Activity{

    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);


        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo mWifi = wifi.getConnectionInfo();

        if (wifi.isWifiEnabled() == false)
        {
            Toast.makeText(context.getApplicationContext(), R.string.disconnected_toast, Toast.LENGTH_LONG).show();
            wifi.setWifiEnabled(true);
        }

        if (mWifi.getSupplicantState() != SupplicantState.COMPLETED) {
            Toast.makeText(context.getApplicationContext(), R.string.no_wifi_connected_toast, Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
        }
    }

}
