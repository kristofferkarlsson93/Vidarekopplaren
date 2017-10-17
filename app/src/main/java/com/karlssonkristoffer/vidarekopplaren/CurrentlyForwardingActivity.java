package com.karlssonkristoffer.vidarekopplaren;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CurrentlyForwardingActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        dbHelper = new DatabaseHelper(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currently_forwarding);

        String stopTime = getIntent().getStringExtra(MainActivity.STOP_TIME_KEY);

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
        CallManager callManager = new CallManager(new ServiceProvider(this));
        callManager.stopForwarding();
        this.finish();
    }

}
