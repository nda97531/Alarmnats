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
//import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
// import android.widget.TextClock;
import android.widget.TimePicker;
import android.widget.Toast;

//import com.android.volley.Response;
//import com.android.volley.VolleyError;
import com.e15.alarmnats.AlarmReceiver;
import com.e15.alarmnats.MainActivity;
import com.e15.alarmnats.Model.Alarm;
import com.e15.alarmnats.R;
import com.e15.alarmnats.ViewSupport.TimePickerFragment;
import com.e15.alarmnats.Model.AlarmItem;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SetAlarmActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener,
        AdapterView.OnItemSelectedListener {

    private static final String TAG = "SetAlarmActivity";
    // declare variables
    private TextView textViewTimePicker;
    // private TextClock textViewTimePicker;
    private AutoCompleteTextView txtSong;
    private Button ringtonePickerButton;
    private EditText label;
    private Button setAlarmButton;
    private Spinner question_spinner;

    private Button btnChooseSong;

    // Set song
//    private Spinner spChoose;
//
//    private ImageView preview;
//
//    private ArrayAdapter<String> adapterSong;
//
//    private LinearLayout background;
//
//    private ImageView imgAlbum;
//
//    private boolean itemClicked = false;

    private Calendar calendar;

    private Uri ringtoneUri;
    private String ringtoneName;

    private Intent receiverIntent;
    private PendingIntent pendingIntent;

    private List<String> stringResults = new ArrayList<>();

    private static final int RINGTONE_REQUEST_CODE = 1;
    private String question = "Default", answer = "default";

    private Alarm alarm;
    private static final int SPOTIFY_REQUEST_CODE = 2;
    private boolean marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_alarm);
        marker = true;

        alarm = (Alarm) getIntent().getSerializableExtra("alarmObject");

        // initialize variables
        textViewTimePicker = findViewById(R.id.text_view_time_picker);
        question_spinner = (Spinner) findViewById(R.id.question_spinner);
        // ringtonePickerButton = findViewById(R.id.button_rigtone_picker);
        label = findViewById(R.id.textbox_label);
        setAlarmButton = findViewById(R.id.button_start_alarm);

        //choose selection

//        adapterSong=new ArrayAdapter(this,android.R.layout.simple_dropdown_item_1line,stringResults);
//
//        txtSong=(AutoCompleteTextView)findViewById(R.id.txtSong);
//
//        txtSong.setAdapter(adapterSong);
//
//        spChoose=(Spinner)findViewById(R.id.spChoose);
//
//        spChoose.setOnItemSelectedListener(this);
//
//        ArrayAdapter<CharSequence>adapter_selection=ArrayAdapter.createFromResource(this,R.array.SearchMusic,android.R.layout.simple_spinner_item);
//
//        spChoose.setAdapter(adapter_selection);
//
//        spChoose.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
//                // your code here
//
//                if(position!=0){
//                    ringtonePickerButton.setEnabled(false);
//
//                    txtSong.setEnabled(true);
//
//                }
//
//
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parentView) {
//                // your code here
//            }
//
//        });
//
//        imgAlbum=(ImageView)findViewById(R.id.imgAlbum);
//
//        background=findViewById(R.id.constraintLayout);
//
//        preview = (ImageView) findViewById(R.id.preview);
//
//        txtSong.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//                // if the change is due to the user clicking a suggested song, don't suggest more
//                if(itemClicked) {
//                    itemClicked = false;
//                    return;
//                }
//
//                // when users enters another character..
//                updateSongs(trackField.getText().toString());
//
//            }
//        });
//
//        // when a user clicks a selected track in from search suggestions, load that into alarmItem
//        trackField.setOnItemClickListener((adapterView, view12, i, l) -> {
//
//            // stops suggesting
//            itemClicked = true;
//            AlarmItem searchItem = searchResultsItems.get(i);
//
//            // updates alarmItem with attributes from search item
//            alarmItem.setName(searchItem.getName());
//            alarmItem.setArtist(searchItem.getArtist());
//            alarmItem.setImageUrl(searchItem.getImageUrl());
//            alarmItem.setTrackUri(searchItem.getTrackUri());
//
//            try {
//                alarmItem.jsonify(); // updates json in alarmItem
//            } catch (JSONException e) {
//                Log.e("MainActivity", "error converting into json");
//                e.printStackTrace();}
//
//            // updates UI
//            trackField.setText(alarmItem.getArtist() + " - " + alarmItem.getName());
//            updateAlbumArt(alarmItem.getImageUrl());
//            preview.setVisibility(View.VISIBLE);
//
//            // hides keybaord
//            hideKeyboard();
//
//        });
//
//        // when a users selects a track from clicking enter on keyboard
//        trackField.setOnKeyListener((view1, keyCode, keyEvent) -> {
//
//            itemClicked = true;
//
//            if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) &&
//                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
//
//                // Perform search on enter press
//                searchTrack(trackField.getText().toString().replaceAll(" ", "+"));
//                hideKeyboard();
//
//                return true;
//            }
//
//            return false;
//        });

        btnChooseSong=(Button)findViewById(R.id.btnChooseSong);

        btnChooseSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SetAlarmActivity.this, SearchSongActivity.class);
                startActivityForResult(intent, SPOTIFY_REQUEST_CODE);
            }
        });

        // calendar instance
        calendar = Calendar.getInstance();

        // receiverIntent
        receiverIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
        receiverIntent.putExtra("alarmTime", textViewTimePicker.getText());

        // time picker
        textViewTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });

        // ringtone picker
