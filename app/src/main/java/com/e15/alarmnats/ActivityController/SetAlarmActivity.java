package com.e15.alarmnats.ActivityController;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.e15.alarmnats.AlarmReceiver;
import com.e15.alarmnats.Model.Alarm;
import com.e15.alarmnats.Model.AlarmItem;
import com.e15.alarmnats.R;

import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;

//import android.support.v7.graphics.Palette;
// import android.widget.TextClock;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;

public class SetAlarmActivity extends AppCompatActivity{

    private static final String TAG = "SetAlarmActivity";
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

    private static final int RINGTONE_REQUEST_CODE = 1;

    private Alarm alarm;
    private static final int SPOTIFY_REQUEST_CODE = 2;

    @Bind(R.id.chooseTask)
    LinearLayout chooseTask;
    @Bind(R.id.tvTask)
    TextView tvTask;
    @Bind(R.id.imageTask)
    ImageView imageTask;

    @Bind(R.id.timePicker)
    TimePicker timePicker;

    @Bind(R.id.cardViewLabel)
    CardView cardViewLabel;
    @Bind(R.id.tvLabelInfo)
    TextView tvLabelInfo;

    @Bind(R.id.cardViewRingtone)
    CardView cardViewRingtone;
    @Bind(R.id.tvRingtoneInfo)
    TextView tvRingtoneInfo;

    @Bind(R.id.cardViewSave)
    CardView cardViewSave;

