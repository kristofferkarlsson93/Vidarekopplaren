package com.karlssonkristoffer.vidarekopplaren.workToComeBackTo;

import android.telephony.PhoneStateListener;

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
        super.onCallForwardingIndicatorChanged(cfi);

    }
}
