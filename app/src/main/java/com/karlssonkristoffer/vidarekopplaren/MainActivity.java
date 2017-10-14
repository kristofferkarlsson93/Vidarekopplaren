package com.karlssonkristoffer.vidarekopplaren;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.yinglan.circleviewlibrary.CircleAlarmTimerView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        newPhoneNumberText = (EditText) findViewById(R.id.newPhoneNumberText);
        String[] phoneNumbers = {"0702871236", "0724510227", "0702871236", "0724510227"};
        ListAdapter phoneNumberAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, phoneNumbers);
        ListView phoneNumberListView = (ListView) findViewById(R.id.phoneNumberListView);
        phoneNumberListView.setAdapter(phoneNumberAdapter);
        phoneNumberListView.setOnItemClickListener(
            new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    MainActivity.this.currentPhoneNumber = String.valueOf(parent.getItemAtPosition(position));
                    Toast.makeText(MainActivity.this, MainActivity.this.currentPhoneNumber, Toast.LENGTH_LONG).show();
                }
            }
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[] {Manifest.permission.READ_PHONE_STATE}, PermissionCodes.PERMISSION_REQUEST_OPERATOR);
            requestPermissions(new String[] {Manifest.permission.CALL_PHONE}, PermissionCodes.PERMISSION_CALL);
        }
        final FloatingActionButton addPhoneNumberButton = (FloatingActionButton) findViewById(R.id.addPhonenumberButton);
        addPhoneNumberButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "Click");
                newPhoneNumberText.setVisibility(View.VISIBLE);
            }
        });
        manageTimer();
        manageStartForwardingButton();
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

                callManager = new CallManager(new ServiceProvider(MainActivity.this));
                callManager.startForwarding();
                hasCalled = true;
                /*Intent startTimerIntent = new Intent(MainActivity.this, CallHandler.class);
                startTimerIntent.putExtra(PHONE_NUMBER_KEY, MainActivity.this.currentPhoneNumber);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 100, startTimerIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 5000, pendingIntent);
*/
                Intent startNextScreenIntent = new Intent(MainActivity.this, CurrentlyForwardingActivity.class);
                //startNextScreenIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                //startNextScreenIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startNextScreenIntent.putExtra(STOP_TIME_KEY, stopTime);
                startActivity(startNextScreenIntent);
                //finish();
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
