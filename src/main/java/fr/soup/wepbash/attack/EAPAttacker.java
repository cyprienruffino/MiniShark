package fr.soup.wepbash.attack;

import android.content.Context;

import fr.soup.wepbash.attack.callbacks.AttackCallback;
import fr.soup.wepbash.attack.exceptions.FailedAttackException;

/**
 * Created by cyprien on 12/07/16.
 */
public class EAPAttacker extends Attacker {


    public EAPAttacker(String ssid) {
        super(ssid);
    }

    @Override
    public void attack(Context context, AttackCallback callback) throws FailedAttackException {
//TODO Create the attack
        callback.succesCallback(ssid,key);
    }
}
