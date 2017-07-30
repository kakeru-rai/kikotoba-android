package com.kikotoba.android.model.entity.user;

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

    private String lastOpedAt = "";

    public String getStartAndroidAppVersionName() {
        return startAndroidAppVersionName;
    }

    public void setStartAndroidAppVersionName(String startAndroidAppVersionName) {
        this.startAndroidAppVersionName = startAndroidAppVersionName;
    }

    public String getLastOpedAt() {
        return lastOpedAt;
    }

    public void setLastOpedAt(String lastOpedAt) {
        this.lastOpedAt = lastOpedAt;
    }

    public Calendar _getLastOpenAppDate() {
        DateFormat df = new SimpleDateFormat(DATE_TIME_FORMAT);
        if (StringUtils.isEmpty(lastOpedAt)) {
            return null;
        }

        try {
            Date date = df.parse(lastOpedAt);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            return cal;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void _setLastOpenAppDate(Calendar calendar) {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        lastOpedAt = sdfDate.format(calendar.getTime());
    }
}
