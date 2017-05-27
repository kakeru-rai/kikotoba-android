package com.kikotoba.android;

import com.kikotoba.android.util.JVersatile;
import com.kikotoba.android.util.Util;

import org.junit.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class JVersatileTest {
    @Test
    public void escapeJsArgumentFromUrl() throws Exception {
        assertEquals("\\'", Util.escapeJsArgumentFromUrl("'"));
        assertEquals("\\\"", Util.escapeJsArgumentFromUrl("\""));
    }

    @Test
    public void isPrimeNumber() throws Exception {
        assertEquals(false, JVersatile.isPrimeNumber(0));
        assertEquals(true,  JVersatile.isPrimeNumber(1));
        assertEquals(true,  JVersatile.isPrimeNumber(2));
        assertEquals(true,  JVersatile.isPrimeNumber(3));
        assertEquals(false, JVersatile.isPrimeNumber(4));
        assertEquals(true,  JVersatile.isPrimeNumber(5));
        assertEquals(false, JVersatile.isPrimeNumber(6));
        assertEquals(true,  JVersatile.isPrimeNumber(7));
        assertEquals(false, JVersatile.isPrimeNumber(8));
        assertEquals(false, JVersatile.isPrimeNumber(9));
        assertEquals(false, JVersatile.isPrimeNumber(10));
        assertEquals(true, JVersatile.isPrimeNumber(11));
    }

    @Test
    public void asdf() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.setTime(new Date(10 * 1000));
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        df.setTimeZone(cal.getTimeZone());
        assertEquals("00:00:10", df.format(cal.getTime()));

//        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
//        cal.setTime(new Date(10 * 1000));
//        cal.setTimeZone(TimeZone.getTimeZone("UTC"));
//        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
//        assertEquals("00:00:10", df.format(cal.getTime()));

    }
}