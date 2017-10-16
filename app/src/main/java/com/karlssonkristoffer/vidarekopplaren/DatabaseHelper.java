package com.karlssonkristoffer.vidarekopplaren;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by karls on 16/10/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 2;
    private static final String PHONE_NUMBER_TABLE = "phone_numbers";
    private static final String HAS_CALLED_TABLE = "has_called";
    private static final String COL_0 = "ID";
    private static final String COL_1_PHONE_NUMBER_TABLE = "phoneNumber";
    private static final String COL_2_PHONE_NUMBER_TABLE = "timestamp";
    
    private static final String COL_1_HAS_CALLED_TABLE = "currentlyCalling";

    public DatabaseHelper(Context context) {
        super(context, PHONE_NUMBER_TABLE, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createPhoneNumberTable(db);
        createHasCalledTable(db);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String dropTableIfExists = "DROP TABLE IF EXISTS " + PHONE_NUMBER_TABLE;
        db.execSQL(dropTableIfExists);
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
       String updateHasCalledTable = "INSERT OR REPLACE INTO "
           + HAS_CALLED_TABLE
           + " ("
           + COL_1_HAS_CALLED_TABLE
           + ") VALUES ("
           + statusInt +
           ")";
        db.execSQL(updateHasCalledTable);
    }

    public boolean getCurrentlyCallingFlag() {
        SQLiteDatabase db = this.getWritableDatabase();
        String getFlag = "SELECT 1 FROM "
            + HAS_CALLED_TABLE;
        Cursor data = db.rawQuery(getFlag, null);

        boolean result = data.getInt(0) == 1;
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
    }
}
