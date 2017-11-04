package com.karlssonkristoffer.vidarekopplaren.components;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.karlssonkristoffer.vidarekopplaren.DatabaseHelper;
import com.karlssonkristoffer.vidarekopplaren.MainActivity;
import com.karlssonkristoffer.vidarekopplaren.PhoneNumber;
import com.karlssonkristoffer.vidarekopplaren.PhoneNumberAdapter;
import com.karlssonkristoffer.vidarekopplaren.R;

import java.util.ArrayList;

/**
 * Created by karls on 04/11/2017.
 */

public class PhoneNumberList {

    private DatabaseHelper dbHelper;
    private Context context;
    private ListView phoneNumberListView;
    private View prelClickedNumber;
    private PhoneNumber currentPhoneNumber;

    public PhoneNumberList(DatabaseHelper dbHelper, Context context, ListView phoneNumberListView) {
        this.dbHelper = dbHelper;
        this.context = context;
        this.phoneNumberListView = phoneNumberListView;
    }

    public void create() {
        Cursor data = dbHelper.getAllPhoneNumbers();
        ArrayList<String> listData = new ArrayList<>();
        while (data.moveToNext()) {
            listData.add(data.getString(1));
        }
        ListAdapter adapter = new PhoneNumberAdapter(context, listData);
        phoneNumberListView.setAdapter(adapter);
        phoneNumberListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        view.setBackgroundColor(0xCC70ffaa);
                        currentPhoneNumber = new PhoneNumber(String.valueOf(parent.getItemAtPosition(position)));
                        if(prelClickedNumber != null && prelClickedNumber != view) {
                            prelClickedNumber.setBackgroundColor(0x00000000);
                        }
                        prelClickedNumber = view;
                    }
                }
        );
    }

    public void update() {
        create();
    }

    public PhoneNumber getCurrentPhoneNumber() {
        return currentPhoneNumber;
    }

    public boolean hasChosenPhoneNumber() {
        return this.currentPhoneNumber != null;
    }
}
