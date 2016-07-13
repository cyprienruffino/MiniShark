package fr.soup.wepbash.attack;


import org.jnetpcap.Pcap;

/**
 * Created by cyprien on 12/07/16.
 */


public class WEPAttacker extends Attacker {

    public WEPAttacker(String ssid) {
        super(ssid);
    }

    @Override
    public void attack(AttackCallback callback) {
        Pcap pcap = new Pcap();
        byte[] pack = {0x0000};
        pcap.sendPacket(pack);
        callback.succesCallback(ssid,key);
    }


}
