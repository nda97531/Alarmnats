package com.e15.alarmnats.ActivityController;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.e15.alarmnats.Model.Alarm;
import com.e15.alarmnats.Database.AlarmDbHelper;
import com.e15.alarmnats.AlarmReceiver;
import com.e15.alarmnats.R;
import com.e15.alarmnats.RecylcerViewAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // variable declarations
    private FloatingActionButton addAlarmButton;

    private AlarmDbHelper dbHelper;
    private Alarm alarm;

    // list to save database alarm data
    private ArrayList<String> mAlarmTimes = new ArrayList<>();
    private ArrayList<Long> mAlarmTimesInMillis = new ArrayList<>();
    private ArrayList<Boolean> mAlarmStatuses = new ArrayList<>();
    private ArrayList<String> mRingtoneNames = new ArrayList<>();
    private ArrayList<String> mRingtoneUris = new ArrayList<>();
    private ArrayList<String> mLabels = new ArrayList<>();
    private ArrayList<Integer> mFlags = new ArrayList<>();

    private RecylcerViewAdapter adapter;

    private Intent alarmIntent;

    private static final int SET_ALARM_INTENET_REQUEST_CODE = 1;
    public static final int SCAN_QR_CODE_INTENT_REQUEST_CODE = 100;
    public static final int MATH_TEST_INTENT_REQUEST_CODE = 200;
    public static final int EDIT_ALARM_INTENET_REQUEST_CODE = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // database helper
        dbHelper = AlarmDbHelper.getInstance(this);
        alarm = new Alarm();

        // get all alarms from db
        getAlarms();

        // intent to pass to broadcast receiver
        System.out.println("ringgg!!!");
        alarmIntent = new Intent(this, AlarmReceiver.class);

        addAlarmButton = findViewById(R.id.addAlarmButton);

        // add new alarm
        addAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Alarm alarm = defaultAlarmObject();
                Intent setAlarmIntent = new Intent(MainActivity.this, SetAlarmActivity.class);
                setAlarmIntent.putExtra("alarmObject", alarm);
                setAlarmIntent.putExtra("isNewAlarm", true);
                MainActivity.this.startActivityForResult(setAlarmIntent, SET_ALARM_INTENET_REQUEST_CODE);
            }
        });

    }

    // get result from new alarm activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        adapter.onRecAdapterActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            alarm = (Alarm) data.getSerializableExtra("alarmObject");
            if (requestCode == SET_ALARM_INTENET_REQUEST_CODE) {
                dbHelper.addAlarm(alarm);
                getAlarms();
            } else if (requestCode == EDIT_ALARM_INTENET_REQUEST_CODE) {
                dbHelper.updateAlarm(alarm);
                getAlarms();
            }
        }
    }

    // get all alarms
    public void getAlarms() {
        System.out.println("getting all alarms");
        List<Alarm> alarmList = dbHelper.getAllAlarms();

        mAlarmTimes.clear();
        mAlarmTimesInMillis.clear();
        mAlarmStatuses.clear();
        mRingtoneNames.clear();
        mRingtoneUris.clear();
        mLabels.clear();
        mFlags.clear();

        for (Alarm alarm : alarmList) {
            mAlarmTimes.add(alarm.getAlarmTime());
            mAlarmTimesInMillis.add(alarm.getAlarmTimeInMillis());
            mAlarmStatuses.add(alarm.isAlarmStatus());
            mRingtoneNames.add(alarm.getRingtoneName());
            mRingtoneUris.add(alarm.getRingtoneUri().toString());
            mLabels.add(alarm.getLabel());
            mFlags.add(alarm.getFlag());
        }
        initRecyclerView();
    }

    // initialize recycler view
    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        adapter = new RecylcerViewAdapter(mAlarmTimes,
                mAlarmTimesInMillis,
                mAlarmStatuses,
                mRingtoneNames,
                mRingtoneUris,
                mLabels,
                mFlags,
                this);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    // delete alarm
    public void deleteAlarm(int flag, int position) {
        Log.d("main", "before if");
        if (dbHelper.deleteAlarm(flag)) {
            Log.d("main", "row deleted");
            cancelAlarm(mFlags.get(position), false); //delete alarm in DB => no need to change status in DB
            getAlarms();
        }
    }

    // cancel / disable Alarm
    public void cancelAlarm(int flag, boolean changeStatus) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent receiverIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), flag, receiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
        System.out.println("canceled");

        if (changeStatus) {
            System.out.println("number switch off: " + dbHelper.updateStatus(flag, 0));
        }
    }

    // enable alarm
    public void enableExistingAlarm(int flag) {
        Alarm alarm = dbHelper.getAlarm(flag);

        Intent receiverIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
        receiverIntent.putExtra("alarmTime", alarm.getAlarmTime());
        receiverIntent.putExtra("question", alarm.getQuestion());
        receiverIntent.putExtra("answer", alarm.getAnswer());
        receiverIntent.putExtra("ringtoneUri", alarm.getRingtoneUri());

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), flag, receiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarm.getAlarmTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        System.out.println("number switch on: " + dbHelper.updateStatus(flag, 1));
    }

    //edit alarm
    public void editAlarm(int flag) {
        Alarm alarm = dbHelper.getAlarm(flag);
        Intent intent = new Intent(this, SetAlarmActivity.class);
        intent.putExtra("alarmObject", alarm);
        intent.putExtra("isNewAlarm", false);
        startActivityForResult(intent, EDIT_ALARM_INTENET_REQUEST_CODE);
    }

    public Alarm defaultAlarmObject() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String currentTime = sdf.format(new Date());
        Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        String ringtoneName = RingtoneManager.getRingtone(this, ringtoneUri).getTitle(this);

        return new Alarm(0,
                currentTime,
                System.currentTimeMillis(),
                true,
                ringtoneUri.toString(),
                ringtoneName,
                "",
                -1,
                getString(R.string.default_question),
                "default");
    }
}