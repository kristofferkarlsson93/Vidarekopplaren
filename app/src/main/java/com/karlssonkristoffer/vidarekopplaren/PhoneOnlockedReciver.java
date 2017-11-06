package com.karlssonkristoffer.vidarekopplaren;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class PhoneOnlockedReciver extends BroadcastReceiver {


    private static final String TAG = "phoneonlockedreciever" ;
    private DatabaseHelper dbHelper;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)){
            dbHelper = new DatabaseHelper(context);
            dbHelper.setCurrentlyCallingFlag(false);
            Intent resetIntent =  new Intent(context, MainActivity.class);
            resetIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(resetIntent);
            Forwarder forwarder = new Forwarder(context);
            forwarder.stop();
            context.unregisterReceiver(this);
        }
    }
}
