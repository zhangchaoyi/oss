package common.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import common.model.Login;
import common.model.Logout;
import common.service.OnlineHabitsService;
import common.utils.DateUtils;

/**
 * 在线习惯页
 * 
 * @author chris
 *
 */
public class OnlineHabitsServiceImpl implements OnlineHabitsService {
	private static Logger logger = Logger.getLogger(OnlineHabitsServiceImpl.class);

	// 新增玩家日平均时长和次数
	public Map<String, Object> queryAddpDayAvgGP(List<String> categories, String icons, String startDate,
			String endDate) {
		String timesSql = "select DATE_FORMAT(A.date_time,'%Y-%m-%d')date,count(*)count,count(distinct A.account)ap from (select A.account,A.date_time from create_role A join device_info B on A.openudid = B.openudid where A.date_time between ? and ? and B.os in ("
				+ icons + ")) A join login B on A.account = B.account and A.date_time = B.date group by A.date_time";
		String timeSql = "select DATE_FORMAT(A.date_time,'%Y-%m-%d')date,sum(B.online_time)online_time,count(distinct A.account)ap from (select A.account,A.date_time from create_role A join device_info B on A.openudid = B.openudid where A.date_time between ? and ? and B.os in ("
				+ icons + ")) A join logout B on A.account = B.account and A.date_time = B.date group by A.date_time;";

		List<Login> qTimes = Login.dao.find(timesSql, startDate, endDate);
		List<Logout> qTime = Logout.dao.find(timeSql, startDate, endDate);

		Map<String, Map<String, Double>> sort = new LinkedHashMap<String, Map<String, Double>>();
		// init
		for (String date : categories) {
			Map<String, Double> subMap = new HashMap<String, Double>();
			subMap.put("times", 0.0);
			subMap.put("time", 0.0);
			sort.put(date, subMap);
		}

		try {
			// 每玩家平均游戏次数
			for (Login l : qTimes) {
				String date = l.getStr("date");
				long count = l.getLong("count");
				long ap = l.getLong("ap");
				double avgTimes = 0.0;
				if (ap != 0) {
					avgTimes = (double) count / (double) ap;
					BigDecimal bg = new BigDecimal(avgTimes);
					avgTimes = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				}
				Map<String, Double> subMap = sort.get(date);
				subMap.put("times", avgTimes);
				sort.put(date, subMap);
			}
			// 每玩家平均游戏时长 --分钟
			for (Logout l : qTime) {
				String date = l.getStr("date");
				long onlineTime = l.getBigDecimal("online_time")==null?0L:l.getBigDecimal("online_time").longValue();
				long ap = l.getLong("ap");
				double avgTime = 0.0;
				if (ap != 0) {
					avgTime = (double) onlineTime / (double) (60 * ap);
					BigDecimal bg = new BigDecimal(avgTime);
					avgTime = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				}
				Map<String, Double> subMap = sort.get(date);
				subMap.put("time", avgTime);
				sort.put(date, subMap);
			}
		} catch (Exception e) {
			logger.debug("Error queryAddpDayAvgGP:", e);
		}
		List<Double> times = new ArrayList<Double>();
		List<Double> time = new ArrayList<Double>();

		for (Map.Entry<String, Map<String, Double>> entry : sort.entrySet()) {
			for (Map.Entry<String, Double> subEntry : entry.getValue().entrySet()) {
				switch (subEntry.getKey()) {
				case "times":
					times.add(subEntry.getValue());
					break;
				case "time":
					time.add(subEntry.getValue());
					break;
				}
			}
		}

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("times", times);
		data.put("time", time);
		logger.debug("queryAddpDayAvgGP:" + data);
		return data;
	}

