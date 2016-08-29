package common.service;

import java.util.List;

public interface AddPlayersService {
	public List<Long> queryAddPlayersData(List<String> categories, String startDate, String endDate);

	public List<Long> queryDeviceInfoData(List<String> categories, String startDate, String endDate);

	public List<Long> queryAddEquipmentData(List<String> categories, String startDate, String endDate);

	public List<Long> dealQueryPlayersChangeRate(List<Long> activateEquipment, List<Long> addEquipment);
}
