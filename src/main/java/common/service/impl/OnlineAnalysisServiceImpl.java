 package common.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import common.model.Login;
import common.service.OnlineAnalysisService;

/**
 * 在线分析页
 * 
 * @author chris
 *
 */
public class OnlineAnalysisServiceImpl implements OnlineAnalysisService {
	private static Logger logger = Logger.getLogger(OnlineAnalysisServiceImpl.class);

	/** 
	 * 时段分布
	 * @param days 所选时间段的天数
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间
	 */
	public List<Long> queryPeriodDistribution(int days, String icons, String startDate, String endDate) {
		String sql = "select hour(A.login_time)hour,count(*)count from (select openudid,login_time from login where date between ? and ?) A join device_info B on A.openudid = B.openudid where B.os in ("
				+ icons + ") group by hour";
		List<Login> distribution = Login.dao.find(sql, startDate, endDate);
		// init
		Map<Integer, Long> sort = new LinkedHashMap<Integer, Long>();
		for (int i = 0; i < 24; i++) {
			sort.put(i, 0L);
		}
		
		for (Login l : distribution) {
			long count = l.getLong("count");
			count = count/(long)days;
			sort.put(l.getInt("hour"), count);
		}
		List<Long> data = new ArrayList<Long>();
		data.addAll(sort.values());
		logger.debug("queryPeriodDistribution:" + data);
		return data;
	}

	/**
	 *  启动次数接口
	 * @param categories 日期列表
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间  
	 */
	public List<Long> queryStartTimes(List<String> categories, String icons, String startDate, String endDate) {
		String sql = "select DATE_FORMAT(date,'%Y-%m-%d')date,count(*)count from (select openudid,date from login where date between ? and ?) A join device_info B on A.openudid = B.openudid where B.os in ("
				+ icons + ") group by date";
		List<Login> startTimes = Login.dao.find(sql, startDate, endDate);
		// init
		Map<String, Long> sort = new LinkedHashMap<String, Long>();
		for (String date : categories) {
			sort.put(date, 0L);
		}
		for (Login l : startTimes) {
			sort.put(l.getStr("date"), l.getLong("count"));
		}
		List<Long> data = new ArrayList<Long>();
		data.addAll(sort.values());
		return data;
	}

	/**
	 *  相邻启动间隔分布
	 * @param categories 日期列表
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间  
	 */
	public Map<String, Object> queryNeighborStartPeriod(List<String> categories, String icons, String startDate,
			String endDate) {
		String sql = "select A.account,A.timestamp from (select account,UNIX_TIMESTAMP(login_time)timestamp,openudid from login where date between ? and ?) A join device_info B on A.openudid = B.openudid where B.os in ("
				+ icons + ") order by A.timestamp";
		List<Login> nsp = Login.dao.find(sql, startDate, endDate);
		// Map<account, List<timestamp>>
		Map<String, List<Long>> sort = new HashMap<String, List<Long>>();
		for (Login l : nsp) {
			String account = l.getStr("account");
			Long count = l.getLong("timestamp");
			if (sort.containsKey(account)) {
				List<Long> subList = sort.get(account);
				subList.add(count);
				sort.put(account, subList);
			} else {
				List<Long> timestamp = new ArrayList<Long>();
				timestamp.add(count);
				sort.put(account, timestamp);
			}
		}
		// Map<period,Map<type,count>>
		Map<String, Integer> collectCount = new LinkedHashMap<String, Integer>();
		Map<String, Set<String>> collectPeople = new LinkedHashMap<String, Set<String>>();
		// init
		for (String s : categories) {
			collectCount.put(s, 0);
			collectPeople.put(s, new HashSet<String>());
		}
		// collect map data 收集次数,以及人数
		try {
			for (Map.Entry<String, List<Long>> entry : sort.entrySet()) {
				List<Long> subList = entry.getValue();
				String account = entry.getKey();
				for (int i = 1; i < subList.size(); i++) {
					long diff = subList.get(i) - subList.get(i - 1);
					diff = diff / 3600;
					String key = "";
					if (diff < 1) {
						key = "0~60 min";
						increaseValue(key, collectCount);
					} else if (diff >= 1 && diff < 2) {
						key = "1~2 h";
						increaseValue(key, collectCount);
					} else if (diff >= 2 && diff < 3) {
						key = "2~3 h";
						increaseValue(key, collectCount);
					} else if (diff >= 3 && diff < 4) {
						key = "3~4 h";
						increaseValue(key, collectCount);
					} else if (diff >= 4 && diff < 5) {
						key = "4~5 h";
						increaseValue(key, collectCount);
					} else if (diff >= 5 && diff < 8) {
						key = "5~8 h";
						increaseValue(key, collectCount);
					} else if (diff >= 8 && diff < 12) {
						key = "8~12 h";
						increaseValue(key, collectCount);
					} else if (diff >= 12 && diff < 24) {
						key = "12~24 h";
						increaseValue(key, collectCount);
					} else if (diff >= 24 && diff < 48) {
						key = "1~2 D";
						increaseValue(key, collectCount);
					} else if (diff >= 48 && diff < 72) {
						key = "2~3 D";
						increaseValue(key, collectCount);
					}
					//丢弃if条件不满足的情况
					if ("".equals(key)) {
						continue;
					}
					Set<String> set = collectPeople.get(key);
					set.add(account);
					collectPeople.put(key, set);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug("queryNeighborStartPeriod error:", e);
		}

		List<Integer> count = new ArrayList<Integer>();
		List<Integer> people = new ArrayList<Integer>();
		count.addAll(collectCount.values());
		for (Map.Entry<String, Set<String>> entry : collectPeople.entrySet()) {
			people.add(entry.getValue().size());
		}

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("count", count);
		data.put("people", people);
		return data;
	}

	private void increaseValue(String key, Map<String, Integer> map) {
		int value = map.get(key);
		value++;
		map.put(key, value);
	}
}
