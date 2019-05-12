package com.e15.alarmnats.ActivityController;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;
import com.spotify.sdk.android.player.Spotify;

public class AlarmFiredActivity extends AppCompatActivity
        implements ConnectionStateCallback, PlayerNotificationCallback {
//    private AlarmDbHelper dbHelper;
    private static final String TAG = "AlarmFiredActivity";

    private static final int REQUEST_CODE = 1337;
    private final String CLIENT_ID = "848eaa9b8e114c4eb674481982b0ed5a";
    private final String REDIRECT_URI = "alarmnats://callback";
    private int numOfAuthTries = 0;

    MediaPlayer mediaPlayer;
    private Player spotifyPlayer;
    private String ringtone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_fired);

        authSpotify();

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

        ringtone = getIntent().getExtras().getString("ringtone");

        Log.d("fired", ringtone);

        Uri ringtoneUri = Uri.parse(ringtone);
        if (ringtone.split(":")[0].equals("spotify")) {

        } else {
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
    // authorizes user towards Spotify, this is called if initPlayer fails to fetch the player
    public void authSpotify() {

        if(numOfAuthTries > 20) {
            Log.e(TAG, "Tried to auth too many times");
            return;
        }

        numOfAuthTries++;

        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(
                CLIENT_ID,
                AuthenticationResponse.Type.TOKEN,
                REDIRECT_URI);

        builder.setScopes(new String[]{"streaming"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        if (resultCode == RESULT_OK) {
            if (requestCode == MainActivity.SCAN_QR_CODE_INTENT_REQUEST_CODE
                    || requestCode == MainActivity.MATH_TEST_INTENT_REQUEST_CODE
                    || requestCode == MainActivity.VERIFY_CAPTCHA_INTENT_REQUEST_CODE) {
                finishActivity();
            } else if (requestCode == REQUEST_CODE) {

                AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);

                if (response.getType() == AuthenticationResponse.Type.TOKEN) {

                    Config config = new Config(this, response.getAccessToken(), CLIENT_ID);

                    Spotify.getPlayer(config, AlarmFiredActivity.this, new Player.InitializationObserver() {
                        @Override
                        public void onInitialized(Player player) {
                            Log.d(TAG, "onInitialized");

                            spotifyPlayer = player;
                            spotifyPlayer.addConnectionStateCallback(AlarmFiredActivity.this);
                            spotifyPlayer.addPlayerNotificationCallback(AlarmFiredActivity.this);
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            Log.e(TAG, "Could not initialize player: " + throwable.getMessage());

                            // saves error logs for debugging
//                            SharedPreferences debugPrefs =
//                                    AlarmFiredActivity.this.getSharedPreferences(TAG, Context.MODE_PRIVATE);
//                            debugPrefs.edit()
//                                    .putString(getString(R.string.tag_debug_onError), throwable.getMessage())
//                                    .apply();


                            // Retries to auth Spotify
                            authSpotify();
                        }
                    });

                }
            }
        }
    }

    protected void onBtnDismissClick(View view) {
        finishActivity();
    }

    private void finishActivity() {
        if (mediaPlayer != null)
            mediaPlayer.stop();
        spotifyPlayer.pause();
        finish();
    }

    @Override
    public void onLoggedIn() {
        // Plays song as soon as auth is done and user is logged in
        spotifyPlayer.play(ringtone);
    }

    @Override
    public void onLoggedOut() {

    }

    @Override
    public void onLoginFailed(Throwable throwable) {
        Log.e(TAG, "onLoginFailed: " + throwable.getMessage());
        authSpotify();
    }

    @Override
    public void onTemporaryError() {
        Log.e(TAG, "onTemporaryError: " + System.currentTimeMillis()+"");
        Log.d(TAG, "retrying to auth..");
        authSpotify();
    }

    @Override
    public void onConnectionMessage(String s) {

    }

    @Override
    public void onPlaybackEvent(EventType eventType, PlayerState playerState) {

    }

    @Override
    public void onPlaybackError(ErrorType errorType, String s) {

    }
}
