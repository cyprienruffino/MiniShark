package fr.soup.wepbash.attack;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapBpfProgram;
import org.jnetpcap.PcapHandler;

import java.io.PrintStream;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;

import fr.soup.wepbash.attack.callbacks.AttackCallback;
import fr.soup.wepbash.attack.callbacks.ErrorCallback;
import fr.soup.wepbash.attack.exceptions.FailedAttackException;
import fr.soup.wepbash.attack.services.DatagramSocketService;

/**
 * Created by cyprien on 12/07/16.
 */


public class WEPAttacker extends Attacker {

    private int port=0; //TODO Setup for ARP packets
    private InetAddress address;

    private DatagramSocketService datagramSocketService;
    private ConcurrentLinkedQueue<byte[]> packets = new ConcurrentLinkedQueue<>();

    public WEPAttacker(String ssid) {
        super(ssid);
    }

    @Override
    public void attack(Context context, AttackCallback callback) throws SocketException, UnknownHostException, FailedAttackException {
        int cnt = 20000;
        /*
////////////////////////////////
Flood
///////////////////////////////
 */

        ServiceConnection datagramSocketServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                datagramSocketService = ((DatagramSocketService.CommunicationBinder) service).getService();
                flood();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        address=DatagramSocketService.getBroadcastAddress(context);
        context.bindService(new Intent(context, DatagramSocketService.class), datagramSocketServiceConnection, Context.BIND_AUTO_CREATE);


        /*
        ///////////////////////
        Capture
        //////////////////////
         */
        PcapBpfProgram filter = new PcapBpfProgram();
        String expression = "port "+port;
        int r = pcap.compile(filter, expression, 0, 0);
        if (r != Pcap.OK) {
            System.out.println("Filter error: " + pcap.getErr());
        }
        pcap.setFilter(filter);

        PcapHandler<PrintStream> handler = new PcapHandler<PrintStream>() {
            @Override
            public void nextPacket(PrintStream out,
                                  long caplen,
                                  int len,
                                  int seconds,
                                  int usecs,
                                  ByteBuffer buffer) {
                packets.add(buffer.array());
                Log.d("WepAttacker","Packet captured on: " + new Date(seconds * 1000).toString());
            }
        };
        PrintStream out = System.out; // Our custom object to send into the handler
        pcap.loop(cnt, handler, out); // Each packet will be dispatched to the handler
        pcap.close();

        extractCipherTexts();

        key=executePTWAttack();
        if(key!=null)
            callback.succesCallback(ssid, key);
        throw new FailedAttackException();
    }

    private void flood(){
        //TODO
        final int nbPackets=20000;
        final byte[] packet={0x0000}; //TODO Create an ARP packet
        byte[][] packets = new byte[nbPackets][];

        for(int i=0;i<nbPackets;i++){
            packets[i]=packet;
        }
        datagramSocketService.send(packets, port, address, new ErrorCallback() {
            @Override
            public void onError(String message) {
                Log.wtf("WEPAttacker", "flood : "+message);//TODO Do something with this callback
            }
        });
    }

    private void extractCipherTexts() {
        //TODO XOR to extract the cipher texts
    }

    private String executePTWAttack() {
        //TODO Create the PTW attack
        return null;
    }
}
