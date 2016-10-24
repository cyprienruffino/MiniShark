package ovh.soup.minishark.views;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import ovh.soup.minishark.R;

/**
 * Created by cyprien on 13/07/16.
 */
public class ActionDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder =new AlertDialog.Builder(getActivity());
        String[] choices={getResources().getString(R.string.connect),getResources().getString(R.string.getKey)};
        Bundle bundle = this.getArguments();

        builder.setTitle(R.string.attack_end + " "+ bundle.get("ssid"))
                .setItems(choices, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:

                                break;
                            case 1:

                                break;
                        }
                    }
                });
        return builder.create();
    }

}
