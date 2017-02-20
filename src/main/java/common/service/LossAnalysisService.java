package common.service;

import java.util.List;
import java.util.Map;
/**
 * 流失分析接口
 * @author chris
 */
public interface LossAnalysisService {
	public Map<String, Object> queryDayLoss(List<String> categories, String icons, String startDate, String endDate, String type, String db);
	
	public Map<String, Object> queryDayReturn(List<String> categories, String icons, String startDate, String endDate, String type, String db);
}
