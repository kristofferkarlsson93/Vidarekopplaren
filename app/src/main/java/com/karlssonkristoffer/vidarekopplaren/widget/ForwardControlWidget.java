package com.karlssonkristoffer.vidarekopplaren.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.ListView;
import android.widget.RemoteViews;

import com.karlssonkristoffer.vidarekopplaren.DatabaseHelper;
import com.karlssonkristoffer.vidarekopplaren.R;
import com.karlssonkristoffer.vidarekopplaren.components.PhoneNumberList;

/**
 * Implementation of App Widget functionality.
 */
public class ForwardControlWidget extends AppWidgetProvider {

    public static String WIDGET_BUTTON = "com.karlssonkristoffer.vidarekopplaren.WIDGET_BUTTON";
    public static String TIME_TEXT = "com.karlssonkristoffer.vidarekopplaren.TIME_TEXT";
    private DatabaseHelper dbHelper;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        /*DatabaseHelper dbHelper = new DatabaseHelper(context);
        ListView phoneNumberListView = (ListView) context.findViewById(R.id.phoneNumberListView);
        PhoneNumberList phoneNumberList = new PhoneNumberList(dbHelper, this, phoneNumberListView);
        phoneNumberList.create();*/
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.forward_control_widget);
        setUpOnClickListner(remoteViews, context);
        setUpTimePicker(remoteViews, context);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

    private static void setUpTimePicker(RemoteViews remoteViews, Context context) {
        Intent intent = new Intent(TIME_TEXT);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.widgetTime, pendingIntent);
    }

    private static void setUpOnClickListner(RemoteViews remoteViews, Context context) {
        Intent intent = new Intent(WIDGET_BUTTON);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.widgetStartButton, pendingIntent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (WIDGET_BUTTON.equals(intent.getAction())) {
            Log.d("testKarlsson", "klick");
        }
        else if (TIME_TEXT.equals(intent.getAction())) {
            Log.d("testKarlsson", "klickText");
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

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }
}

