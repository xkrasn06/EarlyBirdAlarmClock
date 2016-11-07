package com.software.lightning.earlybirdalarmclock;

import android.app.AlarmManager;
import android.app.TimePickerDialog;
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
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.TimePicker;
import android.app.PendingIntent;
import android.content.Intent;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.TimeZone;

import static android.app.PendingIntent.FLAG_NO_CREATE;
import static android.app.PendingIntent.getActivity;



public class MainActivity extends AppCompatActivity {

    final int[] DAYS = {Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY, Calendar.SUNDAY};

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
            long time = sharedPref.getLong("earlybirdalarmclock.next", -1);
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

        if (sharedPref.getBoolean("earlybirdalarmclock.day" + DAYS[0], false)) {
            ((CheckBox)findViewById(R.id.repeatMon)).setChecked(true);
        }
        if (sharedPref.getBoolean("earlybirdalarmclock.day" + DAYS[1], false)) {
            ((CheckBox)findViewById(R.id.repeatTue)).setChecked(true);
        }
        if (sharedPref.getBoolean("earlybirdalarmclock.day" + DAYS[2], false)) {
            ((CheckBox)findViewById(R.id.repeatWed)).setChecked(true);
        }
        if (sharedPref.getBoolean("earlybirdalarmclock.day" + DAYS[3], false)) {
            ((CheckBox)findViewById(R.id.repeatThu)).setChecked(true);
        }
        if (sharedPref.getBoolean("earlybirdalarmclock.day" + DAYS[4], false)) {
            ((CheckBox)findViewById(R.id.repeatFri)).setChecked(true);
        }
        if (sharedPref.getBoolean("earlybirdalarmclock.day" + DAYS[5], false)) {
            ((CheckBox)findViewById(R.id.repeatSat)).setChecked(true);
        }
        if (sharedPref.getBoolean("earlybirdalarmclock.day" + DAYS[6], false)) {
            ((CheckBox)findViewById(R.id.repeatSun)).setChecked(true);
        }

        String[] shortWeekdays = new DateFormatSymbols().getShortWeekdays();
        ((TextView)findViewById(R.id.textView8)).setText(shortWeekdays[Calendar.MONDAY]);
        ((TextView)findViewById(R.id.textView7)).setText(shortWeekdays[Calendar.TUESDAY]);
        ((TextView)findViewById(R.id.textView6)).setText(shortWeekdays[Calendar.WEDNESDAY]);
        ((TextView)findViewById(R.id.textView5)).setText(shortWeekdays[Calendar.THURSDAY]);
        ((TextView)findViewById(R.id.textView4)).setText(shortWeekdays[Calendar.FRIDAY]);
        ((TextView)findViewById(R.id.textView3)).setText(shortWeekdays[Calendar.SATURDAY]);
        ((TextView)findViewById(R.id.textView2)).setText(shortWeekdays[Calendar.SUNDAY]);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final MainActivity _this = this;
        alarmTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                if (button.isChecked()) {
                    stopAlarm();
                    Toast.makeText(MainActivity.this, "Alarm has been stopped", Toast.LENGTH_SHORT).show();
                }
                button.setChecked(false);
            }
        });

        TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {
                //Toast.makeText(MainActivity.this, "d___fsf_____sds", Toast.LENGTH_SHORT).show();
                stopAlarm();
            }
        };


        //((AlarmManager) getSystemService(ALARM_SERVICE)).set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis() + 3000,
        //        PendingIntent.getBroadcast(MainActivity.this, 0, new Intent(this, AlarmReceiver.class), 0));
    }

    public boolean anyDayChecked () {
        if (((CheckBox)findViewById(R.id.repeatMon)).isChecked() || ((CheckBox)findViewById(R.id.repeatTue)).isChecked() ||
                ((CheckBox)findViewById(R.id.repeatWed)).isChecked() || ((CheckBox)findViewById(R.id.repeatThu)).isChecked() ||
                ((CheckBox)findViewById(R.id.repeatFri)).isChecked() || ((CheckBox)findViewById(R.id.repeatSat)).isChecked() ||
                ((CheckBox)findViewById(R.id.repeatSun)).isChecked()) {
            return true;
        }
        return false;
    }

    void createRepeating (Calendar setter, int day) {
        Intent intent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(MainActivity.this, day, intent, 0);
        SharedPreferences.Editor e = sharedPref.edit();
        setter.set(Calendar.DAY_OF_WEEK, day);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, setter.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, pendingIntent);
        e.putBoolean("earlybirdalarmclock.day" + day, true);
        e.commit();
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
            e.putLong("earlybirdalarmclock.next", time);
            e.commit();
            if (anyDayChecked()) {
                Calendar setter = Calendar.getInstance();
                setter.setTimeInMillis(calendar.getTimeInMillis());
                if (((CheckBox)findViewById(R.id.repeatMon)).isChecked()) {
                    createRepeating(setter, Calendar.MONDAY);
                }
                if (((CheckBox)findViewById(R.id.repeatTue)).isChecked()) {
                    createRepeating(setter, Calendar.TUESDAY);
                }
                if (((CheckBox)findViewById(R.id.repeatWed)).isChecked()) {
                    createRepeating(setter, Calendar.WEDNESDAY);
                }
                if (((CheckBox)findViewById(R.id.repeatThu)).isChecked()) {
                    createRepeating(setter, Calendar.THURSDAY);
                }
                if (((CheckBox)findViewById(R.id.repeatFri)).isChecked()) {
                    createRepeating(setter, Calendar.FRIDAY);
                }
                if (((CheckBox)findViewById(R.id.repeatSat)).isChecked()) {
                    createRepeating(setter, Calendar.SATURDAY);
                }
                if (((CheckBox)findViewById(R.id.repeatSun)).isChecked()) {
                    createRepeating(setter, Calendar.SUNDAY);
                }
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
            }
        }
        else
        {
            stopAlarm();
            Toast.makeText(MainActivity.this, "Alarm has been stopped", Toast.LENGTH_SHORT).show();
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


    private void stopAlarm() {
        if (anyDayChecked()) {
            if (pendingIntent != null) {
                alarmManager.cancel(pendingIntent);
            } else {
                Intent intent = new Intent(this, AlarmReceiver.class);
                PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0).cancel();
            }
        } else {
            SharedPreferences.Editor e = sharedPref.edit();
            for (int i : DAYS) {
                Intent intent = new Intent(this, AlarmReceiver.class);
                PendingIntent.getBroadcast(MainActivity.this, i, intent, 0).cancel();
                e.putBoolean("earlybirdalarmclock.day" + i, true);
            }
            e.commit();
        }
    }
}


