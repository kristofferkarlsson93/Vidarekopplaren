package com.karlssonkristoffer.vidarekopplaren;

import android.content.Context;

/**
 * Created by karls on 01/10/2017.
 */

public class ServiceProvider {

    private Context context;
    private PhoneNumber phoneNumber = null;
    private OperatorHolder operatorHolder = null;
    private boolean hasPhoneNumber = false;

    public ServiceProvider(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return this.context;
    }

    public PhoneNumber getPhonenumberObject() {
        return this.phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = new PhoneNumber(phoneNumber, getOperatorHolder());
    }

    public void changeCurrentPhoneNumber(String phonenumber) {
        this.phoneNumber = new PhoneNumber(phonenumber, getOperatorHolder());
    }

    private OperatorHolder getOperatorHolder() {
        if (operatorHolder != null) {
            return this.operatorHolder;
        }
        return new OperatorHolder(context);
    }

    public boolean hasPhoneNumber() {
        return hasPhoneNumber;
    }
}
