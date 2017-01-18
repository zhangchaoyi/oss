package common.service;

import java.util.List;

import common.model.DeviceInfo;

public interface EquipmentAnalyzeService {

	public List<DeviceInfo> queryAddPlayersEquipment(String icons, String startDate, String endDate, String db, String versions, String chId);
	
	public List<DeviceInfo> queryAddPlayersEquipmentResolution(String icons, String startDate, String endDate, String db, String versions, String chId);
	
	public List<DeviceInfo> queryAddPlayersEquipmentOsVersion(String icons, String startDate, String endDate, String db, String versions, String chId);
	
	public List<DeviceInfo> queryAddPlayersEquipmentNet(String icons, String startDate, String endDate, String db, String versions, String chId);
	
	public List<DeviceInfo> queryAddPlayersEquipmentBandOperator(String icons, String startDate, String endDate, String db, String versions, String chId);
	
	public List<DeviceInfo> queryActivePlayersEquipment(String icons, String startDate, String endDate, String db, String versions, String chId);
	
	public List<DeviceInfo> queryActivePlayersEquipmentResolution(String icons, String startDate, String endDate, String db, String versions, String chId);
	
	public List<DeviceInfo> queryActivePlayersEquipmentOsVersion(String icons, String startDate, String endDate, String db, String versions, String chId);
	
	public List<DeviceInfo> queryActivePlayersEquipmentNet(String icons, String startDate, String endDate, String db, String versions, String chId);
	
	public List<DeviceInfo> queryActivePlayersEquipmentBandOperator(String icons, String startDate, String endDate, String db, String versions, String chId);
	
	public List<DeviceInfo> queryPaidPlayersEquipment(String icons, String startDate, String endDate, String db, String versions, String chId);
	
	public List<DeviceInfo> queryPaidPlayersEquipmentResolution(String icons, String startDate, String endDate, String db, String versions, String chId);
	
	public List<DeviceInfo> queryPaidPlayersEquipmentOsVersion(String icons, String startDate, String endDate, String db, String versions, String chId);
	
	public List<DeviceInfo> queryPaidPlayersEquipmentNet(String icons, String startDate, String endDate, String db, String versions, String chId);
	
	public List<DeviceInfo> queryPaidPlayersEquipmentBandOperator(String icons, String startDate, String endDate, String db, String versions, String chId);
}
