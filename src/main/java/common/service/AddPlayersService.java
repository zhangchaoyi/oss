package common.service;

import java.util.List;

import common.model.CreateRole;

public interface AddPlayersService {
	
	public List<Long> queryAddPlayersData(List<String> categories, String startDate, String endDate);

	public List<Long> queryDeviceInfoData(List<String> categories, String startDate, String endDate);

	public List<Long> queryAddEquipmentData(List<String> categories, String startDate, String endDate);

	public List<Long> dealQueryPlayersChangeRate(List<Long> activateEquipment, List<Long> addEquipment);
	
	public List<Long> queryFirstGamePeriod(List<String> gamePeriod, String startDate, String endDate);
	
	public List<Long> querySubsidiaryAccount(List<String> accountPeriod, String startDate, String endDate);
	
	public List<CreateRole> queryArea(String startDate, String endDate);
	
	public List<CreateRole> queryCountry(String startDate, String endDate);
	
	public List<CreateRole> queryAccountType(String startDate, String endDate);
}
