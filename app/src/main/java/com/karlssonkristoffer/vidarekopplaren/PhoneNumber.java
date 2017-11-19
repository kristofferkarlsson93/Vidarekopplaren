package com.karlssonkristoffer.vidarekopplaren;

import android.net.Uri;

/**
 * Created by Kristoffer on 2017-09-19.
 */

public class PhoneNumber {

    public static final String TELIA_NUMBER = "**21*";
    public static final String TELIA_S_NUMBER = "**21*";
    public static final String TELE2_NUMBER = "**21*";
    public static final String VIMLA_NUMBER = "**21*";
    public static final String TELENOR_NUMBER = "**21*";

    public static final String STANDARD_CANCEL_NUMBER = "#21#";
    public static final String TELIA_CANCEL_NUMBER = "##002#";
    public static final String TELIA_S_CANCEL_NUMBER = "##002#";
    public static final String TELE2_CANCEL_NUMBER = "#21#";
    public static final String VIMLA_CANCEL_NUMBER = "##21#";
    public static final String TELENOR_CANCEL_NUMBER = "##21#";

    public static final String OPERATOR_TELIA = "Telia";
    public static final String OPERATOR_TELIA_S = "Telia S";
    public static final String OPERATOR_TELE2 = "Tele2";
    public static final String OPERATOR_VIMLA = "Vimla";
    public static final String OPERATOR_TELENOR = "Telenor";

    private String phoneNumber;

    public PhoneNumber(String phoneNumber, OperatorHolder operatorHolder)
    {
        this.phoneNumber = phoneNumber;
    }

    public PhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Uri getPhoneNumberWithForwardPrefix(String operator) {
        return Uri.parse("tel:" + Uri.encode(getForwardingPrefixByOperator(operator) + phoneNumber + "#"));
    }

    public static String getForwardingPrefixByOperator(String operator) {
        switch (operator) {
            case(OPERATOR_TELIA):
                return TELIA_NUMBER;
            case (OPERATOR_TELIA_S):
                return TELIA_S_NUMBER;
            case (OPERATOR_TELE2):
                return TELE2_NUMBER;
            case (OPERATOR_VIMLA):
                return VIMLA_NUMBER;
            case (OPERATOR_TELENOR):
                return TELENOR_NUMBER;
            default:
                return TELIA_NUMBER;
        }
    }

    public static Uri getCancelNumberBasedOnOperator(String operator) {
        switch (operator) {
            case(OPERATOR_TELIA):
                return Uri.fromParts("tel", TELIA_CANCEL_NUMBER, "#");
            case (OPERATOR_TELIA_S):
                return Uri.fromParts("tel", TELIA_S_CANCEL_NUMBER, "#");
            case (OPERATOR_TELE2):
                return Uri.fromParts("tel", TELE2_CANCEL_NUMBER, "#");
            case (OPERATOR_VIMLA):
                return Uri.fromParts("tel", VIMLA_CANCEL_NUMBER, "#");
            case (OPERATOR_TELENOR):
                return Uri.fromParts("tel", TELENOR_CANCEL_NUMBER, "#");
            default:
                return Uri.fromParts("tel", STANDARD_CANCEL_NUMBER, "#");
        }
    }

    public static String formatPhoneNumber(String phoneNumber) {
        phoneNumber = phoneNumber.replace("-", "");
        phoneNumber = phoneNumber.replace(" ", "");
        return phoneNumber;
    }

    public static boolean validatePhoneNumber(String phoneNumber) {
        if(phoneNumber.length() <9 || phoneNumber.length() > 10) {
            return false;
        }
        return true;
    }
}
