package fr.soup.minishark.sniffer;

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

import fr.soup.minishark.R;


/**
 * Created by cyprien on 19/09/16.
 */
public class TcpDumpWrapper extends Service {
    public static final String FLAGS = "tcpdumpwrapper_flags";
    public static final String REFRESH_DATA_INTENT = "tcpdumpwrapper_refresh_intent";
    public static final String STOP_TCPDUMP = "tcpdumpwrapper_stop_tcpdump";
    public static final String REFRESH_DATA = "tcpdumpwrapper_refresh_data";
    private static final String TCPDUMP = "/data/data/ovh.soup.minishark/files/tcpdump";
    private static final int notificationId = 13371337;

    private String[] command;
    private BroadcastReceiver stopReceiver;
    private Process tcpdump;
    private AsyncTask bufferRead;
    private Notification notification;

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

        command=constructCommand(intent);
        createNotification();
        tcpdumpRunning=true;
        try {
            runTCPDump();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        boolean unbind=super.onUnbind(intent);
        Log.wtf("Unbind","In method");
        return unbind;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.wtf("Bind","In method");
        if(stopReceiver!=null)
            unregisterReceiver(stopReceiver);
        stopReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(STOP_TCPDUMP)) {
                    stop(context);
                }
            }
        };
        registerReceiver(stopReceiver, new IntentFilter(STOP_TCPDUMP));

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

    private void stop(Context context) {
        if(stopReceiver!=null)
            try{
                unregisterReceiver(stopReceiver);
            }catch (IllegalArgumentException e){
                e.printStackTrace();
            }
        tcpdump.destroy();
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(notificationId);
        bufferRead.cancel(true);
        tcpdumpRunning=false;
        Toast.makeText(context.getApplicationContext(), R.string.tcpdump_stopped, Toast.LENGTH_LONG).show();
    }

    private String[] constructCommand(Intent intent) {
        ArrayList<String> ret=new ArrayList<>();
        ret.add("su");
        ret.add("-c");
        ret.add(TCPDUMP);

        if(intent.getStringExtra(SnifferActivity.SNIFFER_FLAGS_INTENT_MANUAL_FLAGS)!=null)
            ret.add(intent.getStringExtra(SnifferActivity.SNIFFER_FLAGS_INTENT_MANUAL_FLAGS));

        if(intent.getStringExtra(SnifferActivity.SNIFFER_FLAGS_INTENT_RUN_UNTIL)!=null){
            ret.add("-G");
            ret.add(intent.getStringExtra(SnifferActivity.SNIFFER_FLAGS_INTENT_RUN_UNTIL));
            ret.add("-W");
            ret.add("1");
        }

        if (intent.getStringExtra(SnifferActivity.SNIFFER_FLAGS_INTENT_SAVE_IN) != null) {
            ret.add("-w");
            ret.add("-");
            ret.add("|");
            ret.add("tee");
            ret.add(intent.getStringExtra(SnifferActivity.SNIFFER_FLAGS_INTENT_SAVE_IN));
            ret.add("|");
            ret.add(TCPDUMP);
            ret.add("-r");
            ret.add("-");
        }

        return ret.toArray(new String[ret.size()]);
    }

    private void runTCPDump() throws IOException {

        tcpdump = Runtime.getRuntime().exec(command);
        tcpdumpStream = new BufferedReader(new InputStreamReader(tcpdump.getInputStream()));
        bufferRead = new AsyncTask() {
            String buffer;
            @Override
            protected Object doInBackground(Object[] objects) {
                while(tcpdumpRunning) {
                    try {
                        if((buffer = tcpdumpStream.readLine())!= null) {
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
        intent.setAction(STOP_TCPDUMP);
        PendingIntent pIntent = PendingIntent.getBroadcast(this, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent activityIntent = new Intent(this, SnifferActivity.class);
        PendingIntent pActivityIntent = PendingIntent.getActivity(this, 0, activityIntent, 0);


        notification = new Notification.Builder(this)
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
}
