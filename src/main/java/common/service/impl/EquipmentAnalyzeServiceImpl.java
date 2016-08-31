package common.service.impl;

import java.util.List;

import common.model.DeviceInfo;
import common.service.EquipmentAnalyzeService;

public class EquipmentAnalyzeServiceImpl implements EquipmentAnalyzeService{

	//新增
	public List<DeviceInfo> queryaddPlayersEquipment(String startDate, String endDate){
		String sql = "select B.model model, count(B.model) count from (select openudid from create_role where create_time >= ? and create_time <= ?) A join device_info B on A.openudid = B.openudid group by B.model;";
		List<DeviceInfo> addPlayersEquipment = DeviceInfo.dao.find(sql, startDate, endDate);
		
		return addPlayersEquipment;
	}
	
	public List<DeviceInfo> queryAddPlayersEquipmentResolution(String startDate, String endDate){
		String sql = "select B.resolution resolution, count(B.resolution) count from (select openudid from create_role where create_time >= ? and create_time <= ? ) A join device_info B on A.openudid = B.openudid group by B.resolution";
		List<DeviceInfo> addPlayersEquipmentResolution = DeviceInfo.dao.find(sql, startDate, endDate);
		
		return addPlayersEquipmentResolution;
	}
	
	public List<DeviceInfo> queryAddPlayersEquipmentOs(String startDate, String endDate){
		String sql = "select B.os os, count(B.os) count from (select openudid from create_role where create_time >= ? and create_time <= ? ) A join device_info B on A.openudid = B.openudid group by B.os;";
		List<DeviceInfo> addPlayersEquipmentOs = DeviceInfo.dao.find(sql, startDate, endDate);
		
		return addPlayersEquipmentOs;
	}
	
	
	public List<DeviceInfo> queryAddPlayersEquipmentNet(String startDate, String endDate){
		String sql = "select B.net net, count(B.net) count from (select openudid from create_role where create_time >= ? and create_time <= ? ) A join device_info B on A.openudid = B.openudid group by B.net";
		List<DeviceInfo> addPlayersEquipmentNet = DeviceInfo.dao.find(sql, startDate, endDate);
	
		return addPlayersEquipmentNet;
	}
	
	public List<DeviceInfo> queryAddPlayersEquipmentBandOperator(String startDate, String endDate){
		String sql = "select B.carrier carrier, count(B.carrier) count from (select openudid from create_role where create_time >= ? and create_time <= ? ) A join device_info B on A.openudid = B.openudid group by B.carrier";
		List<DeviceInfo> addPlayersEquipmentBandOperator = DeviceInfo.dao.find(sql, startDate, endDate);
	
		return addPlayersEquipmentBandOperator;
	}
	
	//活跃
	public List<DeviceInfo> queryActivePlayersEquipment(String startDate, String endDate){
		String sql = "select C.model model, count(C.model) count from create_role A join (select distinct account from login where login_time >= ? and login_time <= ?) B on A.account = B.account join device_info C on A.openudid = C.openudid group by C.model";
		List<DeviceInfo> activePlayersEquipment = DeviceInfo.dao.find(sql, startDate, endDate);
		
		return activePlayersEquipment;
	} 
	
	public List<DeviceInfo> queryActivePlayersEquipmentResolution(String startDate, String endDate){
		String sql = "select C.resolution resolution, count(C.resolution) count from create_role A join (select distinct account from login where login_time >= ? and login_time <= ? ) B on A.account = B.account join device_info C on A.openudid = C.openudid group by C.resolution;";
		List<DeviceInfo> activePlayersEquipmentResolution = DeviceInfo.dao.find(sql, startDate, endDate);
		
		return activePlayersEquipmentResolution;
	}
	
	public List<DeviceInfo> queryActivePlayersEquipmentOs(String startDate, String endDate){
		String sql = "select C.os os, count(C.os) count from create_role A join (select distinct account from login where login_time >= ? and login_time <= ? ) B on A.account = B.account join device_info C on A.openudid = C.openudid group by C.os;";
		List<DeviceInfo> activePlayersEquipmentOs = DeviceInfo.dao.find(sql, startDate, endDate);
		
		return activePlayersEquipmentOs;
	}
	
	
	public List<DeviceInfo> queryActivePlayersEquipmentNet(String startDate, String endDate){
		String sql = "select C.net net, count(C.net) count from create_role A join (select distinct account from login where login_time >= ? and login_time <= ? ) B on A.account = B.account join device_info C on A.openudid = C.openudid group by C.net;";
		List<DeviceInfo> activePlayersEquipmentNet = DeviceInfo.dao.find(sql, startDate, endDate);
	
		return activePlayersEquipmentNet;
	}
	
	public List<DeviceInfo> queryActivePlayersEquipmentBandOperator(String startDate, String endDate){
		String sql = "select C.carrier carrier, count(C.carrier) count from create_role A join (select distinct account from login where login_time >= ? and login_time <= ? ) B on A.account = B.account join device_info C on A.openudid = C.openudid group by C.carrier";
		List<DeviceInfo> activePlayersEquipmentBandOperator = DeviceInfo.dao.find(sql, startDate, endDate);
	
		return activePlayersEquipmentBandOperator;
	}
}
