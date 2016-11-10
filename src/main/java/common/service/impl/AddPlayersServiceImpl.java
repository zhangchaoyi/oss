package common.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import common.model.CreateRole;
import common.model.DeviceInfo;
import common.model.Logout;
import common.service.AddPlayersService;

/**
 * 处理新增玩家页的数据
 * @author chris
 *
 */
public class AddPlayersServiceImpl implements AddPlayersService {
	private static Logger logger = Logger.getLogger(AddPlayersServiceImpl.class);
	/**
	 * 查询每天的新增用户
	 * 整理数据 源数据可能存在缺失 如2016-08-01当天的数据可能返回null,则数据列表长度 和 时间列表长度 不能一一对应
	 * @param categories 日期列表
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间
	 */
	public List<Long> queryAddPlayersData(List<String> categories, String icons, String startDate, String endDate) {
		String sql = "select DATE_FORMAT(A.create_time,'%Y-%m-%d') date,count(*) count from create_role A join device_info B on A.openudid = B.openudid where A.create_time >= ? and A.create_time <= ? and B.os in(" + icons + ") group by DATE_FORMAT(A.create_time,'%Y-%m-%d')";
		List<CreateRole> addPlayersSource = CreateRole.dao.find(sql, startDate, endDate);

		List<Long> data = new ArrayList<Long>();
		Map<String, Long> sort = new TreeMap<String, Long>();
		// 将日期作为Map的key保证查询出来的数据不会缺失
		for (String category : categories) {
			sort.put(category, 0L);
		}
		for (CreateRole cr : addPlayersSource) {
			sort.put(cr.getStr("date"), cr.getLong("count"));
		}
		data.addAll(sort.values());
		logger.debug("queryAddPlayersData:" + data);
		return data;
	}

	/**
	 * 查询每天设备激活的信息
	 * @param categories 日期列表
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间
	 */
	public List<Long> queryDeviceInfoData(List<String> categories, String icons, String startDate, String endDate) {
		String activateEquipmentSql = "select DATE_FORMAT(create_time,'%Y-%m-%d') date,count(*) count from device_info where create_time >= ? and create_time <= ? and os in ("+ icons +") group by DATE_FORMAT(create_time,'%Y-%m-%d')";
		List<DeviceInfo> activateEquipmentSource = DeviceInfo.dao.find(activateEquipmentSql, startDate, endDate);
		return dealQueryDeviceInfoData(activateEquipmentSource, categories);
	}

	/**查询新增设备的信息
	 * @param categories 日期列表
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间
	 */
	public List<Long> queryAddEquipmentData(List<String> categories, String icons, String startDate, String endDate) {
		String sql = "select count(B.openudid) count,DATE_FORMAT(B.create_time,'%Y-%m-%d') date from (select openudid from device_info where create_time >= ? and create_time <= ? and os in(" + icons + ")) A left join (select openudid,min(create_time) create_time from create_role where create_time >= ? and create_time <= ? group by openudid) B on A.openudid = B.openudid where B.openudid is not null group by DATE_FORMAT(B.create_time,'%Y-%m-%d')";
		List<DeviceInfo> addEquipmentSource = DeviceInfo.dao.find(sql, startDate, endDate, startDate,
				endDate);
		return dealQueryDeviceInfoData(addEquipmentSource, categories);
	}

	//处理查询后的中间数据
	private List<Long> dealQueryDeviceInfoData(List<DeviceInfo> source, List<String> categories) {
		List<Long> data = new ArrayList<Long>();
		Map<String, Long> sort = new TreeMap<String, Long>();
		// 将日期作为Map的key保证查询出来的数据不会缺失
		for (String category : categories) {
			sort.put(category, 0L);
		}
		for (DeviceInfo cr : source) {
			sort.put(cr.getStr("date"), cr.getLong("count"));
		}
		data.addAll(sort.values());
		return data;
	}

	/**
	 * 计算玩家转化率百分比 返回List<Long> 兼容格式
	 * @param activateEquipment 激活设备
	 * @param addEquipment 新增设备
	 */
	public List<Long> dealQueryPlayersChangeRate(List<Long> activateEquipment, List<Long> addEquipment) {
		List<Long> data = new ArrayList<Long>();
		for (int i = 0; i < activateEquipment.size(); i++) {
			if (activateEquipment.get(i) == 0) {
				data.add(0L);
				continue;
			}
			double result = (double) addEquipment.get(i) / (double) activateEquipment.get(i);
			BigDecimal bg = new BigDecimal(result);
			double percent = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			percent *= 100;
			data.add(Double.valueOf(percent).longValue());
		}
		return data;
	}
	
