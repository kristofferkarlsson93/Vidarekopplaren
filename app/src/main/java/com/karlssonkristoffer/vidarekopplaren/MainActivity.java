package com.karlssonkristoffer.vidarekopplaren;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.icu.util.Calendar;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.yinglan.circleviewlibrary.CircleAlarmTimerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "mainactivity";

    public static final String PHONE_NUMBER_KEY = "PHONE_NUMBER_KEY";
    public static final String STOP_TIME_KEY = "STOP_TIME_KEY";

    private EditText newPhoneNumberText;
    private CircleAlarmTimerView timerView;
    private String startTime;
    private String stopTime;
    private PendingIntent cancelForwardingIntent;
    private String currentPhoneNumber;
    private CallManager callManager;
    private boolean hasCalled = false;
    private ListView phoneNumberListView;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
         dbHelper = new DatabaseHelper(this);
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[] {Manifest.permission.READ_PHONE_STATE}, PermissionCodes.PERMISSION_REQUEST_OPERATOR);
            requestPermissions(new String[] {Manifest.permission.CALL_PHONE}, PermissionCodes.PERMISSION_CALL);
        }
        manageListView();
        manageInputField();
        manageTimer();
        manageStartForwardingButton();
    }

    private void manageListView() {
        phoneNumberListView = (ListView) findViewById(R.id.phoneNumberListView);
        Cursor data = dbHelper.getAllPhoneNumbers();
        ArrayList<String> listData = new ArrayList<>();
        while (data.moveToNext()) {
            listData.add(data.getString(1));
        }
        ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listData);
        phoneNumberListView.setAdapter(adapter);
        phoneNumberListView.setOnItemClickListener(
            new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    MainActivity.this.currentPhoneNumber = String.valueOf(parent.getItemAtPosition(position));
                    Toast.makeText(MainActivity.this, MainActivity.this.currentPhoneNumber, Toast.LENGTH_LONG).show();
                }
            }
        );


    }

    private void manageInputField() {
        newPhoneNumberText = (EditText) findViewById(R.id.newPhoneNumberText);
        final Button addButton = (Button) findViewById(R.id.addButton);
        final FloatingActionButton addPhoneNumberButton = (FloatingActionButton) findViewById(R.id.addPhonenumberButton);
        addPhoneNumberButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                newPhoneNumberText.setVisibility(View.VISIBLE);
                newPhoneNumberText.requestFocus();
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(newPhoneNumberText, InputMethodManager.SHOW_IMPLICIT);
                newPhoneNumberText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        Log.d(TAG, "Before");
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        addButton.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if(newPhoneNumberText.getText().length() < 1 ) {
                            addButton.setVisibility(View.GONE);
                            newPhoneNumberText.setVisibility(View.GONE);
                            View view = MainActivity.this.getCurrentFocus();
                            if (view != null) {
                                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
                        PhoneNumber phoneNumber = getPhoneNumberFromInput();
                        boolean insertData = dbHelper.addPhoneNumber(phoneNumber);
                        if(insertData) {
                            toastOut(phoneNumber.getPhoneNumber() + " tillagt");
                            newPhoneNumberText.setText("");
                            View view = MainActivity.this.getCurrentFocus();
                            if (view != null) {
                                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                                manageListView();
                            }
                        } else {
                            toastOut("Det blev dåligt. Prova igen");
                        }
                        addButton.setVisibility(View.GONE);
                        newPhoneNumberText.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

    private PhoneNumber getPhoneNumberFromInput() {
        String text = newPhoneNumberText.getText().toString();
        return new PhoneNumber(text, new OperatorHolder(this));
    }

    private void manageTimer() {
        final TextView startTimeText = (TextView) findViewById(R.id.startTimeText);
        final TextView stopTimeText = (TextView) findViewById(R.id.stopTimeText);
        timerView = (CircleAlarmTimerView) findViewById(R.id.circletimerview);
        timerView.setOnTimeChangedListener(new CircleAlarmTimerView.OnTimeChangedListener() {
            @Override
            public void start(String starting) {
                MainActivity.this.startTime = starting;
                startTimeText.setText(starting);
            }

            @Override
            public void end(String ending) {
                MainActivity.this.stopTime = ending;
                stopTimeText.setText(ending);
            }
        });
    }

    private void manageStartForwardingButton() {
        final Button startForwardingButton = (Button) findViewById(R.id.startForwardingButton);
        startForwardingButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int hour = Integer.parseInt(stopTime.substring(0,2));
                int minute = Integer.parseInt(stopTime.substring(3,5));
                Log.d(TAG, "" + hour + ":" + minute);
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);

                Intent startTimerIntent = new Intent(MainActivity.this, CallHandler.class);
                startTimerIntent.putExtra(PHONE_NUMBER_KEY, MainActivity.this.currentPhoneNumber);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 100, startTimerIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 15000, pendingIntent);

                Intent startNextScreenIntent = new Intent(MainActivity.this, CurrentlyForwardingActivity.class);
                //startNextScreenIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                //startNextScreenIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startNextScreenIntent.putExtra(STOP_TIME_KEY, stopTime);
                startActivity(startNextScreenIntent);
                callManager = new CallManager(new ServiceProvider(MainActivity.this));
                callManager.startForwarding();
                hasCalled = true;
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void toastOut(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
