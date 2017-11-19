package com.karlssonkristoffer.vidarekopplaren;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Created by Kristoffer on 2017-09-17.
 */

public class OperatorHolder {
    private TelephonyManager manager;
    private static final String TAG = "operatorholder";

    public OperatorHolder(Context context) {
        manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        Log.d("testKarlsson", manager.getNetworkOperatorName());
    }

    public String getOperatorName() {
        return manager.getNetworkOperatorName();
    }

}
