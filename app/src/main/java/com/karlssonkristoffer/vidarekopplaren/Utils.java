package com.karlssonkristoffer.vidarekopplaren;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by karls on 05/11/2017.
 */

public class Utils {

    public static void toastOut(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

}
