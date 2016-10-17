package common.service;

import java.util.List;
import java.util.Map;

public interface PaymentRankService {
	public List<List<String>> queryRank(String icons, String startDate, String endDate);
	
	public Map<String, Object> queryAccountDetail(String[] accountArray, List<String> categories, String icons, String startDate, String endDate);
}
