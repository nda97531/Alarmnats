package com.e15.alarmnats.ActivityController;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.e15.alarmnats.Model.AlarmItem;
import com.e15.alarmnats.R;
import com.e15.alarmnats.ViewSupport.AddAlarmFragment;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

public class SearchSongActivity extends AppCompatActivity implements AddAlarmFragment.AddFragmentListener {
    private static final int CONTENT_VIEW_ID = 10101010;

    private static final int REQUEST_CODE = 1337;
    private final String CLIENT_ID = "848eaa9b8e114c4eb674481982b0ed5a";
    private final String REDIRECT_URI = "alarmnats://callback";

    private AlarmItem alarmItem;
    private String token;

    public void authSpotify() {

        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(
                CLIENT_ID,
                AuthenticationResponse.Type.TOKEN,
                REDIRECT_URI);

        builder.setScopes(new String[] {"streaming"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);

            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                token = response.getAccessToken();

                // saves auth token locally for use in AlarmActivity
                SharedPreferences prefs = this.getSharedPreferences(getString(R.string.tag_sharedprefs), Context.MODE_PRIVATE);
                prefs.edit()
                        .putString(getString(R.string.tag_sharedpref_token), token)
                        .apply();

            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_song);
        authSpotify();

        AddAlarmFragment addFragment = new AddAlarmFragment();
        getFragmentManager().beginTransaction()
                .add(R.id.fragment_container, addFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack("addFragment")
                .commit();
    }

    @Override
    public void saveClicked(AlarmItem item) {
        alarmItem = item;
        Intent intent = new Intent();
        intent.putExtra("AlarmItem", alarmItem);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void deleteClicked(AlarmItem item) {

    }

    @Override
    public void updateAlarm(AlarmItem oldAlarm, AlarmItem newAlarm) {

    }
}
