package common.service;

import java.util.List;
import java.util.Map;

public interface OnlineAnalysisService {
	public List<Long> queryPeriodDistribution(int days, String icons, String startDate, String endDate);
	
	public List<Long> queryStartTimes(List<String> categories, String icons, String startDate, String endDate);
	
	public Map<String, Object> queryNeighborStartPeriod(List<String> categories, String icons, String startDate, String endDate);
}
