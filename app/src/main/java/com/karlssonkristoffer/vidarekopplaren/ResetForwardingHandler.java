package com.karlssonkristoffer.vidarekopplaren;

import android.app.KeyguardManager;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

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
    public void onReceive(final Context context, Intent intent) {

        this.context = context;
        dbHelper = new DatabaseHelper(context);

        if(phoneIsLocked(context)) {
            sendNotification("Avslutar vidarekoppling vid upplåsning", 001);
            Intent currentlyForwardingActivity = new Intent(context, CurrentlyForwardingActivity.class);
            currentlyForwardingActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(currentlyForwardingActivity);
        } else {
            dbHelper.setCurrentlyCallingFlag(false);
            Forwarder forwarder = new Forwarder(context);
            forwarder.stop();

            final PendingResult result = goAsync();
            Thread thread = new Thread() {
                public void run() {
                    int i;
                    i = 0;
                    Intent resetIntent =  new Intent(context, MainActivity.class);
                    resetIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(resetIntent);
                    result.setResultCode(i);
                    result.finish();
                }
            };
            thread.start();
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

    private boolean phoneIsLocked(Context context) {
        KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        return keyguardManager.inKeyguardRestrictedInputMode();
    }

}
