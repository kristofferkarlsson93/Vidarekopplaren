package com.karlssonkristoffer.vidarekopplaren;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.widget.Toast;

import com.karlssonkristoffer.vidarekopplaren.widget.ForwardControlWidget;

import java.util.Calendar;
import java.util.concurrent.Callable;

/**
 * Created by karls on 05/11/2017.
 */

public class Utils {

    public static void toastOut(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
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

    public static void showDialog(Context context, String title, String message, final Callable<?> callback) {
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
                            Log.d("Utils", "Hoppsan");
                        }
                    }
                })
                .show();
    }

    public static void updateWidget(Context context) {
        //Put in Utils
        Activity thisContext = (Activity) context;
        Intent intent = new Intent(thisContext, ForwardControlWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int ids[] = AppWidgetManager.getInstance(thisContext.getApplication()).getAppWidgetIds(new ComponentName(thisContext.getApplication(), ForwardControlWidget.class));
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

}
