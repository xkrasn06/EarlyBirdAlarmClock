package com.software.lightning.earlybirdalarmclock;

import android.app.AlarmManager;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TimePicker;
import android.app.PendingIntent;
import android.content.Intent;
import android.widget.Toast;
import android.widget.ToggleButton;
import java.util.Calendar;
import java.util.TimeZone;

import static android.app.PendingIntent.FLAG_NO_CREATE;
import static android.app.PendingIntent.getActivity;

public class MainActivity extends AppCompatActivity {

    TimePicker alarmTimePicker;
    private PendingIntent pendingIntent;
    AlarmManager alarmManager;
    SharedPreferences sharedPref;
    ToggleButton button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        setContentView(R.layout.activity_main);
        alarmTimePicker = (TimePicker) findViewById(R.id.timePicker);
        button = (ToggleButton) findViewById(R.id.toggleButton);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        if (PendingIntent.getBroadcast(MainActivity.this, 0, intent, FLAG_NO_CREATE) != null) {     // je nastavena nejaka udalost
            long time = sharedPref.getLong("eayrlybirdalarmclock.next", -1);
            if (time > 0) {
                time += TimeZone.getDefault().getRawOffset();
                button.setChecked(true);
                int minute = (int) ((time / 60000) % 60);
                int hour = (int) ((time / 60000 / 60) % 24);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmTimePicker.setMinute(minute);
                    alarmTimePicker.setHour(hour);
                } else {
                    alarmTimePicker.setCurrentMinute(minute);
                    alarmTimePicker.setCurrentHour(hour);
                }
            }
        }


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
            Toast.makeText(MainActivity.this, "Alarm has been set", Toast.LENGTH_SHORT).show();
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
            SharedPreferences.Editor e = sharedPref.edit();
            e.putLong("eayrlybirdalarmclock.next", time);
            e.commit();
            //salarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, 10000, pendingIntent);
            alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);

        }
        else
        {
            stopAlarm();
        }

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
        if(pendingIntent != null){
            alarmManager.cancel(pendingIntent);
        } else {
            Intent intent = new Intent(this, AlarmReceiver.class);
            PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0).cancel();
        }

        Toast.makeText(MainActivity.this, "Alarm has been stopped", Toast.LENGTH_SHORT).show();
    }
}


