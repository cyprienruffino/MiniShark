package fr.soup.wepbash.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import fr.soup.wepbash.R;

/**
 * Created by cyprien on 13/07/16.
 */
public class CreditsDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder =new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.attack_end);
        builder.setMessage("Soup FRENCH");
        return builder.create();
    }
}
