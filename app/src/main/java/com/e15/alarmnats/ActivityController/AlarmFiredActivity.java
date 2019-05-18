package com.e15.alarmnats.ActivityController;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

import java.net.InetAddress;

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
    private String ringtone, question, answer, label;
    Uri ringtoneUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_fired);

        question = getIntent().getExtras().getString("question");
        answer = getIntent().getExtras().getString("answer");
        label = getIntent().getExtras().getString("label");

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

        ringtoneUri = Uri.parse(ringtone);
        if (ringtone.split(":")[0].equals("spotify")) {
            checkAvailable("spotify.com");
        } else {
            systemRing();
        }
    }

    private void systemRing(){
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

            startQuestion();

        } catch (Exception e) {
            Log.e("media exception", e.toString());
        }
    }

    private void checkAvailable(final String url){
        final Task checkIntenet = new Task();
        checkIntenet.execute(url);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable()
        {
            @Override
            public void run() {
                if ( checkIntenet.getStatus() == AsyncTask.Status.RUNNING )
                    checkIntenet.onPostExecute(url+"_no");
            }
        }, 1000 );
    }

    private void startQuestion(){
        if (question.equals(getString(R.string.qr_question))) {
            Intent intent = new Intent(this, QRscanActivity.class);
            intent.putExtra("isSettingNewAlarm", false);
            intent.putExtra("answer", answer);
            intent.putExtra("label", label);
            startActivityForResult(intent, AlarmListActivity.SCAN_QR_CODE_INTENT_REQUEST_CODE);
        } else if (question.equals(getString(R.string.math_question))) {
            Intent intent = new Intent(this, MathTestActivity.class);
            intent.putExtra("label", label);
            startActivityForResult(intent, AlarmListActivity.MATH_TEST_INTENT_REQUEST_CODE);
        } else if (question.equals(getString(R.string.recaptcha_question))) {
            checkAvailable("google.com");
        } else {
            // Default
            TextView textView = findViewById(R.id.defaultMsg);
            textView.setText(label);

            Button dismissBtn = (Button) findViewById(R.id.button_dismiss);
            dismissBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finishActivity();
                }
            });
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
            if (requestCode == REQUEST_CODE) {

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
                            System.out.println("auth complete");
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            Log.e(TAG, "Could not initialize player: " + throwable.getMessage());
                            authSpotify();
                        }
                    });

                }
            }
            else if (requestCode == AlarmListActivity.SCAN_QR_CODE_INTENT_REQUEST_CODE
                    || requestCode == AlarmListActivity.MATH_TEST_INTENT_REQUEST_CODE
                    || requestCode == AlarmListActivity.VERIFY_CAPTCHA_INTENT_REQUEST_CODE) {
                finishActivity();
            }
        }
        else startQuestion();
    }

    private void finishActivity() {
        if (mediaPlayer != null)
            mediaPlayer.stop();
        if (spotifyPlayer != null)
            spotifyPlayer.pause();

        finish();
    }

    @Override
    public void onLoggedIn() {
        // Plays song as soon as auth is done and user is logged in
        System.out.println("now playing");
        spotifyPlayer.play(ringtone);
        startQuestion();
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

    public class Task extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            System.out.println("start my task");
            String result;
            try {
                InetAddress ipAddr = InetAddress.getByName(params[0]);
                if (!ipAddr.equals("")) {
                    result = params[0]+"_yes";
                } else {
                    result = params[0]+"_no";
                }
            } catch (Exception e) {
                result = params[0]+"_no";
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            switch (result){
                case "spotify.com_yes":{
                    System.out.println("yes spotify");
                    authSpotify();
                    break;
                }
                case "spotify.com_no":{
                    System.out.println("no spotify");
                    ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                    systemRing();
                    break;
                }
                case "google.com_yes":{
                    System.out.println("yes google");
                    Intent intent = new Intent(AlarmFiredActivity.this, RecaptchaActivity.class);
                    intent.putExtra("label", label);
                    startActivityForResult(intent, AlarmListActivity.VERIFY_CAPTCHA_INTENT_REQUEST_CODE);
                    break;
                }
                case "google.com_no":{
                    System.out.println("no google");
                    // Default
                    TextView textView = findViewById(R.id.defaultMsg);
                    textView.setText(label);

                    Button dismissBtn = (Button) findViewById(R.id.button_dismiss);
                    dismissBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            finishActivity();
                        }
                    });
                    break;
                }
                default:{
                    throw new NumberFormatException();
                }
            }
        }
    }
}
