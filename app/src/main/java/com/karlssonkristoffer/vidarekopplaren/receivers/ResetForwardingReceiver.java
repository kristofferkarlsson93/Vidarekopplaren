package com.karlssonkristoffer.vidarekopplaren.receivers;

import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.karlssonkristoffer.vidarekopplaren.Utils;
import com.karlssonkristoffer.vidarekopplaren.appActivities.CurrentlyForwardingActivity;
import com.karlssonkristoffer.vidarekopplaren.DatabaseHelper;
import com.karlssonkristoffer.vidarekopplaren.Forwarder;
import com.karlssonkristoffer.vidarekopplaren.appActivities.MainActivity;
import com.karlssonkristoffer.vidarekopplaren.R;
import com.karlssonkristoffer.vidarekopplaren.constants.Notifications;

/**
 * Created by karls on 14/10/2017.
 */

public class ResetForwardingReceiver extends BroadcastReceiver {
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
            dbHelper.setCurrentlyForwardingFlag(false);
            dbHelper.setShouldKillForwarding(true);
            Intent resetIntent =  new Intent(context, MainActivity.class);
            resetIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(resetIntent);
        }
    }

    private void sendNotification(String message, int id) {

        //Due to android 8 (Oreo) we need to use another builder to be able to use channel
        // to send notifications
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            sendNotificationForOreoAndAbove(message, id);
        } else {
            sendStandardNotification(message, id);
        }
    }

    private void sendNotificationForOreoAndAbove(String message, int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = Notifications.UNLOCK_ON_PHONE_OPENED_NOTIFICATION_CHANEL_ID;
            Notification.Builder oreoBuilder = new Notification.Builder(context, channelId)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                            R.mipmap.ic_launcher))
                    .setContentTitle("Vidarekopplaren")
                    .setContentText(message);
            NotificationManager oreoNotificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            oreoNotificationManager.notify(id, oreoBuilder.build());
        }
    }

    private void sendStandardNotification(String message, int id) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                        R.mipmap.ic_launcher))
                .setContentTitle("Vidarekopplaren")
                .setContentText(message);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id, builder.build());

    }

    private boolean phoneIsLocked(Context context) {
        KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        return keyguardManager.inKeyguardRestrictedInputMode();
    }
}