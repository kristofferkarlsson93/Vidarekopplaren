package com.karlssonkristoffer.vidarekopplaren.components;

import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.karlssonkristoffer.vidarekopplaren.DatabaseHelper;
import com.karlssonkristoffer.vidarekopplaren.R;

import java.util.Calendar;

/**
 * Created by karls on 04/11/2017.
 */

public class TimePicker {


    private String stopTime;
    private Context context;
    private DatabaseHelper dbHelper;
    private TextView choseTimeText;
    private View circleView;
    private long chosenTimeInMilis = 0;

    public TimePicker(Context context, DatabaseHelper dbHelper, TextView choseTimeText, View circleView) {
        this.context = context;

        this.dbHelper = dbHelper;
        this.choseTimeText = choseTimeText;
        this.circleView = circleView;
    }

    public void create() {
        stopTime = dbHelper.getLatestStopForwardingTime();
        int hour = Integer.parseInt(stopTime.substring(0, 2));
        int minute = Integer.parseInt(stopTime.substring(3, 5));
        choseTimeText.setText(stopTime);
        Calendar calendar =  getNewCalendar(hour, minute);
        if (hasSetCorrectTime(calendar.getTimeInMillis())) {
            setSucessOnTime();
        } else {
            clearTimeColors();
        }
        circleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearTimeColors();
                final Calendar calendar = Calendar.getInstance();
                int hour = Integer.parseInt(stopTime.substring(0, 2));
                int minute = Integer.parseInt(stopTime.substring(3, 5));
                final TimePickerDialog timePickerDialog = new TimePickerDialog(context, R.style.TimePickerTheme, new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(android.widget.TimePicker view, int hourOfDay, int minuteOfHour) {
                        stopTime = String.format("%02d", hourOfDay) + ":" + String.format("%02d", minuteOfHour);
                        choseTimeText.setText(stopTime);
                        calendar.setTimeInMillis(System.currentTimeMillis());
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minuteOfHour);
                        chosenTimeInMilis = calendar.getTimeInMillis();
                        if(hasSetCorrectTime(chosenTimeInMilis)) {
                            setSucessOnTime();
                        } else {
                            toastOut("Välj en tid i framtiden");
                            setErrorOnTime();
                        }
                    }
                }, hour, minute, true);
                timePickerDialog.show();
            }
        });
    }

    public String getStopTime() {
        return stopTime;
    }

    public long getChosenTimeInMilis() {
        return chosenTimeInMilis;
    }

    private Calendar getNewCalendar(int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        return calendar;
    }


    public void setErrorOnTime() {
        GradientDrawable circle = (GradientDrawable)circleView.getBackground();
        circle.setStroke(2, Color.RED);
        choseTimeText.setTextColor(Color.RED);
    }

    private void clearTimeColors() {
        GradientDrawable circle = (GradientDrawable)circleView.getBackground();
        circle.setStroke(2, Color.WHITE);
        choseTimeText.setTextColor(Color.WHITE);
    }

    private void setSucessOnTime() {
        GradientDrawable circle = (GradientDrawable)circleView.getBackground();
        circle.setStroke(2, 0xFF70ffaa);
        choseTimeText.setTextColor(0xFF70ffaa);
    }

    public boolean hasSetCorrectTime(long chosenTime) {
        return chosenTime > System.currentTimeMillis() + 60000;
    }

    private void toastOut(String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

}
