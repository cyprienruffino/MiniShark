package fr.soup.minishark.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import fr.soup.minishark.R;

/**
 * Created by cyprien on 13/07/16.
 */
public class ConnectionDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder =new AlertDialog.Builder(getActivity());
        String[] choices={getResources().getString(R.string.yes),getResources().getString(R.string.no)};
        Bundle bundle = this.getArguments();

        builder.setMessage(R.string.not_connected_message);
        builder.setTitle(R.string.connect_title)
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
