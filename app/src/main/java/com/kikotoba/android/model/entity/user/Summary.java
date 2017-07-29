package com.kikotoba.android.model.entity.user;

import java.util.Calendar;

/**
 */
public class Summary {

    private String startAndroidAppVersionName = "";

    private long lastOpenAppDate = 0;

    public String getStartAndroidAppVersionName() {
        return startAndroidAppVersionName;
    }

    public void setStartAndroidAppVersionName(String startAndroidAppVersionName) {
        this.startAndroidAppVersionName = startAndroidAppVersionName;
    }

    public long getLastOpenAppDate() {
        return lastOpenAppDate;
    }

    public void setLastOpenAppDate(long lastOpenAppDate) {
        this.lastOpenAppDate = lastOpenAppDate;
    }

    public Calendar _getLastOpenAppDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(lastOpenAppDate);
        return calendar;
    }

    public void _setLastOpenAppDate(Calendar calendar) {
        lastOpenAppDate = calendar.getTimeInMillis();
    }
}
