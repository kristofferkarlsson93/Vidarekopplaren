package com.karlssonkristoffer.vidarekopplaren;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.PhoneStateListener;
import android.util.Log;

import android.support.v7.app.AppCompatActivity;

import static com.karlssonkristoffer.vidarekopplaren.PermissionCodes.*;

/**
 * https://stackoverflow.com/questions/15842328/android-intent-action-call-uri
 *
 * https://stackoverflow.com/questions/15880091/pushing-phone-call-screen-in-to-background-whilst-making-a-call
 *
 * Created by Kristoffer on 2017-09-18.
 */

public class Forwarder {

    private OperatorHolder operatorHolder;
    private Context context;

    public Forwarder(Context context) {
        this.context = context;
        this.operatorHolder = new OperatorHolder(context);
        Log.d("testKarlsson", "kapapappa");
    }

    public void start(PhoneNumber phoneNumber) {
        Intent callForwardIntent = new Intent(Intent.ACTION_CALL);
        Uri forwardData = phoneNumber.getPhoneNumberWithForwardPrefix(operatorHolder.getOperatorName());
        callForwardIntent.setData(forwardData);

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            context.startActivity(callForwardIntent);
        }
    }

    public void stop() {
        Intent cancelForwardIntent = new Intent(Intent.ACTION_CALL);
        cancelForwardIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri cancelData = PhoneNumber.getCancelNumberBasedOnOperator(this.operatorHolder.getOperatorName());
        cancelForwardIntent.setData(cancelData);

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            context.startActivity(cancelForwardIntent);
        }
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Activity cont = (Activity) this.context;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cont.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}


 /*ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    PERMISSION_CALL);*/