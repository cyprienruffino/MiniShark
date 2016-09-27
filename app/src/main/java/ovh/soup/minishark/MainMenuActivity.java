package fr.soup.minishark;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import fr.soup.minishark.R;
import fr.soup.minishark.sniffer.SnifferActivity;

/**
 * Created by cyprien on 21/09/16.
 */

public class MainMenuActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.menu);
    }

    public void capture(View view) {
        Intent intent = new Intent(this, SnifferActivity.class);
        startActivity(intent);
    }
}
