package common.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import common.model.DeviceInfo;
import common.model.RetainEquipment;
import common.model.RetainUser;
import common.service.RetainPlayersService;

/**
 * 查询和处理留存用户和留存设备的数据
 * @author chris
 *
 */
public class RetainPlayersServiceImpl implements RetainPlayersService{
	public Map<String, Object> queryRetainUser(List<String> categories, String icons, String startDate, String endDate) {
		Map<String, Object> data = new HashMap<String, Object>();
		String sql = "select DATE_FORMAT(date,'%Y-%m-%d')date,sum(add_user) add_user,sum(next_day_retain) next_day_retain,sum(seven_day_retain)seven_day_retain,sum(month_retain)month_retain from retain_user where date between ? and ? and os in (" + icons + ") group by date";
		String eSql = "select DATE_FORMAT(create_time,'%Y-%m-%d') date,count(*) count from device_info where DATE_FORMAT(create_time,'%Y-%m-%d') between ? and ? and os in (" + icons + ") group by date";
		String aSql = "select DATE_FORMAT(date,'%Y-%m-%d')date,sum(add_equipment)add_equipment from retain_equipment where date between ? and ? and os in (" + icons + ") group by date";
		List<RetainUser> retainUser = RetainUser.dao.find(sql, startDate, endDate);
		List<DeviceInfo> activeDevice = DeviceInfo.dao.find(eSql, startDate, endDate);
		List<RetainEquipment> addDevice = RetainEquipment.dao.find(aSql, startDate, endDate);
		
		//Map<Date,Map<type,value>>
		Map<String, Map<String, Object>> sort = new TreeMap<String, Map<String, Object>>();
		//initial in case NullPointer
		for (String category : categories) {
			Map<String, Object> subMap = new HashMap<String, Object>();
			subMap.put("addUser", 0L);
			subMap.put("nextDayRetain", 0D);
			subMap.put("sevenDayRetain", 0D);
			subMap.put("monthRetain", 0D);
			subMap.put("activeDevice", 0L);
			sort.put(category, subMap);
		}
		//载入留存用户数据并计算留存率
		int nDRSum=0;
		int sDRSum=0;
		int mRSum=0;
		int addSum=0;
		for(RetainUser rr : retainUser){
			String date = rr.getStr("date");
			Map<String, Object> subMap = sort.get(date);
			int add = rr.getBigDecimal("add_user").intValue();
			int nDR = rr.getBigDecimal("next_day_retain").intValue();
			int sDR = rr.getBigDecimal("seven_day_retain").intValue();
			int mR = rr.getBigDecimal("month_retain").intValue();
			//divisor can not be 0;
			if(add==0){
				continue;
			}
			//cal sum for avg
			nDRSum += nDR;
			sDRSum += sDR;
			mRSum += mR;
			addSum += add;
			
			BigDecimal nDRBg = new BigDecimal(nDR * 100.0 / add);
			BigDecimal sDRBg = new BigDecimal(sDR * 100.0 / add);
			BigDecimal mRBg = new BigDecimal(mR * 100.0 / add);
			double nDRRate = nDRBg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			double sDRRate = sDRBg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			double mRRate = mRBg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			subMap.put("addUser", (long)add);
			subMap.put("nextDayRetain", nDRRate);
			subMap.put("sevenDayRetain", sDRRate);
			subMap.put("monthRetain", mRRate);
			sort.put(date, subMap);
		}
		//计算激活设备
		for(DeviceInfo di : activeDevice){
			String date = di.getStr("date");
			Map<String, Object> subMap = sort.get(date);
			subMap.put("activeDevice", di.getLong("count"));
			sort.put(date, subMap);
		}
		//计算新增设备
		for(RetainEquipment re : addDevice) {
			String date = re.getStr("date");
			Map<String, Object> subMap = sort.get(date);
			subMap.put("addDevice", re.getBigDecimal("add_equipment").longValue());
			sort.put(date, subMap);
		}
		//计算留存用户平均值
		double nDRRateAvg = 0.0;
		double sDRRateAvg = 0.0;
		double mRRateAvg = 0.0;
		if(addSum!=0){
			BigDecimal nDRBgAvg = new BigDecimal(nDRSum * 100.0 / addSum);
			BigDecimal sDRBgAvg = new BigDecimal(sDRSum * 100.0 / addSum);
			BigDecimal mRBgAvg = new BigDecimal(mRSum * 100.0 / addSum);
			//avg
			nDRRateAvg = nDRBgAvg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			sDRRateAvg = sDRBgAvg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			mRRateAvg = mRBgAvg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		}
		
		List<Long> addData = new ArrayList<Long>();
		List<Double> nDRData = new ArrayList<Double>();
		List<Double> sDRData = new ArrayList<Double>();
		List<Double> mRData = new ArrayList<Double>();
		List<Long> aDData = new ArrayList<Long>();
		List<Long> addDData = new ArrayList<Long>();
		for(Map.Entry<String, Map<String, Object>> entry : sort.entrySet()) {
			for(Map.Entry<String, Object> subEntry : entry.getValue().entrySet()){
				switch(subEntry.getKey()){
					case "addUser":{
						addData.add((Long)subEntry.getValue());
						break;
					}
					case "nextDayRetain":{
						nDRData.add((Double)subEntry.getValue());	
						break;
					}
					case "sevenDayRetain":{
						sDRData.add((Double)subEntry.getValue());
						break;
					}
					case "monthRetain":{
						mRData.add((Double)subEntry.getValue());
						break;
					}
					case "activeDevice":{
						aDData.add((Long) subEntry.getValue());
						break;
					}
					case "addDevice":{
						addDData.add((Long) subEntry.getValue());
					}
				}
			}
		}
		
		data.put("add", addData);
		data.put("nDR", nDRData);
		data.put("sDR", sDRData);
		data.put("mR", mRData);
		data.put("activeDevice", aDData);
		data.put("addDevice", addDData);
		data.put("nDRRateAvg", nDRRateAvg);
		data.put("sDRRateAvg", sDRRateAvg);
		data.put("mRRateAvg", mRRateAvg);
		return data;
	}
	