//        ringtonePickerButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
//                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
//                startActivityForResult(intent, RINGTONE_REQUEST_CODE);
//            }
//        });

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
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
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

//        ringtonePickerButton.setText(alarm.getRingtoneName());

        label.setText(alarm.getLabel());

        question_spinner.setSelection(getQuestionPosition(alarm.getQuestion()));

        ringtoneUri = Uri.parse(alarm.getRingtoneUri());
        if (ringtoneUri.toString().split(":")[0].equals("spotify"))
            setSpotifyMusic(new AlarmItem(ringtoneUri.toString(), alarm.getRingtoneName()));
        else
            setRingtone(ringtoneUri);
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
        if(marker){
            marker= false;
            return;
        }

        String selected = (String) question_spinner.getSelectedItem();

        if (selected.equals(getString(R.string.default_question))) {
            this.answer = "default";
            this.question = selected;
            alarm.setQuestion(this.question);
            alarm.setAnswer(this.answer);
        } else if (selected.equals(getString(R.string.qr_question))) {
            Intent intent = new Intent(this, QRscanActivity.class);
            intent.putExtra("isSettingNewAlarm", true);
            startActivityForResult(intent, MainActivity.SCAN_QR_CODE_INTENT_REQUEST_CODE);
        } else if (selected.equals(getString(R.string.math_question))) { //continue here
            this.answer = "default";
            this.question = selected;
            alarm.setQuestion(this.question);
            alarm.setAnswer(this.answer);
        } else if (selected.equals(getString(R.string.verify_recaptcha))) {
            this.answer = "default";
            this.question = selected;
            alarm.setQuestion(this.question);
            alarm.setAnswer(this.answer);
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
                this.question = getString(R.string.qr_question);
                alarm.setQuestion(this.question);
                alarm.setAnswer(this.answer);
            } else if (requestCode == SPOTIFY_REQUEST_CODE) {
                AlarmItem alarmItem = (AlarmItem) data.getExtras().getSerializable("AlarmItem");
                Log.d(TAG, String.format("AlarmItem == null? %b", alarmItem == null));
                if (alarmItem != null) {
                    Log.d(TAG, alarmItem.getName());
                    setSpotifyMusic(alarmItem);
                }
            }
        }
    }

    // set ringtone
    private void setRingtone(Uri uri) {
        Ringtone ringtone = RingtoneManager.getRingtone(this, uri);
        ringtoneName = ringtone.getTitle(this);

        btnChooseSong.setText(ringtoneName);

        receiverIntent.putExtra("ringtoneUri", ringtoneUri.toString());

        alarm.setRingtoneUri(uri.toString());
        alarm.setRingtoneName(ringtoneName);
    }

    public int getQuestionPosition(String name) {
        if (name.equals(getString(R.string.qr_question))) {
            return 1;
        } else if (name.equals(getString(R.string.math_question))) {
            return 2;
        } else if (name.equals(getString(R.string.verify_recaptcha))){
            return 3;
        }
        return 0;
    }

    private void setSpotifyMusic(AlarmItem alarmItem) {
        ringtoneName = alarmItem.getName();

        btnChooseSong.setText(ringtoneName);

        receiverIntent.putExtra("ringtoneUri", alarmItem.getTrackUri());

        alarm.setRingtoneUri(alarmItem.getTrackUri());
        alarm.setRingtoneName(ringtoneName);
    }

