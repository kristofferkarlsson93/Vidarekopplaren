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

public class CallManager {
    private static final String TAG = "callmanager";

    private Context context;
    private PhoneNumber phoneNumber;
    private ServiceProvider serviceProvider;

    public CallManager(ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
        this.phoneNumber = serviceProvider.getPhonenumberObject();
        this.context = serviceProvider.getContext();
    }

    public void startForwarding() {
        Log.d(TAG, phoneNumber.getPhoneNumberWithForwardPrefix());
        Intent callForwardIntent = new Intent(Intent.ACTION_CALL);
        Uri uri = Uri.parse("tel:" + phoneNumber.getPhoneNumberWithForwardPrefix());
        callForwardIntent.setData(uri);

        Log.d(TAG + "actualnumber", callForwardIntent.getData().toString());

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "In permission");
            context.startActivity(callForwardIntent);
        }

    }

    public void stopForwarding() {
        Log.d(TAG, PhoneNumber.getCancelNumberBasedOnOperator(phoneNumber.getOperator()));
        /*Intent cancelForwardIntent = new Intent((Intent.ACTION_CALL));
        Uri uri = Uri.fromParts("tel",
                PhoneNumber.getCancelNumberBasedOnOperator(phoneNumber.getOperator()), "#");
        cancelForwardIntent.setData(uri);
        context.startActivity(cancelForwardIntent);*/
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