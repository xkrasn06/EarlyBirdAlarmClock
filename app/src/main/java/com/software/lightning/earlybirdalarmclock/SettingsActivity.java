package com.software.lightning.earlybirdalarmclock;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import com.software.lightning.earlybirdalarmclock.util.IabHelper;
import com.software.lightning.earlybirdalarmclock.util.IabResult;
import com.software.lightning.earlybirdalarmclock.util.Purchase;

import java.util.List;


public class SettingsActivity extends AppCompatPreferenceActivity {

    public static final String EXTRA_NO_HEADERS = ":android:no_headers";
    public static final String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsJnYYcIPYkibdrcpH7JwK+1m267fgXZLt5MkwsNKb1V8iW0AlX00XYu/HhyIpt2MBT9zMLRkzx4PT08bVmgpY6yAplvefbOK/gkC7/lQeTYamxBexjsYGpmlmvZGa+QJAiMUeGe2eX9xG/37y3EP6n7ia0Cd//tzMg2Swsux2s9Z5OaoSh7oZ1kBAC3pTGbZG+7OBD8+HB+V0R4l5AxjBludE4gpyoi1wcaQ/52Jah4cZfE+EGg0hMruB/vERjtqSP4bapm2RrrVZriHONA6iL1J9+fni4lB/SdISCArwH37g8eN+b8D5DGZoqCD072SVVcDSvEqqCmN7kNgMoE+nwIDAQAB";
    public static IabHelper mHelper;
    //public final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

    public void toastCredit() {
        //String toastMessage = "Remaining credit is " + AlarmActivity.getCredit();
        //Toast.makeText(SettingsActivity.this, toastMessage , Toast.LENGTH_SHORT).show();
    }
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            //preference.setSummary(stringValue);
            return true;
        }
    };



    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }


    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getInt(preference.getKey(), 0));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new GeneralPreferenceFragment())
                .commit();

        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
        mHelper = new IabHelper(this, SettingsActivity.base64EncodedPublicKey);
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    Log.d("earlybird", "In-app Billing setup failed: " + result);
                } else {
                    Log.d("earlybird", "In-app Billing is set up OK");
                }
            }
        });
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (!super.onMenuItemSelected(featureId, item)) {
                NavUtils.navigateUpFromSameTask(this);
            }
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }


    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }




    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName);
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        SharedPreferences sharedPref;


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            final Activity activity = getActivity();

            final IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener
                    = new IabHelper.OnIabPurchaseFinishedListener() {
                public void onIabPurchaseFinished(IabResult result, Purchase purchase)
                {
                    if (result.isFailure()) {
                        Log.d("earlybird", "Error purchasing: " + result);
                        return;
                    }
                    else if (purchase.getSku().equals("com.software.lightning.earlybirdalarmclock")) {
                        Toast.makeText(activity, "purchased", Toast.LENGTH_SHORT).show();
                    }
                }
            };

            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);
            bindPreferenceSummaryToValue(findPreference("pref_percentage"));


            final Preference creditViewPref = findPreference("pref_credit_view");
            creditViewPref.setSummary("" + String.format("%.2f", sharedPref.getFloat("pref_credit", 0)) + "$");

            final Preference creditPref = findPreference("pref_credit");
            creditPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    SharedPreferences.Editor e = sharedPref.edit();
                    e.putFloat("pref_credit", sharedPref.getFloat("pref_credit", 0) + 2);
                    e.commit();
                    creditViewPref.setSummary("" + String.format("%.2f", sharedPref.getFloat("pref_credit", 0)) + "$");
                    try {
                        mHelper.launchPurchaseFlow(activity, "android.test.purchased", 10001,
                                mPurchaseFinishedListener, "mypurchasetoken");
                    } catch (Exception ex) {
                        Log.d("earlybird_log", "Error purchasing: " + ex.getMessage());
                    }
                    return false;
                }
            });
        }
    }
}
