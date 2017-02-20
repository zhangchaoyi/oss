package common.service;

import java.util.List;
import java.util.Map;
/**
 * 付费排行接口
 * @author chris
 */
public interface PaymentRankService {
	public List<List<String>> queryRank(String icons, String startDate, String endDate, String db, String versions, String chId);
	
	public Map<String, Object> queryAccountDetail(String[] accountArray, List<String> categories, String icons, String startDate, String endDate, String db);
}
