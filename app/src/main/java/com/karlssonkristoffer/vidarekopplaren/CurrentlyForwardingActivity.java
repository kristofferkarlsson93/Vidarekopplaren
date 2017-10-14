package com.karlssonkristoffer.vidarekopplaren;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CurrentlyForwardingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currently_forwarding);

        String stopTime = getIntent().getStringExtra(MainActivity.STOP_TIME_KEY);

        TextView forwardStopTime = (TextView) findViewById(R.id.forwardStopTime);
        forwardStopTime.setText(stopTime);
        final Button stopForwardButton = (Button) findViewById(R.id.stopForwardButton);
        stopForwardButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });

    }

}
