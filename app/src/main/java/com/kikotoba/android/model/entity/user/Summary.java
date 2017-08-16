package com.kikotoba.android.model.entity.user;

import com.google.firebase.crash.FirebaseCrash;
import com.kikotoba.android.model.entity.EntityUtil;

import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 */
public class Summary {

    private final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private String startAndroidAppVersionName = "";
    private String currentAndroidAppVersionName = "";

    private String lastOpenedAt = "";
    private String firstOpenedAt = "";

    public String getStartAndroidAppVersionName() {
        return startAndroidAppVersionName;
    }

    public void setStartAndroidAppVersionName(String startAndroidAppVersionName) {
        this.startAndroidAppVersionName = startAndroidAppVersionName;
    }

    public String getCurrentAndroidAppVersionName() {
        return currentAndroidAppVersionName;
    }

    public void setCurrentAndroidAppVersionName(String currentAndroidAppVersionName) {
        this.currentAndroidAppVersionName = currentAndroidAppVersionName;
    }

    public String getLastOpenedAt() {
        return lastOpenedAt;
    }

    public void setLastOpenedAt(String lastOpenedAt) {
        this.lastOpenedAt = lastOpenedAt;
    }

    public String getFirstOpenedAt() {
        return firstOpenedAt;
    }

    public void setFirstOpenedAt(String firstOpenedAt) {
        this.firstOpenedAt = firstOpenedAt;
    }

    public Calendar _getLastOpenAppDate() {
        if (StringUtils.isEmpty(lastOpenedAt)) {
            return null;
        }
        try {
            DateFormat df = new SimpleDateFormat(DATE_TIME_FORMAT);
            Date date = df.parse(lastOpenedAt);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            return cal;
        } catch (ParseException e) {
            FirebaseCrash.report(e);
            return null;
        }
    }

    public void _setLastOpenAppDate(Calendar calendar) {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        lastOpenedAt = sdfDate.format(calendar.getTime());
    }

    public Calendar _getFirstOpenedAt() {
        return EntityUtil.datetime2Calendar(firstOpenedAt);
    }

    public void _setFirstOpenedAt(Calendar firstOpenedAt) {
        this.firstOpenedAt = EntityUtil.calendar2Datetime(firstOpenedAt);
    }
}
