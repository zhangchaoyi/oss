package common.service;

import java.util.List;
import java.util.Map;

public interface OnlineCountService {
	public Map<String, Object> queryCCU(String startDate, String endDate);

	public Map<String, Object> queryPCU(List<String> categories, String startDate, String endDate);
}
