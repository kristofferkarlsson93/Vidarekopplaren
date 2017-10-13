package com.karlssonkristoffer.vidarekopplaren;

import android.Manifest;
import android.os.Build;
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
    private EditText newPhoneNumberText;
    private CircleAlarmTimerView timerView;
    private String startTime;
    private String stopTime;

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
                    String clickedNumber = String.valueOf(parent.getItemAtPosition(position));
                    Toast.makeText(MainActivity.this, clickedNumber, Toast.LENGTH_LONG).show();
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
                Log.d(TAG, MainActivity.this.startTime);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
