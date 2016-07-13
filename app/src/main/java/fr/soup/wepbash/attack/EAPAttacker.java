package fr.soup.wepbash.attack;

/**
 * Created by cyprien on 12/07/16.
 */
public class EAPAttacker extends Attacker {


    public EAPAttacker(String ssid) {
        super(ssid);
    }

    @Override
    public void attack(AttackCallback callback) throws FailedAttackException {

        callback.succesCallback(ssid,key);
    }
}
