package com.e15.alarmnats.ActivityController;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.e15.alarmnats.AlarmReceiver;
import com.e15.alarmnats.R;
import com.e15.alarmnats.TimePickerFragment;

import java.util.Calendar;

public class SetAlarmActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, AdapterView.OnItemSelectedListener {

    // declare variables
    private TextClock textViewTimePicker;
    private Button ringtonePickerButton;
    private EditText label;
    private Button setAlarmButton;
    private Spinner question_spinner;

    private Calendar calendar;

    private Uri ringtoneUri;
    private String ringtoneName;

    private Intent receiverIntent;
    private PendingIntent pendingIntent;

    private static final int RINGTONE_REQUEST_CODE = 1;
    private String question = "default", answer = "default";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_alarm);

        // initialize variables
        textViewTimePicker = findViewById(R.id.text_view_time_picker);
        ringtonePickerButton = findViewById(R.id.button_rigtone_picker);
        label = findViewById(R.id.textbox_label);
        setAlarmButton = findViewById(R.id.button_start_alarm);

        // calendar instance
        calendar = Calendar.getInstance();

        // receiverIntent
        receiverIntent = new Intent(this, AlarmReceiver.class);
        receiverIntent.putExtra("alarmTime", textViewTimePicker.getText());

        // ringtone
        ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        setRingtone(ringtoneUri);

        // time picker
        textViewTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });

        // ringtone picker
        ringtonePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
                startActivityForResult(intent, RINGTONE_REQUEST_CODE);
            }
        });

        // SET ALARM
        setAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String time = (String) textViewTimePicker.getText();
                String labelText = label.getText().toString();

                // SET ALARM MANAGER
                setPendingIntent();

                int hour = Integer.parseInt(time.split(":")[0]);
                int min = Integer.parseInt(time.split(":")[1]);
//                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, min);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);

                if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
                    calendar.add(Calendar.DATE, 1);
                    Toast.makeText(SetAlarmActivity.this, "Delay for 1 day", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SetAlarmActivity.this, "Alarm is set for " + time, Toast.LENGTH_SHORT).show();
                }

                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
//                alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

                /// send result to mainActivity
                Intent intentReturnToMain = new Intent();
                intentReturnToMain.putExtra("timeString", time);
                intentReturnToMain.putExtra("timeInMillis", calendar.getTimeInMillis());
                intentReturnToMain.putExtra("label", labelText);
                intentReturnToMain.putExtra("ringtoneUri", ringtoneUri.toString());
                intentReturnToMain.putExtra("ringtoneName", ringtoneName);
                intentReturnToMain.putExtra("flags", (int) calendar.getTimeInMillis());
                intentReturnToMain.putExtra("question", question);
                intentReturnToMain.putExtra("answer", answer);

                setResult(RESULT_OK, intentReturnToMain);


                finish();
            }
        });

        //initialize the question spinner
        question_spinner = (Spinner) findViewById(R.id.question_spinner);
        question_spinner.setOnItemSelectedListener(this);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter
                = ArrayAdapter.createFromResource(this, R.array.question_list, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        question_spinner.setAdapter(adapter);
    }

    // create pending intent
    private void setPendingIntent() {
        receiverIntent.putExtra("question", this.question);
        receiverIntent.putExtra("answer", this.answer);

        final int _id = (int) calendar.getTimeInMillis();
        this.pendingIntent = PendingIntent.getBroadcast(this, _id, receiverIntent, pendingIntent.FLAG_UPDATE_CURRENT);
    }

    //spinner callback
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selected = (String) question_spinner.getSelectedItem();

        if (selected.equals(getString(R.string.default_question))) {
            this.question = "default";
            this.answer = "default";
        } else if (selected.equals(getString(R.string.qr_question))) {
            this.question = "qr";
            Intent intent = new Intent(this, QRscanActivity.class);
            startActivityForResult(intent, MainActivity.SCAN_QR_CODE_INTENT_REQUEST_CODE);
        } else if (selected.equals(getString(R.string.math_question))) { //continue here
            this.question = "math";
            this.answer = "default";
        }
    }

    //spinner callback
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    // time set callback
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        textViewTimePicker.setText(hourOfDay + ":" + minute);
        System.out.println("set time callback");

        receiverIntent.putExtra("alarmTime", textViewTimePicker.getText());
    }

    // result from activity for ringtone picker
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == RINGTONE_REQUEST_CODE) {
                ringtoneUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                setRingtone(ringtoneUri);
            } else if (requestCode == MainActivity.SCAN_QR_CODE_INTENT_REQUEST_CODE) {
                this.answer = data.getStringExtra("code");
            }
        }
    }

    // set ringtone
    private void setRingtone(Uri uri) {
        Ringtone ringtone = RingtoneManager.getRingtone(this, uri);
        ringtoneName = ringtone.getTitle(this);

        ringtonePickerButton.setText(ringtoneName);

        receiverIntent.putExtra("ringtoneUri", ringtoneUri.toString());

    }



//    public void cancelAlarm(int flag) {
//        receiverIntent = new Intent(this, AlarmReceiver.class);
//        pendingIntent = PendingIntent.getBroadcast(this, flag, receiverIntent, 0);
//
//        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        alarmManager.cancel(pendingIntent);
//
//        finish();
//    }

}
