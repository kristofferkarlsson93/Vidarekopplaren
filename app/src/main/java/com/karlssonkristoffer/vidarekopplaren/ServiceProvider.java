package com.karlssonkristoffer.vidarekopplaren;

import android.content.Context;

/**
 * Created by karls on 01/10/2017.
 */

public class ServiceProvider {

    private Context context;
    private PhoneNumber phoneNumber = null;

    public ServiceProvider(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return this.context;
    }

    public PhoneNumber getPhonenumberObject() {
        if(this.phoneNumber != null) {
            return this.phoneNumber;
        }
        OperatorHolder operatorHolder = new OperatorHolder(context);
        this.phoneNumber = new PhoneNumber("0702871236", operatorHolder);
        return this.phoneNumber;
    }
}
