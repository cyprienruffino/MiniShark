package fr.soup.minishark.sniffer;

import android.app.Notification;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import fr.soup.minishark.R;


/**
 * Created by cyprien on 19/09/16.
 */
public class TcpDumpWrapper extends Service {

    private final IBinder mBinder = new TcpDumpWrapperBinder();
    public class TcpDumpWrapperBinder extends Binder {
        public TcpDumpWrapper getService() {
            // Return this instance of LocalService so clients can call public methods
            return TcpDumpWrapper.this;
        }
    }

    public static final String FLAGS = "tcpdumpwrapper_flags";
    public static final String REFRESH_DATA_INTENT = "tcpdumpwrapper_refresh_intent";
    public static final String STOP_TCPDUMP = "tcpdumpwrapper_stop_tcpdump";
    public static final String REFRESH_DATA = "tcpdumpwrapper_refresh_data";
    private static final String TCP_DUMPABSOLUTE_PATH = "/data/data/ovh.soup.minishark_icon/files/tcpdump";

    private String flags;

    private boolean tcpdumpRunning;

    private BroadcastReceiver stopReceiver;
    private AsyncTask tcpdumpTask;

    @Override
    public void onCreate() {
        super.onCreate();
        stopReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(STOP_TCPDUMP)) {
                    tcpdumpRunning = false;
                    if(tcpdumpTask !=null)
                        tcpdumpTask.cancel(true);
                }
            }
        };
        registerReceiver(stopReceiver, new IntentFilter(STOP_TCPDUMP));



        try {
            InputStream ins = getResources().openRawResource (R.raw.tcpdump);
            byte[] buffer = new byte[ins.available()];
            ins.read(buffer);
            ins.close();
            FileOutputStream fos = openFileOutput("tcpdump", Context.MODE_PRIVATE);
            fos.write(buffer);
            fos.close();

            File file = getFileStreamPath ("tcpdump");
            file.setExecutable(true);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        flags=intent.getStringExtra(SnifferActivity.SNIFFER_FLAGS_INTENT);
        createNotification();
        tcpdumpRunning=true;
        runTCPDump();
        return mBinder;
    }


    private void runTCPDump(){

        tcpdumpTask = new AsyncTask() {
            Process tcpdump;
            ArrayList<String> buffer = new ArrayList<String>();

            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    if(flags == null) flags = "";
                    tcpdump =  Runtime.getRuntime().exec(new String[]{"su", "-c", TCP_DUMPABSOLUTE_PATH, flags});

                    //TODO Trouver le moyen de récupérer les logs de tcpdump

                    Intent refresh;
                    refresh = new Intent(TcpDumpWrapper.REFRESH_DATA_INTENT);
                    refresh.putExtra(REFRESH_DATA, buffer);
                    sendBroadcast(refresh);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };

        tcpdumpTask.execute(tcpdumpTask);
    }

    private void createNotification(){

        long time = new Date().getTime();
        String tmpStr = String.valueOf(time);
        String last4Str = tmpStr.substring(tmpStr.length() - 5);
        int notificationId = Integer.valueOf(last4Str);
        PendingIntent pIntent = PendingIntent.getActivity(this, notificationId, new Intent().setAction(STOP_TCPDUMP), PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new Notification.Builder(this)
                .setContentTitle(getString(R.string.notification_sniffing_title))
                .setContentText(getString(R.string.notification_sniffing))
                .setContentIntent(pIntent)
                .addAction(android.R.drawable.ic_notification_clear_all,
                        getString(R.string.notification_stop),
                        pIntent)
                .build();

        time = new Date().getTime();
        tmpStr = String.valueOf(time);
        last4Str = tmpStr.substring(tmpStr.length() - 5);
        notificationId = Integer.valueOf(last4Str);

        startForeground(notificationId, notification);

    }
}
