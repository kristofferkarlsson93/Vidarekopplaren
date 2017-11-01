package com.karlssonkristoffer.vidarekopplaren;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by karls on 16/10/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "databasehelper";

    private static final int VERSION = 9;
    private static final String PHONE_NUMBER_TABLE = "phone_numbers";
    private static final String HAS_CALLED_TABLE = "has_called";
    private static final String FORWARD_END_TABLE = "forward_end_table";
    private static final String COL_0 = "ID";
    private static final String COL_1_PHONE_NUMBER_TABLE = "phoneNumber";
    private static final String COL_2_PHONE_NUMBER_TABLE = "timestamp";
    
    private static final String COL_1_HAS_CALLED_TABLE = "currentlyCalling";
    private static final String COL_1_FORWARD_END_TABLE = "callEnds";

    public DatabaseHelper(Context context) {
        super(context, PHONE_NUMBER_TABLE, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createPhoneNumberTable(db);
        createHasCalledTable(db);
        createForwardEndTable(db);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String dropPhoneNumberTableIfExists = "DROP TABLE IF EXISTS "
                + PHONE_NUMBER_TABLE;
        String dropHasCalledTableIfExists = "DROP TABLE IF EXISTS "
                + HAS_CALLED_TABLE;

        String dropForwardEndTableIfExists = "DROP TABLE IF EXISTS "
                + FORWARD_END_TABLE;


        db.execSQL(dropPhoneNumberTableIfExists);
        db.execSQL(dropHasCalledTableIfExists);
        db.execSQL(dropForwardEndTableIfExists);
        onCreate(db);
    }

    public Cursor getAllPhoneNumbers() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM "
                + PHONE_NUMBER_TABLE
                + " ORDER BY DATETIME ("
                + COL_2_PHONE_NUMBER_TABLE
                + ") DESC";
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public boolean addPhoneNumber(PhoneNumber phoneNumber) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1_PHONE_NUMBER_TABLE, phoneNumber.getPhoneNumber());
        long result = db.insert(PHONE_NUMBER_TABLE, null, contentValues);

        if(result == -1) {
            return false;
        }
        return true;
    }

    public void setCurrentlyCallingFlag(boolean status) {
       SQLiteDatabase db = this.getWritableDatabase();
       int statusInt = status ? 1 : 0;
       String updateHasCalledTable = "UPDATE "
           + HAS_CALLED_TABLE
           + " SET "
           + COL_1_HAS_CALLED_TABLE
           + " = "
           + statusInt;
        db.execSQL(updateHasCalledTable);
    }

    public void setStopForwardingTime(String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        String update = "UPDATE "
                + FORWARD_END_TABLE
                + " SET "
                + COL_1_FORWARD_END_TABLE
                + " = '"
                + time
                +"'";
        db.execSQL(update);
    }

    public boolean getCurrentlyCallingFlag() {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean result = false;
        String getFlag = "SELECT * FROM "
            + HAS_CALLED_TABLE;
        Cursor data = db.rawQuery(getFlag, null);
        if (data.getCount() > 0) {
            data.moveToFirst();
            result = data.getInt(1) > 0;
        }
        return result;
    }

    public String getLatestStopForwardingTime() {
        SQLiteDatabase db = this.getWritableDatabase();
        String result = " ";
        String getTimeString = "SELECT * FROM "
                + FORWARD_END_TABLE;
        Cursor data = db.rawQuery(getTimeString, null);
        if (data.getCount() > 0) {
            data.moveToFirst();
            result = data.getString(1);
        }
        return result;
    }


    private void createPhoneNumberTable(SQLiteDatabase db) {
        String createPhoneNumberTable = "CREATE TABLE "
            + PHONE_NUMBER_TABLE
            + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_1_PHONE_NUMBER_TABLE + " TEXT, "
            + COL_2_PHONE_NUMBER_TABLE + " DATETIME DEFAULT CURRENT_TIMESTAMP"
            + ")";
        db.execSQL(createPhoneNumberTable);
    }

    private void createHasCalledTable(SQLiteDatabase db) {
        String createHasCalledTable = "CREATE TABLE "
            + HAS_CALLED_TABLE
            + " ("
            + "ID INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_1_HAS_CALLED_TABLE + " INTEGER "
            + ")";
        db.execSQL(createHasCalledTable);

        String setStartValue = "INSERT INTO "
                + HAS_CALLED_TABLE
                + " ("
                + COL_1_HAS_CALLED_TABLE
                +") VALUES (0)";
        db.execSQL(setStartValue);
    }

    private void createForwardEndTable(SQLiteDatabase db) {
        String createForwardEndTable = "CREATE TABLE "
                + FORWARD_END_TABLE
                + " ("
                + "ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_1_FORWARD_END_TABLE+ " TEXT"
                + ")";
        db.execSQL(createForwardEndTable);

        String setStartValue = "INSERT INTO "
                + FORWARD_END_TABLE
                + " ("
                + COL_1_FORWARD_END_TABLE
                +") VALUES ('00:00')";
        db.execSQL(setStartValue);
    }
}
