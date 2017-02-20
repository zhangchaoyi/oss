package common.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;
/**
 * 时间处理工具类
 * @author chris
 */
public class DateUtils {

	private static Logger logger = Logger.getLogger(DateUtils.class);
	private static final ThreadLocal<DateFormat> dfD = new ThreadLocal<DateFormat>() {
		@ Override
		protected DateFormat initialValue() {
		return new SimpleDateFormat("yyyy-MM-dd");
		}
	};
	private static final ThreadLocal<DateFormat> dfM = new ThreadLocal<DateFormat>() {
		@ Override
		protected DateFormat initialValue() {
		return new SimpleDateFormat("yyyy-MM");
		}
	};
	//避免被实例化
	private DateUtils(){}
	/**
	 * 字符串转时间Date类型 yyyy-MM-dd
	 * @param dateString
	 * @return
	 * @author chris
	 */
	public static Date strToDate(String dateString){
		Date date = null;
		try {  
		    date = dfD.get().parse(dateString);  
		}catch (ParseException e){  
			logger.info("throwable exception",e);
			logger.info(e.getStackTrace());
		}
		return date;
	}
	/**
	 * 字符串转时间Date类型 yyyy-MM
	 * @param dateString
	 * @return
	 * @author chris
	 */
	public static Date strToMonth(String dateString){
		Date date = null;
		try {  
		    date = dfD.get().parse(dateString);  
		}catch (ParseException e){  
			logger.info("throwable exception",e);
			logger.info(e.getStackTrace());
		}
		return date;
	}
	/**
	 * Date转字符串类型 yyyy-MM-dd
	 * @param date
	 * @return
	 * @author chris
	 */
	public static String dateToStr(Date date) {
		return dfD.get().format(date);
	}
	/**
	 * Date转字符串类型 yyyy-MM
	 * @param date
	 * @return
	 * @author chris
	 */
	public static String monthToStr(Date date) {
		return dfM.get().format(date);
	}
	/**
	 * 获取时间区间列表
	 * @param startDate
	 * @param endDate
	 * @return List<String>
	 * @author chris
	 */
	public static List<String> getDateList(String startDate, String endDate){
		Date start = strToDate(startDate); 
		Date end = strToDate(endDate);
		
		Calendar cal = Calendar.getInstance();
		List<String> dateList = new ArrayList<String>();
		try {
			for(Date date=start;date.before(end)||date.equals(end);){
				dateList.add(dateToStr(date));
				cal.setTime(date);
				cal.add(Calendar.DATE, 1);
				date = cal.getTime();
			}
		}catch(Exception e){
			e.printStackTrace();
			logger.info(e.getStackTrace());
			logger.info("data transform failed");
		}
		return dateList;
	}
	/**
	 * 获取时间区间列表
	 * @param startMonth
	 * @param endMonth
	 * @return List<String>
	 * @author chris
	 */
	public static List<String> getMonthList(String startMonth, String endMonth){
		Date start = strToMonth(startMonth); 
		Date end = strToMonth(endMonth);
		
		Calendar cal = Calendar.getInstance();
		List<String> monthList = new ArrayList<String>();
		try {
			for(Date date=start;date.before(end)||date.equals(end);){
				monthList.add(monthToStr(date));
				cal.setTime(date);
				cal.add(Calendar.MONTH, 1);
				date = cal.getTime();
			}
		}catch(Exception e){
			e.printStackTrace();
			logger.info(e.getStackTrace());
			logger.info("data transform failed");
		}
		return monthList;
	}
	/**
	 * 时间别名转化
	 * @param d
	 * @return
	 * @author chris
	 */
	public static String convertDate(String d){
		Calendar cal = Calendar.getInstance();
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("今日", 0);
		map.put("昨日", -1);
		map.put("7日前", -7);
		map.put("30日前", -30);
		Date date=new Date();
		for(Map.Entry<String, Integer> entry:map.entrySet()){
			cal.setTime(date);
			cal.add(Calendar.DATE, entry.getValue());
			String dateString = dfM.get().format(cal.getTime());
			if(d.equals(dateString)){
				return entry.getKey();
			}
		}		
		return d;
	}
	/**
	 * 获取当天时间 yyyy-MM-dd
	 * @return String
	 * @author chris
	 */
	public static String getTodayDate(){
		Calendar cal = Calendar.getInstance();
		Date date=new Date();
		cal.setTime(date);
		return dfD.get().format(cal.getTime());
	}
	/**
	 * 获取七天前时间 yyyy-MM-dd
	 * @return
	 * @author chris
	 */
	public static String getSevenAgoDate(){
		Calendar cal = Calendar.getInstance();
		Date date=new Date();
		cal.setTime(date);
		cal.add(Calendar.DATE, -6);
		return dfD.get().format(cal.getTime());
	}
	//将时间段划分成周 Map<start,end>  --以左边时间起始划分,右边时间不足补足七天
//	public static Map<String, String> divideDateToWeek(String startDate, String endDate){
//		Date start = DateUtils.strToDate(startDate);
//		Date end = DateUtils.strToDate(endDate);
//		Map<String, String> week = new LinkedHashMap<String, String>();
//		Calendar cal = Calendar.getInstance();
//		for(Date date=start;date.before(end)||date.equals(end);){
//			String startString = DateUtils.dateToStr(date);
//			cal.setTime(date);
//			cal.add(Calendar.DATE, 6);
//			date=cal.getTime();
//			String endString = DateUtils.dateToStr(date);
//			week.put(startString, endString);
//			cal.setTime(date);
//			cal.add(Calendar.DATE, 1);
//			date=cal.getTime();
//		}
//		return week;
//	}
	/**
	 * 将时间段划分成周 Map<start,end>  --以右边时间起始划分,左边时间不足补足七天 
	 * @param startDate
	 * @param endDate
	 * @return
	 * @author chris
	 */
	public static Map<String, String> divideDateToWeek(String startDate, String endDate) {
		Date start = DateUtils.strToDate(startDate);
		Date end = DateUtils.strToDate(endDate);
		Map<String, String> week = new TreeMap<String, String>();
		Calendar cal = Calendar.getInstance();
		for(Date date=end;date.after(start)||date.equals(start);) {
			String endString = DateUtils.dateToStr(date);
			cal.setTime(date);
			cal.add(Calendar.DATE, -6);
			date = cal.getTime();
			String startString = DateUtils.dateToStr(date);
			week.put(startString, endString);
			cal.setTime(date);
			cal.add(Calendar.DATE, -1);
			date = cal.getTime();
		}
		return week;
	}
	/**
	 * 秒转化成其他时间格式
	 * @param s
	 * @return String
	 * @author chris
	 */
	public static String getTimeFromSecond(long s){
		final long DAY = 60*60*24;
		final long HOUR = 60*60;
		final long MINUTE = 60;
		long day = s/DAY;
		long hour = (s%DAY)/HOUR;
		long minute = ((s%DAY)%HOUR)/MINUTE;
		long second = ((s%DAY)%HOUR)%MINUTE;
		String str = "";
		if(day!=0) {
			str += String.valueOf(day) + "天";
		}
		if(hour!=0) {
			str += String.valueOf(hour) + "小时";
		}
		if(minute!=0) {
			str += String.valueOf(minute) + "分钟";
		}
		if(second!=0) {
			str += String.valueOf(second) + "秒";
		}
		return str;
	}
}
