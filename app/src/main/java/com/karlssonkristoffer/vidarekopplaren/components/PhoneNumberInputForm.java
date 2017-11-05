package com.karlssonkristoffer.vidarekopplaren.components;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.karlssonkristoffer.vidarekopplaren.DatabaseHelper;
import com.karlssonkristoffer.vidarekopplaren.MainActivity;
import com.karlssonkristoffer.vidarekopplaren.PhoneNumber;
import com.karlssonkristoffer.vidarekopplaren.Utils;

/**
 * Created by karls on 05/11/2017.
 */

public class PhoneNumberInputForm {

    private MainActivity context;
    private DatabaseHelper dbHelper;
    private Button addButton;
    private FloatingActionButton addPhoneNumberButton;
    private PhoneNumberList phoneNumberList;
    private EditText phoneNumberTextField;

    public PhoneNumberInputForm(
            Context context,
            DatabaseHelper dbHelper,
            EditText phoneNumberTextField,
            Button addButton,
            FloatingActionButton addPhoneNumberButton,
            PhoneNumberList phoneNumberList
    ) {

        this.context = (MainActivity) context;
        this.dbHelper = dbHelper;
        this.phoneNumberTextField = phoneNumberTextField;
        this.addButton = addButton;
        this.addPhoneNumberButton = addPhoneNumberButton;
        this.phoneNumberList = phoneNumberList;
    }

    public void create() {
        addPhoneNumberButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(final View v) {
                phoneNumberTextField.setVisibility(View.VISIBLE);
                phoneNumberTextField.requestFocus();
                showKeyboard();
                phoneNumberTextField.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        addButton.setVisibility(View.VISIBLE);
                        phoneNumberTextField.setTextColor(Color.BLACK);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if(phoneNumberTextField.getText().length() < 1 ) {
                            addButton.setVisibility(View.GONE);
                            phoneNumberTextField.setVisibility(View.GONE);
                            View view = context.getCurrentFocus();
                            if (view != null) {
                                hideKeyboard(view);
                            }
                        } else {
                            addButton.setVisibility(View.VISIBLE);
                            phoneNumberTextField.setVisibility(View.VISIBLE);
                        }
                    }
                });

                addButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        String input = getPhoneNumberFromInput();
                        input = PhoneNumber.formatPhoneNumber(input);
                        if (!PhoneNumber.validatePhoneNumber(input)) {
                            Utils.toastOut(context, "Felformaterat telefonnummer");
                            phoneNumberTextField.setTextColor(Color.RED);
                            return;
                        }
                        PhoneNumber phoneNumber = new PhoneNumber(input);
                        boolean insertData = dbHelper.addPhoneNumber(phoneNumber);
                        if(insertData) {
                            Utils.toastOut(context, phoneNumber.getPhoneNumber() + " tillagt");
                            phoneNumberTextField.setText("");
                            phoneNumberList.update();
                            View view = context.getCurrentFocus();
                            if (view != null) {
                                hideKeyboard(view);
                            }
                        } else {
                            Utils.toastOut(context, "Oväntat fel. Prova igen");
                        }
                        addButton.setVisibility(View.GONE);
                        phoneNumberTextField.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

    private String getPhoneNumberFromInput() {
        return phoneNumberTextField.getText().toString();
    }

    private void showKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(phoneNumberTextField, InputMethodManager.SHOW_IMPLICIT);
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
