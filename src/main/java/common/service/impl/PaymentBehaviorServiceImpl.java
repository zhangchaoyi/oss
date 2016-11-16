package common.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import common.model.LogCharge;
import common.service.PaymentBehaviorService;

/**
 * 付费行为页
 * @author chris
 *
 */
public class PaymentBehaviorServiceImpl implements PaymentBehaviorService{
	private static Logger logger = Logger.getLogger(PaymentBehaviorServiceImpl.class);
	/**
	 * 付费等级金额
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间 
	 */
	public Map<String,Object> queryRankMoney(String icons, String startDate, String endDate) {
		String sql = "select A.level, sum(A.count)revenue from log_charge A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where C.os in (" + icons + ") and DATE_FORMAT(timestamp,'%Y-%m-%d') between ? and ? group by A.level";
		List<LogCharge> rankMoney = LogCharge.dao.find(sql, startDate, endDate);

		Map<String, Object> data = new HashMap<String, Object>();
		List<Integer> level = new ArrayList<Integer>();
		List<Double> revenue = new ArrayList<Double>();
		
		for(LogCharge lc : rankMoney) {
			level.add(lc.getInt("level")==null?0:lc.getInt("level"));
			revenue.add(lc.getDouble("revenue"));
		}
		data.put("level", level);
		data.put("revenue", revenue);
		logger.info("data:" + data);
		return data;
	}
	/**
	 * 付费等级次数
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间
	 */
	public Map<String, Object> queryRankTimes(String icons, String startDate, String endDate) {
		String sql = "select A.level, count(*)count from log_charge A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where C.os in (" + icons + ") and DATE_FORMAT(timestamp,'%Y-%m-%d') between ? and ? group by A.level";
		List<LogCharge> rankTimes = LogCharge.dao.find(sql, startDate, endDate);
		
		Map<String, Object> data = new HashMap<String, Object>();
		List<Integer> level = new ArrayList<Integer>();
		List<Long> count = new ArrayList<Long>();
		
		for(LogCharge lc : rankTimes) {
			level.add(lc.getInt("level")==null?0:lc.getInt("level"));
			count.add(lc.getLong("count"));
		}
		data.put("level", level);
		data.put("count", count);
		logger.info("data:" + data);
		return data;
	}
	/**
	 * 付费间隔 --首充游戏时间分布
	 * @param categories 日期列表
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间
	 */
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
	/**
	 * 付费间隔  --二充到首充时间分布
	 * @param categories 日期列表
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间
	 */
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
	/**
	 * 三充到二充时间分布
	 * @param categories 日期列表
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间
	 */
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
	
	/**
	 * 玩家首付周期 --游戏天数
	 * @param categories 日期列表
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间
	 */
	public List<Integer> queryFpGameDays(List<String> categories, String icons, String startDate, String endDate) {
		String sql = "select count(*)count from (select*from log_charge where DATE_FORMAT(timestamp,'%Y-%m-%d')between ? and ? and charge_times = 1)A join (select E.account,E.date from login E join device_info F on E.openudid = F.openudid where E.date <= ? and F.os in (" + icons + ") group by E.date,E.account) B on A.account = B.account where DATE_FORMAT(A.timestamp,'%Y-%m-%d') >= B.date group by A.account;";
		List<LogCharge> gdPeriod = LogCharge.dao.find(sql, startDate, endDate, endDate);
		
		//inital
		Map<String, Integer> sort = new LinkedHashMap<String, Integer>();
		for(String period : categories){
			sort.put(period, 0);
		}
		
		//load data
		for(LogCharge lc : gdPeriod) {
			long count = lc.getLong("count");
			if(count==1){
				increaseValue("d1", sort);
			}else if(count>=2 && count<=3){
				increaseValue("d2", sort);
			}else if(count>=4 && count<=7){
				increaseValue("d4", sort);
			}else if(count>=8 && count<=14){
				increaseValue("w2", sort);
			}else if(count>=15 && count<=21){
				increaseValue("w3", sort);
			}else if(count>=22 && count<=28){
				increaseValue("w4", sort);
			}else if(count>=29 && count<=35){
				increaseValue("w5", sort);
			}else if(count>=36 && count<=42){
				increaseValue("w6", sort);
			}else if(count>=43 && count<=49){
				increaseValue("w7", sort);
			}else if(count>=50 && count<=56){
				increaseValue("w8", sort);
			}else if(count>=57 && count<=84){
				increaseValue("w9", sort);
			}else if(count>84){
				increaseValue("w12", sort);
			}
		}
		
		List<Integer> data = new ArrayList<Integer>();
		data.addAll(sort.values());
		return data;
	}
	
