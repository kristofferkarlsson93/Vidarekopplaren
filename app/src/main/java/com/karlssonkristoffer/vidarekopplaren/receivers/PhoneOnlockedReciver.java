package com.karlssonkristoffer.vidarekopplaren.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.karlssonkristoffer.vidarekopplaren.DatabaseHelper;
import com.karlssonkristoffer.vidarekopplaren.appActivities.MainActivity;

public class PhoneOnlockedReciver extends BroadcastReceiver {


    private static final String TAG = "phoneonlockedreciever" ;
    private DatabaseHelper dbHelper;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)){
            dbHelper = new DatabaseHelper(context);
            dbHelper.setCurrentlyForwardingFlag(false);
            dbHelper.setShouldKillForwarding(true);

            Intent resetIntent =  new Intent(context, MainActivity.class);
            resetIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(resetIntent);
            context.unregisterReceiver(this);
        }
    }
}
