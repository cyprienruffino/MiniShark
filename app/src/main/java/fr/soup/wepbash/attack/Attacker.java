package fr.soup.wepbash.attack;

/**
 * Created by cyprien on 12/07/16.
 */
public abstract class Attacker implements AttackerInterface {

    protected String ssid;
    protected String key;

    public Attacker(String ssid) {
        this.ssid=ssid;
    }

    public void initiateLibpcap(){

    }
}
