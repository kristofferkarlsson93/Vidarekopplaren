package com.karlssonkristoffer.vidarekopplaren;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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
        dbHelper = new DatabaseHelper(context);
        this.context = context;
        Bundle extras = intent.getExtras();
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
