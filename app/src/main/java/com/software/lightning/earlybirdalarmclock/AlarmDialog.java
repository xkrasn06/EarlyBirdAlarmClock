package com.software.lightning.earlybirdalarmclock;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.view.WindowManager;

/**
 * Created by lubom on 31.10.2016.
 */

public class AlarmDialog extends Activity {
    static Ringtone ringtone;

    public static void alarmDialog(Context context, String title, String message) {
        //Intent mIntent = new Intent(context,AlarmDialog.class); //Same as above two lines
        //mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //context.startActivity(mIntent);
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null)
        {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
        ringtone = RingtoneManager.getRingtone(context, alarmUri);
        //ringtone.play();

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
        //alertDialog.show();
        alertDialog.getWindow()
                .setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alertDialog.show();
    }

    private int m_alarmId;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Get the alarm ID from the intent extra data
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if (extras != null) {
            m_alarmId = extras.getInt("AlarmID", -1);
        } else {
            m_alarmId = -1;
        }

        // Show the popup dialog
        showDialog(0);
    }

    @Override
    protected Dialog onCreateDialog(int id)
    {
        super.onCreateDialog(id);

        // Build the dialog
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("EarlyBird Alarm");
        alert.setMessage("Its time for the alarm with ID: " + m_alarmId);
        alert.setCancelable(false);
        final AlarmDialog _this = this;
        alert.setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                ((AlarmManager) getSystemService(ALARM_SERVICE)).set(AlarmManager.RTC_WAKEUP,0,
                        PendingIntent.getBroadcast(AlarmDialog.this, 0, new Intent(_this, AlarmReceiver.class), 0));
            }
        });

        // Create and return the dialog
        AlertDialog dlg = alert.create();

        return dlg;
    }
}
