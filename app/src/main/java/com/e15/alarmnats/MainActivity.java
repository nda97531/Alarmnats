package com.e15.alarmnats;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.e15.alarmnats.ActivityController.AlarmListActivity;
import com.e15.alarmnats.ActivityController.WeatherActivity;
import com.e15.alarmnats.ViewSupport.MyPagerAdapter;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AlarmListActivity fragmentOne = new AlarmListActivity();
        WeatherActivity fragmentTwo= new WeatherActivity();

        MyPagerAdapter pagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(fragmentOne);
        pagerAdapter.addFragment(fragmentTwo);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(pagerAdapter);
    }
}