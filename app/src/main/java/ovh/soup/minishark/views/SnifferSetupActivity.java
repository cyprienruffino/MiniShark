package ovh.soup.minishark.views;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;

import ovh.soup.minishark.R;
import ovh.soup.minishark.sniffer.TcpDumpWrapper;

/**
 * Created by cyprien on 08/07/16.
 */
public class SnifferSetupActivity extends Activity{

    public static final String PCAP_FOLDER="/storage/emulated/legacy/pcap/";
    public static final String SNIFFER_FLAGS_INTENT_MANUAL_FLAGS = "snifferactivityflagsintentmanualflags";
    public static final String SNIFFER_FLAGS_INTENT_SAVE_IN = "snifferactivityflagsintentsavein";
    public static final String SNIFFER_FLAGS_INTENT_RUN_UNTIL = "snifferactivityflagsintentrununtil";

    private boolean receiverRegistered = false;


    final BroadcastReceiver tcpdumpInitializedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Intent intent1 = new Intent(TcpDumpWrapper.START_TCPDUMP_INTENT);
            createIntent(intent1);
            sendBroadcast(intent1);
            Intent intent2 = new Intent(context, SnifferActivity.class);
            startActivity(intent2);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("SnifferSetupActivity","Created");
        setContentView(R.layout.sniffersetup);
        checkForWifi();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        registerReceiver(tcpdumpInitializedReceiver, new IntentFilter(TcpDumpWrapper.INIT_BROADCAST));
        receiverRegistered=true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(receiverRegistered)
            unregisterReceiver(tcpdumpInitializedReceiver);
        receiverRegistered=false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!receiverRegistered)
            registerReceiver(tcpdumpInitializedReceiver, new IntentFilter(TcpDumpWrapper.INIT_BROADCAST));
        receiverRegistered=true;
    }

    @Override
    protected void onStop(){
        super.onStop();
        Log.d("SnifferSetupActivity","Stopped");
        if(receiverRegistered)
            unregisterReceiver(tcpdumpInitializedReceiver);
        receiverRegistered=false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainMenuActivity.class);
        startActivity(intent);
        finish();
    }

    public void snifferStart(View view) {
        Intent intent = new Intent(this, TcpDumpWrapper.class);
        startService(intent);
    }

    private void checkForWifi(){
        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo mWifi = wifi.getConnectionInfo();

        if (mWifi.getSupplicantState() != SupplicantState.COMPLETED)
            new ConnectionDialog().show(getFragmentManager(), "connection_dialog");
    }

    private void createIntent(Intent intent) {
        EditText editText = (EditText) findViewById(R.id.manualflags);
        if(editText.getText().toString()!="")
            intent.putExtra(SnifferSetupActivity.SNIFFER_FLAGS_INTENT_MANUAL_FLAGS, editText.getText().toString());
        else
            intent.putExtra(SnifferSetupActivity.SNIFFER_FLAGS_INTENT_MANUAL_FLAGS,"");

        if(((CheckBox)findViewById(R.id.saveinfile)).isChecked()) {
            editText = (EditText) findViewById(R.id.pcapfile);
            intent.putExtra(SnifferSetupActivity.SNIFFER_FLAGS_INTENT_SAVE_IN, PCAP_FOLDER + editText.getText().toString());
        }
        else
            intent.putExtra(SnifferSetupActivity.SNIFFER_FLAGS_INTENT_SAVE_IN,"");

        if(((CheckBox)findViewById(R.id.rununtil)).isChecked()) {
            editText = (EditText) findViewById(R.id.rununtiltime);
            intent.putExtra(SnifferSetupActivity.SNIFFER_FLAGS_INTENT_RUN_UNTIL, editText.getText().toString());
        }
        else
            intent.putExtra(SnifferSetupActivity.SNIFFER_FLAGS_INTENT_RUN_UNTIL,"");
    }


}
