package fr.soup.wepbash.attack.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;

import org.jnetpcap.Pcap;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import fr.soup.wepbash.attack.callbacks.ErrorCallback;

/**
 * Created by Valentin on 23/01/2016.
 */
public class DatagramSocketService extends Service {
    public int port;
    private DatagramSocket socket;
    private final IBinder binder = new CommunicationBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class CommunicationBinder extends Binder {
        public DatagramSocketService getService() {
            return DatagramSocketService.this;
        }
    }

    public void send(final byte[] packet, final int port, final InetAddress address,final ErrorCallback errorCallback){
        byte[][]packets={packet};
        send(packets, port, address, errorCallback);
    }

    public void send(final byte[][] packets, final int port, final InetAddress address, final ErrorCallback errorCallback){
        new AsyncTask<byte[], Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (socket == null) {
                    try {
                        socket = new DatagramSocket(port);
                        socket.setBroadcast(true);
                        socket.setSoTimeout(Pcap.DEFAULT_TIMEOUT);
                    } catch (IOException e) {
                        if (errorCallback != null)
                            errorCallback.onError("Erreur inconu 1");
                        e.printStackTrace();
                    }
                }
            }

            @Override
            protected Void doInBackground(byte[]... params) {
                for (byte[] packet : params) {
                    try {
                        DatagramPacket datagrampacket = new DatagramPacket(packet, packet.length, address, port);
                        socket.send(datagrampacket);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                return null;
            }
        }.execute(packets);
    }

    public void close() {
        if (socket != null)
                    socket.close();
    }

    public static InetAddress getBroadcastAddress(Context context) throws UnknownHostException {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcp = wifi.getDhcpInfo();

        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++)
            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
        return InetAddress.getByAddress(quads);
    }
}
