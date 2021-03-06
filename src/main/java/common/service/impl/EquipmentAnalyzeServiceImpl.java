package common.service.impl;

import java.util.List;
import common.model.DeviceInfo;
import common.service.EquipmentAnalyzeService;

/**
 * 查询设备分析页数据
 * @author chris
 *
 */
public class EquipmentAnalyzeServiceImpl implements EquipmentAnalyzeService{
	/**
	 * 新增用户机型
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间 
	 */
	public List<DeviceInfo> queryAddPlayersEquipment(String icons, String startDate, String endDate, String db, String versions, String chId){
		String sql = "select B.model model, count(B.model) count from (select openudid from create_role where create_time >= ? and create_time <= ?) A join device_info B on A.openudid = B.openudid where B.os in (" + icons + ") and B.script_version in ("+versions+") and B.ch_id in ("+chId+") group by B.model";
		List<DeviceInfo> addPlayersEquipment = DeviceInfo.dao.use(db).find(sql, startDate, endDate);	
		return addPlayersEquipment;
	}
	/**
	 * 新增用户分辨率
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间 
	 */
	public List<DeviceInfo> queryAddPlayersEquipmentResolution(String icons, String startDate, String endDate, String db, String versions, String chId){
		String sql = "select B.resolution resolution, count(B.resolution) count from (select openudid from create_role where create_time >= ? and create_time <= ? ) A join device_info B on A.openudid = B.openudid where B.os in (" + icons + ") and B.script_version in ("+versions+") and B.ch_id in ("+chId+") group by B.resolution";
		List<DeviceInfo> addPlayersEquipmentResolution = DeviceInfo.dao.use(db).find(sql, startDate, endDate);
		return addPlayersEquipmentResolution;
	}
	/**
	 * 新增用户操作系统
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间 
	 */
	public List<DeviceInfo> queryAddPlayersEquipmentOsVersion(String icons, String startDate, String endDate, String db, String versions, String chId){
		String sql = "select B.os_version,count(B.os_version) count from (select openudid from create_role where create_time >= ? and create_time <= ? ) A join device_info B on A.openudid = B.openudid where B.os in (" + icons + ") and B.script_version in ("+versions+") and B.ch_id in ("+chId+") group by B.os_version";
		List<DeviceInfo> addPlayersEquipmentOs = DeviceInfo.dao.use(db).find(sql, startDate, endDate);
		return addPlayersEquipmentOs;
	}
	/**
	 * 新增用户联网方式
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间 
	 */
	public List<DeviceInfo> queryAddPlayersEquipmentNet(String icons, String startDate, String endDate, String db, String versions, String chId){
		String sql = "select B.net net, count(B.net) count from (select openudid from create_role where create_time >= ? and create_time <= ? ) A join device_info B on A.openudid = B.openudid where B.os in (" + icons + ") and B.script_version in ("+versions+") and B.ch_id in ("+chId+") group by B.net";
		List<DeviceInfo> addPlayersEquipmentNet = DeviceInfo.dao.use(db).find(sql, startDate, endDate);
		return addPlayersEquipmentNet;
	}
	/**
	 * 新增用户宽带运营商
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间 
	 */
	public List<DeviceInfo> queryAddPlayersEquipmentBandOperator(String icons, String startDate, String endDate, String db, String versions, String chId){
		String sql = "select B.carrier carrier, count(B.carrier) count from (select openudid from create_role where create_time >= ? and create_time <= ? ) A join device_info B on A.openudid = B.openudid where B.os in (" + icons + ") and B.script_version in ("+versions+") and B.ch_id in ("+chId+") group by B.carrier";
		List<DeviceInfo> addPlayersEquipmentBandOperator = DeviceInfo.dao.use(db).find(sql, startDate, endDate);
		return addPlayersEquipmentBandOperator;
	}
	/**
	 * 活跃用户机型
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间 
	 */
	public List<DeviceInfo> queryActivePlayersEquipment(String icons, String startDate, String endDate, String db, String versions, String chId){
		String sql = "select C.model model, count(C.model) count from create_role A join (select distinct account from login where login_time >= ? and login_time <= ?) B on A.account = B.account join device_info C on A.openudid = C.openudid where C.os in (" + icons + ") and C.script_version in ("+versions+") and C.ch_id in ("+chId+") group by C.model";
		List<DeviceInfo> activePlayersEquipment = DeviceInfo.dao.use(db).find(sql, startDate, endDate);
		return activePlayersEquipment;
	} 
	/**
	 * 活跃用户分辨率
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间 
	 */
	public List<DeviceInfo> queryActivePlayersEquipmentResolution(String icons, String startDate, String endDate, String db, String versions, String chId){
		String sql = "select C.resolution resolution, count(C.resolution) count from create_role A join (select distinct account from login where login_time >= ? and login_time <= ? ) B on A.account = B.account join device_info C on A.openudid = C.openudid where C.os in (" + icons + ") and C.script_version in ("+versions+") and C.ch_id in ("+chId+") group by C.resolution;";
		List<DeviceInfo> activePlayersEquipmentResolution = DeviceInfo.dao.use(db).find(sql, startDate, endDate);
		return activePlayersEquipmentResolution;
	}
	/**
	 * 活跃用户操作系统
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间 
	 */
	public List<DeviceInfo> queryActivePlayersEquipmentOsVersion(String icons, String startDate, String endDate, String db, String versions, String chId){
		String sql = "select C.os_version , count(C.os_version) count from create_role A join (select distinct account from login where login_time >= ? and login_time <= ?) B on A.account = B.account join device_info C on A.openudid = C.openudid where C.os in (" + icons + ") and C.script_version in ("+versions+") and C.ch_id in ("+chId+") group by C.os_version";
		List<DeviceInfo> activePlayersEquipmentOsVersion = DeviceInfo.dao.use(db).find(sql, startDate, endDate);
		return activePlayersEquipmentOsVersion;
	}
	/**
	 * 活跃用户联网方式
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间 
	 */
	public List<DeviceInfo> queryActivePlayersEquipmentNet(String icons, String startDate, String endDate, String db, String versions, String chId){
		String sql = "select C.net net, count(C.net) count from create_role A join (select distinct account from login where login_time >= ? and login_time <= ? ) B on A.account = B.account join device_info C on A.openudid = C.openudid where C.os in (" + icons + ") and C.script_version in ("+versions+") and C.ch_id in ("+chId+") group by C.net;";
		List<DeviceInfo> activePlayersEquipmentNet = DeviceInfo.dao.use(db).find(sql, startDate, endDate);
		return activePlayersEquipmentNet;
	}
	/**
	 * 活跃用户宽带运营商
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间 
	 */
	public List<DeviceInfo> queryActivePlayersEquipmentBandOperator(String icons, String startDate, String endDate, String db, String versions, String chId){
		String sql = "select C.carrier carrier, count(C.carrier) count from create_role A join (select distinct account from login where login_time >= ? and login_time <= ? ) B on A.account = B.account join device_info C on A.openudid = C.openudid where C.os in (" + icons + ") and C.script_version in ("+versions+") and C.ch_id in ("+chId+") group by C.carrier";
		List<DeviceInfo> activePlayersEquipmentBandOperator = DeviceInfo.dao.use(db).find(sql, startDate, endDate);
		return activePlayersEquipmentBandOperator;
	}
	