	// 新增玩家周平均时长和次数
	public Map<String, Object> queryAddpWeekAvgGP(String icons, String startDate, String endDate) {
		Map<String, String> week = DateUtils.divideDateToWeek(startDate, endDate);
		String timesSql = "select count(*)count,count(distinct A.account)ap from (select A.account,A.date_time from create_role A join device_info B on A.openudid = B.openudid where A.date_time between ? and ? and B.os in ("
				+ icons + ")) A join login B on A.account = B.account and A.date_time = B.date";
		String timeSql = "select sum(B.online_time)online_time,count(distinct A.account)ap from (select A.account,A.date_time from create_role A join device_info B on A.openudid = B.openudid where A.date_time between ? and ? and B.os in ("
				+ icons + ")) A join logout B on A.account = B.account and A.date_time = B.date;";

		Map<String, Map<String, Double>> sort = new LinkedHashMap<String, Map<String, Double>>();

		try {
			// init & load data
			for (Map.Entry<String, String> entry : week.entrySet()) {
				String start = entry.getKey();
				String end = entry.getValue();
				// init
				String period = start + "~" + end;
				Map<String, Double> subMap = new HashMap<String, Double>();
				subMap.put("times", 0.0);
				subMap.put("time", 0.0);

				// load
				List<Login> qTimes = Login.dao.find(timesSql, start, end);
				List<Logout> qTime = Logout.dao.find(timeSql, start, end);
				// 每玩家平均游戏次数 --最多只有一条记录
				for (Login l : qTimes) {
					long count = l.getLong("count");
					long ap = l.getLong("ap");
					double avgTimes = 0.0;
					if (ap != 0) {
						avgTimes = (double) count / (double) ap;
						BigDecimal bg = new BigDecimal(avgTimes);
						avgTimes = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
					}
					subMap.put("times", avgTimes);
				}
				// 每玩家平均游戏时长 --分钟 --最多只有一条记录
				for (Logout l : qTime) {
					long onlineTime = l.getBigDecimal("online_time") == null ? 0L
							: l.getBigDecimal("online_time").longValue();
					long ap = l.getLong("ap");
					double avgTime = 0.0;
					if (ap != 0) {
						avgTime = (double) onlineTime / (double) (60 * ap);
						BigDecimal bg = new BigDecimal(avgTime);
						avgTime = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
					}
					subMap.put("time", avgTime);
				}
				sort.put(period, subMap);
			}
		} catch (Exception e) {
			logger.debug("Error queryAddpWeekAvgGP:", e);
		}

		List<Double> times = new ArrayList<Double>();
		List<Double> time = new ArrayList<Double>();
		for (Map.Entry<String, Map<String, Double>> entry : sort.entrySet()) {
			for (Map.Entry<String, Double> subEntry : entry.getValue().entrySet()) {
				switch (subEntry.getKey()) {
				case "times":
					times.add(subEntry.getValue());
					break;
				case "time":
					time.add(subEntry.getValue());
					break;

				}
			}
		}

		List<String> categories = new ArrayList<String>();
		categories.addAll(sort.keySet());

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("times", times);
		data.put("time", time);
		data.put("categories", categories);
		logger.debug("queryAddpWeekAvgGP:" + data);
		return data;
	}
	
	// 新增玩家月平均时长和次数
	public Map<String, Object> queryAddpMonthAvgGP(List<String>categories, String icons, String startDate, String endDate) {
		String start = DateUtils.monthToStr(DateUtils.strToDate(startDate));
		String end = DateUtils.monthToStr(DateUtils.strToDate(endDate));
		
		String timesSql = "select DATE_FORMAT(A.date_time,'%Y-%m')month,count(*)count,count(distinct A.account)ap from (select A.account,A.date_time from create_role A join device_info B on A.openudid = B.openudid where DATE_FORMAT(A.date_time,'%Y-%m') between ? and ? and B.os in (" + icons + ")) A join login B on A.account = B.account and A.date_time = B.date group by month;";
		String timeSql = "select DATE_FORMAT(A.date_time,'%Y-%m')month,sum(B.online_time)online_time,count(distinct A.account)ap from (select A.account,A.date_time from create_role A join device_info B on A.openudid = B.openudid where DATE_FORMAT(A.date_time,'%Y-%m') between ? and ? and B.os in (" + icons + ")) A join logout B on A.account = B.account and A.date_time = B.date group by month;";
	
		List<String> month = categories;
		
		Map<String, Map<String, Double>> sort = new LinkedHashMap<String, Map<String, Double>>();
		//init
		for(String m : month) {
			Map<String, Double> subMap = new HashMap<String, Double>();
			subMap.put("times", 0.0);
			subMap.put("time", 0.0);
		}
		
		Map<String, Object> data = new HashMap<String, Object>();
		return data;
	}
	
