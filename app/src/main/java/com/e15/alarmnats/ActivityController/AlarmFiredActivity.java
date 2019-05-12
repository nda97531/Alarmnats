package com.e15.alarmnats.ActivityController;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.e15.alarmnats.MainActivity;
import com.e15.alarmnats.R;

public class AlarmFiredActivity extends AppCompatActivity {
//    private AlarmDbHelper dbHelper;

    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_fired);

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);

//        dbHelper = AlarmDbHelper.getInstance(this);

        String ringtone = getIntent().getExtras().getString("ringtone");

        Log.d("fired", ringtone);

        Uri ringtoneUri = Uri.parse(ringtone);
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(this, ringtoneUri);
            final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                mediaPlayer.setLooping(true);
                mediaPlayer.prepare();
                mediaPlayer.start();
            }
        } catch (Exception e) {
            Log.e("media exception", e.toString());
        }

        String question = getIntent().getExtras().getString("question");
        String answer = getIntent().getExtras().getString("answer");

        if (question.equals(getString(R.string.qr_question))) {
            Intent intent = new Intent(this, QRtestActivity.class);
            intent.putExtra("answer", answer);
            startActivityForResult(intent, MainActivity.SCAN_QR_CODE_INTENT_REQUEST_CODE);
        } else if (question.equals(getString(R.string.math_question))) {
            Intent intent = new Intent(this, MathTestActivity.class);
            startActivityForResult(intent, MainActivity.MATH_TEST_INTENT_REQUEST_CODE);
        } else if (question.equals(getString(R.string.verify_recaptcha))) {
            Intent intent = new Intent(this, RecaptchaActivity.class);
            startActivityForResult(intent, MainActivity.VERIFY_CAPTCHA_INTENT_REQUEST_CODE);
        } else {
            // Default
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == MainActivity.SCAN_QR_CODE_INTENT_REQUEST_CODE
                    || requestCode == MainActivity.MATH_TEST_INTENT_REQUEST_CODE
                    || requestCode == MainActivity.VERIFY_CAPTCHA_INTENT_REQUEST_CODE) {
                finishActivity();
            }
        }
    }

    protected void onBtnDismissClick(View view) {
        finishActivity();
    }

    private void finishActivity() {
        mediaPlayer.stop();
        finish();
    }
}
