package com.e15.alarmnats;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.e15.alarmnats.ActivityController.AlarmListActivity;
import com.e15.alarmnats.ActivityController.WeatherActivity;
import com.e15.alarmnats.ViewSupport.MyPagerAdapter;

public class MainActivity extends AppCompatActivity {
    ImageView alarm_function, weather_function;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle(R.string.app_name);

        alarm_function = findViewById(R.id.imgAlarmFunction);
        weather_function = findViewById(R.id.imgWeatherFunction);

        AlarmListActivity fragmentOne = new AlarmListActivity();
        WeatherActivity fragmentTwo= new WeatherActivity();

        MyPagerAdapter pagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(fragmentOne);
        pagerAdapter.addFragment(fragmentTwo);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position==0){
                    alarm_function.setImageResource(R.drawable.ic_alarm_choose);
                    alarm_function.setScaleX(1);
                    alarm_function.setScaleY(1);

                    weather_function.setImageResource(R.drawable.ic_weather_not_choose);
                    weather_function.setScaleX((float)0.7);
                    weather_function.setScaleY((float)0.7);
                }
                else {
                    alarm_function.setImageResource(R.drawable.ic_alarm_not_choose);
                    alarm_function.setScaleX((float)0.7);
                    alarm_function.setScaleY((float)0.7);

                    weather_function.setImageResource(R.drawable.ic_weather_choose);
                    weather_function.setScaleX(1);
                    weather_function.setScaleY(1);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        alarm_function.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(0, true);
            }
        });

        weather_function.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(1, true);
            }
        });
    }
}