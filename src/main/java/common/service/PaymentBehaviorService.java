package common.service;

import java.util.List;
import java.util.Map;

public interface PaymentBehaviorService {
	
	public Map<String,Object> queryRankMoney(String icons, String startDate, String endDate, String db);
	
	public Map<String, Object> queryRankTimes(String icons, String startDate, String endDate, String db);
	
	public List<Integer> queryFirstPeriod(List<String> categories, String icons, String startDate, String endDate, String db);
	
	public List<Integer> querySTFPeriod(List<String> categories, String icons, String startDate, String endDate, String db);
	
	public List<Integer> queryTTSPeriod(List<String> categories, String icons, String startDate, String endDate, String db);
	
	public List<Integer> queryFpGameDays(List<String> categories, String icons, String startDate, String endDate, String db);
	
	public List<Integer> queryFpGamePeriod(List<String> categories, String icons, String startDate, String endDate, String db);
	
	public Map<String, Object> queryFpRank(String icons, String startDate, String endDate, String db);
	
	public List<Integer> queryFpMoney(List<String> categories, String icons, String startDate, String endDate, String db);
}
