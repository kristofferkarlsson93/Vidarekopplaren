package com.karlssonkristoffer.vidarekopplaren;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import static com.karlssonkristoffer.vidarekopplaren.PermissionCodes.*;


public class Forwarder {

    private OperatorHolder operatorHolder;
    private Context context;

    public Forwarder(Context context) {
        this.context = context;
        this.operatorHolder = new OperatorHolder(context);
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

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Activity cont = (Activity) this.context;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cont.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
