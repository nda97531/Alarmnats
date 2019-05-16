package com.e15.alarmnats.ActivityController;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.e15.alarmnats.R;

public class ChooseRingToneActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_ring_tone);
    }

    protected void chooseSpotifyRingtone(View v){
        Intent returnIntent = new Intent();
        returnIntent.putExtra("ringtoneType", "spotify");
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    protected void chooseSystemRingtone(View v){
        Intent returnIntent = new Intent();
        returnIntent.putExtra("ringtoneType", "system");
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}
