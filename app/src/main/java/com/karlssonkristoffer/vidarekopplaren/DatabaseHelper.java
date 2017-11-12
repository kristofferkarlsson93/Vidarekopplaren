package com.karlssonkristoffer.vidarekopplaren;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "databasehelper";

    private static final int VERSION = 12;
    public static final String PHONE_NUMBER_TABLE = "phone_numbers";
    public static final String HAS_CALLED_TABLE = "has_called";
    public static final String FORWARD_END_TABLE = "forward_end_table";
    public static final String SHOULD_KILL_FORWARDING_TABLE = "should_kill_forwarding_table";
    public static final String COL_0 = "ID";
    public static final String COL_1_PHONE_NUMBER_TABLE = "phoneNumber";
    public static final String COL_2_PHONE_NUMBER_TABLE = "timestamp";

    public static final String COL_1_HAS_CALLED_TABLE = "currentlyCalling";
    public static final String COL_1_FORWARD_END_TABLE = "callEnds";
    public static final String COL_1_SHOULD_KILL_FORWARDING_TABLE = "ShouldKillForwarding";

    public DatabaseHelper(Context context) {
        super(context, PHONE_NUMBER_TABLE, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createPhoneNumberTable(db);
        createHasCalledTable(db);
        createForwardEndTable(db);
        createShouldKillForwardingTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String dropPhoneNumberTableIfExists = "DROP TABLE IF EXISTS "
                + PHONE_NUMBER_TABLE;
        String dropHasCalledTableIfExists = "DROP TABLE IF EXISTS "
                + HAS_CALLED_TABLE;
        String dropForwardEndTableIfExists = "DROP TABLE IF EXISTS "
                + FORWARD_END_TABLE;
        String dropShouldKillForwardingTable = "DROP TABLE IF EXISTS "
                + SHOULD_KILL_FORWARDING_TABLE;

        db.execSQL(dropPhoneNumberTableIfExists);
        db.execSQL(dropHasCalledTableIfExists);
        db.execSQL(dropForwardEndTableIfExists);
        db.execSQL(dropShouldKillForwardingTable);
        onCreate(db);
    }


    public Cursor getAllPhoneNumbers() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT rowid _id, * FROM "
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

    public void setCurrentlyForwardingFlag(boolean status) {
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

    public String getLatestUsedPhoneNumber() {
        SQLiteDatabase db = this.getWritableDatabase();
        String result = " ";
        String getLatest = "SELECT * FROM "
                + PHONE_NUMBER_TABLE
                + " ORDER BY "
                + COL_2_PHONE_NUMBER_TABLE
                + " DESC LIMIT 1";

        Cursor data = db.rawQuery(getLatest, null);
        if (data.getCount() > 0) {
            data.moveToFirst();
            result = data.getString(1);
        }
        return result;
    }

    public void setLatestUsedPhoneNumber(long id) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
        dateFormat.format(new Date());
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d("testKarlsson_ID", String.valueOf(id));
        String update = "UPDATE "
                + PHONE_NUMBER_TABLE
                + " SET "
                + COL_2_PHONE_NUMBER_TABLE
                + " = CURRENT_TIMESTAMP"
                + " WHERE ID = '"
                + id
                + "'";
        db.execSQL(update);
    }

    public void setShouldKillForwarding(boolean shouldKillForwarding) {
        int statusInt = shouldKillForwarding ? 1 : 0;
        SQLiteDatabase db = this.getWritableDatabase();
        String update = "UPDATE "
                + SHOULD_KILL_FORWARDING_TABLE
                + " SET "
                + COL_1_SHOULD_KILL_FORWARDING_TABLE
                + " = '"
                + statusInt
                + "'";

        db.execSQL(update);
    }

    public boolean getShouldKillForwarding() {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean result = false;
        String getFlag = "SELECT * FROM "
                + SHOULD_KILL_FORWARDING_TABLE;
        Cursor data = db.rawQuery(getFlag, null);
        if (data.getCount() > 0) {
            data.moveToFirst();
            result = data.getInt(1) > 0;
        }
        return result;
    }

    public void deletePhoneNumber(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String deleteItem = "DELETE FROM "
            + PHONE_NUMBER_TABLE
            + " WHERE id = '"
            + id
            + "'";
        db.execSQL(deleteItem);
    }

    private void createShouldKillForwardingTable(SQLiteDatabase db) {
        String createShouldKillForwardingTable = "CREATE TABLE "
            + SHOULD_KILL_FORWARDING_TABLE
            + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_1_SHOULD_KILL_FORWARDING_TABLE
            + " INTEGER)";
        db.execSQL(createShouldKillForwardingTable);

        String setStartValue = "INSERT INTO "
            + SHOULD_KILL_FORWARDING_TABLE
            + " ("
            + COL_1_SHOULD_KILL_FORWARDING_TABLE
            + ") VALUES (0)";
        db.execSQL(setStartValue);
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
                + ") VALUES (0)";
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
