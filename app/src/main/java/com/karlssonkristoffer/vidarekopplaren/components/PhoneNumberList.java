package com.karlssonkristoffer.vidarekopplaren.components;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.karlssonkristoffer.vidarekopplaren.DatabaseHelper;
import com.karlssonkristoffer.vidarekopplaren.MainActivity;
import com.karlssonkristoffer.vidarekopplaren.PhoneNumber;
import com.karlssonkristoffer.vidarekopplaren.R;
import com.karlssonkristoffer.vidarekopplaren.Utils;

import java.util.ArrayList;
import java.util.concurrent.Callable;

/**
 * Created by karls on 04/11/2017.
 */

public class PhoneNumberList {

    private final int CHOSEN_COLOR = 0xCC70ffaa;
    private final int NEUTRAL_COLOR = 0x00000000;

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

        Cursor cursor = dbHelper.getAllPhoneNumbers();
        String[] fields = new String[] {DatabaseHelper.COL_1_PHONE_NUMBER_TABLE, DatabaseHelper.COL_0};
        int[] toView = new int[] {R.id.phoneNumber};
        final SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(context, R.layout.phonenumberrow, cursor, fields, toView, 0);
        phoneNumberListView.setAdapter(simpleCursorAdapter);

        phoneNumberListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        view.setBackgroundColor(CHOSEN_COLOR);
                        Cursor cursor = (Cursor) parent.getAdapter().getItem(position);
                        currentPhoneNumber = new PhoneNumber(cursor.getString(2)); // 2 is the index of the phoneNumber
                        if(prelClickedNumber != null && prelClickedNumber != view) {
                            prelClickedNumber.setBackgroundColor(NEUTRAL_COLOR);
                        }
                        prelClickedNumber = view;
                        dbHelper.setLatestUsedPhoneNumber(id);
                    }
                }
        );
        phoneNumberListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, final long id) {
                Utils.showDialog(context, context.getString(R.string.deleteHeader), context.getString(R.string.deleteQuestion), new Callable<Boolean>() {
                    public Boolean call() {
                        delete(id);
                        return Boolean.TRUE;
                    }
                });
                return true;
            }
        });
    }

    public void delete(long id) {
        this.dbHelper.deletePhoneNumber(id);
        this.create();
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
