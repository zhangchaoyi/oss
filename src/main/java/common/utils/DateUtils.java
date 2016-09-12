package common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;

public class DateUtils {

	private static Logger logger = Logger.getLogger(DateUtils.class);
	public static Date strToDate(String dateString){
		Date date = null;
		SimpleDateFormat sdf;
		try {  
		    sdf = new SimpleDateFormat("yyyy-MM-dd");  
		    date = sdf.parse(dateString);  
		}catch (ParseException e){  
			logger.debug("throwable exception",e);
			logger.debug(e.getStackTrace());

		}
		return date;
	}
	
	public static String dateToStr(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
		return sdf.format(date);
	}
	
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
			logger.debug(e.getStackTrace());
			logger.debug("data transform failed");
		}
		return dateList;
	}
	
	public static void main(String args[]){
		System.out.println(getDateList("2016-08-01", "2016-08-10"));
	}
	
}
