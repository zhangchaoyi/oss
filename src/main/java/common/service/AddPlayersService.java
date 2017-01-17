package common.service;

import java.util.List;

import common.model.CreateRole;

public interface AddPlayersService {
	
	public List<Long> queryAddPlayersData(List<String> categories, String icons, String startDate, String endDate, String db, String versions, String chId);

	public List<Long> queryDeviceInfoData(List<String> categories, String icons, String startDate, String endDate, String db, String versions, String chId);

	public List<Long> queryAddEquipmentData(List<String> categories, String icons, String startDate, String endDate, String db, String versions, String chId);

	public List<Long> dealQueryPlayersChangeRate(List<Long> activateEquipment, List<Long> addEquipment);
	
	public List<Long> queryFirstGamePeriod(List<String> gamePeriod, String icons, String startDate, String endDate, String db, String versions, String chId);
	
	public List<Long> querySubsidiaryAccount(List<String> accountPeriod, String icons, String startDate, String endDate, String db, String versions, String chId);
	
	public List<CreateRole> queryArea(String icons, String startDate, String endDate, String db, String versions, String chId);
	
	public List<CreateRole> queryCountry(String icons, String startDate, String endDate, String db, String versions, String chId);
	
	public List<CreateRole> queryAccountType(String icons, String startDate, String endDate, String db, String versions, String chId);
}
