package com.software.lightning.earlybirdalarmclock;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;

/**
 * Created by pavel on 22.11.16.
 */

public class CreditInfoPreference extends Preference {
    public CreditInfoPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayoutResource(R.layout.preference_credit_info);
    }

    public CreditInfoPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CreditInfoPreference(Context context) {
        super(context);
    }
}
