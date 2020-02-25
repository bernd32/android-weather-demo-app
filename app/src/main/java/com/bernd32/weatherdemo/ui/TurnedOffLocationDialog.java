package com.bernd32.weatherdemo.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.bernd32.weatherdemo.R;

/**
 * This dialog fragment is showed if user's location if turned off
 */

public class TurnedOffLocationDialog extends DialogFragment {

    private static final String TAG = "TurnedOffLocationDialog";

    public TurnedOffLocationDialog() {
    }

    public static TurnedOffLocationDialog newInstance() {
        TurnedOffLocationDialog frag = new TurnedOffLocationDialog();
        return frag;
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.d(TAG, "onCreateDialog: started");
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.location_turned_off)
                .setPositiveButton(R.string.turn_on_location, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                })
                .setNegativeButton(R.string.enter_a_city, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(getContext(), AddNewLocation.class));
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

}
