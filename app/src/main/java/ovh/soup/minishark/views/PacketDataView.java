package ovh.soup.minishark.views;

import android.app.Activity;
import android.os.Bundle;

import ovh.soup.minishark.R;

/**
 * Created by cyprien on 28/09/16.
 */

public class PacketDataView extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.packet_activity);
    }
}
