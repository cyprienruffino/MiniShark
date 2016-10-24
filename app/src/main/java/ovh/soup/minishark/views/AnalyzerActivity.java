package ovh.soup.minishark.views;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

import ovh.soup.minishark.R;
import ovh.soup.minishark.analyzer.PacketAnalyzer;

/**
 * Created by cyprien on 28/09/16.
 */

public class AnalyzerActivity extends Activity {
    private ArrayAdapter<String> adapter;
    private ArrayList<String> packetsText;
    private String pcapPath="/storage/emulated/legacy/pcap/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.analyzer_view);

    }

    public void loadPackets(View view) {
        String path = pcapPath+((EditText)findViewById(R.id.editTextPcapPath)).getText().toString();
        PacketAnalyzer packetAnalyzer = new PacketAnalyzer(path);
        packetAnalyzer.extractPackets();

        packetsText=packetAnalyzer.extractVerbosePacketHeaders();
        adapter=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,packetsText);

        ListView listView = (ListView)findViewById(R.id.packetsListView);
        listView.setAdapter(adapter);

        adapter.notifyDataSetChanged();

    }
}
