package fr.soup.wepbash.attack;

/**
 * Created by cyprien on 12/07/16.
 */
public class WPSAttacker extends Attacker {


    public WPSAttacker(String ssid) {
        super(ssid);
    }

    @Override
    public void attack(AttackCallback callback) {

        callback.succesCallback(ssid,key);
    }
}
