package common.service;

import java.util.List;

import common.model.DeviceInfo;

public interface EquipmentAnalyzeService {

	public List<DeviceInfo> queryAddPlayersEquipment(String icons, String startDate, String endDate, String db);
	
	public List<DeviceInfo> queryAddPlayersEquipmentResolution(String icons, String startDate, String endDate, String db);
	
	public List<DeviceInfo> queryAddPlayersEquipmentOsVersion(String icons, String startDate, String endDate, String db);
	
	public List<DeviceInfo> queryAddPlayersEquipmentNet(String icons, String startDate, String endDate, String db);
	
	public List<DeviceInfo> queryAddPlayersEquipmentBandOperator(String icons, String startDate, String endDate, String db);
	
	public List<DeviceInfo> queryActivePlayersEquipment(String icons, String startDate, String endDate, String db);
	
	public List<DeviceInfo> queryActivePlayersEquipmentResolution(String icons, String startDate, String endDate, String db);
	
	public List<DeviceInfo> queryActivePlayersEquipmentOsVersion(String icons, String startDate, String endDate, String db);
	
	public List<DeviceInfo> queryActivePlayersEquipmentNet(String icons, String startDate, String endDate, String db);
	
	public List<DeviceInfo> queryActivePlayersEquipmentBandOperator(String icons, String startDate, String endDate, String db);
	
	public List<DeviceInfo> queryPaidPlayersEquipment(String icons, String startDate, String endDate, String db);
	
	public List<DeviceInfo> queryPaidPlayersEquipmentResolution(String icons, String startDate, String endDate, String db);
	
	public List<DeviceInfo> queryPaidPlayersEquipmentOsVersion(String icons, String startDate, String endDate, String db);
	
	public List<DeviceInfo> queryPaidPlayersEquipmentNet(String icons, String startDate, String endDate, String db);
	
	public List<DeviceInfo> queryPaidPlayersEquipmentBandOperator(String icons, String startDate, String endDate, String db);
}
