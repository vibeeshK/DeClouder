package commonTechs;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SimpleDateObj {
	/*
	 * Helps to create and manage a date field in SimpleDateFormat type
	 */
	public int year;
	public int month;
	public int day;
	public int hour;
	public int minute;
	public int second;

	public static SimpleDateFormat DTEFORM_yyyyMMddHHmmss = new SimpleDateFormat("yyyyMMddHHmmss");

	public SimpleDateObj(Date inDate){
		//Initialize your Date however you like it.
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(inDate);
		year = calendar.get(Calendar.YEAR);
		//Add one to month {0 - 11}
		month = calendar.get(Calendar.MONTH) + 1;
		day = calendar.get(Calendar.DAY_OF_MONTH);
		hour = calendar.get(Calendar.HOUR_OF_DAY);
		minute = calendar.get(Calendar.MINUTE);
		second = calendar.get(Calendar.SECOND);
	}

	public SimpleDateObj(
			int inYear,
			int inMonth,
			int inDay,
			int inHour,
			int inMinute,
			int inSecond
	){
		year = inYear;
		month = inMonth;
		day = inDay;
		hour = inHour;
		minute = inMinute;
		second = inSecond;
	}

	public static Date getDate(SimpleDateObj inSimpleDateObj) throws ParseException{

		Date outDate = null;

		//form string in line with the SimpleDateFormat SIMPL_DTE_FORMAT yyyyMMddHHmmss;

		String strDate = "";		
		strDate += inSimpleDateObj.year;		
		strDate += (inSimpleDateObj.month < 10) ? "0" + inSimpleDateObj.month : inSimpleDateObj.month;
		strDate += (inSimpleDateObj.day < 10) ? "0" + inSimpleDateObj.day : inSimpleDateObj.day;
		strDate += (inSimpleDateObj.hour < 10) ? "0" + inSimpleDateObj.hour : inSimpleDateObj.hour;
		strDate += (inSimpleDateObj.minute < 10) ? "0" + inSimpleDateObj.minute : inSimpleDateObj.minute;
		strDate += (inSimpleDateObj.second < 10) ? "0" + inSimpleDateObj.second : inSimpleDateObj.second;
		outDate = DTEFORM_yyyyMMddHHmmss.parse(strDate);
		
		return outDate;
	}
}