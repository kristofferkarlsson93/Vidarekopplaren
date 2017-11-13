package com.karlssonkristoffer.vidarekopplaren.widget;

import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.karlssonkristoffer.vidarekopplaren.DatabaseHelper;
import com.karlssonkristoffer.vidarekopplaren.Forwarder;
import com.karlssonkristoffer.vidarekopplaren.PhoneNumber;
import com.karlssonkristoffer.vidarekopplaren.R;
import com.karlssonkristoffer.vidarekopplaren.Utils;
import com.karlssonkristoffer.vidarekopplaren.components.PhoneNumberList;
import com.karlssonkristoffer.vidarekopplaren.components.TimePicker;

/**
 * Implementation of App Widget functionality.
 */
public class ForwardControlWidget extends AppWidgetProvider {

    public static String WIDGET_BUTTON = "com.karlssonkristoffer.vidarekopplaren.WIDGET_BUTTON";
    public static String TIME_TEXT = "com.karlssonkristoffer.vidarekopplaren.TIME_TEXT";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.forward_control_widget);
        setPhoneNumber(remoteViews, context);
        setUpTimePicker(remoteViews, context);
        setUpOnClickListner(remoteViews, context);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.forward_control_widget);
        if (WIDGET_BUTTON.equals(intent.getAction())) {
            if(dbHelper.getCurrentlyCallingFlag()) {
                stopForwarding(context, dbHelper, remoteViews);
            } else {
                startForwarding(context, dbHelper, remoteViews);
            }
        }
    }

    private void stopForwarding(Context context, DatabaseHelper dbHelper, RemoteViews remoteViews) {
        Utils.toastOut(context, "Avslutar vidarekoppling");
        dbHelper.setCurrentlyForwardingFlag(false);
        remoteViews.setInt(R.id.widgetStartButton, "setBackgroundColor", Color.BLACK);
        Forwarder forwarder = new Forwarder(context);
        forwarder.stop();
    }

    public void startForwarding(Context context, DatabaseHelper dbHelper, RemoteViews remoteViews) {
        dbHelper.setCurrentlyForwardingFlag(true);
        String stopTime = dbHelper.getLatestStopForwardingTime();
        TimeInfo timeInfo = new TimeInfo(stopTime);
        if(TimePicker.hasSetCorrectTime(timeInfo.getTimeInMillis())) {
            remoteViews.setInt(R.id.widgetStartButton, "setBackgroundColor", Color.RED);
            Utils.toastOut(context, "Startar vidarekoppling");
            Forwarder forwarder = new Forwarder(context);
            forwarder.startWithTimerToStop(timeInfo.getTimeInMillis(), new PhoneNumber(dbHelper.getLatestUsedPhoneNumber()));
        } else {
            Utils.toastOut(context, "Tiden är ogiltig. Öppna appen för att starta vidarekoppling");
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them

        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    private static void setPhoneNumber(RemoteViews remoteViews, Context context) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        remoteViews.setTextViewText(R.id.widgetPhoneNumberText, dbHelper.getLatestUsedPhoneNumber());
    }

    private static void setUpTimePicker(RemoteViews remoteViews, Context context) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        remoteViews.setTextViewText(R.id.widgetTime, databaseHelper.getLatestStopForwardingTime());
        Intent intent = new Intent(TIME_TEXT);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.widgetTime, pendingIntent);
    }

    private static void setUpOnClickListner(RemoteViews remoteViews, Context context) {
        Intent intent = new Intent(WIDGET_BUTTON);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.widgetStartButton, pendingIntent);
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }
}

