package com.karlssonkristoffer.vidarekopplaren;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by karls on 04/11/2017.
 */

public class PhoneNumberAdapter extends ArrayAdapter<String> {

    public PhoneNumberAdapter(@NonNull Context context, ArrayList<String> phoneNumbers) {
        super(context, R.layout.phonenumberrow, phoneNumbers);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View view = layoutInflater.inflate(R.layout.phonenumberrow, parent, false);
        String phoneNumber = getItem(position);
        TextView phoneNumberText = (TextView) view.findViewById(R.id.phoneNumber);
        phoneNumberText.setText(phoneNumber);
        return view;
    }
}
