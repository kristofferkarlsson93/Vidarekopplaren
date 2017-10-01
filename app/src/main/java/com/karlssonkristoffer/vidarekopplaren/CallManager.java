package com.karlssonkristoffer.vidarekopplaren;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

/**
 * https://stackoverflow.com/questions/15842328/android-intent-action-call-uri
 * Created by Kristoffer on 2017-09-18.
 */

public class CallManager {
    private static final String TAG = "callmanager";

    private Context context;
    private PhoneNumber phoneNumber;
    private ServiceProvider serviceProvider;

    public CallManager(ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
        this.phoneNumber = serviceProvider.getPhonenumberObject();
    }

    public void startForwarding() {
        Log.d(TAG, phoneNumber.getPhoneNumberWithForwardPrefix() );
        /*Intent callForwardIntent = new Intent((Intent.ACTION_CALL));
        Uri uri = Uri.fromParts("tel", phoneNumber.getPhoneNumberWithForwardPrefix(), "#");
        callForwardIntent.setData(uri);
        context.startActivity(callForwardIntent);*/

    }

    public void stopForwarding() {
        Log.d(TAG, PhoneNumber.getCancelNumberBasedOnOperator(phoneNumber.getOperator()));
        /*Intent cancelForwardIntent = new Intent((Intent.ACTION_CALL));
        Uri uri = Uri.fromParts("tel",
                PhoneNumber.getCancelNumberBasedOnOperator(phoneNumber.getOperator()), "#");
        cancelForwardIntent.setData(uri);
        context.startActivity(cancelForwardIntent);*/
    }
}