	// 活跃玩家日平均时长和次数
	public Map<String, Object> queryActivepDayAvgGP(List<String> categories, String icons, String startDate,
			String endDate) {
		String timesSql = "select DATE_FORMAT(A.date,'%Y-%m-%d')date,count(*)count,count(distinct A.account)ap from (select account,openudid,date from login where date between ? and ? ) A join device_info B on A.openudid = B.openudid where B.os in ("
				+ icons + ") group by A.date";
		String timeSql = "select DATE_FORMAT(A.date,'%Y-%m-%d')date,sum(A.online_time)online_time,count(distinct A.account)ap from (select account,online_time,date from logout where date between ? and ?) A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where C.os in ("
				+ icons + ") group by A.date";

		List<Login> qTimes = Login.dao.find(timesSql, startDate, endDate);
		List<Logout> qTime = Logout.dao.find(timeSql, startDate, endDate);

		Map<String, Map<String, Double>> sort = new LinkedHashMap<String, Map<String, Double>>();
		// init
		for (String date : categories) {
			Map<String, Double> subMap = new HashMap<String, Double>();
			subMap.put("times", 0.0);
			subMap.put("time", 0.0);
			sort.put(date, subMap);
		}
		try {
			// 每玩家平均游戏次数
			for (Login l : qTimes) {
				String date = l.getStr("date");
				long count = l.getLong("count");
				long ap = l.getLong("ap");
				double avgTimes = 0.0;
				if (ap != 0) {
					avgTimes = (double) count / (double) ap;
					BigDecimal bg = new BigDecimal(avgTimes);
					avgTimes = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				}
				Map<String, Double> subMap = sort.get(date);
				subMap.put("times", avgTimes);
				sort.put(date, subMap);
			}
			// 每玩家平均游戏时长 --分钟
			for (Logout l : qTime) {
				String date = l.getStr("date");
				long onlineTime = l.getBigDecimal("online_time")==null?0L:l.getBigDecimal("online_time").longValue();
				long ap = l.getLong("ap");
				double avgTime = 0.0;
				if (ap != 0) {
					avgTime = (double) onlineTime / (double) (60 * ap);
					BigDecimal bg = new BigDecimal(avgTime);
					avgTime = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				}
				Map<String, Double> subMap = sort.get(date);
				subMap.put("time", avgTime);
				sort.put(date, subMap);
			}
		} catch (Exception e) {
			logger.debug("Error queryActivepDayAvgGP:", e);
		}
		List<Double> times = new ArrayList<Double>();
		List<Double> time = new ArrayList<Double>();

		for (Map.Entry<String, Map<String, Double>> entry : sort.entrySet()) {
			for (Map.Entry<String, Double> subEntry : entry.getValue().entrySet()) {
				switch (subEntry.getKey()) {
				case "times":
					times.add(subEntry.getValue());
					break;
				case "time":
					time.add(subEntry.getValue());
					break;
				}
			}
		}

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("times", times);
		data.put("time", time);
		logger.debug("queryActivepDayAvgGP:" + data);
		return data;
	}

