package com.karlssonkristoffer.vidarekopplaren;

import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class CurrentlyForwardingActivity extends AppCompatActivity {

    private static final String TAG = "currentlyforwarding";
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        dbHelper = new DatabaseHelper(this);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currently_forwarding);
        if(!this.dbHelper.getCurrentlyCallingFlag()) {
            finish();
            return;
        }

        String stopTime = dbHelper.getLatestStopForwardingTime();
        KeyguardManager myKM = (KeyguardManager) this.getSystemService(Context.KEYGUARD_SERVICE);
        if( myKM.inKeyguardRestrictedInputMode()) {
            PhoneOnlockedReciver reciver = new PhoneOnlockedReciver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_USER_PRESENT);
            filter.addAction(Intent.ACTION_SCREEN_ON);
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            registerReceiver(reciver, filter);
        }
        TextView forwardStopTime = (TextView) findViewById(R.id.forwardStopTime);
        forwardStopTime.setText(stopTime);
        final Button stopForwardButton = (Button) findViewById(R.id.stopForwardButton);
        stopForwardButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                resetAll();
            }
        });
    }

    private void resetAll() {
        dbHelper.setCurrentlyCallingFlag(false);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        PendingIntent stopCancelTimer;
        stopCancelTimer = PendingIntent.getBroadcast(this, MainActivity.CANCEL_INTENT_CODE, new Intent(this, ResetForwardingHandler.class), PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.cancel(stopCancelTimer);
        Forwarder forwarder = new Forwarder(this);
        forwarder.stop();
        this.finish();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        this.setIntent(intent);
    }
}
