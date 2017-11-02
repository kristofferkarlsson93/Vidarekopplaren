package com.karlssonkristoffer.vidarekopplaren;

import android.telephony.PhoneStateListener;
import android.util.Log;

/**
 * Created by karls on 02/11/2017.
 */

public class MyPhoneStateListener extends PhoneStateListener {
    private static final MyPhoneStateListener ourInstance = new MyPhoneStateListener();

    public static MyPhoneStateListener getInstance() {
        return ourInstance;
    }

    private MyPhoneStateListener() {
    }

    @Override
    public void onCallForwardingIndicatorChanged(boolean cfi) {
        Log.d("testKarlsson", "Call state changed: " + cfi);
        super.onCallForwardingIndicatorChanged(cfi);

    }
}
