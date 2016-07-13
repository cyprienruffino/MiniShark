package fr.soup.wepbash.attack;

/**
 * Created by cyprien on 12/07/16.
 */
public interface AttackerInterface {

    public void attack(AttackCallback callback) throws FailedAttackException;

}
