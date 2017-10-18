package com.karlssonkristoffer.vidarekopplaren;

/**
 * Created by Kristoffer on 2017-09-19.
 */

public class PhoneNumber {

    public static final String TELIA_NUMBER = "**21*";
    public static final String TELE2_NUMBER = "*21*";
    public static final String TELIA_CANCEL_NUMBER = "##002#";  //kanske ##21# istället??
    public static final String TELE2_CANCEL_NUMBER = "#21";

    public static final String OPERATOR_TELIA = "Telia";
    public static final String OPERATOR_TELE2 = "Tele2";


    private String phoneNumber;
    private OperatorHolder operatorHolder;
    private boolean hasOperatorInformation;

    public PhoneNumber(String phoneNumber, OperatorHolder operatorHolder)
    {
        this.phoneNumber = phoneNumber;
        this.operatorHolder = operatorHolder;
        hasOperatorInformation = false;
    }

    public PhoneNumber(String phoneNumber) {
        hasOperatorInformation = true;
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getPhoneNumberWithForwardPrefix() {
        return (getForwardingPrefixBasedOnOperator(operatorHolder.getOperatorName()) +
                phoneNumber + "#");
    }

    public boolean hasOperatorInformation() {
        return hasOperatorInformation;
    }

    public String getOperator() {
        return operatorHolder.getOperatorName();
    }

    public void setOperatorHolder(OperatorHolder operatorHolder) {
        operatorHolder = operatorHolder;
    }

    public static String getForwardingPrefixBasedOnOperator(String operator) {
        switch (operator) {
            case(OPERATOR_TELIA):
                return TELIA_NUMBER;
            case (OPERATOR_TELE2):
                return TELE2_NUMBER;
            default:
                return TELIA_NUMBER;
        }
    }

    public static String getCancelNumberBasedOnOperator(String operator) {
        switch (operator) {
            case(OPERATOR_TELIA):
                return TELIA_CANCEL_NUMBER;
            case (OPERATOR_TELE2):
                return TELE2_CANCEL_NUMBER;
            default:
                return TELIA_CANCEL_NUMBER;
        }
    }

    public static String formatPhoneNumber(String phoneNumber) {
        phoneNumber = phoneNumber.replace("-", "");
        phoneNumber = phoneNumber.replace(" ", "");
        return phoneNumber;
    }

    public static final boolean validatePhoneNumber(String phoneNumber) {
        if(phoneNumber.length()<9 || phoneNumber.length() > 10) {
            return false;
        }
        return true;
    }
}
