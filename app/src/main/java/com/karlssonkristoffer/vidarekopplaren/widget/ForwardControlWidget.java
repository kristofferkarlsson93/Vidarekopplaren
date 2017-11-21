package com.karlssonkristoffer.vidarekopplaren.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.karlssonkristoffer.vidarekopplaren.DatabaseHelper;
import com.karlssonkristoffer.vidarekopplaren.Forwarder;
import com.karlssonkristoffer.vidarekopplaren.appActivities.MainActivity;
import com.karlssonkristoffer.vidarekopplaren.PhoneNumber;
import com.karlssonkristoffer.vidarekopplaren.R;
import com.karlssonkristoffer.vidarekopplaren.Utils;
import com.karlssonkristoffer.vidarekopplaren.components.TimePicker;

/**
 * Implementation of App Widget functionality.
 */
public class ForwardControlWidget extends AppWidgetProvider {

    public static String WIDGET_BUTTON = "com.karlssonkristoffer.vidarekopplaren.WIDGET_BUTTON";
    public static String START_APP = "com.karlssonkristoffer.vidarekopplaren.START_APP";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.forward_control_widget);
        String stopTime = dbHelper.getLatestStopForwardingTime();
        TimeInfo timeInfo = new TimeInfo(stopTime);
        if(dbHelper.getCurrentlyCallingFlag() && TimePicker.hasSetCorrectTime(timeInfo.getTimeInMillis())) {
            remoteViews.setInt(R.id.widgetStartButton, "setBackgroundResource", R.drawable.widgetstopbutton);
            remoteViews.setInt(R.id.widgetStartButton, "setText", R.string.stop);
        } else {
            remoteViews.setInt(R.id.widgetStartButton, "setBackgroundResource", R.drawable.widgetstartbutton);
            remoteViews.setInt(R.id.widgetStartButton, "setText", R.string.forward);

        }

        setPhoneNumber(remoteViews, context, dbHelper);
        setUpAppStartOnClick(remoteViews, context, dbHelper);
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
        } else if (START_APP.equals(intent.getAction())) {
            Intent startAppIntent = new Intent(context, MainActivity.class);
            context.startActivity(startAppIntent);


        }
    }

    private void stopForwarding(Context context, DatabaseHelper dbHelper, RemoteViews remoteViews) {
        Utils.toastOut(context, "Avslutar vidarekoppling");
        dbHelper.setCurrentlyForwardingFlag(false);
        Forwarder forwarder = new Forwarder(context);
        forwarder.stop();
        Utils.cancelTimer(context);
        Utils.updateWidget(context);
    }

    public void startForwarding(Context context, DatabaseHelper dbHelper, RemoteViews remoteViews) {
        String stopTime = dbHelper.getLatestStopForwardingTime();
        TimeInfo timeInfo = new TimeInfo(stopTime);
        if(TimePicker.hasSetCorrectTime(timeInfo.getTimeInMillis())) {
            dbHelper.setCurrentlyForwardingFlag(true);
            remoteViews.setInt(R.id.widgetStartButton, "setBackgroundResource", R.drawable.stopbutton);
            Forwarder forwarder = new Forwarder(context);
            forwarder.startWithTimerToStop(timeInfo.getTimeInMillis(), new PhoneNumber(dbHelper.getLatestUsedPhoneNumber()));
            Utils.toastOut(context, "Startar vidarekoppling");
        } else {
            Utils.toastOut(context, "Tiden är ogiltig. Öppna appen för att starta vidarekoppling");
        }
        Utils.updateWidget(context);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        Utils.updateWidget(context);
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    private static void setPhoneNumber(RemoteViews remoteViews, Context context, DatabaseHelper dbHelper) {
        remoteViews.setTextViewText(R.id.widgetPhoneNumberText, dbHelper.getLatestUsedPhoneNumber());
    }

    private static void setUpAppStartOnClick(RemoteViews remoteViews, Context context, DatabaseHelper dbHelper) {
        remoteViews.setTextViewText(R.id.widgetTime, dbHelper.getLatestStopForwardingTime());
        Intent intent = new Intent(START_APP);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.widgetPhoneNumberLayout, pendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.widgetTimeLayout, pendingIntent);
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

