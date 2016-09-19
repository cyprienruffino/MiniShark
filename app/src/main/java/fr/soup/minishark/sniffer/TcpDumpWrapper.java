package fr.soup.minishark.sniffer;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by cyprien on 19/09/16.
 */
public class TcpDumpWrapper extends Service {
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }
}
