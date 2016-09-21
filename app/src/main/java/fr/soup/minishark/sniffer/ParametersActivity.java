package fr.soup.minishark.sniffer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import fr.soup.minishark.R;

/**
 * Created by cyprien on 21/09/16.
 */

public class ParametersActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.parameters);
    }

    public void snifferStart(View view) {
        Intent intent = new Intent(this, SnifferActivity.class);
        String flags = "";

        EditText editText = (EditText) findViewById(R.id.manualflags);
        flags += editText.getText().toString() + " ";


        if(((CheckBox)findViewById(R.id.saveinfile)).isChecked()) {
            editText = (EditText) findViewById(R.id.pcapfile);
            flags += "-w /storage/emulated/legacy/" + editText.getText().toString() + " ";
        }

        if(((CheckBox)findViewById(R.id.rununtil)).isChecked()) {
            editText = (EditText) findViewById(R.id.rununtiltime);
            flags += "-G " + editText.getText().toString() + " ";
        }

        intent.putExtra(SnifferActivity.SNIFFER_FLAGS_INTENT, flags);
        startActivity(intent);
    }
}
