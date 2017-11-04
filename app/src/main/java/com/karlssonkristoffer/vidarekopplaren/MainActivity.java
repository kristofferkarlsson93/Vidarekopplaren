package com.karlssonkristoffer.vidarekopplaren;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import java.util.Calendar;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.karlssonkristoffer.vidarekopplaren.components.PhoneNumberList;
import com.yinglan.circleviewlibrary.CircleAlarmTimerView;
import com.karlssonkristoffer.vidarekopplaren.components.TimePicker;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "mainactivity";

    public static final int CANCEL_INTENT_CODE = 100;

    private EditText newPhoneNumberText;
    private CircleAlarmTimerView timerView;
    private Forwarder forwarder;
    private DatabaseHelper dbHelper;
    private Button startForwardingButton;
    private PhoneNumberList phoneNumberList;
    private TimePicker timePicker;

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        dbHelper = new DatabaseHelper(this);

        if(dbHelper.getCurrentlyCallingFlag()) {
            startNextActivity();
        }
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[] {Manifest.permission.READ_PHONE_STATE}, PermissionCodes.PERMISSION_REQUEST_OPERATOR);
            requestPermissions(new String[] {Manifest.permission.CALL_PHONE}, PermissionCodes.PERMISSION_CALL);
        }
        startForwardingButton = (Button) findViewById(R.id.startForwardingButton);
        setupComponents();
        manageInputField();
        manageStartForwardingButton();
    }

    private void setupComponents() {
        ListView phoneNumberListView = (ListView) findViewById(R.id.phoneNumberListView);
        phoneNumberList = new PhoneNumberList(dbHelper, this, phoneNumberListView);
        phoneNumberList.create();

        TextView choseTimeText = (TextView) findViewById(R.id.choseTimeText);
        View circleView = (View) findViewById(R.id.circle);
        timePicker = new TimePicker(this, dbHelper, choseTimeText, circleView);
        timePicker.create();
    }

    private void manageInputField() {
        newPhoneNumberText = (EditText) findViewById(R.id.newPhoneNumberText);
        final Button addButton = (Button) findViewById(R.id.addButton);
        final FloatingActionButton addPhoneNumberButton = (FloatingActionButton) findViewById(R.id.addPhonenumberButton);
        addPhoneNumberButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(final View v) {
                newPhoneNumberText.setVisibility(View.VISIBLE);
                newPhoneNumberText.requestFocus();
                showKeyboard();
                newPhoneNumberText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        addButton.setVisibility(View.VISIBLE);
                        newPhoneNumberText.setTextColor(Color.BLACK);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if(newPhoneNumberText.getText().length() < 1 ) {
                            addButton.setVisibility(View.GONE);
                            newPhoneNumberText.setVisibility(View.GONE);
                            View view = MainActivity.this.getCurrentFocus();
                            if (view != null) {
                                hideKeyboard(view);
                            }
                        } else {
                            addButton.setVisibility(View.VISIBLE);
                            newPhoneNumberText.setVisibility(View.VISIBLE);
                        }
                    }
                });

                addButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        String input = getPhoneNumberFromInput();
                         input = PhoneNumber.formatPhoneNumber(input);
                        if (!PhoneNumber.validatePhoneNumber(input)) {
                            toastOut("Felformaterat telefonnummer");
                            newPhoneNumberText.setTextColor(Color.RED);
                            return;
                        }
                        PhoneNumber phoneNumber = new PhoneNumber(input);
                        boolean insertData = dbHelper.addPhoneNumber(phoneNumber);
                        if(insertData) {
                            toastOut(phoneNumber.getPhoneNumber() + " tillagt");
                            newPhoneNumberText.setText("");
                            phoneNumberList.update();
                            View view = MainActivity.this.getCurrentFocus();
                            if (view != null) {
                               hideKeyboard(view);
                            }
                        } else {
                            toastOut("Oväntat fel. Prova igen");
                        }
                        addButton.setVisibility(View.GONE);
                        newPhoneNumberText.setVisibility(View.GONE);
                    }
                });
            }
        });
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
            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 10000, pendingIntent);
            startNextActivity();
            TelephonyManager manager;
            manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            manager.listen(MyPhoneStateListener.getInstance(), PhoneStateListener.LISTEN_CALL_FORWARDING_INDICATOR);
            forwarder = new Forwarder(MainActivity.this);
            forwarder.start(MainActivity.this.phoneNumberList.getCurrentPhoneNumber());
            }
        });
    }

    private void startNextActivity() {
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
            toastOut("Välj ett telefonnummer");
            return true;
        } else if(!timePicker.hasSetCorrectTime(chosenTime)) {
            toastOut("Välj en tid i framtiden");
            timePicker.setErrorOnTime();
            return true;
        }
        return false;
    }



    private Calendar getCalendar(int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        return calendar;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void toastOut(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
