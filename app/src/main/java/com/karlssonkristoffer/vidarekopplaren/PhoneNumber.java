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

    public PhoneNumber(String phoneNumber, OperatorHolder operatorHolder)
    {
        this.phoneNumber = phoneNumber;
        this.operatorHolder = operatorHolder;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getPhoneNumberWithForwardPrefix() {
        return (getForwardingPrefixBasedOnOperator(operatorHolder.getOperatorName()) +
                phoneNumber + "#");
    }

    public String getOperator() {
        return operatorHolder.getOperatorName();
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
}