	/**
	 * 玩家首付周期 --累计游戏时长
	 * @param categories 日期列表
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间
	 */
	public List<Integer> queryFpGamePeriod(List<String> categories, String icons, String startDate, String endDate) {
		String sql = "select sum(online_time)online_time from (select*from log_charge where DATE_FORMAT(timestamp,'%Y-%m-%d')between ? and ? and charge_times = 1)A join (select * from logout where date < ?) B on A.account = B.account join create_role C on A.account = C.account join device_info D on C.openudid = D.openudid where DATE_FORMAT(A.timestamp,'%Y-%m-%d') >= B.date and D.os in (" + icons + ") group by A.account";
		List<LogCharge> gamePeriod = LogCharge.dao.find(sql, startDate, endDate, endDate);
		
		//inital
		Map<String, Integer> sort = new LinkedHashMap<String, Integer>();
		for(String period : categories){
			sort.put(period, 0);
		}
		//load data
		for(LogCharge lc : gamePeriod) {
			long onlineTime = lc.getBigDecimal("online_time").longValue();
			collectSort(onlineTime, sort);
		}
		
		List<Integer> data = new ArrayList<Integer>();
		data.addAll(sort.values());
		return data;
	}
	
	/**
	 * 玩家首付等级
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间
	 */
	public Map<String, Object> queryFpRank(String icons, String startDate, String endDate) {
		String sql = "select A.level,count(*)count from log_charge A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where DATE_FORMAT(A.timestamp,'%Y-%m-%d')between ? and ? and A.charge_times = 1 and C.os in (" + icons + ") group by A.level";
		List<LogCharge> fpRank = LogCharge.dao.find(sql, startDate, endDate);
		
		Map<String, Object> data = new HashMap<String, Object>();
		List<Integer> levelList = new ArrayList<Integer>();
		List<Long> countList = new ArrayList<Long>();
		
		//load data
		for(LogCharge lc : fpRank) {
			int level = lc.getInt("level");
			long count = lc.getLong("count");
			levelList.add(level);
			countList.add(count);
		}
		data.put("level", levelList);
		data.put("count", countList);
		logger.info("data:" + data);
		return data;
	}
	/**
	 * 玩家首付金额
	 * @param categories 日期列表
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间
	 */
	public List<Integer> queryFpMoney(List<String> categories, String icons, String startDate, String endDate) {
		String sql = "select A.count revenue from log_charge A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where DATE_FORMAT(A.timestamp,'%Y-%m-%d')between ? and ? and A.charge_times = 1 and C.os in (" + icons + ")";
		List<LogCharge> fpMoney = LogCharge.dao.find(sql, startDate, endDate);
		
		//inital
		Map<String, Integer> sort = new LinkedHashMap<String, Integer>();
		for(String period : categories){
			sort.put(period, 0);
		}
		//load data
		for(LogCharge lc : fpMoney) {
			double revenue = lc.getDouble("revenue");
			if(revenue<=10){
				increaseValue("m1", sort);
			}else if(revenue>10 && revenue<=50){
				increaseValue("m11", sort);
			}else if(revenue>50 && revenue<=100){
				increaseValue("m51", sort);
			}else if(revenue>100 && revenue<=200){
				increaseValue("m101", sort);
			}else if(revenue>200 && revenue<=500){
				increaseValue("m201", sort);
			}else if(revenue>500 && revenue<=1000){
				increaseValue("m501", sort);
			}else if(revenue>1000){
				increaseValue("m1000", sort);
			}
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
