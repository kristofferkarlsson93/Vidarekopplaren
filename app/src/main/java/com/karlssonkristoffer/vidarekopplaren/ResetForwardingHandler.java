package com.karlssonkristoffer.vidarekopplaren;

import android.app.KeyguardManager;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by karls on 14/10/2017.
 */

public class ResetForwardingHandler extends BroadcastReceiver {
    private static final int NOTIFICATION_ID = 1;

    private final String TAG =  "resetforwardingactivity";
    private CallManager callManager;
    private DatabaseHelper dbHelper;
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {

        KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        if( keyguardManager.inKeyguardRestrictedInputMode()) {
            Log.d("testKarlsson", "locked in resetHndler");
            Intent currentlyForwardingActivity = new Intent(context, MainActivity.class);
            currentlyForwardingActivity.putExtra(MainActivity.SHOULD_STOP_FORWARDING, true);
            context.startActivity(currentlyForwardingActivity);
        } else {
            Log.d("testKarlsson", "öppen telefon");
            dbHelper = new DatabaseHelper(context);
            this.context = context;
            dbHelper.setCurrentlyCallingFlag(false);
            NotificationCompat.Builder notification = new NotificationCompat.Builder(context);
            notification.setContentTitle("Vidarekoppling avslutad");
            notification.setContentText("Avslutad");
            Intent resetIntent =  new Intent(context, MainActivity.class);
            context.startActivity(resetIntent);
            CallManager callManager = new CallManager(new ServiceProvider(context));
            callManager.stopForwarding();
        }





    }


}
