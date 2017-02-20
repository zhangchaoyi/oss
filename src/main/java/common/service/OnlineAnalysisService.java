package common.service;

import java.util.List;
import java.util.Map;
/**
 * 在线分析接口
 * @author chris
 */
public interface OnlineAnalysisService {
	public List<Long> queryPeriodDistribution(int days, String icons, String startDate, String endDate, String db, String versions, String chId);
	
	public List<Long> queryStartTimes(List<String> categories, String icons, String startDate, String endDate, String db, String versions, String chId);
	
	public Map<String, Object> queryNeighborStartPeriod(List<String> categories, String icons, String startDate, String endDate, String db, String versions, String chId);
}
