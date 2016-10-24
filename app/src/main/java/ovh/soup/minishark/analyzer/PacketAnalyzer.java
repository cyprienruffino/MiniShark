package ovh.soup.minishark.analyzer;

import android.util.Log;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapClosedException;
import org.jnetpcap.packet.JPacket;
import org.jnetpcap.packet.JPacketHandler;
import org.jnetpcap.packet.PcapPacket;

import java.util.ArrayList;

/**
 * Created by cyprien on 28/09/16.
 */

public class PacketAnalyzer {
    private Pcap pcap;
    private StringBuilder errbuf;
    private ArrayList<JPacket> packets;

    public PacketAnalyzer(String filename){
        this.errbuf=new StringBuilder();
        this.pcap=Pcap.openOffline(filename,errbuf);
        if (pcap == null) {
            Log.wtf("Pcap","Error while opening device for capture: "
                    + errbuf.toString());
            throw new PcapClosedException();
        }
    }

    public void extractPackets(){
        packets = new ArrayList<>();

        pcap.loop(Pcap.LOOP_INFINITE,
                new JPacketHandler<ArrayList<JPacket>>() {
                    @Override
                    public void nextPacket(JPacket packet, ArrayList<JPacket> mPackets) {
                        PcapPacket copy = new PcapPacket(packet);
                        mPackets.add(copy);
                    }
                }
                , null
        );
    }

    public ArrayList<String> extractVerbosePacketHeaders() {
        ArrayList<String> headers = new ArrayList<>();

        for (JPacket packet : packets) {
            headers.add(packet.toString());
        }

        /*
        Ip4 ip = new Ip4();
        Ip4.Timestamp ts = new Ip4.Timestamp();
        Ip4.LooseSourceRoute lsroute = new Ip4.LooseSourceRoute();
        Ip4.StrictSourceRoute ssroute = new Ip4.StrictSourceRoute();



        if (packet.hasHeader(ip) && ip.hasSubHeaders()) {

            if (ip.hasSubHeader(lsroute)) {
            }

            if (ip.hasSubHeader(ssroute)) {
            }

            if (ip.hasSubHeader(ts)) {
            }

        }
        Tcp tcp = new Tcp();
        if (packet.hasHeader(tcp)) {
            Log.wtf("TCP",packet.getHeader(tcp).getDescription());
        }
            Udp udp = new Udp();
        if (packet.hasHeader(udp)) {
            Log.wtf("UDP",packet.getHeader(udp).getDescription());
        }
*/

        return headers;
    }
}
