package com.software.lightning.earlybirdalarmclock;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;
import java.util.Calendar;

import static android.app.PendingIntent.getActivity;

public class MainActivity extends AppCompatActivity {

    TimePicker alarmTimePicker;
    private PendingIntent pendingIntent;
    AlarmManager alarmManager;
    SeekBar seekBar;
    TextView textView;
    boolean inSettings = false;

    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        alarmTimePicker = (TimePicker) findViewById(R.id.timePicker);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //((AlarmManager) getSystemService(ALARM_SERVICE)).set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis() + 3000,
        //        PendingIntent.getBroadcast(MainActivity.this, 0, new Intent(this, AlarmReceiver.class), 0));
    }


    public void OnToggleClicked(View view)
    {
        long time;
        if (((ToggleButton) view).isChecked())
        {
            Toast.makeText(MainActivity.this, "ALARM ON", Toast.LENGTH_SHORT).show();
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, alarmTimePicker.getCurrentHour());
            calendar.set(Calendar.MINUTE, alarmTimePicker.getCurrentMinute());
            Intent intent = new Intent(this, AlarmReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);

            time=(calendar.getTimeInMillis()-(calendar.getTimeInMillis()%60000));
            if(System.currentTimeMillis()>time)
            {
                if (calendar.AM_PM == 0)
                    time = time + (1000*60*60*12);
                else
                    time = time + (1000*60*60*24);
            }
            //salarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, 10000, pendingIntent);
            alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);

        }
        else
        {
            Toast.makeText(MainActivity.this, "ALARM OFF", Toast.LENGTH_SHORT).show();
            stopAlarm();
        }
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        int perc = sharedPref.getInt("pref_percentage", 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            intent.putExtra( SettingsActivity.EXTRA_NO_HEADERS, true );
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void stopAlarm(){
        if(alarmManager != null){
            alarmManager.cancel(pendingIntent);
        }

        Toast.makeText(MainActivity.this, "ALARM WAS STOPED", Toast.LENGTH_SHORT).show();
    }


    private void createAlarmDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set title
        alertDialogBuilder.setTitle("Your Title");

        // set dialog message
        alertDialogBuilder
                .setMessage("Click yes to exit!")
                .setCancelable(false)
                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        MainActivity.this.finish();
                    }
                })
                .setNegativeButton("No",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
        }



}


