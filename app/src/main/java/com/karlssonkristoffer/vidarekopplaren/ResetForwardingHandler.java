package com.karlssonkristoffer.vidarekopplaren;

import android.app.KeyguardManager;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

/**
 * Created by karls on 14/10/2017.
 */

public class ResetForwardingHandler extends BroadcastReceiver {
    private static final int NOTIFICATION_ID = 1;

    private final String TAG =  "resetforwardingactivity";
    private Forwarder forwarder;
    private DatabaseHelper dbHelper;
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;
        dbHelper = new DatabaseHelper(context);
        KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        if( keyguardManager.inKeyguardRestrictedInputMode()) {
            sendNotification("Avslutar vidarekoppling vid upplåsning", 001);
            Intent currentlyForwardingActivity = new Intent(context, CurrentlyForwardingActivity.class);
            currentlyForwardingActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(currentlyForwardingActivity);
        } else {
            dbHelper.setCurrentlyCallingFlag(false);
            Intent resetIntent =  new Intent(context, MainActivity.class);
            resetIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(resetIntent);
            Forwarder forwarder = new Forwarder(context);
            forwarder.stop();
        }
    }

    private void sendNotification(String message, int id) {
        NotificationCompat.Builder mBuilder =
            new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Vidarekopplaren")
                .setContentText(message);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id, mBuilder.build());
    }


}
