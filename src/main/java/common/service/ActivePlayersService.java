package common.service;

import java.util.List;
import java.util.Map;

public interface ActivePlayersService {

	public List<Long> queryDau(List<String> categories, String startDate, String endDate);
	
	public Map<String, Object> queryActivePlayersInfo(List<String> categories, String startDate, String endDate);
	
	public List<Double> queryActivePlayersDauMauRate(List<String> categories, String startDate, String endDate);
	
	public Map<String, List<Long>> queryPaidInActiveUser(List<String> categories, String startDate, String endDate);
	
	public List<Long> queryPlayDays(List<String> playDaysPeriod, String startDate, String endDate);
	
	public Map<Integer, Long> queryRank(String startDate, String endDate);
	
	public Map<String, Object> queryArea(String startDate, String endDate);
	
	public Map<String, Object> queryCountry(String startDate, String endDate);
	
	public Map<String, Object> queryAccountType(String startDate, String endDate);
	
}