	// 活跃玩家周平均时长和次数
	public Map<String, Object> queryActivepWeekAvgGP(String icons, String startDate, String endDate) {
		Map<String, String> week = DateUtils.divideDateToWeek(startDate, endDate);
		String timesSql = "select count(*)count,count(distinct A.account)ap from (select account,openudid,date from login where date between ? and ? ) A join device_info B on A.openudid = B.openudid where B.os in (" + icons + ")";
		String timeSql = "select sum(A.online_time)online_time,count(distinct A.account)ap from (select account,online_time,date from logout where date between ? and ?) A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where C.os in (" + icons + ")";

		Map<String, Map<String, Double>> sort = new LinkedHashMap<String, Map<String, Double>>();

		try {
			// init & load data
			for (Map.Entry<String, String> entry : week.entrySet()) {
				String start = entry.getKey();
				String end = entry.getValue();
				// init
				String period = start + "~" + end;
				Map<String, Double> subMap = new HashMap<String, Double>();
				subMap.put("times", 0.0);
				subMap.put("time", 0.0);

				// load
				List<Login> qTimes = Login.dao.find(timesSql, start, end);
				List<Logout> qTime = Logout.dao.find(timeSql, start, end);
				// 每玩家平均游戏次数 --最多只有一条记录
				for (Login l : qTimes) {
					long count = l.getLong("count");
					long ap = l.getLong("ap");
					double avgTimes = 0.0;
					if (ap != 0) {
						avgTimes = (double) count / (double) ap;
						BigDecimal bg = new BigDecimal(avgTimes);
						avgTimes = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
					}
					subMap.put("times", avgTimes);
				}
				// 每玩家平均游戏时长 --分钟 --最多只有一条记录
				for (Logout l : qTime) {
					long onlineTime = l.getBigDecimal("online_time") == null ? 0L
							: l.getBigDecimal("online_time").longValue();
					long ap = l.getLong("ap");
					double avgTime = 0.0;
					if (ap != 0) {
						avgTime = (double) onlineTime / (double) (60 * ap);
						BigDecimal bg = new BigDecimal(avgTime);
						avgTime = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
					}
					subMap.put("time", avgTime);
				}
				sort.put(period, subMap);
			}
		} catch (Exception e) {
			logger.debug("Error queryActivepWeekAvgGP:", e);
		}

		List<Double> times = new ArrayList<Double>();
		List<Double> time = new ArrayList<Double>();
		for (Map.Entry<String, Map<String, Double>> entry : sort.entrySet()) {
			for (Map.Entry<String, Double> subEntry : entry.getValue().entrySet()) {
				switch (subEntry.getKey()) {
				case "times":
					times.add(subEntry.getValue());
					break;
				case "time":
					time.add(subEntry.getValue());
					break;

				}
			}
		}

		List<String> categories = new ArrayList<String>();
		categories.addAll(sort.keySet());

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("times", times);
		data.put("time", time);
		data.put("categories", categories);
		logger.debug("queryActivepWeekAvgGP:" + data);
		return data;
	}
	
	// 付费玩家日平均时长和次数
	public Map<String, Object> queryPpDayAvgGP(List<String> categories, String icons, String startDate,
			String endDate) {
		String timesSql = "select DATE_FORMAT(A.date,'%Y-%m-%d')date,count(*)count,count(distinct A.account)pp from (select distinct A.account,DATE_FORMAT(A.timestamp,'%Y-%m-%d')date from log_charge A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where DATE_FORMAT(A.timestamp,'%Y-%m-%d') between ? and ? and C.os in ("
				+ icons + ")) A join login B on A.account = B.account and A.date = B.date group by A.date";
		String timeSql = "select DATE_FORMAT(A.date,'%Y-%m-%d')date,sum(B.online_time)online_time,count(distinct A.account)pp from (select distinct A.account,DATE_FORMAT(A.timestamp,'%Y-%m-%d')date from log_charge A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where DATE_FORMAT(A.timestamp,'%Y-%m-%d') between ? and ? and C.os in ("
				+ icons + ")) A join logout B on A.account = B.account and A.date = B.date group by A.date;";

		List<Login> qTimes = Login.dao.find(timesSql, startDate, endDate);
		List<Logout> qTime = Logout.dao.find(timeSql, startDate, endDate);

		Map<String, Map<String, Double>> sort = new LinkedHashMap<String, Map<String, Double>>();
		// init
		for (String date : categories) {
			Map<String, Double> subMap = new HashMap<String, Double>();
			subMap.put("times", 0.0);
			subMap.put("time", 0.0);
			sort.put(date, subMap);
		}
		try {
			// 每玩家平均游戏次数
			for (Login l : qTimes) {
				String date = l.getStr("date");
				long count = l.getLong("count");
				long pp = l.getLong("pp");
				double avgTimes = 0.0;
				if (pp != 0) {
					avgTimes = (double) count / (double) pp;
					BigDecimal bg = new BigDecimal(avgTimes);
					avgTimes = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				}
				Map<String, Double> subMap = sort.get(date);
				subMap.put("times", avgTimes);
				sort.put(date, subMap);
			}
			// 每玩家平均游戏时长 --分钟
			for (Logout l : qTime) {
				String date = l.getStr("date");
				long onlineTime = l.getBigDecimal("online_time")==null?0L:l.getBigDecimal("online_time").longValue();
				long pp = l.getLong("pp");
				double avgTime = 0.0;
				if (pp != 0) {
					avgTime = (double) onlineTime / (double) (60 * pp);
					BigDecimal bg = new BigDecimal(avgTime);
					avgTime = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				}
				Map<String, Double> subMap = sort.get(date);
				subMap.put("time", avgTime);
				sort.put(date, subMap);
			}
		} catch (Exception e) {
			logger.debug("Error queryPpDayAvgGP:", e);
		}
		List<Double> times = new ArrayList<Double>();
		List<Double> time = new ArrayList<Double>();

		for (Map.Entry<String, Map<String, Double>> entry : sort.entrySet()) {
			for (Map.Entry<String, Double> subEntry : entry.getValue().entrySet()) {
				switch (subEntry.getKey()) {
				case "times":
					times.add(subEntry.getValue());
					break;
				case "time":
					time.add(subEntry.getValue());
					break;
				}
			}
		}

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("times", times);
		data.put("time", time);
		logger.debug("queryPpDayAvgGP:" + data);
		return data;
	}
	
