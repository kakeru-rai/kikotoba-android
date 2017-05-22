package com.kikotoba.android.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JVersatile {

	/**
	 * 素数判定
	 * @param value
	 * @return 素数ならtrue
	 */
	public static boolean isPrimeNumber(Integer value) {
		if (value == 0) {
			return false;
		} else if (value == 1) {
			return true;
		}

		int divisor = 0;
		for (int i = 2; i <= value; i++) {
			if (value % i == 0) {
				divisor = i;
				break;
			}
		}

		return divisor == value;
	}

	/**
	 * minからmaxまでの整数をランダムで返す
	 *
	 * @param min
	 * @param max
	 * @return
	 */
	public static int getRandomInt(int min, int max) {
		return (int) Math.floor(Math.random() * (max - min + 1) + min);
	}

	public static int[] toArray(List<Integer> list) {
		int[] ret = new int[list.size()];
		for (int i = 0; i < ret.length; i++)
			ret[i] = list.get(i);
		return ret;
	}

	public static Object reflectMethodInstance(Object object, String methodName) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Method method = object.getClass().getMethod(methodName, new Class[] {});
		return method.invoke(object, new Object[] {});
	}

	public static Object reflectMethodInstance(Object object, String methodName, Class[] argClasses, Object[] args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
		Method method;
		method = object.getClass().getMethod(methodName, argClasses);
		return method.invoke(object, args);
	}

	public static Object reflectMethodStatic(Class clazz, String methodName) throws Exception {
		Method method = null;
		Object ret = null;
		Exception ex = null;

		try {
			method = clazz.getDeclaredMethod(methodName, new Class[]{});
			method.setAccessible(true);
			ret = method.invoke(null);
		} catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			ex = e;
			e.printStackTrace();
		}

		if (ex != null) {
			throw ex;
		}

		return ret;

	}
	public static Object reflectMethodStatic(Class clazz, String methodName, Class[] argClasses, Object[] args) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Method method = clazz.getDeclaredMethod(methodName, argClasses);
		method.setAccessible(true);
		return method.invoke(null, args);
	}

	public static Object reflectNewInstance(Class clazz) throws InstantiationException, IllegalAccessException {
		return clazz.newInstance();
	}

	public static Object reflectNewInstance(Class clazz, Class[] argClasses, Object[] args) throws NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Constructor constructor = clazz.getConstructor(argClasses);
		return constructor.newInstance(args);
	}

	public static void sleep(long msec) throws InterruptedException {
		Thread.sleep(msec);
	}

	public static String regExpReplaceAll(String src, String regExp, String replacement) {
		Pattern p = Pattern.compile(regExp);
		Matcher m = p.matcher(src);
		return m.replaceAll(replacement);
	}

	public static Matcher regExp(String src, String regExp) {
		Pattern p = Pattern.compile(regExp);
		Matcher m = p.matcher(src);
		return m;
	}

	/**
	 * DBのdatetimeフォーマットとCalendarの変換
	 * @param cal
	 * @return yyyy-mm-dd hh:ii:ss
	 */
	public static String convertCalToStrDateTime(Calendar cal) {
		return String.format("%04d-%02d-%02d %02d:%02d:%02d",
				cal.get(Calendar.YEAR),
				cal.get(Calendar.MONTH) + 1,
				cal.get(Calendar.DAY_OF_MONTH),
				cal.get(Calendar.HOUR_OF_DAY),
				cal.get(Calendar.MINUTE),
				cal.get(Calendar.SECOND)
				);
	}

	/**
	 * DBのdatetimeフォーマットとCalendarの変換
	 * @param cal
	 * @return yyyy-mm-dd hh:ii:ss
	 */
	public static String convertCalToStrDate(Calendar cal) {
		return String.format("%04d-%02d-%02d",
				cal.get(Calendar.YEAR),
				cal.get(Calendar.MONTH) + 1,
				cal.get(Calendar.DAY_OF_MONTH)
				);
	}

	/**
	 * DBのdatetimeフォーマットとCalendarの変換
	 * @param datetime yyyy-mm-dd hh:ii:ss
	 * @return
	 */
	public static Calendar convertStrToCalDateTime(String datetime) throws NumberFormatException{
		String regExp = "([0-9]{4,4})-([0-9]{2,2})-([0-9]{2,2}) ([0-9]{2,2}):([0-9]{2,2}):([0-9]{2,2})";
		Matcher matcher = JVersatile.regExp(datetime, regExp);

		if (!matcher.matches()) {
			throw new IllegalArgumentException("フォーマット不正：" + datetime);
		}

		Calendar cal = Calendar.getInstance();
		resetCalendar(cal);
		int y = Integer.valueOf(matcher.group(1)).intValue();
		int m = Integer.valueOf(matcher.group(2)).intValue() - 1;
		int d = Integer.valueOf(matcher.group(3)).intValue();
		int h = Integer.valueOf(matcher.group(4)).intValue();
		int i = Integer.valueOf(matcher.group(5)).intValue();
		int s = Integer.valueOf(matcher.group(6)).intValue();
		cal.set(y, m, d, h, i, s);
		return cal;
	}

	/**
	 * DBのdateフォーマットとCalendarの変換
	 * @param date yyyy-mm-dd
	 * @return
	 */
	public static Calendar convertStrToCalDate(String date) throws NumberFormatException{
		String regExp = "([0-9]{4,4})-([0-9]{2,2})-([0-9]{2,2})";
		Matcher matcher = JVersatile.regExp(date, regExp);

		if (!matcher.matches()) {
			throw new IllegalArgumentException("フォーマット不正：" + date);
		}

		Calendar cal = Calendar.getInstance();
		resetCalendar(cal);
		int y = Integer.valueOf(matcher.group(1)).intValue();
		int m = Integer.valueOf(matcher.group(2)).intValue() - 1;
		int d = Integer.valueOf(matcher.group(3)).intValue();
		cal.set(y, m, d);
		return cal;
	}

	public static void resetCalendar(Calendar cal) {
		cal.set(0, 0, 0, 0, 0, 0);
		cal.set(Calendar.MILLISECOND, 0);
	}

	public static List<Integer> array2List(int[] array) {
		List<Integer> list = new ArrayList<Integer>();
	    for (int index = 0; index < array.length; index++)
	    {
	        list.add(array[index]);
	    }
	    return list;
	}

	public static List<String> array2List(String[] array) {
		List<String> list = new ArrayList<String>();
	    for (int index = 0; index < array.length; index++)
	    {
	        list.add(array[index]);
	    }
	    return list;
	}

	public static String convertInputStreamToString(InputStream is) throws IOException {
		InputStreamReader reader = new InputStreamReader(is);
		StringBuilder builder = new StringBuilder();
		char[] buf = new char[1024];
		int numRead;
		while (0 <= (numRead = reader.read(buf))) {
			builder.append(buf, 0, numRead);
		}
		return builder.toString();
	}
	
	/**
	 *
	 * @param text
	 * @param encoding ex) utf-8
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static InputStream textToStream(String text, String encoding) throws UnsupportedEncodingException {
		return new ByteArrayInputStream(text.getBytes(encoding));
	}

}
