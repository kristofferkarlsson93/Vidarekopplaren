package com.karlssonkristoffer.vidarekopplaren;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

/**
 * Created by karls on 19/10/2017.
 */

public class PhoneOnlockedReciver extends BroadcastReceiver {


    private static final String TAG = "phoneonlockedreciever" ;
    private Forwarder forwarder;
    private DatabaseHelper dbHelper;
    private Context context;

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

    private void sendNotification(String message) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Vidarekopplaren")
                        .setContentText(message);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(005, mBuilder.build());
    }
}
