package fr.soup.minishark.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import java.util.ArrayList;

import fr.soup.minishark.R;

/**
 * Created by cyprien on 13/07/16.
 */
public class DeviceDialog extends DialogFragment{

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder =new AlertDialog.Builder(getActivity());

        ArrayList<String> choices=getArguments().getStringArrayList("ifs");
        String[]choices_arr = new String[choices.size()];
        for (int i=0;i<choices.size();i++)choices_arr[i]=choices.get(i);
        builder.setTitle(R.string.choose_device)
                .setItems(choices_arr, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //TODO
                    }
                });

        return builder.create();
    }
}
