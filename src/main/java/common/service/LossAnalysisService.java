package common.service;

import java.util.List;
import java.util.Map;

public interface LossAnalysisService {
	public Map<String, Object> queryDayLoss(List<String> categories, String icons, String startDate, String endDate, String type);
	
	public Map<String, Object> queryDayReturn(List<String> categories, String icons, String startDate, String endDate, String type);
}
