package com.kikotoba.android.model.entity;

import com.google.firebase.crash.FirebaseCrash;

import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by raix on 2017/08/16.
 */
public class EntityUtil {
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static Calendar datetime2Calendar(String datetime) {
        if (StringUtils.isEmpty(datetime)) {
            return null;
        }
        try {
            DateFormat df = new SimpleDateFormat(DATE_TIME_FORMAT);
            Date date = df.parse(datetime);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            return cal;
        } catch (ParseException e) {
            FirebaseCrash.report(e);
            return null;
        }
    }

    public static String calendar2Datetime(Calendar calendar) {
        SimpleDateFormat sdfDate = new SimpleDateFormat(DATE_TIME_FORMAT);
        return sdfDate.format(calendar.getTime());
    }

}
