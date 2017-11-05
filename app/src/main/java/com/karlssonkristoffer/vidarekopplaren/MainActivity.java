package com.karlssonkristoffer.vidarekopplaren;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.karlssonkristoffer.vidarekopplaren.components.PhoneNumberInputForm;
import com.karlssonkristoffer.vidarekopplaren.components.PhoneNumberList;
import com.yinglan.circleviewlibrary.CircleAlarmTimerView;
import com.karlssonkristoffer.vidarekopplaren.components.TimePicker;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "mainactivity";

    public static final int CANCEL_INTENT_CODE = 100;

    private EditText newPhoneNumberText;
    private Forwarder forwarder;
    private DatabaseHelper dbHelper;
    private Button startForwardingButton;
    private PhoneNumberList phoneNumberList;
    private TimePicker timePicker;
    private PhoneNumberInputForm phoneNumberInputForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        dbHelper = new DatabaseHelper(this);

        if(dbHelper.getCurrentlyCallingFlag()) {
            startStatusForwardingActivity();
        }
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cancelPosibleNotifications();

        if(isFirstTime()) {
            showInstruction();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[] {Manifest.permission.READ_PHONE_STATE}, PermissionCodes.PERMISSION_REQUEST_OPERATOR);
            requestPermissions(new String[] {Manifest.permission.CALL_PHONE}, PermissionCodes.PERMISSION_CALL);
        }
        startForwardingButton = (Button) findViewById(R.id.startForwardingButton);
        setupComponents();
        manageStartForwardingButton();
        manageInfoSign();
    }

    private void setupComponents() {
        ListView phoneNumberListView = (ListView) findViewById(R.id.phoneNumberListView);
        phoneNumberList = new PhoneNumberList(dbHelper, this, phoneNumberListView);
        phoneNumberList.create();

        TextView choseTimeText = (TextView) findViewById(R.id.choseTimeText);
        View circleView = (View) findViewById(R.id.circle);
        timePicker = new TimePicker(this, dbHelper, choseTimeText, circleView);
        timePicker.create();

        newPhoneNumberText = (EditText) findViewById(R.id.newPhoneNumberText);
        final Button addButton = (Button) findViewById(R.id.addButton);
        final FloatingActionButton addPhoneNumberButton = (FloatingActionButton) findViewById(R.id.addPhonenumberButton);

        phoneNumberInputForm = new PhoneNumberInputForm(this, dbHelper, newPhoneNumberText, addButton, addPhoneNumberButton, phoneNumberList);
        phoneNumberInputForm.create();
    }


    private void manageStartForwardingButton() {
        startForwardingButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

           if(checkForErrorsBeforeForwarding(timePicker.getChosenTimeInMilis())) {
               return;
           }
            dbHelper.setCurrentlyCallingFlag(true);
            dbHelper.setStopForwardingTime(timePicker.getStopTime());
            Intent startTimerIntent = new Intent(MainActivity.this, ResetForwardingHandler.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), CANCEL_INTENT_CODE , startTimerIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 15000, pendingIntent);
            startStatusForwardingActivity();
            //TelephonyManager manager;
            //manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            //manager.listen(MyPhoneStateListener.getInstance(), PhoneStateListener.LISTEN_CALL_FORWARDING_INDICATOR);
            forwarder = new Forwarder(MainActivity.this);
            forwarder.start(MainActivity.this.phoneNumberList.getCurrentPhoneNumber());
            }
        });
    }

    private void startStatusForwardingActivity() {
        Intent startNextScreenIntent = new Intent(MainActivity.this, CurrentlyForwardingActivity.class);
        startActivity(startNextScreenIntent);
    }

    private String getPhoneNumberFromInput() {
        return newPhoneNumberText.getText().toString();
    }

    private void showKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(newPhoneNumberText, InputMethodManager.SHOW_IMPLICIT);
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private boolean checkForErrorsBeforeForwarding(long chosenTime) {
        if (!phoneNumberList.hasChosenPhoneNumber()) {
            Utils.toastOut(this, "Välj ett telefonnummer");
            return true;
        } else if(!timePicker.hasSetCorrectTime(chosenTime)) {
            Utils.toastOut(this, "Välj en tid i framtiden");
            timePicker.setErrorOnTime();
            return true;
        }
        return false;
    }

    private boolean isFirstTime()
    {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        boolean ranBefore = preferences.getBoolean("RanBefore", false);
        if (!ranBefore) {
            // first time
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("RanBefore", true);
            editor.commit();
        }
        return !ranBefore;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void cancelPosibleNotifications() {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancelAll();
    }

    private void showInstruction() {
        final AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.InformationDialog));
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle("Information")
                .setMessage("Denna app fungerar enbart på telefoner där vidarekoppling är tillåten.")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    /*AlertDialog.Builder builder;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
    } else {
        builder = new AlertDialog.Builder(this);
    }

    builder.setTitle("Information")
            .setMessage("Denna app fungerar enbart på telefoner där vidarekoppling är tillåten")
            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // continue with delete
                }
            })
            .show();
}*/
    private void manageInfoSign() {
        ImageView infoSign = (ImageView) findViewById(R.id.infoSign);
        infoSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInstruction();
            }
        });
    }
}