//    @Override
//    public void onErrorResponse(VolleyError error) {
//
//    }
//
//    //Called when a response is received.
//    @Override
//    public void onResponse(String response) {
//
//        try {
//            JSONObject reader = new JSONObject(response);
//            JSONObject tracks  = reader.getJSONObject("tracks");
//            JSONArray items  = tracks.getJSONArray("items");
//
//            //searchResultsItems.clear();
//            stringResults.clear();
//
//            for(int i = 0; i < items.length(); i++) {
//                JSONObject result = items.getJSONObject(i);
//                String uri = result.getString("uri");
//                String name = result.getString("name");
//                String artist = result.getJSONArray("artists").getJSONObject(0).getString("name");
//                String imageUrl = result.getJSONObject("album").getJSONArray("images").getJSONObject(0).getString("url");
//
//                // creating a minimum AlarmItem for storing uri, image, name and artist..
//                // the rest of the attributes will be set separately
////                AlarmItem item = new AlarmItem(uri, imageUrl, name, artist,
////                        0,
////                        0,
////                        false,
////                        0);
//
//                stringResults.add(name);
//                //searchResultsItems.add(item);
//            }
//
//            try {
//                adapterSong = new ArrayAdapter<String>(getBaseContext(),
//                        android.R.layout.simple_dropdown_item_1line,
//                        stringResults);
//            } catch (NullPointerException e) {
//                e.printStackTrace();
//            }
//
//            txtSong.setAdapter(adapterSong);
//
//            adapterSong.notifyDataSetChanged();
//
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//
//    }
//
//    public void updateAlbumArt(String imageUrl) {
//
//        // setting thumbnail ..
//        Picasso.with(getBaseContext())
//                .load(imageUrl)
//                .fit()
//                .centerCrop()
//                .into(imgAlbum, new com.squareup.picasso.Callback() {
//                    @Override
//                    public void onSuccess() {
//                        updateBackgroundColor();
//                    }
//                    @Override
//                    public void onError() {
//                        Log.e("MainActivity", "Error setting image using Picasso");
//                    }
//                });
//    }
//
//    public void updateBackgroundColor() {
//        Palette.PaletteAsyncListener paletteListener = new Palette.PaletteAsyncListener() {
//            public void onGenerated(Palette p) {
//
//                int d = 0x0E0E0E;
//
//                GradientDrawable gd = new GradientDrawable(
//                        GradientDrawable.Orientation.TOP_BOTTOM,
//                        new int[] {p.getDarkMutedColor(d), d});
//
//                Drawable[] grads = {background.getBackground(), gd};
//
//                TransitionDrawable transitionDrawable = new TransitionDrawable(grads);
//                background.setBackground(transitionDrawable);
//                transitionDrawable.startTransition(500);
//
//            }
//        };
//
//        Bitmap bitmap = ((BitmapDrawable) imgAlbum.getDrawable()).getBitmap();
//        Palette.from(bitmap).generate(paletteListener);
//    }
//
//    public void setTrackFromTitle(String title) {
//
//        try {
//            JSONObject reader = new JSONObject(title);
//            JSONObject tracks  = reader.getJSONObject("tracks");
//            JSONArray items  = tracks.getJSONArray("items");
//            JSONObject result = items.getJSONObject(0);
//            String uri = result.getString("uri");
//            String name = result.getString("name");
//            String artist = items.getJSONObject(0).getJSONArray("artists").getJSONObject(0).getString("name");
//            String imageUrl = result.getJSONObject("album").getJSONArray("images").getJSONObject(0).getString("url");
//
//            // updates UI
//            txtSong.setText(artist + " - " + name);
//
//            // updates album art
//            updateAlbumArt(imageUrl);
//
//            preview.setVisibility(View.VISIBLE);
//
//            // updates alarmItem
////            alarmItem.setArtist(artist);
////            alarmItem.setName(name);
////            alarmItem.setTrackUri(uri);
////            alarmItem.setImageUrl(imageUrl);
//
////            try {
////                alarmItem.jsonify(); // updates json in alarmItem
////            } catch (JSONException e) {}
//
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
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
