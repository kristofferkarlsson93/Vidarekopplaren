package com.karlssonkristoffer.vidarekopplaren.widget;

import com.karlssonkristoffer.vidarekopplaren.Utils;

import java.util.Calendar;

/**
 * Created by karls on 13/11/2017.
 */

public class TimeInfo {

    private int hourOfDay;
    private int minuteOfHour;
    private String timeString;

    public TimeInfo(int hourOfDay, int minuteOfHour) {
        this.hourOfDay = hourOfDay;
        this.minuteOfHour = minuteOfHour;
        this.timeString =  String.format("%02d", hourOfDay) + ":" + String.format("%02d", minuteOfHour);
    }

    public TimeInfo(String timeString) {
        this.hourOfDay = Integer.parseInt(timeString.substring(0, 2));
        this.minuteOfHour = Integer.parseInt(timeString.substring(3, 5));
        this.timeString = timeString;
    }

    public void setHourOfDay(int hourOfDay) {
        this.hourOfDay = hourOfDay;
    }

    public void setMinuteOfHour(int minuteOfHour) {
        this.minuteOfHour = minuteOfHour;
    }

    public int getHourOfDay() {
        return hourOfDay;
    }

    public int getMinuteOfHour() {
        return minuteOfHour;
    }

    public long getTimeInMillis() {
        Calendar calendar = Utils.getNewCalendar(hourOfDay, minuteOfHour);
        return calendar.getTimeInMillis();
    }

}
