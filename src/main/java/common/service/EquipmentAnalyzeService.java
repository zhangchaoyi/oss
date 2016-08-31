package common.service;

import java.util.List;

import common.model.DeviceInfo;

public interface EquipmentAnalyzeService {

	public List<DeviceInfo> queryaddPlayersEquipment(String startDate, String endDate);
	
	public List<DeviceInfo> queryAddPlayersEquipmentResolution(String startDate, String endDate);
	
	public List<DeviceInfo> queryAddPlayersEquipmentOs(String startDate, String endDate);
	
	public List<DeviceInfo> queryAddPlayersEquipmentNet(String startDate, String endDate);
	
	public List<DeviceInfo> queryAddPlayersEquipmentBandOperator(String startDate, String endDate);
	
	public List<DeviceInfo> queryActivePlayersEquipment(String startDate, String endDate);
	
	public List<DeviceInfo> queryActivePlayersEquipmentResolution(String startDate, String endDate);
	
	public List<DeviceInfo> queryActivePlayersEquipmentOs(String startDate, String endDate);
	
	public List<DeviceInfo> queryActivePlayersEquipmentNet(String startDate, String endDate);
	
	public List<DeviceInfo> queryActivePlayersEquipmentBandOperator(String startDate, String endDate);
}
