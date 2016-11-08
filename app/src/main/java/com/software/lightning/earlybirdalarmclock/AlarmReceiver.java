package com.software.lightning.earlybirdalarmclock;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;


public class AlarmReceiver extends BroadcastReceiver {


    Ringtone ringtone;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wl.acquire();
        Intent alarmIntent = new Intent("android.intent.action.MAIN");

        alarmIntent.setClass(context, AlarmActivity.class);
        alarmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        alarmIntent.putExtra("AlarmID", intent.getIntExtra("AlarmID", -1));

        context.startActivity(alarmIntent);
        wl.release();
    }


}
