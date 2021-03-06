package common.service;

import java.util.List;
import java.util.Map;
/**
 * 在线习惯接口
 * @author chris
 */
public interface OnlineHabitsService {
	public Map<String, Object> queryAddpDayAvgGP(List<String> categories, String icons, String startDate,
			String endDate, String db, String versions, String chId);

	public Map<String, Object> queryAddpWeekAvgGP(String icons, String startDate, String endDate, String db, String versions, String chId);

	public Map<String, Object> queryAddpMonthAvgGP(List<String> categories, String icons, String startDate,
			String endDate, String db, String versions, String chId);

	public Map<String, Object> queryActivepDayAvgGP(List<String> categories, String icons, String startDate,
			String endDate, String db, String versions, String chId);

	public Map<String, Object> queryActivepWeekAvgGP(String icons, String startDate, String endDate, String db, String versions, String chId);

	public Map<String, Object> queryActivepMonthAvgGP(List<String> categories, String icons, String startDate,
			String endDate, String db, String versions, String chId);

	public Map<String, Object> queryPpDayAvgGP(List<String> categories, String icons, String startDate, String endDate, String db, String versions, String chId);

	public Map<String, Object> queryPpWeekAvgGP(String icons, String startDate, String endDate, String db, String versions, String chId);

	public Map<String, Object> queryPpMonthAvgGP(List<String> categories, String icons, String startDate,
			String endDate, String db, String versions, String chId);

	public List<Integer> queryAddDayGameTimes(List<String> categories, String icons, String startDate, String endDate, String db, String versions, String chId);
	
	public List<Integer> queryAddDayGameTime(List<String> categories, String icons, String startDate, String endDate, String db, String versions, String chId);
	
	public Map<String, List<Integer>> queryAddDaySinglePeriod(List<String> categories, String icons, String startDate, String endDate, String db, String versions, String chId);
	
	public List<Integer> queryAddDayPeriod(String icons, String startDate, String endDate, String db, String versions, String chId);
	
	public List<Integer> queryActiveDayGameTimes(List<String> categories, String icons, String startDate,
			String endDate, String db, String versions, String chId);
	
	public List<Integer> queryActiveWeekGameTimes(List<String> categories, String icons, String startDate, String endDate, String db, String versions, String chId);
	
	public List<Integer> queryActiveWeekGameDays(List<String> categories, String icons, String startDate, String endDate, String db, String versions, String chId);
	
	public List<Integer> queryActiveMonthGameDays(List<String> categories, String icons, String startDate, String endDate, String db, String versions, String chId);
	
	public List<Integer> queryActiveDayGameTime(List<String> categories, String icons, String startDate, String endDate, String db, String versions, String chId);
	
	public List<Integer> queryActiveWeekGameTime(List<String> categories, String icons, String startDate,
			String endDate, String db, String versions, String chId);
	
	public Map<String, List<Integer>> queryActiveDaySinglePeriod(List<String> categories, String icons, String startDate, String endDate, String db, String versions, String chId);
	
	public List<Integer> queryActiveDayPeriod(String icons, String startDate, String endDate, String db, String versions, String chId);
}
