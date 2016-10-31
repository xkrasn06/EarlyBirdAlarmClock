package com.software.lightning.earlybirdalarmclock;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by lubom on 31.10.2016.
 */

public class AlarmDialog {

    public static void alarmDialog(Context context, String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        // Set Dialog Title
        alertDialog.setTitle(title);

        // Set Dialog Message
        alertDialog.setMessage(message);


        // Set OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        // Show Alert Message
        alertDialog.show();
    }
}
