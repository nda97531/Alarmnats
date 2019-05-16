package com.e15.alarmnats.ViewSupport;

import com.e15.alarmnats.R;

public enum ModelObject {

    ALARM_CLOCK(1, R.layout.activity_alarm_list),
    WEATHER(2, R.layout.activity_weather);

    private int mTitleResId;
    private int mLayoutResId;

    ModelObject(int titleResId, int layoutResId) {
        mTitleResId = titleResId;
        mLayoutResId = layoutResId;
    }

    public int getTitleResId() {
        return mTitleResId;
    }

    public int getLayoutResId() {
        return mLayoutResId;
    }

}