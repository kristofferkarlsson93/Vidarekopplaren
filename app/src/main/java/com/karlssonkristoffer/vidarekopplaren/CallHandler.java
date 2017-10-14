package com.karlssonkristoffer.vidarekopplaren;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by karls on 14/10/2017.
 */

public class CallHandler extends BroadcastReceiver {

    private final String TAG =  "callhandler";
    private CallManager callManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context,"Hej", Toast.LENGTH_LONG).show();
        Log.d(TAG, "HEEEEEEEj" );
        Bundle extras = intent.getExtras();
        String phonenumber = extras.getString(MainActivity.PHONE_NUMBER_KEY);
        Log.d(TAG, phonenumber);
        CallManager callManager = new CallManager(new ServiceProvider(context));
        callManager.stopForwarding();

    }
}