	public Map<String, Object> queryRetainEquipment(List<String> categories, String icons, String startDate, String endDate){
		Map<String, Object> data = new HashMap<String, Object>();
		String sql = "select DATE_FORMAT(date,'%Y-%m-%d')date,sum(add_equipment)add_equipment,sum(first_day)first_day,sum(second_day)second_day,sum(third_day)third_day,sum(forth_day)forth_day,sum(fifth_day)fifth_day,sum(sixth_day)sixth_day,sum(seven_day)seven_day,sum(fourteen_day)fourteen_day,sum(thirty_day)thirty_day from retain_equipment where date between ? and ? and os in (" + icons + ") group by date";
		String eSql = "select DATE_FORMAT(create_time,'%Y-%m-%d') date,count(*) count from device_info where DATE_FORMAT(create_time,'%Y-%m-%d') between ? and ? and os in (" + icons + ") group by date";
		List<RetainEquipment> retainEquipment = RetainEquipment.dao.find(sql,startDate,endDate);
		List<DeviceInfo> activeDevice = DeviceInfo.dao.find(eSql, startDate, endDate);
		
		//Map<Date, Map<type,value>>
		Map<String, Map<String, Object>> sort = new TreeMap<String, Map<String, Object>>();
		for (String category : categories) {
			Map<String, Object> subMap = new HashMap<String, Object>();
			subMap.put("addEquipment", 0L);
			subMap.put("activeDevice", 0L);;
			subMap.put("fD", 0D);
			subMap.put("sD", 0D);
			subMap.put("tD", 0D);
			subMap.put("fourD", 0D);
			subMap.put("fifD", 0D);
			subMap.put("sixD", 0D);
			subMap.put("sevenD", 0D);
			subMap.put("ftD", 0D);
			subMap.put("ttD", 0D);
			sort.put(category, subMap);
		}

		for(RetainEquipment re : retainEquipment) {
			String date = re.getStr("date");
			Map<String, Object> subMap = sort.get(date);
			
			int addEquipment = re.getBigDecimal("add_equipment").intValue();
			int firstDay = re.getBigDecimal("first_day").intValue();
			int secondDay = re.getBigDecimal("second_day").intValue();
			int thirdDay = re.getBigDecimal("third_day").intValue();
			int forthDay = re.getBigDecimal("forth_day").intValue();
			int fifthDay = re.getBigDecimal("fifth_day").intValue();
			int sixthDay = re.getBigDecimal("sixth_day").intValue();
			int sevenDay = re.getBigDecimal("seven_day").intValue();
			int fourteenDay = re.getBigDecimal("fourteen_day").intValue();
			int thirtyDay = re.getBigDecimal("thirty_day").intValue();
			//divisor can not be 0;
			if(addEquipment==0){
				continue;
			}
			
			BigDecimal fDBg = new BigDecimal(firstDay * 100.0 / addEquipment);
			BigDecimal sDBg = new BigDecimal(secondDay * 100.0 / addEquipment);
			BigDecimal tDBg = new BigDecimal(thirdDay * 100.0 / addEquipment);
			BigDecimal fourDBg = new BigDecimal(forthDay * 100.0 / addEquipment);
			BigDecimal fifDBg = new BigDecimal(fifthDay * 100.0 / addEquipment);
			BigDecimal sixDbg = new BigDecimal(sixthDay * 100.0 / addEquipment);
			BigDecimal sevenDBg = new BigDecimal(sevenDay * 100.0 / addEquipment);
			BigDecimal ftDBg = new BigDecimal(fourteenDay * 100.0 / addEquipment);
			BigDecimal ttDBg = new BigDecimal(thirtyDay * 100.0 / addEquipment);
			
			double fDRate = fDBg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			double sDRate = sDBg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			double tDRate = tDBg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			double fourDRate = fourDBg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			double fifDRate = fifDBg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			double sixDRate = sixDbg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			double sevenDRate = sevenDBg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			double ftDRate = ftDBg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			double ttDRate = ttDBg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			
			subMap.put("addEquipment",(long)addEquipment);
			subMap.put("fD", fDRate);
			subMap.put("sD", sDRate);
			subMap.put("tD", tDRate);
			subMap.put("fourD", fourDRate);
			subMap.put("fifD", fifDRate);
			subMap.put("sixD", sixDRate);
			subMap.put("sevenD", sevenDRate);
			subMap.put("ftD", ftDRate);
			subMap.put("ttD", ttDRate);
			sort.put(date, subMap);
		}
		for(DeviceInfo di : activeDevice) {
			String date = di.getStr("date");
			Map<String, Object> subMap = sort.get(date);
			subMap.put("activeDevice", di.getLong("count"));
			sort.put(date, subMap);
		}
		List<Long> addEquipmentData = new ArrayList<Long>();
		List<Long> activeDeviceData = new ArrayList<Long>();
		List<Double> fDData = new ArrayList<Double>();
		List<Double> sDData = new ArrayList<Double>();
		List<Double> tDData = new ArrayList<Double>();
		List<Double> fourDData = new ArrayList<Double>();
		List<Double> fifDData = new ArrayList<Double>();
		List<Double> sixDData = new ArrayList<Double>();
		List<Double> sevenDData = new ArrayList<Double>();
		List<Double> ftDData = new ArrayList<Double>();
		List<Double> ttDData = new ArrayList<Double>();
		
		
		for(Map.Entry<String, Map<String, Object>> entry : sort.entrySet()) {
			for(Map.Entry<String, Object> subEntry : entry.getValue().entrySet()){
				switch(subEntry.getKey()){
					case "addEquipment":{
						addEquipmentData.add((Long)subEntry.getValue());
						break;
					}
					case "activeDevice":{
						activeDeviceData.add((Long)subEntry.getValue());
						break;
					}
					case "fD":{
						fDData.add((Double)subEntry.getValue());	
						break;
					}
					case "sD":{
						sDData.add((Double)subEntry.getValue());
						break;
					}
					case "tD":{
						tDData.add((Double)subEntry.getValue());
						break;
					}
					case "fourD":{
						fourDData.add((Double)subEntry.getValue());	
						break;
					}
					case "fifD":{
						fifDData.add((Double)subEntry.getValue());	
						break;
					}
					case "sixD":{
						sixDData.add((Double)subEntry.getValue());
						break;
					}
					case "sevenD":{
						sevenDData.add((Double)subEntry.getValue());
						break;
					}
					case "ftD":{
						ftDData.add((Double)subEntry.getValue());
						break;
					}
					case "ttD":{
						ttDData.add((Double)subEntry.getValue());
						break;
					}
					
				}
			}
		}
		
		data.put("addEquipment", addEquipmentData);
		data.put("activeDevice", activeDeviceData);
		data.put("fD", fDData);
		data.put("sD", sDData);
		data.put("tD", tDData);
		data.put("fourD", fourDData);
		data.put("fifD", fifDData);
		data.put("sixD", sixDData);
		data.put("sevenD", sevenDData);
		data.put("ftD", ftDData);
		data.put("ttD", ttDData);
		
		return data;
	}

}
