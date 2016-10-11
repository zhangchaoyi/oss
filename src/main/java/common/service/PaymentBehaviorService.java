package common.service;

import java.util.Map;

public interface PaymentBehaviorService {
	
	public Map<String,Object> queryRankMoney(String icons, String startDate, String endDate);
	
	public Map<String, Object> queryRankTimes(String icons, String startDate, String endDate);
}
