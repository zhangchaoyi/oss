package common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateUtils {

	public static Date strToDate(String dateString){
		Date date = null;
		SimpleDateFormat sdf;
		try {  
		    sdf = new SimpleDateFormat("yyyy-MM-dd");  
		    date = sdf.parse(dateString);  
		}catch (ParseException e){  
		    System.out.println(e.getMessage());  
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
		
		for(Date date=start;date.before(end)||date.equals(end);){
			dateList.add(dateToStr(date));
			cal.setTime(date);
			cal.add(Calendar.DATE, 1);
			date = cal.getTime();
		}
		
		return dateList;
	}
	
	public static void main(String args[]){
		System.out.println(getDateList("2016-08-01", "2016-08-10"));

	}
	
}
