package fr.soup.wepbash;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by cyprien on 12/07/16.
 */
public interface DiscoveryCallback {
    void callback(Context context, ArrayList<String> results);
}
