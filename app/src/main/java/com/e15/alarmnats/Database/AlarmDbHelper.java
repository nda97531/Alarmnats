package com.e15.alarmnats.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import com.e15.alarmnats.Model.Alarm;

import java.util.ArrayList;
import java.util.List;

public class AlarmDbHelper extends SQLiteOpenHelper {
    private static AlarmDbHelper mInstance = null;

    public static final String DATABASE_NAME = "alarm.db";
    public static final int DATABASE_VERSION = 1;

    private Context mCtx;

    private SQLiteDatabase db;

    private AlarmDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mCtx = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;

        final String SQL_CREATE_ALARM_TABLE = "CREATE TABLE " +
                AlarmContract.AlarmTable.TABLE_NAME + " ( " +
                AlarmContract.AlarmTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                AlarmContract.AlarmTable.COLUMN_ALARM_TIME + " TEXT, " +
                AlarmContract.AlarmTable.COLUMN_ALARM_IN_MILLIES + " INTEGER, " +
                AlarmContract.AlarmTable.COLUMN_ALARM_STATUS + " INTEGER, " +
                AlarmContract.AlarmTable.COLUMN_RINGTONE_NAME + " TEXT, " +
                AlarmContract.AlarmTable.COLUMN_RINGTONE_URI + " TEXT, " +
                AlarmContract.AlarmTable.COLUMN_LABEL + " TEXT, " +
                AlarmContract.AlarmTable.COLUMN_NAME_FLAG + " INTEGER, " +

                AlarmContract.AlarmTable.COLUMN_QUESTION + " TEXT, " +
                AlarmContract.AlarmTable.COLUMN_ANSWER + " TEXT" +
                ")";

