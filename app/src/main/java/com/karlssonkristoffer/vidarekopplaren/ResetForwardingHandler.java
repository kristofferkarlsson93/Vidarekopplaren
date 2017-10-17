package com.karlssonkristoffer.vidarekopplaren;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by karls on 14/10/2017.
 */

public class ResetForwardingHandler extends BroadcastReceiver {

    private final String TAG =  "resetforwardingactivity";
    private CallManager callManager;
    private DatabaseHelper dbHelper;
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        dbHelper = new DatabaseHelper(context);
        this.context = context;
        Bundle extras = intent.getExtras();
        dbHelper.setCurrentlyCallingFlag(false);
        Log.d(TAG, String.valueOf(dbHelper.getCurrentlyCallingFlag()));
        CallManager callManager = new CallManager(new ServiceProvider(context));
        callManager.stopForwarding();

    }


}
