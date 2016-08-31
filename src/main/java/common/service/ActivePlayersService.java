package common.service;

import java.util.List;
import java.util.Map;

import common.model.DeviceInfo;

public interface ActivePlayersService {

	public List<Long> queryDau(List<String> categories, String startDate, String endDate);
	
	public Map<String, List<Long>> queryActivePlayersInfo(List<String> categories, String startDate, String endDate);
	
}
