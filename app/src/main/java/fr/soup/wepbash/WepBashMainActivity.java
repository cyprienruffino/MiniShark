package fr.soup.wepbash;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by cyprien on 08/07/16.
 */
public class WepBashMainActivity extends Activity{



    ListView lv;
    WiFiDiscovery discovery;
    ArrayAdapter<String> adapter;
    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        lv = (ListView)findViewById(R.id.wifilist);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AttackerInterface attacker = null;
                String str = (String) lv.getItemAtPosition(position);
                if (str.contains("WEP")) {
                    attacker = new WEPAttacker(str.split(" ")[0]);
                } else if (str.contains("EAP")) {
                    attacker = new EAPAttacker(str.split(" ")[0]);
                } else if (str.contains("WPS")) {
                    attacker = new WPSAttacker(str.split(" ")[0]);
                }
                try {
                    attacker.attack(new AttackCallback() {
                        @Override
                        public void succesCallback(String ssid, String key) {
                            chooserDialog(ssid, key);
                        }
                    });
                } catch (FailedAttackException e) {
                    Toast.makeText(context, "Sorry, the attack failed :/", Toast.LENGTH_SHORT).show();
                } catch (NullPointerException e) {
                    Toast.makeText(context, "Hmmm, that's a strange error...", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void wifisearch(View view) {
        WiFiDiscovery discovery=new WiFiDiscovery(this, new DiscoveryCallback() {
            @Override
            public void callback(Context context, ArrayList<String>results) {
                ((WepBashMainActivity)context).updateListView(results);
            }
        });
        discovery.discover();
        Toast.makeText(this, "Scanning....", Toast.LENGTH_SHORT).show();
    }

    private void updateListView(ArrayList<String> results) {
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, results);
        lv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void chooserDialog(String ssid, String key){

    }

}
