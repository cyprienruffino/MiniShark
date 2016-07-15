package fr.soup.wepbash.attack;

import android.content.Context;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import fr.soup.wepbash.attack.callbacks.AttackCallback;
import fr.soup.wepbash.attack.exceptions.FailedAttackException;
import fr.soup.wepbash.attack.exceptions.NoDevicesAvailableException;
import fr.soup.wepbash.attack.exceptions.PcapErrorException;

/**
 * Created by cyprien on 12/07/16.
 */
public interface AttackerInterface {

    ArrayList<String> detectDevices() throws NoDevicesAvailableException, PcapErrorException;
    void chooseDevice(int dev) throws SocketException;
    void attack(Context context, AttackCallback callback) throws FailedAttackException, SocketException, UnknownHostException;
}
