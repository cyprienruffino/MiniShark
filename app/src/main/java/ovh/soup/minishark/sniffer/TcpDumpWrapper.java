package ovh.soup.minishark.sniffer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import ovh.soup.minishark.R;
import ovh.soup.minishark.views.SnifferActivity;
import ovh.soup.minishark.views.SnifferSetupActivity;

/**
 * Created by ${USER} on ${DATE}.
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

public class TcpDumpWrapper extends Service {
    public static final String START_TCPDUMP_INTENT = "tcpdumpwrapper_start_tcpdump_intent";
    public static final String STOP_TCPDUMP_INTENT = "tcpdumpwrapper_stop_tcpdump_intent";
    public static final String REFRESH_DATA_INTENT = "tcpdumpwrapper_refresh_intent";
    public static final String INIT_DATA_INTENT = "tcpdumpwrapper_init_intent";
    public static final String REFRESH_DATA = "tcpdumpwrapper_refresh_data";
    public static final String INIT_DATA = "tcpdumpwrapper_init_data";
    public static final String INIT_BROADCAST = "tcpdumpwrapper_init_broadcast";
    public static final String INIT_REQUEST = "tcpdumpwrapper_init_request";

    private static String TCPDUMP;
    private static final int notificationId = 13371337;

    private String command;
    private BroadcastReceiver startReceiver;
    private BroadcastReceiver stopReceiver;
    private BroadcastReceiver initReceiver;
    private Process tcpdump;
    private AsyncTask bufferRead;
    private ArrayList<String> packets;

    private boolean receiversRegistered = false;

    public boolean tcpdumpRunning;
    public BufferedReader tcpdumpStream;


    private final IBinder mBinder = new TcpDumpWrapperBinder();

    public class TcpDumpWrapperBinder extends Binder {
        public TcpDumpWrapper getService() {
            // Return this instance of LocalService so clients can call public methods
            return TcpDumpWrapper.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.wtf("Bind","In method");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        boolean unbind=super.onUnbind(intent);
        Log.wtf("Unbind","In method");
        return unbind;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.wtf("Service","Killed");
        closeReceivers();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("Service","Created");

        TCPDUMP = getFilesDir() + "/tcpdump";

        packets=new ArrayList<>();

        stopReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) { if (intent.getAction().equals(STOP_TCPDUMP_INTENT)) stop(context); }
        };
        initReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) { sendBroadcast(new Intent(INIT_DATA_INTENT).putExtra(INIT_DATA, packets));}
        };
        startReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) { command=constructCommand(intent); createNotification(); runTCPDump();}
        };

        openReceivers();
        recopyTcpdump();

        Intent init = new Intent(INIT_BROADCAST);
        sendBroadcast(init);

    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.wtf("Service","Task removed");
        stop(getApplicationContext());
    }

    private void recopyTcpdump() {
        File file = new File(TCPDUMP);
        if(!file.exists()) {
            try {
                InputStream ins = getResources().openRawResource(R.raw.tcpdump);
                byte[] buffer = new byte[ins.available()];
                ins.read(buffer);
                ins.close();
                FileOutputStream fos = openFileOutput("tcpdump", Context.MODE_PRIVATE);
                fos.write(buffer);
                fos.close();

                file = getFileStreamPath("tcpdump");
                file.setExecutable(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop(Context context) {
        tcpdump.destroy();
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(notificationId);
        bufferRead.cancel(true);
        tcpdumpRunning=false;
        closeReceivers();

        Toast.makeText(context.getApplicationContext(), R.string.tcpdump_stopped, Toast.LENGTH_SHORT).show();
        stopSelf();
    }

    private String constructCommand(Intent intent) {
        String ret=("su -c ");
        ret+=(TCPDUMP);


        if(!(intent.getStringExtra(SnifferSetupActivity.SNIFFER_FLAGS_INTENT_MANUAL_FLAGS).equals("") && intent.getStringExtra(SnifferSetupActivity.SNIFFER_FLAGS_INTENT_MANUAL_FLAGS).equals(" ")))
            ret+=(intent.getStringExtra(SnifferSetupActivity.SNIFFER_FLAGS_INTENT_MANUAL_FLAGS));

        if(!intent.getStringExtra(SnifferSetupActivity.SNIFFER_FLAGS_INTENT_RUN_UNTIL).equals("")){
            ret+=("-G");
            ret+=(intent.getStringExtra(SnifferSetupActivity.SNIFFER_FLAGS_INTENT_RUN_UNTIL));
            ret+=(" -W 1");
        }

        if (!intent.getStringExtra(SnifferSetupActivity.SNIFFER_FLAGS_INTENT_SAVE_IN).equals("")) {
            ret+=(" -U -w - | tee ");
            ret+=(intent.getStringExtra(SnifferSetupActivity.SNIFFER_FLAGS_INTENT_SAVE_IN));
            ret+=("|");
            ret+=(TCPDUMP);
            ret+=(" -r -");
        }

        Log.wtf("Command",ret);

        return ret;
    }

    private void runTCPDump() {
        tcpdumpRunning=true;
        File pcapFolder = new File(SnifferSetupActivity.PCAP_FOLDER);
        if(!pcapFolder.exists())
            pcapFolder.mkdirs();

        try {
            tcpdump = Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            tcpdumpRunning=false;
            Toast.makeText(this, "Failed to run TCPDump, do you have root permissions?", Toast.LENGTH_LONG).show();
            e.printStackTrace();
            return;
        }
        tcpdumpStream = new BufferedReader(new InputStreamReader(tcpdump.getInputStream()));
        bufferRead = new AsyncTask<Object, Void, Void>() {
            String buffer;
            @Override
            protected Void doInBackground(Object[] params) {
                while(tcpdumpRunning) {
                    try {
                        if((buffer = tcpdumpStream.readLine())!= null) {
                            packets.add(buffer);
                            Intent refresh;
                            refresh = new Intent(TcpDumpWrapper.REFRESH_DATA_INTENT);
                            refresh.putExtra(REFRESH_DATA, buffer);
                            Log.i("Packet caught",buffer);
                            sendBroadcast(refresh);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }
        }.execute();
    }

    private void createNotification(){
        //Necessary to maintain service up

        Intent intent= new Intent();
        intent.setAction(STOP_TCPDUMP_INTENT);
        PendingIntent pIntent = PendingIntent.getBroadcast(this, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent activityIntent = new Intent(getApplicationContext(), SnifferActivity.class);
        PendingIntent pActivityIntent = PendingIntent.getActivity(getApplicationContext(), 0, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.minishark_icon_low_res)
                .setContentTitle(getString(R.string.notification_sniffing_title))
                .setContentText(getString(R.string.notification_sniffing))
                .addAction(android.R.drawable.ic_notification_clear_all,
                        getString(R.string.notification_stop),
                        pIntent)
                .setDeleteIntent(pIntent)
                .setContentIntent(pActivityIntent)
                .build();

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(notificationId, notification);
    }

    private void openReceivers(){
        if(!receiversRegistered){
            registerReceiver(startReceiver, new IntentFilter(START_TCPDUMP_INTENT));
            registerReceiver(stopReceiver, new IntentFilter(STOP_TCPDUMP_INTENT));
            registerReceiver(initReceiver, new IntentFilter(INIT_REQUEST));
        }
        receiversRegistered = true;
    }

    private void closeReceivers(){
        if (receiversRegistered) {
            unregisterReceiver(startReceiver);
            unregisterReceiver(stopReceiver);
            unregisterReceiver(initReceiver);
        }
        receiversRegistered=false;
    }
}
