package common.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import common.model.LogCharge;
import common.service.PaymentBehaviorService;
import common.utils.StringUtils;

/**
 * 付费行为页
 * @author chris
 *
 */
public class PaymentBehaviorServiceImpl implements PaymentBehaviorService{
	//付费等级金额
	public Map<String,Object> queryRankMoney(String icons, String startDate, String endDate) {
		String sql = "select A.level, sum(A.count)revenue from log_charge A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where C.os in (" + icons + ") and DATE_FORMAT(timestamp,'%Y-%m-%d') between ? and ? group by A.level";
		List<LogCharge> rankMoney = LogCharge.dao.find(sql, startDate, endDate);

		Map<String, Object> data = new HashMap<String, Object>();
		List<Integer> level = new ArrayList<Integer>();
		List<Double> revenue = new ArrayList<Double>();
		
		for(LogCharge lc : rankMoney) {
			level.add(lc.getInt("level"));
			revenue.add(lc.getDouble("revenue"));
		}
		data.put("level", level);
		data.put("revenue", revenue);
		
		return data;
	}
	//付费等级次数
	public Map<String, Object> queryRankTimes(String icons, String startDate, String endDate) {
		String sql = "select A.level, count(*)count from log_charge A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where C.os in (" + icons + ") and DATE_FORMAT(timestamp,'%Y-%m-%d') between ? and ? group by A.level";
		List<LogCharge> rankTimes = LogCharge.dao.find(sql, startDate, endDate);
		
		Map<String, Object> data = new HashMap<String, Object>();
		List<Integer> level = new ArrayList<Integer>();
		List<Long> count = new ArrayList<Long>();
		
		for(LogCharge lc : rankTimes) {
			level.add(lc.getInt("level"));
			count.add(lc.getLong("count"));
		}
		data.put("level", level);
		data.put("count", count);
		
		return data;
	}
	//付费间隔 --首充游戏时间分布
	public List<Integer> queryFirstPeriod(List<String> categories, String icons, String startDate, String endDate) {
		String sql = "select UNIX_TIMESTAMP(A.timestamp)firstPaid,UNIX_TIMESTAMP(B.create_time)create_time from (select * from log_charge where DATE_FORMAT(timestamp,'%Y-%m-%d') between ? and ? and charge_times = 1) A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where C.os in (" + icons + ")";
		List<LogCharge> firstPeriod = LogCharge.dao.find(sql, startDate, endDate);
		
		//inital
		Map<String, Integer> sort = new LinkedHashMap<String, Integer>();
		for(String period : categories){
			sort.put(period, 0);
		}
		//load data
		for(LogCharge lc : firstPeriod) {
			long firstPaid = lc.getLong("firstPaid");
			long cTime = lc.getLong("create_time");
			long diff = firstPaid - cTime;
			diff = diff/60;
			collectSort(diff, sort);
		}
		
		List<Integer> data = new ArrayList<Integer>();
		data.addAll(sort.values());
		return data;
	}
	//付费间隔  --二充到首充时间分布
	public List<Integer> querySTFPeriod(List<String> categories, String icons, String startDate, String endDate) {
		String sql = "select UNIX_TIMESTAMP(A.timestamp)first,UNIX_TIMESTAMP(B.timestamp)second from (select*from log_charge where DATE_FORMAT(timestamp,'%Y-%m-%d') between ? and ? and charge_times = 1) A left join (select*from log_charge where DATE_FORMAT(timestamp,'%Y-%m-%d') between ? and ? and charge_times = 2) B on A.account = B.account join create_role C on A.account = C.account join device_info D on C.openudid = D.openudid where B.account is not null and D.os in (" + icons + ")";
		List<LogCharge> sTFPeriod = LogCharge.dao.find(sql, startDate, endDate, startDate, endDate);
		
		//inital
		Map<String, Integer> sort = new LinkedHashMap<String, Integer>();
		for(String period : categories){
			sort.put(period, 0);
		}
		//load data
		for(LogCharge lc : sTFPeriod) {
			long firstPaid = lc.getLong("first");
			long secondPaid = lc.getLong("second");
			long diff = secondPaid - firstPaid;
			diff = diff/60;
			collectSort(diff, sort);
		}
		
		List<Integer> data = new ArrayList<Integer>();
		data.addAll(sort.values());
		return data;
	}
	//三充到二充时间分布
	public List<Integer> queryTTSPeriod(List<String> categories, String icons, String startDate, String endDate) {
		String sql = "select UNIX_TIMESTAMP(A.timestamp)second,UNIX_TIMESTAMP(B.timestamp)third from (select*from log_charge where DATE_FORMAT(timestamp,'%Y-%m-%d') between ? and ? and charge_times = 2) A left join (select*from log_charge where DATE_FORMAT(timestamp,'%Y-%m-%d') between ? and ? and charge_times = 3) B on A.account = B.account join create_role C on A.account = C.account join device_info D on C.openudid = D.openudid where B.account is not null and D.os in (" + icons + ");";
		List<LogCharge> tTSPeriod = LogCharge.dao.find(sql, startDate, endDate, startDate, endDate);
		
		//inital
		Map<String, Integer> sort = new LinkedHashMap<String, Integer>();
		for(String period : categories){
			sort.put(period, 0);
		}
		//load data
		for(LogCharge lc : tTSPeriod) {
			long secondPaid = lc.getLong("second");
			long thirdPaid = lc.getLong("third");
			long diff = thirdPaid - secondPaid;
			diff = diff/60;
			collectSort(diff, sort);
		}
		
		List<Integer> data = new ArrayList<Integer>();
		data.addAll(sort.values());
		return data;
	}
	
	private void collectSort(long diff, Map<String, Integer> sort) {
		if(diff < 10){
			increaseValue("l10min", sort);
		}else if(diff>=10 && diff <=30){
			increaseValue("l30min", sort);
		}else if(diff>=30 && diff <=60){
			increaseValue("l60min", sort);
		}else if(diff>=60 && diff <=60*2){
			increaseValue("l2h", sort);
		}else if(diff>=60*2 && diff <=60*4){
			increaseValue("l4h", sort);
		}else if(diff>=60*4 && diff <=60*6){
			increaseValue("l6h", sort);
		}else if(diff>=60*6 && diff <=60*10){
			increaseValue("l10h", sort);
		}else if(diff>=60*10 && diff <=60*15){
			increaseValue("l15h", sort);
		}else if(diff>=60*15 && diff <=60*20){
			increaseValue("l20h", sort);
		}else if(diff>=60*20 && diff <=60*30){
			increaseValue("l30h", sort);
		}else if(diff>=60*30 && diff <=60*40){
			increaseValue("l40h", sort);
		}else if(diff>=60*40 && diff <=60*60){
			increaseValue("l60h", sort);
		}else if(diff>=60*60 && diff <=60*100){
			increaseValue("l100h", sort);
		}else if(diff> 60*100){
			increaseValue("m100h", sort);
		}
	}
	
	private void increaseValue(String key, Map<String, Integer> map){
		int value = map.get(key);
		value++;
		map.put(key, value);
	}
	
}
