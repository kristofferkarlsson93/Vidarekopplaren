package com.karlssonkristoffer.vidarekopplaren;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * Created by karls on 19/10/2017.
 */

public class PhoneOnlockedReciver extends BroadcastReceiver {


    private static final String TAG = "phoneonlockedreciever" ;
    private CallManager callManager;
    private DatabaseHelper dbHelper;
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)){
            dbHelper = new DatabaseHelper(context);
            dbHelper.setCurrentlyCallingFlag(false);
            //sendNotification("Vidarekoppling avslutad");
            Intent resetIntent =  new Intent(context, MainActivity.class);
            context.startActivity(resetIntent);
            CallManager callManager = new CallManager(new ServiceProvider(context));
            callManager.stopForwarding();
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
