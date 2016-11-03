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
 *
 * This file is part of Minishark.
 *
 *   Minishark is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   Minishark is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with Minishark.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Project repository : https://github.com/Moi4167/Minishark
 */

public class AnalyzerActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.analyzer_view);

    }

    public void loadPackets(View view) {
        String pcapPath = "/storage/emulated/legacy/pcap/";
        String path = pcapPath +((EditText)findViewById(R.id.editTextPcapPath)).getText().toString();
        PacketAnalyzer packetAnalyzer = new PacketAnalyzer(path);
        packetAnalyzer.extractPackets();

        ArrayList<String> packetsText = packetAnalyzer.extractVerbosePacketHeaders();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, packetsText);

        ListView listView = (ListView)findViewById(R.id.packetsListView);
        listView.setAdapter(adapter);

        adapter.notifyDataSetChanged();

    }
}
