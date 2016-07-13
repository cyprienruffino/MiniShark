package fr.soup.wepbash.attack;


/**
 * Created by cyprien on 12/07/16.
 */


public class WEPAttacker extends Attacker {

    public WEPAttacker(String ssid) {
        super(ssid);
    }

    @Override
    public void attack(AttackCallback callback) {

        callback.succesCallback(ssid,key);
    }


}
