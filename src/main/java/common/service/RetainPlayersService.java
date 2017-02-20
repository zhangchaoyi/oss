package common.service;

import java.util.List;
import java.util.Map;
/**
 * 留存接口
 * @author chris
 */
public interface RetainPlayersService {
	
	public Map<String, Object> queryRetainUser(List<String> categories, String icons, String startDate, String endDate, String db);

	public Map<String, Object> queryRetainEquipment(List<String> categories, String icons, String startDate,
			String endDate, String db);
}
