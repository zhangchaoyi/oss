package common.service;

import java.util.List;
import java.util.Map;
/**
 * 活跃玩家接口
 * @author chris
 */
public interface ActivePlayersService {

	public List<Long> queryDau(List<String> categories, String icons, String startDate, String endDate, String db, String versions, String chId);
	
	public Map<String, Object> queryDAUWAUMAU(final List<String> categories, String icons, final String db, String versions, String chId);
	
	public List<Double> queryActivePlayersDauMauRate(final List<String> categories, String icons, final String db, String versions, String chId);
	
	public Map<String, List<Long>> queryPaidInActiveUser(List<String> categories, String icons, String startDate, String endDate, String db, String versions, String chId);
	
	public List<Long> queryPlayDays(List<String> playDaysPeriod, String icons, String startDate, String endDate, String db, String versions, String chId);
	
	public Map<Integer, Long> queryRank(String icons, String startDate, String endDate, String db, String versions, String chId);
	
	public Map<String, Object> queryArea(String icons, String startDate, String endDate, String db, String versions, String chId);
	
	public Map<String, Object> queryCountry(String icons, String startDate, String endDate, String db, String versions, String chId);
	
	public Map<String, Object> queryAccountType(String icons, String startDate, String endDate, String db, String versions, String chId);
	
}
