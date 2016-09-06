package common.service;

import java.util.List;
import java.util.Map;

public interface RetainPlayersService {
	
	public Map<String, Object> queryRetainUser(List<String> categories, String icons, String startDate, String endDate);

	public Map<String, Object> queryRetainEquipment(List<String> categories, String icons, String startDate,
			String endDate);
}
