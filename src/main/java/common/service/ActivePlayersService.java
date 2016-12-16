package common.service;

import java.util.List;
import java.util.Map;

public interface ActivePlayersService {

	public List<Long> queryDau(List<String> categories, String icons, String startDate, String endDate, String db);
	
	public Map<String, Object> queryActivePlayersInfo(List<String> categories, String icons, String startDate, String endDate, String db);
	
	public List<Double> queryActivePlayersDauMauRate(List<String> categories, String icons, String startDate, String endDate, String db);
	
	public Map<String, List<Long>> queryPaidInActiveUser(List<String> categories, String icons, String startDate, String endDate, String db);
	
	public List<Long> queryPlayDays(List<String> playDaysPeriod, String icons, String startDate, String endDate, String db);
	
	public Map<Integer, Long> queryRank(String icons, String startDate, String endDate, String db);
	
	public Map<String, Object> queryArea(String icons, String startDate, String endDate, String db);
	
	public Map<String, Object> queryCountry(String icons, String startDate, String endDate, String db);
	
	public Map<String, Object> queryAccountType(String icons, String startDate, String endDate, String db);
	
}
