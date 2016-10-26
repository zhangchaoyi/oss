package common.service;

import java.util.List;
import java.util.Map;

public interface OnlineHabitsService {
	public Map<String, Object> queryAddpDayAvgGP(List<String>categories, String icons, String startDate, String endDate);
	
	public Map<String, Object> queryAddpWeekAvgGP(String icons, String startDate, String endDate);
	
	public Map<String, Object> queryAddpMonthAvgGP(List<String>categories, String icons, String startDate, String endDate);
	
	public Map<String, Object> queryActivepDayAvgGP(List<String>categories, String icons, String startDate, String endDate);
	
	public Map<String, Object> queryActivepWeekAvgGP(String icons, String startDate, String endDate);
	
	public Map<String, Object> queryActivepMonthAvgGP(List<String>categories, String icons, String startDate, String endDate);

	public Map<String, Object> queryPpDayAvgGP(List<String>categories, String icons, String startDate, String endDate);
	
	public Map<String, Object> queryPpWeekAvgGP(String icons, String startDate, String endDate);

	public Map<String, Object> queryPpMonthAvgGP(List<String>categories, String icons, String startDate, String endDate);
}
