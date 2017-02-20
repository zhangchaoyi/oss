package common.service;

import java.util.Map;
/**
 * dashboard接口
 * @author chris
 */
public interface DashboardService {
	public Map<String, String> queryDashboardData(String db);
}
