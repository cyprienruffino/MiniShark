package ovh.soup.minishark.sniffer;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import ovh.soup.minishark.R;
import ovh.soup.minishark.dialogs.ConnectionDialog;

/**
 * Created by cyprien on 08/07/16.
 */
public class SnifferActivity extends Activity{

    public static final String SNIFFER_FLAGS_INTENT_MANUAL_FLAGS = "snifferactivityflagsintentmanualflags";
    public static final String SNIFFER_FLAGS_INTENT_SAVE_IN = "snifferactivityflagsintentsavein";
    public static final String SNIFFER_FLAGS_INTENT_RUN_UNTIL = "snifferactivityflagsintentrununtil";
    private static final String TCPDUMP_BINDER = "snifferactivitybundletcpdump";

    final Context context = this;
    private ListView listView;
    private boolean tcpdumpBound = false;
    private ArrayList<String> packets;
    private ArrayAdapter<String> adapter;
    TcpDumpWrapper mService;
    TcpDumpWrapper.TcpDumpWrapperBinder binder;

    private BroadcastReceiver sharkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == TcpDumpWrapper.REFRESH_DATA_INTENT) {
                Log.wtf("Refresh","InReceiver");
                packets.add(intent.getStringExtra(TcpDumpWrapper.REFRESH_DATA));
                adapter.notifyDataSetChanged();
            }
        }
    };

    private BroadcastReceiver stopReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == TcpDumpWrapper.STOP_TCPDUMP) {
                if(mConnection!=null && binder.isBinderAlive() && tcpdumpBound) {
                    unbindService(mConnection);
                    tcpdumpBound = false;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sniffer);

        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo mWifi = wifi.getConnectionInfo();

        if (wifi.isWifiEnabled() == false)
        {
            Toast.makeText(context.getApplicationContext(), R.string.disconnected_toast, Toast.LENGTH_LONG).show();
            wifi.setWifiEnabled(true);
        }

        if (mWifi.getSupplicantState() != SupplicantState.COMPLETED) {
            new ConnectionDialog().show(getFragmentManager(),"connection_dialog");
        }

        listView=(ListView)findViewById(R.id.sharkListView);
        listView.setTranscriptMode(2);
        packets=new ArrayList<>();
        adapter= new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, packets);

        listView.setAdapter(adapter);
        registerReceiver(sharkReceiver, new IntentFilter(TcpDumpWrapper.REFRESH_DATA_INTENT));
        registerReceiver(stopReceiver, new IntentFilter(TcpDumpWrapper.STOP_TCPDUMP));
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBinder(TCPDUMP_BINDER, binder);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState.getBinder(TCPDUMP_BINDER)!=null)
            binder= (TcpDumpWrapper.TcpDumpWrapperBinder) savedInstanceState.getBinder(TCPDUMP_BINDER);

    }

    public void snifferStart(View view) {
        if(tcpdumpBound) {
            unbindService(mConnection);
            tcpdumpBound=false;
        }

        Intent intent = new Intent(this, TcpDumpWrapper.class);
        EditText editText = (EditText) findViewById(R.id.manualflags);

        if(editText.getText().toString()!=null)
            intent.putExtra(SnifferActivity.SNIFFER_FLAGS_INTENT_MANUAL_FLAGS, editText.getText().toString() + " ");
        else
            intent.putExtra(SnifferActivity.SNIFFER_FLAGS_INTENT_MANUAL_FLAGS,"");

        if(((CheckBox)findViewById(R.id.saveinfile)).isChecked()) {
            editText = (EditText) findViewById(R.id.pcapfile);
            intent.putExtra(SnifferActivity.SNIFFER_FLAGS_INTENT_SAVE_IN, "/storage/emulated/legacy/" + editText.getText().toString());
        }
        else
            intent.putExtra(SnifferActivity.SNIFFER_FLAGS_INTENT_SAVE_IN,"");

        if(((CheckBox)findViewById(R.id.rununtil)).isChecked()) {
            editText = (EditText) findViewById(R.id.rununtiltime);
            intent.putExtra(SnifferActivity.SNIFFER_FLAGS_INTENT_RUN_UNTIL, editText.getText().toString());
        }
        else
            intent.putExtra(SnifferActivity.SNIFFER_FLAGS_INTENT_RUN_UNTIL,"");
        
        tcpdumpBound=bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        ((Button)findViewById(R.id.button_snifferStart)).setText(R.string.restart_capture);
    }


     private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            binder = (TcpDumpWrapper.TcpDumpWrapperBinder) service;
            mService = binder.getService();
            tcpdumpBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            tcpdumpBound = false;
        }
    };

    public void clear(View view) {
        packets.clear();
        adapter.notifyDataSetChanged();
    }

    public void snifferStop(View view) {
        Intent intent = new Intent();
        intent.setAction(TcpDumpWrapper.STOP_TCPDUMP);
        sendBroadcast(intent);
    }
}
