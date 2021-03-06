package common.service;

import java.util.List;
import java.util.Map;
/**
 * 在线人数接口
 * @author chris
 */
public interface OnlineCountService {
	public Map<String, Object> queryCCU(String startDate, String endDate, String db);

	public Map<String, Object> queryPCU(List<String> categories, String startDate, String endDate, String db);
}
