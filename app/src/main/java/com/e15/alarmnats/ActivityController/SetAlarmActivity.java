package com.e15.alarmnats.ActivityController;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
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
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.e15.alarmnats.AlarmReceiver;
import com.e15.alarmnats.Model.Alarm;
import com.e15.alarmnats.R;
import com.e15.alarmnats.TimePickerFragment;

import java.util.Calendar;

public class SetAlarmActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, AdapterView.OnItemSelectedListener {

    // declare variables
    private TextView textViewTimePicker;
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

    private Alarm alarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_alarm);

        alarm = (Alarm) getIntent().getSerializableExtra("alarmObject");

        // initialize variables
        textViewTimePicker = findViewById(R.id.text_view_time_picker);
        question_spinner = (Spinner) findViewById(R.id.question_spinner);
        ringtonePickerButton = findViewById(R.id.button_rigtone_picker);
        label = findViewById(R.id.textbox_label);
        setAlarmButton = findViewById(R.id.button_start_alarm);

        // calendar instance
        calendar = Calendar.getInstance();

        // receiverIntent
        receiverIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
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

                alarm.setAlarmTime((String) textViewTimePicker.getText());
                alarm.setLabel(label.getText().toString());
                if (getIntent().getExtras().getBoolean("isNewAlarm")) {
                    alarm.setFlag((int) (System.currentTimeMillis() / 1000));
                }

                // SET ALARM MANAGER
                setPendingIntent(alarm.getFlag());

                int hour = Integer.parseInt(alarm.getAlarmTime().split(":")[0]);
                int min = Integer.parseInt(alarm.getAlarmTime().split(":")[1]);
//                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, min);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);

                if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
                    calendar.add(Calendar.DATE, 1);
                    Toast.makeText(SetAlarmActivity.this, "Delay for 1 day", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SetAlarmActivity.this, "Alarm is set for " + alarm.getAlarmTime(), Toast.LENGTH_SHORT).show();
                }

                alarm.setAlarmTimeInMillis(calendar.getTimeInMillis());
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
//                alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

                /// send result to mainActivity
                Intent intentReturnToMain = new Intent();
                intentReturnToMain.putExtra("alarmObject", alarm);

                setResult(RESULT_OK, intentReturnToMain);

                finish();
            }
        });

        //initialize the question spinner

        question_spinner.setOnItemSelectedListener(this);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter
                = ArrayAdapter.createFromResource(this, R.array.question_list, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        question_spinner.setAdapter(adapter);

        // show old data
        textViewTimePicker.setText(alarm.getAlarmTime());
        ringtonePickerButton.setText(alarm.getRingtoneName());
        label.setText(alarm.getLabel());
        System.out.println("spinner position: " + getQuestionPosition(alarm.getQuestion()) + " " + alarm.getQuestion());
        question_spinner.setSelection(getQuestionPosition(alarm.getQuestion()));
    }

    // create pending intent
    private void setPendingIntent(int flag_id) {
        receiverIntent.putExtra("question", this.question);
        receiverIntent.putExtra("answer", this.answer);

        this.pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), flag_id, receiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    //spinner callback
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selected = (String) question_spinner.getSelectedItem();
        this.question = selected;

        if (selected.equals(getString(R.string.default_question))) {
            this.answer = "default";
        } else if (selected.equals(getString(R.string.qr_question))) {
            Intent intent = new Intent(this, QRscanActivity.class);
            startActivityForResult(intent, MainActivity.SCAN_QR_CODE_INTENT_REQUEST_CODE);
        } else if (selected.equals(getString(R.string.math_question))) { //continue here
            this.answer = "default";
        }
        alarm.setQuestion(this.question);
        alarm.setAnswer(this.answer);
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

        alarm.setRingtoneUri(uri.toString());
        alarm.setRingtoneName(ringtoneName);

    }

    public int getQuestionPosition(String name) {
        if (name.equals(getString(R.string.qr_question))) {
            return 1;
        } else if (name.equals(getString(R.string.math_question))) {
            return 2;
        }
        return 0;
    }
}