	// 付费玩家周平均时长和次数
	public Map<String, Object> queryPpWeekAvgGP(String icons, String startDate, String endDate) {
		Map<String, String> week = DateUtils.divideDateToWeek(startDate, endDate);
		String timesSql = "select count(*)count,count(distinct A.account)pp from (select distinct A.account,DATE_FORMAT(A.timestamp,'%Y-%m-%d')date from log_charge A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where DATE_FORMAT(A.timestamp,'%Y-%m-%d') between ? and ? and C.os in (" + icons + ")) A join login B on A.account = B.account and A.date = B.date";
		String timeSql = "select sum(B.online_time)online_time,count(distinct A.account)pp from (select distinct A.account,DATE_FORMAT(A.timestamp,'%Y-%m-%d')date from log_charge A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where DATE_FORMAT(A.timestamp,'%Y-%m-%d') between ? and ? and C.os in (" + icons + ")) A join logout B on A.account = B.account and A.date = B.date;";
		
		Map<String, Map<String, Double>> sort = new LinkedHashMap<String, Map<String, Double>>();

		try {
			// init & load data
			for (Map.Entry<String, String> entry : week.entrySet()) {
				String start = entry.getKey();
				String end = entry.getValue();
				// init
				String period = start + "~" + end;
				Map<String, Double> subMap = new HashMap<String, Double>();
				subMap.put("times", 0.0);
				subMap.put("time", 0.0);

				// load
				List<Login> qTimes = Login.dao.find(timesSql, start, end);
				List<Logout> qTime = Logout.dao.find(timeSql, start, end);
				// 每玩家平均游戏次数 --最多只有一条记录
				for (Login l : qTimes) {
					long count = l.getLong("count");
					long ap = l.getLong("pp");
					double avgTimes = 0.0;
					if (ap != 0) {
						avgTimes = (double) count / (double) ap;
						BigDecimal bg = new BigDecimal(avgTimes);
						avgTimes = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
					}
					subMap.put("times", avgTimes);
				}
				// 每玩家平均游戏时长 --分钟 --最多只有一条记录
				for (Logout l : qTime) {
					long onlineTime = l.getBigDecimal("online_time") == null ? 0L
							: l.getBigDecimal("online_time").longValue();
					long ap = l.getLong("pp");
					double avgTime = 0.0;
					if (ap != 0) {
						avgTime = (double) onlineTime / (double) (60 * ap);
						BigDecimal bg = new BigDecimal(avgTime);
						avgTime = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
					}
					subMap.put("time", avgTime);
				}
				sort.put(period, subMap);
			}
		} catch (Exception e) {
			logger.debug("Error queryPpWeekAvgGP:", e);
		}

		List<Double> times = new ArrayList<Double>();
		List<Double> time = new ArrayList<Double>();
		for (Map.Entry<String, Map<String, Double>> entry : sort.entrySet()) {
			for (Map.Entry<String, Double> subEntry : entry.getValue().entrySet()) {
				switch (subEntry.getKey()) {
				case "times":
					times.add(subEntry.getValue());
					break;
				case "time":
					time.add(subEntry.getValue());
					break;

				}
			}
		}

		List<String> categories = new ArrayList<String>();
		categories.addAll(sort.keySet());

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("times", times);
		data.put("time", time);
		data.put("categories", categories);
		logger.debug("queryPpWeekAvgGP:" + data);
		return data;
	}
}