	/**
	 * 付费用户机型
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间 
	 */
	public List<DeviceInfo> queryPaidPlayersEquipment(String icons, String startDate, String endDate, String db, String versions, String chId) {
		String sql = "select C.model model,count(C.model) count from (select distinct account from log_charge where timestamp >= ? and timestamp <= ? and is_product = 1) A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where C.os in (" + icons + ") and C.script_version in ("+versions+") and C.ch_id in ("+chId+") group by C.model";
		List<DeviceInfo> paidPlayerEquipment = DeviceInfo.dao.use(db).find(sql, startDate, endDate);
		return paidPlayerEquipment;
	}
	/**
	 * 付费用户分辨率
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间 
	 */
	public List<DeviceInfo> queryPaidPlayersEquipmentResolution(String icons, String startDate, String endDate, String db, String versions, String chId) {
		String sql = "select C.resolution resolution,count(C.resolution) count from (select distinct account from log_charge where timestamp >= ? and timestamp <= ? and is_product = 1) A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where C.os in (" + icons + ") and C.script_version in ("+versions+") and C.ch_id in ("+chId+") group by C.resolution";
		List<DeviceInfo> paidPlayersEquipmentResolution = DeviceInfo.dao.use(db).find(sql, startDate, endDate);
		return 	paidPlayersEquipmentResolution;
	}
	/**
	 * 付费用户操作系统
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间 
	 */
	public List<DeviceInfo> queryPaidPlayersEquipmentOsVersion(String icons, String startDate, String endDate, String db, String versions, String chId) {
		String sql = "select C.os_version,count(C.os_version) count from (select distinct account from log_charge where timestamp >= ? and timestamp <= ? and is_product = 1) A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where C.os in (" + icons + ") and C.script_version in ("+versions+") and C.ch_id in ("+chId+") group by C.os_version";
		List<DeviceInfo> paidPlayersEquipmentOsVersion = DeviceInfo.dao.use(db).find(sql, startDate, endDate);
		return 	paidPlayersEquipmentOsVersion;
	}
	/**
	 * 付费用户联网方式
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间 
	 */
	public List<DeviceInfo> queryPaidPlayersEquipmentNet(String icons, String startDate, String endDate, String db, String versions, String chId) {
		String sql = "select C.net net,count(C.net) count from (select distinct account from log_charge where timestamp >= ? and timestamp <= ? and is_product = 1) A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where C.os in (" + icons + ") and C.script_version in ("+versions+") and C.ch_id in ("+chId+") group by C.net;";
		List<DeviceInfo> paidPlayersEquipmentNet = DeviceInfo.dao.use(db).find(sql, startDate, endDate);
		return 	paidPlayersEquipmentNet;
	}
	/**
	 * 付费用户宽带运营商
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间 
	 */
	public List<DeviceInfo> queryPaidPlayersEquipmentBandOperator(String icons, String startDate, String endDate, String db, String versions, String chId) {
		String sql = "select C.carrier carrier,count(C.carrier) count from (select distinct account from log_charge where timestamp >= ? and timestamp <= ? and is_product = 1) A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where C.os in (" + icons + ") and C.script_version in ("+versions+") and C.ch_id in ("+chId+") group by C.carrier;";
		List<DeviceInfo> paidPlayersEquipmentCarrier = DeviceInfo.dao.use(db).find(sql, startDate, endDate);
		return 	paidPlayersEquipmentCarrier;
	}
}
