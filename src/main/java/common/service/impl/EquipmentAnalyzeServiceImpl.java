package common.service.impl;

import java.util.List;

import common.model.DeviceInfo;
import common.service.EquipmentAnalyzeService;

public class EquipmentAnalyzeServiceImpl implements EquipmentAnalyzeService{

	public List<DeviceInfo> queryaddPlayersEquipment(String startDate, String endDate){
		String sql = "select B.model model, count(B.model) count from (select openudid from create_role where create_time >= ? and create_time <= ?) A join device_info B on A.openudid = B.openudid group by B.model;";
		List<DeviceInfo> addPlayersEquipment = DeviceInfo.dao.find(sql, startDate, endDate);
		
		return addPlayersEquipment;
	}
	
	public List<DeviceInfo> queryaddPlayersEquipmentResolution(String startDate, String endDate){
		String sql = "select B.resolution resolution, count(B.resolution) count from (select openudid from create_role where create_time >= ? and create_time <= ? ) A join device_info B on A.openudid = B.openudid group by B.resolution";
		List<DeviceInfo> addPlayersEquipmentResolution = DeviceInfo.dao.find(sql, startDate, endDate);
		
		return addPlayersEquipmentResolution;
	}
}
