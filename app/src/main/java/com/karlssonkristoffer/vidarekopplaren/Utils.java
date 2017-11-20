package com.karlssonkristoffer.vidarekopplaren;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.widget.Toast;

import com.karlssonkristoffer.vidarekopplaren.appActivities.MainActivity;
import com.karlssonkristoffer.vidarekopplaren.receivers.ResetForwardingReceiver;
import com.karlssonkristoffer.vidarekopplaren.widget.ForwardControlWidget;

import java.util.Calendar;
import java.util.concurrent.Callable;

/**
 * Created by karls on 05/11/2017.
 */

public class Utils {

    public static void toastOut(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void showDialog(Context context, String title, String message) {
        final AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.InformationDialog));
        } else {
            builder = new AlertDialog.Builder(context);
        }
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                }
            })
            .show();
    }

    public static void showDialog(final Context context, String title, String message, final Callable<?> callback) {
        final AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.InformationDialog));
        } else {
            builder = new AlertDialog.Builder(context);
        }
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        callback.call();
                    } catch (Exception e) {
                        Utils.toastOut(context, "Kunde ej utföra åtgärden.");
                    }
                }
            })
            .show();
    }

    public static void updateWidget(Context context) {
        Intent intent = new Intent(context, ForwardControlWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int ids[] = AppWidgetManager.getInstance(context.getApplicationContext()).getAppWidgetIds(new ComponentName(context.getApplicationContext(), ForwardControlWidget.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
        context.sendBroadcast(intent);
    }

    public static Calendar getNewCalendar(int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        return calendar;
    }


    public static void cancelTimer(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent stopCancelTimer;
        stopCancelTimer = PendingIntent.getBroadcast(
            context,
            MainActivity.CANCEL_INTENT_CODE,
            new Intent(context, ResetForwardingReceiver.class),
            PendingIntent.FLAG_CANCEL_CURRENT);

        alarmManager.cancel(stopCancelTimer);
    }

}
