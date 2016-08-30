package common.service;

import java.util.List;

import common.model.DeviceInfo;

public interface EquipmentAnalyzeService {

	public List<DeviceInfo> queryaddPlayersEquipment(String startDate, String endDate);
	
	public List<DeviceInfo> queryaddPlayersEquipmentResolution(String startDate, String endDate);
}
