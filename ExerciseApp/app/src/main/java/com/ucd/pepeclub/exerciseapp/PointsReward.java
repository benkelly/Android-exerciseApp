package com.ucd.pepeclub.exerciseapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class PointsReward extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        Bundle mArgs = getArguments();
        builder.setMessage(mArgs.getString("points"))
                .setPositiveButton("Show Analysis", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // launch new activity with analysis
                        ((RunTracker) getActivity()).createAnalysisActivity();
                    }
                })
                .setNegativeButton("Go Away", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // close the fragment
                        getActivity().getFragmentManager().popBackStack();
                    }
                });

        return builder.create();
    }


}
