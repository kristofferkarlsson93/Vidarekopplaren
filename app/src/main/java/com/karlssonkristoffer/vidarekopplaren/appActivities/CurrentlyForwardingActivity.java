package com.karlssonkristoffer.vidarekopplaren.appActivities;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.karlssonkristoffer.vidarekopplaren.DatabaseHelper;
import com.karlssonkristoffer.vidarekopplaren.Forwarder;
import com.karlssonkristoffer.vidarekopplaren.R;
import com.karlssonkristoffer.vidarekopplaren.Utils;
import com.karlssonkristoffer.vidarekopplaren.receivers.PhoneOnlockedReciver;

public class CurrentlyForwardingActivity extends AppCompatActivity {

    private static final String TAG = "currentlyforwarding";
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        dbHelper = new DatabaseHelper(this);
        super.onCreate(savedInstanceState);
        if(!this.dbHelper.getCurrentlyCallingFlag()) {
            finish();
            return;
        }
        setContentView(R.layout.activity_currently_forwarding);
        String stopTime = dbHelper.getLatestStopForwardingTime();
        KeyguardManager keyGuardManager = (KeyguardManager) this.getSystemService(Context.KEYGUARD_SERVICE);
        if( keyGuardManager.inKeyguardRestrictedInputMode()) {
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
        if (Utils.wantsToHideApp(this) && dbHelper.getShouldHideApp()) {
            putAppInBackground();
            dbHelper.setShouldHideApp(false);
        }
    }

    private void putAppInBackground() {
        new android.os.Handler().postDelayed(
            new Runnable() {
                public void run() {
                    Utils.updateWidget(CurrentlyForwardingActivity.this);
                    Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                    homeIntent.addCategory(Intent.CATEGORY_HOME);
                    homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(homeIntent);
                }
            },
            3000);
    }

    private void resetAll() {
        dbHelper.setCurrentlyForwardingFlag(false);
        Utils.cancelTimer(this);
        Forwarder forwarder = new Forwarder(this);
        forwarder.stop();
        Utils.updateWidget(this);
        this.finish();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        this.setIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Utils.updateWidget(this);
        if(!dbHelper.getCurrentlyCallingFlag()) {
            this.finish();
            return;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
