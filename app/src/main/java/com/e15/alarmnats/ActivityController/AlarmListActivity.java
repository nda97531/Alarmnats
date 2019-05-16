package com.e15.alarmnats.ActivityController;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.e15.alarmnats.AlarmReceiver;
import com.e15.alarmnats.Database.AlarmDbHelper;
import com.e15.alarmnats.Model.Alarm;
import com.e15.alarmnats.R;
import com.e15.alarmnats.ViewSupport.RecyclerViewAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class AlarmListActivity extends Fragment{

    // variable declarations
    private FloatingActionButton addAlarmButton;

    private Alarm alarm;
    private AlarmDbHelper dbHelper;

    // list to save database alarm data
    private ArrayList<String> mAlarmTimes = new ArrayList<>();
    private ArrayList<Long> mAlarmTimesInMillis = new ArrayList<>();
    private ArrayList<Boolean> mAlarmStatuses = new ArrayList<>();
    private ArrayList<String> mRingtoneNames = new ArrayList<>();
    private ArrayList<String> mRingtoneUris = new ArrayList<>();
    private ArrayList<String> mLabels = new ArrayList<>();
    private ArrayList<String> mQuestions = new ArrayList<>();
    private ArrayList<Integer> mFlags = new ArrayList<>();

    private RecyclerViewAdapter adapter;

    private Intent alarmIntent;

    private static final int SET_ALARM_INTENT_REQUEST_CODE = 1;
    public static final int SCAN_QR_CODE_INTENT_REQUEST_CODE = 100;
    public static final int MATH_TEST_INTENT_REQUEST_CODE = 200;
    public static final int VERIFY_CAPTCHA_INTENT_REQUEST_CODE = 201;
    public static final int EDIT_ALARM_INTENT_REQUEST_CODE = 300;
    public static final int CAMERA_PERMISSION_REQUEST_CODE = 301;
    public static final int CHOOSE_TASK_REQUEST_CODE = 400;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_alarm_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        // Inflate the layout for this fragment
        alarm = new Alarm();
        dbHelper = AlarmDbHelper.getInstance(getContext());
        // get all alarms from db
        getAlarms();

        // intent to pass to broadcast receiver
        alarmIntent = new Intent(getContext(), AlarmReceiver.class);

        addAlarmButton = getView().findViewById(R.id.addAlarmButton);

        // add new alarm
        addAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Alarm alarm = defaultAlarmObject();
                Intent setAlarmIntent = new Intent(getContext(), SetAlarmActivity.class);
                setAlarmIntent.putExtra("alarmObject", alarm);
                setAlarmIntent.putExtra("isNewAlarm", true);
                AlarmListActivity.this.startActivityForResult(setAlarmIntent, SET_ALARM_INTENT_REQUEST_CODE);
            }
        });
    }

    // get result from new alarm activity
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        adapter.onRecAdapterActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            alarm = (Alarm) data.getSerializableExtra("alarmObject");
            if (requestCode == SET_ALARM_INTENT_REQUEST_CODE) {
                dbHelper.addAlarm(alarm);
                getAlarms();
            } else if (requestCode == EDIT_ALARM_INTENT_REQUEST_CODE) {
                dbHelper.updateAlarm(alarm);
                getAlarms();
            }
        }
    }

    // get all alarms
    public void getAlarms() {
        List<Alarm> alarmList = dbHelper.getAllAlarms();

        mAlarmTimes.clear();
        mAlarmTimesInMillis.clear();
        mAlarmStatuses.clear();
        mRingtoneNames.clear();
        mRingtoneUris.clear();
        mLabels.clear();
        mQuestions.clear();
        mFlags.clear();

        for (Alarm onealarm : alarmList) {
            mAlarmTimes.add(onealarm.getAlarmTime());
            mAlarmTimesInMillis.add(onealarm.getAlarmTimeInMillis());
            mAlarmStatuses.add(onealarm.isAlarmStatus());
            mRingtoneNames.add(onealarm.getRingtoneName());
            mRingtoneUris.add(onealarm.getRingtoneUri());
            mLabels.add(onealarm.getLabel());
            mQuestions.add(onealarm.getQuestion());
            mFlags.add(onealarm.getFlag());
        }
        initRecyclerView();
    }

    // initialize recycler view
    private void initRecyclerView() {
        RecyclerView recyclerView = getView().findViewById(R.id.recycler_view);
        adapter = new RecyclerViewAdapter(mAlarmTimes,
                mAlarmTimesInMillis,
                mAlarmStatuses,
                mRingtoneNames,
                mRingtoneUris,
                mLabels,
                mQuestions,
                mFlags,
                getContext(),
                AlarmListActivity.this);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
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
        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        Intent receiverIntent = new Intent(getContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), flag, receiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);

        if (changeStatus) {
            dbHelper.updateStatus(flag, 0);
        }
    }

    // enable alarm
    public void enableExistingAlarm(int flag) {
        Alarm alarm = dbHelper.getAlarm(flag);

        Intent receiverIntent = new Intent(getContext(), AlarmReceiver.class);
        receiverIntent.putExtra("alarmTime", alarm.getAlarmTime());
        receiverIntent.putExtra("question", alarm.getQuestion());
        receiverIntent.putExtra("answer", alarm.getAnswer());
        receiverIntent.putExtra("ringtoneUri", alarm.getRingtoneUri());

        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), flag, receiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarm.getAlarmTimeInMillis(), pendingIntent);
        dbHelper.updateStatus(flag, 1);
    }

    //edit alarm
    public void editAlarm(int flag) {
        Alarm alarm = dbHelper.getAlarm(flag);
        Intent intent = new Intent(getContext(), SetAlarmActivity.class);
        intent.putExtra("alarmObject", alarm);
        intent.putExtra("isNewAlarm", false);
        startActivityForResult(intent, EDIT_ALARM_INTENT_REQUEST_CODE);
    }

    public Alarm defaultAlarmObject() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String currentTime = sdf.format(new Date());
        Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        String ringtoneName = RingtoneManager.getRingtone(getContext(), ringtoneUri).getTitle(getContext());

        return new Alarm(0,
                currentTime,
                System.currentTimeMillis(),
                true,
                ringtoneName,
                ringtoneUri.toString(),
                "",
                -1,
                getString(R.string.default_question),
                "default");
    }
}