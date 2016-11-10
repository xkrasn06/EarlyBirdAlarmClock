package com.software.lightning.earlybirdalarmclock;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class AlarmActivity extends AppCompatActivity {

    Ringtone ringtone;
    static double snoozeCredit = 0.0;

    public static void rechargeCredit(double amount) {
        snoozeCredit += amount;
    }

    public static void spendCredit(double amount) {
        snoozeCredit -= amount;
    }

    public void showCredit() {
        String toastMessage = "Remaining snooze credit is " + AlarmActivity.getCredit();
        Toast.makeText(AlarmActivity.this, toastMessage , Toast.LENGTH_SHORT).show();
    }

    public static double getCredit() {
        return snoozeCredit;
    }
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null)
        {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
        ringtone = RingtoneManager.getRingtone(this, alarmUri);
        ringtone.play();

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
        alert.setMessage("Its time for waking up.");
        alert.setCancelable(false);

        alert.setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                ringtone.stop();
                AlarmActivity.this.finish();
            }
        });

        final AlarmActivity _this = this;
        alert.setNegativeButton("Postpone (0.20$)", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                ((AlarmManager) getSystemService(ALARM_SERVICE)).set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis() + 600000,
                        PendingIntent.getBroadcast(AlarmActivity.this, 0, new Intent(_this, AlarmReceiver.class), 0));
                ringtone.stop();

                spendCredit(0.20);
                showCredit();
                AlarmActivity.this.finish();
            }
        });
        AlertDialog dlg = alert.create();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD|
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        return dlg;
    }
}
