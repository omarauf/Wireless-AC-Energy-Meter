package com.example.Wireless_AC_Energy_Meter.Helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.Wireless_AC_Energy_Meter.Data.Day;
import com.example.Wireless_AC_Energy_Meter.Data.Month;
import com.example.Wireless_AC_Energy_Meter.Data.Period;
import com.example.Wireless_AC_Energy_Meter.Data.Week;

import java.util.ArrayList;

public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandler.class.getSimpleName();
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "AC";

    //  table name
    private static final String TABLE_DAYS = "Days";
    private static final String TABLE_PERIODS = "Periods";
    private static final String TABLE_WEEKS = "Weeks";
    private static final String TABLE_MONTHS = "Months";

    // Common table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_ENERGY = "Energy";
    private static final String KEY_POWER = "Power";
    private static final String KEY_CURRENT = "Current";
    private static final String KEY_VOLTAGE = "Voltage";
    private static final String KEY_POWER_FACTOR = "Power_Factor";

    // Days table Columns names
    private static final String KEY_DAY = "Day";

    // Period table Columns names
    private static final String KEY_PERIOD = "Period";

    // Week table Columns names
    private static final String KEY_WEEK = "Week";

    // Month table Columns names
    private static final String KEY_MONTH = "Month";

    public SQLiteHandler(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /* Creating Tables */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_DAYS_TABLE = "CREATE TABLE " + TABLE_DAYS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_ENERGY + " TEXT,"
                + KEY_POWER + " TEXT, " + KEY_CURRENT + " TEXT,"
                + KEY_VOLTAGE + " TEXT," + KEY_POWER_FACTOR + " TEXT, " + KEY_DAY  + " TEXT" + ")";
        db.execSQL(CREATE_DAYS_TABLE);

        String CREATE_PERIOD_TABLE = "CREATE TABLE " + TABLE_PERIODS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_ENERGY + " TEXT,"
                + KEY_POWER + " TEXT, " + KEY_CURRENT + " TEXT,"
                + KEY_VOLTAGE + " TEXT," + KEY_POWER_FACTOR + " TEXT, " + KEY_DAY  + " TEXT, " + KEY_PERIOD + " TEXT" + ")";
        db.execSQL(CREATE_PERIOD_TABLE);

        String CREATE_WEEKS_TABLE = "CREATE TABLE " + TABLE_WEEKS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_ENERGY + " TEXT,"
                + KEY_POWER + " TEXT, " + KEY_CURRENT + " TEXT,"
                + KEY_VOLTAGE + " TEXT," + KEY_POWER_FACTOR + " TEXT, " + KEY_WEEK  + " TEXT" + ")";
        db.execSQL(CREATE_WEEKS_TABLE);

        String CREATE_MONTHS_TABLE = "CREATE TABLE " + TABLE_MONTHS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_ENERGY + " TEXT,"
                + KEY_POWER + " TEXT, " + KEY_CURRENT + " TEXT,"
                + KEY_VOLTAGE + " TEXT," + KEY_POWER_FACTOR + " TEXT, " + KEY_MONTH  + " TEXT" + ")";
        db.execSQL(CREATE_MONTHS_TABLE);
        Log.d(TAG, "Database tables created");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DAYS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WEEKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MONTHS);
        // Create tables again
        onCreate(db);
    }


    /*-------------------------------------------DAYS-------------------------------------------*/

    //region DAY

    /**
     * Storing days details in database
     * */
    public void addDay(String energy, String power, String current, String voltage, String powerFactor, String day) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ENERGY, energy);
        values.put(KEY_POWER, power);
        values.put(KEY_CURRENT, current);
        values.put(KEY_VOLTAGE, voltage);
        values.put(KEY_POWER_FACTOR, powerFactor);
        values.put(KEY_DAY, day);

        // Inserting Row
        long id = db.insert(TABLE_DAYS, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New day inserted into database: " + id);
    }

    /**
     * Getting day data from database
     * */
    public ArrayList<Day> getDaysDetails(){
        ArrayList<Day> days = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_DAYS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Day day = new Day();
                day.setEnergy(Double.valueOf(cursor.getString(1)));
                day.setPower(Double.valueOf(cursor.getString(2)));
                day.setCurrent(Double.valueOf(cursor.getString(3)));
                day.setVoltage(Double.valueOf(cursor.getString(4)));
                day.setPowerFactor(Double.valueOf(cursor.getString(5)));
                day.setDay(cursor.getString(6));
                days.add(day);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        Log.d(TAG, "Fetching days from database: " + days.toString());
        return days;
    }

    /**
     * Re crate database Delete all tables and create them again
     * */
    public void deleteDays() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_DAYS, null, null);
        db.close();

        Log.d(TAG, "Deleted all days info from database");
    }

    //endregion



    /*-------------------------------------------PERIODS-------------------------------------------*/

    //region PERIOD

    /**
     * Storing days details in database
     * */
    public void addPeriod(String energy, String power, String current, String voltage, String powerFactor, String day, String period) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ENERGY, energy);
        values.put(KEY_POWER, power);
        values.put(KEY_CURRENT, current);
        values.put(KEY_VOLTAGE, voltage);
        values.put(KEY_POWER_FACTOR, powerFactor);
        values.put(KEY_DAY, day);
        values.put(KEY_PERIOD, period);

        // Inserting Row
        long id = db.insert(TABLE_PERIODS, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New period inserted into database: " + id);
    }

    /**
     * Getting day data from database
     * */
    public ArrayList<Period> getPeriodsDetails(){
        ArrayList<Period> periods = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_PERIODS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Period period = new Period();
                period.setEnergy(Double.valueOf(cursor.getString(1)));
                period.setPower(Double.valueOf(cursor.getString(2)));
                period.setCurrent(Double.valueOf(cursor.getString(3)));
                period.setVoltage(Double.valueOf(cursor.getString(4)));
                period.setPowerFactor(Double.valueOf(cursor.getString(5)));
                period.setDay(cursor.getString(6));
                period.setPeriod(cursor.getString(7));
                periods.add(period);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        Log.d(TAG, "Fetching periods from database: " + periods.toString());
        return periods;
    }

    /**
     * Re crate database Delete all tables and create them again
     * */
    public void deletePeriods() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_PERIODS, null, null);
        db.close();

        Log.d(TAG, "Deleted all periods info from database");
    }

    //endregion

    /*-------------------------------------------WEEKS------------------------------------------*/

    //region WEEK

    /**
     * Storing days details in database
     * */
    public void addWeek(String energy, String power, String current, String voltage, String powerFactor, String week) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ENERGY, energy);
        values.put(KEY_POWER, power);
        values.put(KEY_CURRENT, current);
        values.put(KEY_VOLTAGE, voltage);
        values.put(KEY_POWER_FACTOR, powerFactor);
        values.put(KEY_WEEK, week);

        // Inserting Row
        long id = db.insert(TABLE_WEEKS, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New week inserted into database: " + id);
    }

    /**
     * Getting day data from database
     * */
    public ArrayList<Week> getWeeksDetails(){
        ArrayList<Week> weeks = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_WEEKS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Week week = new Week();
                week.setEnergy(Double.valueOf(cursor.getString(1)));
                week.setPower(Double.valueOf(cursor.getString(2)));
                week.setCurrent(Double.valueOf(cursor.getString(3)));
                week.setVoltage(Double.valueOf(cursor.getString(4)));
                week.setPowerFactor(Double.valueOf(cursor.getString(5)));
                week.setWeek(Integer.valueOf(cursor.getString(6)));
                weeks.add(week);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        Log.d(TAG, "Fetching weeks from database: " + weeks.toString());
        return weeks;
    }

    /**
     * Re crate database Delete all tables and create them again
     * */
    public void deleteWeeks() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_WEEKS, null, null);
        db.close();

        Log.d(TAG, "Deleted all weeks info from database");
    }

    //endregion

    /*-------------------------------------------MONTHS------------------------------------------*/

    //region MONTH

    /**
     * Storing days details in database
     * */
    public void addMonth(String energy, String power, String current, String voltage, String powerFactor, String month) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ENERGY, energy);
        values.put(KEY_POWER, power);
        values.put(KEY_CURRENT, current);
        values.put(KEY_VOLTAGE, voltage);
        values.put(KEY_POWER_FACTOR, powerFactor);
        values.put(KEY_MONTH, month);

        // Inserting Row
        long id = db.insert(TABLE_MONTHS, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New month inserted into database: " + id);
    }

    /**
     * Getting day data from database
     * */
    public ArrayList<Month> getMonthsDetails(){
        ArrayList<Month> months = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_MONTHS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Month month = new Month();
                month.setEnergy(Double.valueOf(cursor.getString(1)));
                month.setPower(Double.valueOf(cursor.getString(2)));
                month.setCurrent(Double.valueOf(cursor.getString(3)));
                month.setVoltage(Double.valueOf(cursor.getString(4)));
                month.setPowerFactor(Double.valueOf(cursor.getString(5)));
                month.setMonth(cursor.getString(6));
                months.add(month);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        Log.d(TAG, "Fetching months from database: " + months.toString());
        return months;
    }

    /**
     * Re crate database Delete all tables and create them again
     * */
    public void deleteMonths() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_MONTHS, null, null);
        db.close();

        Log.d(TAG, "Deleted all moths info from database");
    }

    //endregion


}
