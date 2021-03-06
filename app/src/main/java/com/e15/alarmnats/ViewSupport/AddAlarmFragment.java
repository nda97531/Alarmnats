package com.e15.alarmnats.ViewSupport;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.graphics.Palette;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.e15.alarmnats.ActivityController.SearchSongActivity;
import com.e15.alarmnats.Model.AlarmItem;
import com.e15.alarmnats.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AddAlarmFragment extends Fragment implements Response.Listener<String>, Response.ErrorListener {

    private static final String TAG = "AddAlarmFragment";
    private boolean editing;

    private RelativeLayout background;
    private AutoCompleteTextView trackField;
    private TextView timeText;
    private ImageView preview;
    private ImageView albumImage;

    private boolean itemClicked = false;

    private AlarmItem alarmItem = new AlarmItem("", "", "", "", 6, 0, true, (int) System.currentTimeMillis());
    private AlarmItem oldAlarmItem;

    private ArrayAdapter<String> searchAdapter;
    private List<AlarmItem> searchResultsItems = new ArrayList<>();
    private List<String> stringResults = new ArrayList<>();

    public interface AddFragmentListener {
        void saveClicked(AlarmItem item);

        void deleteClicked(AlarmItem item);

        void updateAlarm(AlarmItem oldAlarm, AlarmItem newAlarm);
    }

    private AddFragmentListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context.getClass() == SearchSongActivity.class) {
            Log.d("SearchSongActivity", "Correct class");
            listener = (AddFragmentListener) context;
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_alarm, container, false);
        initUI(view);

        return view;
    }

    private void initUI(View view) {
        albumImage = (ImageView) view.findViewById(R.id.album_image);
        Button saveButton = (Button) view.findViewById(R.id.addBtn);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view17) {

                // if no track has been set yet, do nothing
                if (alarmItem.getTrackUri().equals("") && !editing)
                    return;

                // if editing, remove the old alarm
                if (editing) {
                    listener.updateAlarm(oldAlarmItem, alarmItem);
                } else {
                    listener.saveClicked(alarmItem);
                }

                AddAlarmFragment.this.exitFragment();
            }
        });

        background = (RelativeLayout) view.findViewById(R.id.add_background);

        searchAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_dropdown_item_1line,
                stringResults);

        trackField = (AutoCompleteTextView) view.findViewById(R.id.track_field);
        trackField.setAdapter(searchAdapter);

        if (editing) {
            updateAlbumArt(oldAlarmItem.getImageUrl());
            trackField.setText(oldAlarmItem.getArtist() + " - " + oldAlarmItem.getName());
            timeText.setText(oldAlarmItem.getFormatedTime());
            saveButton.setText("Save alarm");
        }

        // when users enters text, suggest new songs..
        trackField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {

                // if the change is due to the user clicking a suggested song, don't suggest more

                if (itemClicked) {
                    itemClicked = false;
                    return;
                }
                Log.d(TAG, "afterTextChanged");
                // when users enters another character..
                updateSongs(trackField.getText().toString());
            }
        });

        // when a user clicks a selected track in from search suggestions, load that into alarmItem
        trackField.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view12, int i, long l) {

                // stops suggesting
                itemClicked = true;
                AlarmItem searchItem = searchResultsItems.get(i);

                // updates alarmItem with attributes from search item
                alarmItem.setName(searchItem.getName());
                alarmItem.setArtist(searchItem.getArtist());
                alarmItem.setImageUrl(searchItem.getImageUrl());
                alarmItem.setTrackUri(searchItem.getTrackUri());

                try {
                    alarmItem.jsonify(); // updates json in alarmItem
                } catch (JSONException e) {
                    Log.e("AlarmListActivity", "error converting into json");
                    e.printStackTrace();
                }

                // updates UI
                trackField.setText(alarmItem.getArtist() + " - " + alarmItem.getName());
                AddAlarmFragment.this.updateAlbumArt(alarmItem.getImageUrl());
                preview.setVisibility(View.VISIBLE);

                // hides keybaord
                AddAlarmFragment.this.hideKeyboard();

            }
        });

        // when a users selects a track from clicking enter on keyboard
        trackField.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view1, int keyCode, KeyEvent keyEvent) {


                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    itemClicked = true;

                    // Perform search on enter press
                    AddAlarmFragment.this.searchTrack(trackField.getText().toString().replaceAll(" ", "+"));
                    AddAlarmFragment.this.hideKeyboard();

                    return true;
                }

                return false;
            }
        });

        preview = (ImageView) view.findViewById(R.id.preview);

    }

    public void cancelDeleteClicked() {
        exitFragment();
    }

    public void deleteButtonClicked() {
        listener.deleteClicked(oldAlarmItem);
        exitFragment();
    }

    // searches for track with http GET