    @Bind(R.id.cardViewCancel)
    CardView cardViewCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_activity_set_alarm);
        ButterKnife.bind(this);
        setTitle("Set Alarm");

        alarm = (Alarm) getIntent().getSerializableExtra("alarmObject");

        timePicker.setIs24HourView(true);

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
//                Log.e("AlarmListActivity", "error converting into json");
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

        // calendar instance
        calendar = Calendar.getInstance();

        // receiverIntent
        receiverIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
        receiverIntent.putExtra("alarmTime", alarm.getAlarmTime());

        // show old data
        tvTask.setText(alarm.getQuestion()); //show task
        if(alarm.getQuestion().equals(getString(R.string.default_question)))
            imageTask.setImageResource(R.drawable.baseline_alarm_black_48);
        else if(alarm.getQuestion().equals(getString(R.string.qr_question)))
            imageTask.setImageResource(R.mipmap.qrcode_alarme);
        else if(alarm.getQuestion().equals(getString(R.string.math_question)))
            imageTask.setImageResource(R.drawable.ic_math);
        else if(alarm.getQuestion().equals(getString(R.string.recaptcha_question)))
            imageTask.setImageResource(R.drawable.ic_recaptcha);

        timePicker.setHour(Integer.parseInt(alarm.getAlarmTime().split(":")[0])); //show alarm time
        timePicker.setMinute(Integer.parseInt(alarm.getAlarmTime().split(":")[1])); //

        tvLabelInfo.setText(alarm.getLabel()); // show label

        tvRingtoneInfo.setText(alarm.getRingtoneName()); // show ringtone

        ringtoneUri = Uri.parse(alarm.getRingtoneUri());
        if (ringtoneUri.toString().split(":")[0].equals("spotify"))
            setSpotifyMusic(new AlarmItem(ringtoneUri.toString(), alarm.getRingtoneName()));
        else
            setRingtone(ringtoneUri);
    }

    // result from activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            switch(requestCode){
                case RINGTONE_REQUEST_CODE: {
                    ringtoneUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                    setRingtone(ringtoneUri);
                    break;
                }
                case SPOTIFY_REQUEST_CODE: {
                    AlarmItem alarmItem = (AlarmItem) data.getExtras().getSerializable("AlarmItem");
                    Log.d(TAG, String.format("AlarmItem == null? %b", alarmItem == null));
                    if (alarmItem != null) {
                        Log.d(TAG, alarmItem.getName());
                        setSpotifyMusic(alarmItem);
                    }
                    break;
                }
                case AlarmListActivity.SCAN_QR_CODE_INTENT_REQUEST_CODE: {
                    alarm.setQuestion(getString(R.string.qr_question));
                    alarm.setAnswer(data.getStringExtra("code"));
                    tvTask.setText(alarm.getQuestion());
                    imageTask.setImageResource(R.mipmap.qrcode_alarme);
                    break;
                }
                case AlarmListActivity.CHOOSE_TASK_REQUEST_CODE:{
                    taskSelected(data.getExtras().getString("task"));
                    tvTask.setText(alarm.getQuestion());
                }
                default:
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode== AlarmListActivity.CAMERA_PERMISSION_REQUEST_CODE) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(this, QRscanActivity.class);
                intent.putExtra("isSettingNewAlarm", true);
                startActivityForResult(intent, AlarmListActivity.SCAN_QR_CODE_INTENT_REQUEST_CODE);
            } else {
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
            }
            return;
        }
    }

    public void taskSelected(String selected) {
        if (selected.equals(getString(R.string.default_question))) {
            alarm.setQuestion(selected);
            alarm.setAnswer("default");
            imageTask.setImageResource(R.drawable.baseline_alarm_black_48);
        }
        else if (selected.equals(getString(R.string.qr_question))) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                System.out.println("permission!!");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                        AlarmListActivity.CAMERA_PERMISSION_REQUEST_CODE);
            }
            else {
                Intent intent = new Intent(this, QRscanActivity.class);
                intent.putExtra("isSettingNewAlarm", true);
                startActivityForResult(intent, AlarmListActivity.SCAN_QR_CODE_INTENT_REQUEST_CODE);
            }
        }
        else if (selected.equals(getString(R.string.math_question))) { //continue here
            alarm.setQuestion(selected);
            alarm.setAnswer("default");
            imageTask.setImageResource(R.drawable.ic_math);
        }
        else if (selected.equals(getString(R.string.recaptcha_question))) {
            alarm.setQuestion(selected);
            alarm.setAnswer("default");
            imageTask.setImageResource(R.drawable.ic_recaptcha);
        }
    }

    // set tvRingtoneInfo
    private void setRingtone(Uri uri) {
        Ringtone ringtone = RingtoneManager.getRingtone(this, uri);
        ringtoneName = ringtone.getTitle(this);

        tvRingtoneInfo.setText(ringtoneName);

        receiverIntent.putExtra("ringtoneUri", ringtoneUri.toString());

        alarm.setRingtoneUri(uri.toString());
        alarm.setRingtoneName(ringtoneName);
    }

    private void setSpotifyMusic(AlarmItem alarmItem) {
        ringtoneName = alarmItem.getName();

        tvRingtoneInfo.setText(ringtoneName);

        receiverIntent.putExtra("ringtoneUri", alarmItem.getTrackUri());

        alarm.setRingtoneUri(alarmItem.getTrackUri());
        alarm.setRingtoneName(ringtoneName);
    }

    public void chooseTaskClick(View view) {
        Intent intent = new Intent(this, ChooseTaskActivity.class);
        startActivityForResult(intent, AlarmListActivity.CHOOSE_TASK_REQUEST_CODE);
    }

    public void labelClick(View view) {
        final AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.label_dialog, null);

        final EditText editText = (EditText) dialogView.findViewById(R.id.editTextLabel);
        Button buttonSubmit = (Button) dialogView.findViewById(R.id.buttonSubmitLabel);
        Button buttonCancel = (Button) dialogView.findViewById(R.id.buttonCancelLabel);

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.dismiss();
            }
        });
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alarm.setLabel(editText.getText().toString());
                tvLabelInfo.setText(editText.getText().toString());
                dialogBuilder.dismiss();
            }
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }

    public void chooseRingtoneClick(View view) {
        Intent intent = new Intent(SetAlarmActivity.this, SearchSongActivity.class);
        startActivityForResult(intent, SPOTIFY_REQUEST_CODE);

//        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
//        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
//        startActivityForResult(intent, RINGTONE_REQUEST_CODE);
    }

    public void saveAlarmClick(View view) {
        //                calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
        calendar.set(Calendar.MINUTE, timePicker.getMinute());
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DATE, 1);
            Toast.makeText(SetAlarmActivity.this, "Delay for 1 day", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(SetAlarmActivity.this, "Alarm is set for " + timePicker.getHour()+":"+timePicker.getMinute(), Toast.LENGTH_SHORT).show();
        }

        alarm.setAlarmTime(timePicker.getHour()+":"+timePicker.getMinute());
        if (getIntent().getExtras().getBoolean("isNewAlarm")) {
            alarm.setFlag((int) (System.currentTimeMillis() / 1000));
        }
        alarm.setAlarmTimeInMillis(calendar.getTimeInMillis());

        // SET ALARM MANAGER
        receiverIntent.putExtra("question", alarm.getQuestion());
        receiverIntent.putExtra("answer", alarm.getAnswer());
        receiverIntent.putExtra("alarmTime", alarm.getAlarmTime());

        this.pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), alarm.getFlag(),
                receiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
//                alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

        /// send result to mainActivity
        Intent intentReturnToMain = new Intent();
        intentReturnToMain.putExtra("alarmObject", alarm);

        setResult(RESULT_OK, intentReturnToMain);

        finish();
    }

    public void cancelClick(View view) {
        finish();
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
//                        Log.e("AlarmListActivity", "Error setting image using Picasso");
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