	/**
	 * 查询首次游戏时间  筛选时间区间内的每个帐号首次在线时长
	 * @param gamePeriod 游戏时间段  ---在controller定义好
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间
	 */
	public List<Long> queryFirstGamePeriod(List<String> gamePeriod, String icons, String startDate, String endDate){	
		List<Long> data = new ArrayList<Long>();
		String sql = "select B.online_time time from (select account,min(logout_time)logout_time from logout where logout_time > ? group by account) A join logout B on A.account= B.account and A.logout_time = B.logout_time join create_role C on A.account = C.account join device_info D on C.openudid = D.openudid where C.create_time >= ? and C.create_time <= ? and D.os in("+ icons + ");"; 	
		List<Logout> firstGamePeriod = Logout.dao.find(sql, startDate, startDate, endDate);
		Map<String, Integer> firstGamePeriodCollect = new LinkedHashMap<String, Integer>();
		for(String gp :gamePeriod){
			firstGamePeriodCollect.put(gp,0);
		}

		for(Logout logout : firstGamePeriod){
			int oT = logout.getInt("time");
			if(oT <= 4){
				increaseValue("1~4 s" ,firstGamePeriodCollect);
				continue;
			}
			if(oT>=5 && oT <=10){
				increaseValue("5~10 s" ,firstGamePeriodCollect);
				continue;
			}
			if(oT>=11 && oT <=30){
				increaseValue("11~30 s" ,firstGamePeriodCollect);
				continue;
			}
			if(oT>=31 && oT <=60){
				increaseValue("31~60 s" ,firstGamePeriodCollect);
				continue;
			}
			if(oT>=61 && oT <=180){
				increaseValue("1~3 min" ,firstGamePeriodCollect);
				continue;
			}
			if(oT>=181 && oT <=600){
				increaseValue("3~10 min" ,firstGamePeriodCollect);
				continue;
			}
			if(oT>=601 && oT <=1800){
				increaseValue("10~30 min" ,firstGamePeriodCollect);
				continue;
			}
			if(oT>=1801 && oT <=3600){
				increaseValue("30~60 min" ,firstGamePeriodCollect);
				continue;
			}
			increaseValue(">60 min" ,firstGamePeriodCollect);
			
		}	
		for (Map.Entry<String, Integer> entry : firstGamePeriodCollect.entrySet()) {
			data.add(Long.parseLong(String.valueOf(entry.getValue())));
		}
		return data;
	}
	
	/**
	 * 小号分析
	 * @param accountPeriod 游戏时间段  ---在controller定义好
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间
	 */
	public List<Long> querySubsidiaryAccount(List<String> accountPeriod, String icons, String startDate, String endDate){
		List<Long> data = new ArrayList<Long>();
		String sql = "select accountNum,count(accountNum) equipmentCount from (select B.accountNum from device_info A left join (select count(openudid)accountNum,openudid,min(create_time) create_time from create_role where create_time >= ? and create_time <= ? group by openudid) B on A.openudid = B.openudid where B.openudid is not null and A.os in(" + icons + ")) C group by accountNum;";
		List<DeviceInfo> subAccount = DeviceInfo.dao.find(sql, startDate, endDate);
		Map<String, Long> subAccountCollect = new LinkedHashMap<String, Long>();
		for(String ap : accountPeriod){
			subAccountCollect.put(ap, 0L);
		}
		for(DeviceInfo deviceInfo : subAccount){
			Long accountNum = deviceInfo.getLong("accountNum");
			Long equipmentCount = deviceInfo.getLong("equipmentCount");
			if(accountNum<=7){
				subAccountCollect.put(String.valueOf(accountNum), equipmentCount);
			}
			if(accountNum>7 && accountNum <=10){
				subAccountCollect.put("8~10", equipmentCount);
			}
			if(accountNum>10){
				subAccountCollect.put(">10", equipmentCount);
			}			
		}
		data.addAll(subAccountCollect.values());
		return data;
	}
	
	/**
	 * 地区 --对应国内省份
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间
	 */
	public List<CreateRole> queryArea(String icons, String startDate, String endDate){
		String sql = "select B.province province,count(B.province) count from (select openudid from create_role where create_time >= ? and create_time <= ?) A join device_info B on A.openudid = B.openudid where B.os in (" + icons + ") group by B.province";
		List<CreateRole> area = CreateRole.dao.find(sql, startDate, endDate);
		
		return area;
	}
	/**
	 * 国家
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间
	 */
	public List<CreateRole> queryCountry(String icons, String startDate, String endDate){
		String sql = "select B.country country,count(B.country) count from (select openudid from create_role where create_time >= ? and create_time <= ?) A join device_info B on A.openudid = B.openudid where B.os in (" + icons + ") group by B.country;";
		List<CreateRole> countries = CreateRole.dao.find(sql, startDate, endDate);
		
		return countries;
	}
	/**
	 * 账户类型
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间
	 */
	public List<CreateRole> queryAccountType(String icons, String startDate, String endDate){
		String sql = "select A.account_type,count(A.account_type) count from create_role A join device_info B on A.openudid = B.openudid where A.create_time >= ? and A.create_time <= ? and B.os in (" + icons + ") group by A.account_type";
		List<CreateRole> accountType = CreateRole.dao.find(sql, startDate, endDate);
		return accountType;
	}
	//用于map的计数器
	private void increaseValue(String key, Map<String, Integer> map){
		int value = map.get(key);
		value++;
		map.put(key, value);
	}
	
}