//    @RequiresApi(api = Build.VERSION_CODES.M)
    public void searchTrack(String trackName) {


        // if trackname is too short, do nothing
        if (trackName.length() < 3)
            return;

        String url = "https://api.spotify.com/v1/search?q=" + trackName + "&type=track&limit=1";

        RequestQueue queue = Volley.newRequestQueue(getContext());

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        AddAlarmFragment.this.setTrackFromTitle(response);
                    }
                },
                this) {
            @Override
            public Map<String, String> getHeaders() {

                SharedPreferences prefs = getContext().getSharedPreferences(getString(R.string.tag_sharedprefs), Context.MODE_PRIVATE);
                String token = prefs.getString(getString(R.string.tag_sharedpref_token), "");

                Map<String, String> params = new HashMap<>();
                params.put("Authorization", "Bearer " + token);
                return params;
            }
        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public List<String> updateSongs(String input) {

        final List<String> songs = new ArrayList<>();

        // if input is too short, return empty array
        if (input.length() < 3)
            return songs;

        String url = "https://api.spotify.com/v1/search?q=" + input.replaceAll(" ", "+") + "&type=track&limit=3";

        SharedPreferences prefs = getContext().getSharedPreferences(getString(R.string.tag_sharedprefs), Context.MODE_PRIVATE);
        String token = prefs.getString(getString(R.string.tag_sharedpref_token), "");

        Log.d("AlarmListActivity", "url: " + url);
        Log.d("AlarmListActivity", "token: " + token);

        RequestQueue queue = Volley.newRequestQueue(getContext());

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, this, this) {
            @Override
            public Map<String, String> getHeaders() {

                SharedPreferences prefs = getContext().getSharedPreferences(getString(R.string.tag_sharedprefs), Context.MODE_PRIVATE);
                String token = prefs.getString(getString(R.string.tag_sharedpref_token), "");

                Map<String, String> params = new HashMap<>();
                params.put("Authorization", "Bearer " + token);
                return params;
            }
        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);

        return songs;
    }

    @Override
    public void onResponse(String response) {

        try {
            JSONObject reader = new JSONObject(response);
            JSONObject tracks = reader.getJSONObject("tracks");
            JSONArray items = tracks.getJSONArray("items");

            searchResultsItems.clear();
            stringResults.clear();

            for (int i = 0; i < items.length(); i++) {
                JSONObject result = items.getJSONObject(i);
                String uri = result.getString("uri");
                String name = result.getString("name");
                String artist = result.getJSONArray("artists").getJSONObject(0).getString("name");
                String imageUrl = result.getJSONObject("album").getJSONArray("images").getJSONObject(0).getString("url");

                // creating a minimum AlarmItem for storing uri, image, name and artist..
                // the rest of the attributes will be set separately
                AlarmItem item = new AlarmItem(uri, imageUrl, name, artist,
                        0,
                        0,
                        false,
                        0);

                stringResults.add(String.format("%s - %s", artist, name));
                searchResultsItems.add(item);
            }

            try {
                searchAdapter = new ArrayAdapter<>(getContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        stringResults);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            trackField.setAdapter(searchAdapter);

            searchAdapter.notifyDataSetChanged();


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    // resets alarm and returns to homefragment
    public void exitFragment() {
        alarmItem = new AlarmItem("", "", "", "", 6, 0, true, (int) System.currentTimeMillis());
        getFragmentManager().popBackStack();
    }

    public void setTrackFromTitle(String title) {

        try {
            JSONObject reader = new JSONObject(title);
            JSONObject tracks = reader.getJSONObject("tracks");
            JSONArray items = tracks.getJSONArray("items");
            JSONObject result = items.getJSONObject(0);
            String uri = result.getString("uri");
            String name = result.getString("name");
            String artist = items.getJSONObject(0).getJSONArray("artists").getJSONObject(0).getString("name");
            String imageUrl = result.getJSONObject("album").getJSONArray("images").getJSONObject(0).getString("url");

            // updates UI
            trackField.setText(artist + " - " + name);

            // updates album art
            updateAlbumArt(imageUrl);

            preview.setVisibility(View.VISIBLE);

            // updates alarmItem
            alarmItem.setArtist(artist);
            alarmItem.setName(name);
            alarmItem.setTrackUri(uri);
            alarmItem.setImageUrl(imageUrl);

            try {
                alarmItem.jsonify(); // updates json in alarmItem
            } catch (JSONException e) {
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates album art and updates background color gradient based on image
     *
     * @param imageUrl image url to load as album art
     */
    public void updateAlbumArt(String imageUrl) {

        // setting thumbnail ..
        Picasso.with(getContext())
                .load(imageUrl)
                .fit()
                .centerCrop()
                .into(albumImage, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        updateBackgroundColor();
                    }

                    @Override
                    public void onError() {
                        Log.e("AlarmListActivity", "Error setting image using Picasso");
                    }
                });
    }

    /**
     * Updates the background gradient based on album art
     */
    public void updateBackgroundColor() {
        Palette.PaletteAsyncListener paletteListener = new Palette.PaletteAsyncListener() {
            public void onGenerated(Palette p) {

                int d = 0x0E0E0E;

                GradientDrawable gd = new GradientDrawable(
                        GradientDrawable.Orientation.TOP_BOTTOM,
                        new int[]{p.getDarkMutedColor(d), d});

                Drawable[] grads = {background.getBackground(), gd};

                TransitionDrawable transitionDrawable = new TransitionDrawable(grads);
                background.setBackground(transitionDrawable);
                transitionDrawable.startTransition(500);

            }
        };

        Bitmap bitmap = ((BitmapDrawable) albumImage.getDrawable()).getBitmap();
        Palette.from(bitmap).generate(paletteListener);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Log.e("AlarmListActivity", error.toString());
//        Toast.makeText(, "Lỗi mạng!", Toast.LENGTH_SHORT).show();

    }

    public void setOldAlarmItem(AlarmItem oldAlarmItem) {
        this.oldAlarmItem = oldAlarmItem;
    }

    public void setAlarmItem(AlarmItem alarmItem) {
        this.alarmItem = alarmItem;
    }

    public void setEditing() {
        this.editing = true;
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        private TextView timeText;
        private AlarmItem alarmItem;

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), R.style.Theme_AppCompat_Light_Dialog, this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hour, int minute) {

            // updates alarmItem
            alarmItem.setHour(hour);
            alarmItem.setMinute(minute);

            timeText.setText(alarmItem.getFormatedTime());

            try {
                alarmItem.jsonify(); // updates json in alarmItem
            } catch (JSONException e) {
                Log.e("AlarmListActivity", "error converting into json");
                e.printStackTrace();
            }
        }

        public void setTimeText(TextView timeText) {
            this.timeText = timeText;
        }

        public void setAlarmItem(AlarmItem alarmItem) {
            this.alarmItem = alarmItem;
        }

    }
}
