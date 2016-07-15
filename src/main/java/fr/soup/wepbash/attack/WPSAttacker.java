package fr.soup.wepbash.attack;

import android.content.Context;

import fr.soup.wepbash.attack.callbacks.AttackCallback;

/**
 * Created by cyprien on 12/07/16.
 */
public class WPSAttacker extends Attacker {

    public WPSAttacker(String ssid) {
        super(ssid);
    }

    @Override
    public void attack(Context context, AttackCallback callback) {
//TODO Do the bruteforce attack
        callback.succesCallback(ssid,key);
    }
}
