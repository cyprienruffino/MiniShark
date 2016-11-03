package ovh.soup.minishark.views;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import ovh.soup.minishark.R;
import ovh.soup.minishark.sniffer.TcpDumpWrapper;

/**
 * Created by cyprien on 08/07/16.
 *
 * This file is part of Minishark.
 *
 *   Minishark is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   Minishark is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with Minishark.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Project repository : https://github.com/Moi4167/Minishark
 */

public class SnifferActivity extends Activity{
    private boolean tcpdumpBound = false;
    private ArrayList<String> packets;
    private ArrayAdapter<String> adapter;
    private boolean receiversRegistered = false;
    TcpDumpWrapper mService;
    TcpDumpWrapper.TcpDumpWrapperBinder binder;


    private BroadcastReceiver sharkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(TcpDumpWrapper.REFRESH_DATA_INTENT)) {
                Log.wtf("Refresh","InReceiver");
                packets.add(intent.getStringExtra(TcpDumpWrapper.REFRESH_DATA));
                adapter.notifyDataSetChanged();
            }
            if(intent.getAction().equals(TcpDumpWrapper.INIT_DATA_INTENT)){
                Log.wtf("Init","InReceiver");
                packets.addAll(intent.getStringArrayListExtra(TcpDumpWrapper.INIT_DATA));
                adapter.notifyDataSetChanged();
            }
        }
    };

    private BroadcastReceiver stopReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(TcpDumpWrapper.STOP_TCPDUMP_INTENT)) {
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

        Log.wtf("SnifferActivity","OnCreate");

        ListView listView = (ListView) findViewById(R.id.sharkListView);
        listView.setTranscriptMode(2);
        packets=new ArrayList<>();
        adapter= new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, packets);
        listView.setAdapter(adapter);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        openReceivers();
        if(!tcpdumpBound)
            tcpdumpBound=bindService(new Intent(this,TcpDumpWrapper.class), mConnection, Context.BIND_AUTO_CREATE);

        sendBroadcast(new Intent(TcpDumpWrapper.INIT_REQUEST));
    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.wtf("SnifferActivity","OnStop");
        if(tcpdumpBound){
            unbindService(mConnection);
            tcpdumpBound=false;
        }
        closeReceivers();
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeReceivers();
    }

    @Override
    protected void onResume() {
        super.onResume();
        openReceivers();
        Intent intent = new Intent(TcpDumpWrapper.INIT_REQUEST);
        sendBroadcast(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        snifferRestart(null);
    }


    public void snifferRestart(View view) {
        closeReceivers();
        mService.stop(this);

        if(tcpdumpBound){
            unbindService(mConnection);
            tcpdumpBound=false;
        }

        Intent intent = new Intent(this,SnifferSetupActivity.class);
        startActivity(intent);
        finish();
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
        intent.setAction(TcpDumpWrapper.STOP_TCPDUMP_INTENT);
        sendBroadcast(intent);
        closeReceivers();
    }

    private void closeReceivers() {
        if(receiversRegistered) {
            unregisterReceiver(sharkReceiver);
            unregisterReceiver(stopReceiver);
        }
        receiversRegistered=false;
    }

    private void openReceivers() {
        if(!receiversRegistered){
            registerReceiver(sharkReceiver, new IntentFilter(TcpDumpWrapper.REFRESH_DATA_INTENT));
            registerReceiver(stopReceiver, new IntentFilter(TcpDumpWrapper.STOP_TCPDUMP_INTENT));
        }
        receiversRegistered=true;
    }

}