        db.execSQL(SQL_CREATE_ALARM_TABLE);
    }

    public void addAlarm(Alarm alarm) {
        ContentValues cv = new ContentValues();
        cv.put(AlarmContract.AlarmTable.COLUMN_ALARM_TIME, alarm.getAlarmTime());
        cv.put(AlarmContract.AlarmTable.COLUMN_ALARM_IN_MILLIES, alarm.getAlarmTimeInMillis());
        cv.put(AlarmContract.AlarmTable.COLUMN_ALARM_STATUS, alarm.isAlarmStatus() ? 1 : 0);
        cv.put(AlarmContract.AlarmTable.COLUMN_RINGTONE_NAME, alarm.getRingtoneName());
        cv.put(AlarmContract.AlarmTable.COLUMN_RINGTONE_URI, alarm.getRingtoneUri().toString());
        cv.put(AlarmContract.AlarmTable.COLUMN_LABEL, alarm.getLabel());
        cv.put(AlarmContract.AlarmTable.COLUMN_NAME_FLAG, alarm.getFlag());

        cv.put(AlarmContract.AlarmTable.COLUMN_QUESTION, alarm.getQuestion());
        cv.put(AlarmContract.AlarmTable.COLUMN_ANSWER, alarm.getAnswer());

        db.insert(AlarmContract.AlarmTable.TABLE_NAME, null, cv);
    }

    public Alarm getAlarm(Integer flag) {

        // Filter results WHERE "title" = 'My Title'
        String selection = AlarmContract.AlarmTable.COLUMN_NAME_FLAG + " = ?";
        String[] selectionArgs = {flag.toString()};

        Cursor c = db.query(
                AlarmContract.AlarmTable.TABLE_NAME,   // The table to query
                null,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );

        ArrayList<Alarm> alarms = new ArrayList<>();
        while (c.moveToNext()) {
            Alarm alarm = new Alarm(
                    c.getInt(c.getColumnIndexOrThrow(AlarmContract.AlarmTable._ID)),
                    c.getString(c.getColumnIndexOrThrow(AlarmContract.AlarmTable.COLUMN_ALARM_TIME)),
                    c.getLong(c.getColumnIndexOrThrow(AlarmContract.AlarmTable.COLUMN_ALARM_IN_MILLIES)),
                    c.getInt(c.getColumnIndexOrThrow(AlarmContract.AlarmTable.COLUMN_ALARM_STATUS)) != 0,
                    c.getString(c.getColumnIndexOrThrow(AlarmContract.AlarmTable.COLUMN_RINGTONE_NAME)),
                    c.getString(c.getColumnIndexOrThrow(AlarmContract.AlarmTable.COLUMN_RINGTONE_URI)),
                    c.getString(c.getColumnIndexOrThrow(AlarmContract.AlarmTable.COLUMN_LABEL)),
                    c.getInt(c.getColumnIndexOrThrow(AlarmContract.AlarmTable.COLUMN_NAME_FLAG)),
                    c.getString(c.getColumnIndexOrThrow(AlarmContract.AlarmTable.COLUMN_QUESTION)),
                    c.getString(c.getColumnIndexOrThrow(AlarmContract.AlarmTable.COLUMN_ANSWER))
            );

            alarms.add(alarm);
        }
        c.close();
        return alarms.get(0);
    }

    public int updateStatus(Integer flag, int status) {
        // SET
        ContentValues values = new ContentValues();
        values.put(AlarmContract.AlarmTable.COLUMN_ALARM_STATUS, status);

        // WHERE
        String selection = AlarmContract.AlarmTable.COLUMN_NAME_FLAG + " = ?";
        String[] selectionArgs = {flag.toString()};

        return db.update(
                AlarmContract.AlarmTable.TABLE_NAME,
                values,
                selection,
                selectionArgs);
    }

    public boolean deleteAlarm(Integer flag) {
        Log.d("dbhelper", "delete method");

        String where = AlarmContract.AlarmTable.COLUMN_NAME_FLAG + " = ?";
        String[] whereAgs = new String[]{flag.toString()};

        return db.delete(AlarmContract.AlarmTable.TABLE_NAME, where, whereAgs) > 0;
    }

    public void updateAlarm(Alarm alarm) {
        ContentValues cv = new ContentValues();
        cv.put(AlarmContract.AlarmTable.COLUMN_ALARM_TIME, alarm.getAlarmTime());
        cv.put(AlarmContract.AlarmTable.COLUMN_ALARM_IN_MILLIES, alarm.getAlarmTimeInMillis());
        cv.put(AlarmContract.AlarmTable.COLUMN_ALARM_STATUS, alarm.isAlarmStatus() ? 1 : 0);
        cv.put(AlarmContract.AlarmTable.COLUMN_RINGTONE_NAME, alarm.getRingtoneName());
        cv.put(AlarmContract.AlarmTable.COLUMN_RINGTONE_URI, alarm.getRingtoneUri());
        cv.put(AlarmContract.AlarmTable.COLUMN_LABEL, alarm.getLabel());
        cv.put(AlarmContract.AlarmTable.COLUMN_QUESTION, alarm.getQuestion());
        cv.put(AlarmContract.AlarmTable.COLUMN_ANSWER, alarm.getAnswer());

        String where = AlarmContract.AlarmTable.COLUMN_NAME_FLAG + " = ?";
        String[] whereAgs = new String[]{alarm.getFlag().toString()};

        db.update(AlarmContract.AlarmTable.TABLE_NAME, cv, where, whereAgs);
    }

    public static AlarmDbHelper getInstance(Context ctx) {
        if (mInstance == null) {
            mInstance = new AlarmDbHelper(ctx.getApplicationContext());
        }
        return mInstance;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + AlarmContract.AlarmTable.TABLE_NAME);
        onCreate(db);
    }

    public List<Alarm> getAllAlarms() {
        List<Alarm> alarmList = new ArrayList<>();

        db = getReadableDatabase();

        Cursor c = db.rawQuery("SELECT * FROM " + AlarmContract.AlarmTable.TABLE_NAME, null);

        if (c.moveToFirst()) {
            do {
                Alarm alarm = new Alarm();

                alarm.setAlarmTime(c.getString(c.getColumnIndex(AlarmContract.AlarmTable.COLUMN_ALARM_TIME)));
                alarm.setAlarmTimeInMillis(c.getInt(c.getColumnIndex(AlarmContract.AlarmTable.COLUMN_ALARM_IN_MILLIES)));
                alarm.setAlarmStatus(c.getInt(c.getColumnIndex(AlarmContract.AlarmTable.COLUMN_ALARM_STATUS)) != 0 ? true : false);
                alarm.setRingtoneName(c.getString(c.getColumnIndex(AlarmContract.AlarmTable.COLUMN_RINGTONE_NAME)));
                alarm.setRingtoneUri(c.getString(c.getColumnIndex(AlarmContract.AlarmTable.COLUMN_RINGTONE_URI)));
                alarm.setLabel(c.getString(c.getColumnIndex(AlarmContract.AlarmTable.COLUMN_LABEL)));
                alarm.setFlag(c.getInt(c.getColumnIndex(AlarmContract.AlarmTable.COLUMN_NAME_FLAG)));

                alarm.setQuestion(c.getString(c.getColumnIndex(AlarmContract.AlarmTable.COLUMN_QUESTION)));
                alarm.setAnswer(c.getString(c.getColumnIndex(AlarmContract.AlarmTable.COLUMN_ANSWER)));

                alarmList.add(alarm);

            } while (c.moveToNext());
        }

        c.close();

        return alarmList;
    }
}
