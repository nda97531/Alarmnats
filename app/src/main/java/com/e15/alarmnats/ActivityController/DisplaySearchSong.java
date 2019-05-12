package com.e15.alarmnats.ActivityController;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;

import com.e15.alarmnats.R;

public class DisplaySearchSong extends AppCompatActivity {

    private static final int CONTENT_VIEW_ID = 10101010;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_search_song);
        AddFragment addFragment = new AddFragment();
        getFragmentManager().beginTransaction()
                .add(R.id.fragment_container, addFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack("addFragment")
                .commit();
    }

}
