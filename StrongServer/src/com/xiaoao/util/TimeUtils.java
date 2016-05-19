package com.xiaoao.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import com.mysql.jdbc.log.Log;

public class TimeUtils
{
	
	// ************************************************************************
	// fields
	
	public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static final String SIMPETIME_FORMAT = "yyyy-MM-dd";
	public static final String SIMPETIME_FORMAT2 = "yyyyMMdd";
	
	// ************************************************************************
	// public interface
	
	// ------------------------------------------------------------------------
	public static String GetCurrentTimeStamp()
	{
		// note : don't change date format - it is used for posting dates to other applications
		SimpleDateFormat sdfDate = new SimpleDateFormat( DATETIME_FORMAT );
		
		return sdfDate.format( new Date() );
	}
	
	// ------------------------------------------------------------------------
	// alternative to obsolete Date.toGMTString() method
	public static String ToGMTString( Date date )
	{
		SimpleDateFormat sdfDate = new SimpleDateFormat();
		
		sdfDate.setTimeZone( new SimpleTimeZone( 0, "GMT" ) );
		sdfDate.applyPattern( "dd MMM yyyy HH:mm:ss z" );
		
		return sdfDate.format( date );
	}
	
	public static String ToString( Date date )
	{
		SimpleDateFormat sdfDate = new SimpleDateFormat( DATETIME_FORMAT );
		return sdfDate.format( date );
	}
	
	// ------------------------------------------------------------------------
	public static Date ParseTimeStamp( String timestamp ) throws ParseException
	{
		SimpleDateFormat sdfDate = new SimpleDateFormat( DATETIME_FORMAT );
		
		return sdfDate.parse( timestamp );
	}
	
	// ------------------------------------------------------------------------
	public static Date ParseTimeStampSafe( String timestamp )
	{
		try
		{
			return ParseTimeStamp( timestamp );
		}
		catch ( ParseException e )
		{
		}
		
		return null;
	}
	
	// ------------------------------------------------------------------------
	public static long MidnightTimeMillis( int utcOffset )
	{
		return MidnightTimeMillis( 0, utcOffset );
	}
		
	// ------------------------------------------------------------------------
	public static long MidnightTimeMillis( int dayOffset, int utcOffset )
	{
		Calendar cal = Calendar.getInstance( TimeZone.getTimeZone( "UTC" ) );
		cal.add( Calendar.DAY_OF_YEAR, dayOffset );
		cal.set( Calendar.HOUR_OF_DAY, 0 );
		cal.set( Calendar.MINUTE, 0 );
		cal.set( Calendar.SECOND, 0 );
		cal.set( Calendar.MILLISECOND, 0 );
		cal.add( Calendar.HOUR_OF_DAY, utcOffset );
		
		return cal.getTimeInMillis();
	}
	
	// ------------------------------------------------------------------------
	public static Date MidnightTime( int utcOffset )
	{
		return new Date( MidnightTimeMillis( utcOffset ) );
	}
	
	// ------------------------------------------------------------------------
	public static Date MidnightTime( int dayOffset, int utcOffset )
	{
		return new Date( MidnightTimeMillis( dayOffset, utcOffset ) );
	}
	
	public static String  formatSimpeTime(Date d) {
		SimpleDateFormat sdfDate = new SimpleDateFormat( SIMPETIME_FORMAT );
		return sdfDate.format(d);
	}
	
	public static String  formatSimpeTime2(Date d) {
		SimpleDateFormat sdfDate = new SimpleDateFormat( SIMPETIME_FORMAT2 );
		return sdfDate.format(d);
	}
	
	public static Date parseSimpeTime(String d) {
		try {
			SimpleDateFormat sdfDate = new SimpleDateFormat( SIMPETIME_FORMAT );
			return sdfDate.parse(d);
		} catch (Exception e) {
		}
		return null;
	}
	
	public static boolean isSameDay(Date date1,Date date2) {
		if(date1 == null || date2 == null) {
			return false;
		}
		if(formatSimpeTime(date1).equals(formatSimpeTime(date2))) {
			return true;
		}
		return false;
	}
	
	
	/**
	 * 计算两个日期之间相差的天数
	 * 
	 * @param smdate
	 *            较小的时间
	 * @param bdate
	 *            较大的时间
	 * @return 相差天数
	 * @throws ParseException
	 */
	public static int daysBetween(Date smdate, Date bdate){
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			smdate = sdf.parse(sdf.format(smdate));
			bdate = sdf.parse(sdf.format(bdate));
			Calendar cal = Calendar.getInstance();
			cal.setTime(smdate);
			long time1 = cal.getTimeInMillis();
			cal.setTime(bdate);
			long time2 = cal.getTimeInMillis();
			long between_days = (time2 - time1) / (1000 * 3600 * 24);
			
			return Integer.parseInt(String.valueOf(between_days));
		} catch (Exception e) {
		}
		return 0;
	}

	/**
	 * 字符串的日期格式的计算
	 */
	public static int daysBetween(String smdate, String bdate)
			throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		cal.setTime(sdf.parse(smdate));
		long time1 = cal.getTimeInMillis();
		cal.setTime(sdf.parse(bdate));
		long time2 = cal.getTimeInMillis();
		long between_days = (time2 - time1) / (1000 * 3600 * 24);

		return Integer.parseInt(String.valueOf(between_days));
	} 
	
	public static Date addDay(Date date,int day) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_YEAR, day);
		return cal.getTime();
	}
	
	public static Date addMinute(Date date,int mins) {
		if(date == null) {
			date = new Date();
		}
		Date d = new Date();
		d.setTime(System.currentTimeMillis()+mins*60*1000);
		return d;
	}
	
	public static int getWeek() {
		Calendar cal = Calendar.getInstance();
		int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
	    if (w < 0) w = 0;
	    return w;
	}
	
	/**
	 * 周一 到 周日 为 1-7
	 * @return
	 */
	public static int getWeek(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
	    if (w <= 0) w = 7;
	    return w;
	}
}
