package common.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

import common.model.CreateRole;
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
				long onlineTime = l.getBigDecimal("online_time") == null ? 0L
						: l.getBigDecimal("online_time").longValue();
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
	public Map<String, Object> queryAddpMonthAvgGP(List<String> categories, String icons, String startDate,
			String endDate) {
		String start = DateUtils.monthToStr(DateUtils.strToDate(startDate));
		String end = DateUtils.monthToStr(DateUtils.strToDate(endDate));

		String timesSql = "select DATE_FORMAT(A.date_time,'%Y-%m')month,count(*)count,count(distinct A.account)ap from (select A.account,A.date_time from create_role A join device_info B on A.openudid = B.openudid where DATE_FORMAT(A.date_time,'%Y-%m') between ? and ? and B.os in ("
				+ icons + ")) A join login B on A.account = B.account and A.date_time = B.date group by month;";
		String timeSql = "select DATE_FORMAT(A.date_time,'%Y-%m')month,sum(B.online_time)online_time,count(distinct A.account)ap from (select A.account,A.date_time from create_role A join device_info B on A.openudid = B.openudid where DATE_FORMAT(A.date_time,'%Y-%m') between ? and ? and B.os in ("
				+ icons + ")) A join logout B on A.account = B.account and A.date_time = B.date group by month;";

		List<Login> qTimes = Login.dao.find(timesSql, start, end);
		List<Logout> qTime = Logout.dao.find(timeSql, start, end);
		List<String> month = categories;

		// Map<month,Map<type,value>>
		Map<String, Map<String, Double>> sort = new LinkedHashMap<String, Map<String, Double>>();
		try {
			// init
			for (String m : month) {
				Map<String, Double> subMap = new HashMap<String, Double>();
				subMap.put("times", 0.0);
				subMap.put("time", 0.0);
				sort.put(m, subMap);
			}
			// 每玩家平均游戏次数
			for (Login l : qTimes) {
				String m = l.getStr("month");
				long count = l.getLong("count");
				long ap = l.getLong("ap");
				double avgTimes = 0.0;
				if (ap != 0) {
					avgTimes = (double) count / (double) ap;
					BigDecimal bg = new BigDecimal(avgTimes);
					avgTimes = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				}
				Map<String, Double> subMap = sort.get(m);
				subMap.put("times", avgTimes);
				sort.put(m, subMap);
			}
			// 每玩家平均游戏时长 --分钟
			for (Logout l : qTime) {
				String m = l.getStr("month");
				long onlineTime = l.getBigDecimal("online_time").longValue();
				long ap = l.getLong("ap");
				double avgTime = 0.0;
				if (ap != 0) {
					avgTime = (double) onlineTime / (double) (60 * ap);
					BigDecimal bg = new BigDecimal(avgTime);
					avgTime = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				}
				Map<String, Double> subMap = sort.get(m);
				subMap.put("time", avgTime);
				sort.put(m, subMap);
			}
		} catch (Exception e) {
			logger.debug("Error queryAddpMonthAvgGP:", e);
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
		logger.debug("queryAddpMonthAvgGP:" + data);
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
				long onlineTime = l.getBigDecimal("online_time") == null ? 0L
						: l.getBigDecimal("online_time").longValue();
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
		String timesSql = "select count(*)count,count(distinct A.account)ap from (select account,openudid,date from login where date between ? and ? ) A join device_info B on A.openudid = B.openudid where B.os in ("
				+ icons + ")";
		String timeSql = "select sum(A.online_time)online_time,count(distinct A.account)ap from (select account,online_time,date from logout where date between ? and ?) A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where C.os in ("
				+ icons + ")";

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

	// 活跃玩家月平均时长和次数
	public Map<String, Object> queryActivepMonthAvgGP(List<String> categories, String icons, String startDate,
			String endDate) {
		String start = DateUtils.monthToStr(DateUtils.strToDate(startDate));
		String end = DateUtils.monthToStr(DateUtils.strToDate(endDate));

		String timesSql = "select A.month,count(*)count,count(distinct A.account)ap from (select account,openudid,DATE_FORMAT(date,'%Y-%m')month from login where DATE_FORMAT(date,'%Y-%m') between ? and ? ) A join device_info B on A.openudid = B.openudid where B.os in ("
				+ icons + ") group by A.month;";
		String timeSql = "select A.month,sum(A.online_time)online_time,count(distinct A.account)ap from (select account,online_time,DATE_FORMAT(date,'%Y-%m')month from logout where DATE_FORMAT(date,'%Y-%m') between ? and ?) A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where C.os in ("
				+ icons + ") group by A.month;";

		List<Login> qTimes = Login.dao.find(timesSql, start, end);
		List<Logout> qTime = Logout.dao.find(timeSql, start, end);
		List<String> month = categories;

		// Map<month,Map<type,value>>
		Map<String, Map<String, Double>> sort = new LinkedHashMap<String, Map<String, Double>>();
		try {
			// init
			for (String m : month) {
				Map<String, Double> subMap = new HashMap<String, Double>();
				subMap.put("times", 0.0);
				subMap.put("time", 0.0);
				sort.put(m, subMap);
			}
			// 每玩家平均游戏次数
			for (Login l : qTimes) {
				String m = l.getStr("month");
				long count = l.getLong("count");
				long ap = l.getLong("ap");
				double avgTimes = 0.0;
				if (ap != 0) {
					avgTimes = (double) count / (double) ap;
					BigDecimal bg = new BigDecimal(avgTimes);
					avgTimes = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				}
				Map<String, Double> subMap = sort.get(m);
				subMap.put("times", avgTimes);
				sort.put(m, subMap);
			}
			// 每玩家平均游戏时长 --分钟
			for (Logout l : qTime) {
				String m = l.getStr("month");
				long onlineTime = l.getBigDecimal("online_time").longValue();
				long ap = l.getLong("ap");
				double avgTime = 0.0;
				if (ap != 0) {
					avgTime = (double) onlineTime / (double) (60 * ap);
					BigDecimal bg = new BigDecimal(avgTime);
					avgTime = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				}
				Map<String, Double> subMap = sort.get(m);
				subMap.put("time", avgTime);
				sort.put(m, subMap);
			}
		} catch (Exception e) {
			logger.debug("Error queryActivepMonthAvgGP:", e);
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
		logger.debug("queryActivepMonthAvgGP:" + data);
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
				long onlineTime = l.getBigDecimal("online_time") == null ? 0L
						: l.getBigDecimal("online_time").longValue();
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
		String timesSql = "select count(*)count,count(distinct A.account)pp from (select distinct A.account,DATE_FORMAT(A.timestamp,'%Y-%m-%d')date from log_charge A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where DATE_FORMAT(A.timestamp,'%Y-%m-%d') between ? and ? and C.os in ("
				+ icons + ")) A join login B on A.account = B.account and A.date = B.date";
		String timeSql = "select sum(B.online_time)online_time,count(distinct A.account)pp from (select distinct A.account,DATE_FORMAT(A.timestamp,'%Y-%m-%d')date from log_charge A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where DATE_FORMAT(A.timestamp,'%Y-%m-%d') between ? and ? and C.os in ("
				+ icons + ")) A join logout B on A.account = B.account and A.date = B.date;";

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

	// 付费玩家月平均时长和次数
	public Map<String, Object> queryPpMonthAvgGP(List<String> categories, String icons, String startDate,
			String endDate) {
		String start = DateUtils.monthToStr(DateUtils.strToDate(startDate));
		String end = DateUtils.monthToStr(DateUtils.strToDate(endDate));

		String timesSql = "select DATE_FORMAT(A.date,'%Y-%m')month,count(*)count,count(distinct A.account)pp from (select distinct A.account,DATE_FORMAT(A.timestamp,'%Y-%m-%d')date from log_charge A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where DATE_FORMAT(A.timestamp,'%Y-%m') between ? and ? and C.os in ("
				+ icons + ")) A join login B on A.account = B.account and A.date = B.date group by month";
		String timeSql = "select DATE_FORMAT(A.date,'%Y-%m')month,sum(B.online_time)online_time,count(distinct A.account)pp from (select distinct A.account,DATE_FORMAT(A.timestamp,'%Y-%m-%d')date from log_charge A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where DATE_FORMAT(A.timestamp,'%Y-%m') between ? and ? and C.os in ("
				+ icons + ")) A join logout B on A.account = B.account and A.date = B.date group by month;";

		List<Login> qTimes = Login.dao.find(timesSql, start, end);
		List<Logout> qTime = Logout.dao.find(timeSql, start, end);
		List<String> month = categories;

		// Map<month,Map<type,value>>
		Map<String, Map<String, Double>> sort = new LinkedHashMap<String, Map<String, Double>>();
		try {
			// init
			for (String m : month) {
				Map<String, Double> subMap = new HashMap<String, Double>();
				subMap.put("times", 0.0);
				subMap.put("time", 0.0);
				sort.put(m, subMap);
			}
			// 每玩家平均游戏次数
			for (Login l : qTimes) {
				String m = l.getStr("month");
				long count = l.getLong("count");
				long pp = l.getLong("pp");
				double avgTimes = 0.0;
				if (pp != 0) {
					avgTimes = (double) count / (double) pp;
					BigDecimal bg = new BigDecimal(avgTimes);
					avgTimes = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				}
				Map<String, Double> subMap = sort.get(m);
				subMap.put("times", avgTimes);
				sort.put(m, subMap);
			}
			// 每玩家平均游戏时长 --分钟
			for (Logout l : qTime) {
				String m = l.getStr("month");
				long onlineTime = l.getBigDecimal("online_time").longValue();
				long pp = l.getLong("pp");
				double avgTime = 0.0;
				if (pp != 0) {
					avgTime = (double) onlineTime / (double) (60 * pp);
					BigDecimal bg = new BigDecimal(avgTime);
					avgTime = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				}
				Map<String, Double> subMap = sort.get(m);
				subMap.put("time", avgTime);
				sort.put(m, subMap);
			}
		} catch (Exception e) {
			logger.debug("Error queryPpMonthAvgGP:", e);
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
		logger.debug("queryPpMonthAvgGP:" + data);
		return data;
	}

	// detail
	// 新增玩家日游戏次数
	public List<Integer> queryAddDayGameTimes(List<String> categories, String icons, String startDate, String endDate) {
		String sql = "select count(*)count from (select A.account,A.date_time from create_role A join device_info B on A.openudid = B.openudid where A.date_time between ? and ? and B.os in ("
				+ icons + ")) A join login B on A.account = B.account and A.date_time = B.date group by A.account";
		List<CreateRole> dGT = CreateRole.dao.find(sql, startDate, endDate);
		// init
		Map<String, Integer> sort = new LinkedHashMap<String, Integer>();
		for (String p : categories) {
			sort.put(p, 0);
		}
		for (CreateRole cr : dGT) {
			long count = cr.getLong("count");
			if (count == 1) {
				increaseValue("1", sort);
			} else if (count == 2 || count == 3) {
				increaseValue("2~3", sort);
			} else if (count == 4 || count == 5) {
				increaseValue("4~5", sort);
			} else if (count >= 6 && count <= 10) {
				increaseValue("6~10", sort);
			} else if (count >= 11 && count <= 20) {
				increaseValue("11~20", sort);
			} else if (count >= 21 && count <= 50) {
				increaseValue("21~50", sort);
			} else if (count > 50) {
				increaseValue("50+", sort);
			}
		}

		List<Integer> data = new ArrayList<Integer>();
		data.addAll(sort.values());
		logger.debug("queryAddDayGameTimes:" + data); 
		return data;
	}

	// 活跃玩家日游戏次数 ---每天形成一个次数分布, 再将多天的次数分布取平均值
	public List<Integer> queryActiveDayGameTimes(List<String> categories, String icons, String startDate,
			String endDate) {
		String sql = "select DATE_FORMAT(A.date,'%Y-%m-%d')date,count(*)count from (select account,openudid,date from login where date between ? and ?) A join device_info B on A.openudid = B.openudid where B.os in (" + icons + ") group by A.account,A.date;";
		List<Login> dGT = Login.dao.find(sql, startDate, endDate);
		//Map<date,Map<categories, Integer>>
		Map<String, Map<String, Integer>> sort = new LinkedHashMap<String, Map<String, Integer>>();
		//init
		List<String> dateList = DateUtils.getDateList(startDate, endDate);
		for(String date : dateList){
			Map<String, Integer> subMap = new LinkedHashMap<String, Integer>();
			for(String c : categories){
				subMap.put(c, 0);
			}
			sort.put(date, subMap);
		}
		for(Login l : dGT){
			String date = l.getStr("date");
			long count = l.getLong("count");
			Map<String, Integer> subMap = sort.get(date);
			if (count == 1) {
				increaseValue("1", subMap);
			} else if (count == 2 || count == 3) {
				increaseValue("2~3", subMap);
			} else if (count == 4 || count == 5) {
				increaseValue("4~5", subMap);
			} else if (count >= 6 && count <= 10) {
				increaseValue("6~10", subMap);
			} else if (count >= 11 && count <= 20) {
				increaseValue("11~20", subMap);
			} else if (count >= 21 && count <= 50) {
				increaseValue("21~50", subMap);
			} else if (count > 50) {
				increaseValue("50+", subMap);
			}
			sort.put(date, subMap);
		}
		int sum1 = 0;
		int sum2 = 0;
		int sum3 = 0;
		int sum4 = 0;
		int sum5 = 0;
		int sum6 = 0;
		int sum7 = 0;
		
		for(Map.Entry<String, Map<String, Integer>> entry : sort.entrySet()){
			for(Map.Entry<String, Integer> subEntry : entry.getValue().entrySet()){
				switch(subEntry.getKey()){
				case "1":
					sum1 += subEntry.getValue();
					break;
				case "2~3":
					sum2 += subEntry.getValue();
					break;
				case "4~5":
					sum3 += subEntry.getValue();
					break;
				case "6~10":
					sum4 += subEntry.getValue();
					break;
				case "11~20":
					sum5 += subEntry.getValue();
					break;
				case "21~50":
					sum6 += subEntry.getValue();
					break;
				case "50+":
					sum7 += subEntry.getValue();
					break;
				}
			}
		}
		List<Integer> data = new ArrayList<Integer>();
		List<Integer> sum = new ArrayList<Integer>();
		sum.addAll(Arrays.asList(sum1,sum2,sum3,sum4,sum5,sum6,sum7));
		int length=dateList.size();
		if(length!=0){
			for(int i : sum){
				int avg = i/length;
				data.add(avg);
			}
		}
		logger.debug("queryActiveDayGameTimes:" + data);
		return data;
	}

	//活跃玩家周游戏次数  ---每周形成一个次数分布, 再将多周的次数分布取平均值 
	public List<Integer> queryActiveWeekGameTimes(List<String> categories, String icons, String startDate, String endDate) {
		Map<String, String> week = DateUtils.divideDateToWeek(startDate, endDate);
		String sql = "select count(*)count from (select account,openudid,date from login where date between ? and ?) A join device_info B on A.openudid = B.openudid where B.os in (" + icons + ") group by A.account;";
		
		//init & load
		Map<String, Map<String, Integer>> sort = new LinkedHashMap<String, Map<String, Integer>>();
		for(Map.Entry<String, String> entry : week.entrySet()){
			String start = entry.getKey();
			String end = entry.getValue();
			String period = start + "~" + end;
			Map<String, Integer> subMap = new LinkedHashMap<String, Integer>();
			for(String c : categories){
				subMap.put(c, 0);
			}
			List<Login> wGT = Login.dao.find(sql, start, end);
			for(Login l : wGT){
				long count = l.getLong("count");
				if (count == 1) {
					increaseValue("1", subMap);
				} else if (count == 2 || count == 3) {
					increaseValue("2~3", subMap);
				} else if (count == 4 || count == 5) {
					increaseValue("4~5", subMap);
				} else if (count >= 6 && count <= 10) {
					increaseValue("6~10", subMap);
				} else if (count >= 11 && count <= 20) {
					increaseValue("11~20", subMap);
				} else if (count >= 21 && count <= 50) {
					increaseValue("21~50", subMap);
				} else if (count > 50) {
					increaseValue("50+", subMap);
				}
			}
			sort.put(period, subMap);
		}
		int sum1 = 0;
		int sum2 = 0;
		int sum3 = 0;
		int sum4 = 0;
		int sum5 = 0;
		int sum6 = 0;
		int sum7 = 0;
		
		for(Map.Entry<String, Map<String, Integer>> entry : sort.entrySet()){
			for(Map.Entry<String, Integer> subEntry : entry.getValue().entrySet()){
				switch(subEntry.getKey()){
				case "1":
					sum1 += subEntry.getValue();
					break;
				case "2~3":
					sum2 += subEntry.getValue();
					break;
				case "4~5":
					sum3 += subEntry.getValue();
					break;
				case "6~10":
					sum4 += subEntry.getValue();
					break;
				case "11~20":
					sum5 += subEntry.getValue();
					break;
				case "21~50":
					sum6 += subEntry.getValue();
					break;
				case "50+":
					sum7 += subEntry.getValue();
					break;
				}
			}
		}
		List<Integer> data = new ArrayList<Integer>();
		List<Integer> sum = new ArrayList<Integer>();
		sum.addAll(Arrays.asList(sum1,sum2,sum3,sum4,sum5,sum6,sum7));
		int length=week.size();
		if(length!=0){
			for(int i : sum){
				int avg = i/length;
				data.add(avg);
			}
		}
		logger.debug("queryActiveWeekGameTimes:" + data);
		return data;
	}
	
	//活跃玩家周游戏天数  ---每周形成一个次数分布, 再将多周的次数分布取平均值
	public List<Integer> queryActiveWeekGameDays(List<String> categories, String icons, String startDate, String endDate) {
		Map<String, String> week = DateUtils.divideDateToWeek(startDate, endDate);
		String sql = "select count(distinct A.date)count from (select account,openudid,date from login where date between ? and ?) A join device_info B on A.openudid = B.openudid where B.os in (" + icons + ") group by A.account;";
		
		//init & load
		Map<String, Map<String, Integer>> sort = new LinkedHashMap<String, Map<String, Integer>>();
		for(Map.Entry<String, String> entry : week.entrySet()){
			String start = entry.getKey();
			String end = entry.getValue();
			String period = start + "~" + end;
			Map<String, Integer> subMap = new LinkedHashMap<String, Integer>();
			for(String c : categories){
				subMap.put(c, 0);
			}
			List<Login> wGT = Login.dao.find(sql, start, end);
			for(Login l : wGT){
				int count = l.getLong("count").intValue();
				switch(count){
				case 1:
					increaseValue("1", subMap);
					break;
				case 2:
					increaseValue("2", subMap);
					break;
				case 3:
					increaseValue("3", subMap);
					break;
				case 4:
					increaseValue("4", subMap);
					break;
				case 5:
					increaseValue("5", subMap);
					break;
				case 6:
					increaseValue("6", subMap);
					break;
				case 7:
					increaseValue("7", subMap);
					break;
				}
			}
			sort.put(period, subMap);
		}
		
		int sum1 = 0;
		int sum2 = 0;
		int sum3 = 0;
		int sum4 = 0;
		int sum5 = 0;
		int sum6 = 0;
		int sum7 = 0;
		
		for(Map.Entry<String, Map<String, Integer>> entry : sort.entrySet()){
			for(Map.Entry<String, Integer> subEntry : entry.getValue().entrySet()){
				switch(subEntry.getKey()){
				case "1":
					sum1 += subEntry.getValue();
					break;
				case "2":
					sum2 += subEntry.getValue();
					break;
				case "3":
					sum3 += subEntry.getValue();
					break;
				case "4":
					sum4 += subEntry.getValue();
					break;
				case "5":
					sum5 += subEntry.getValue();
					break;
				case "6":
					sum6 += subEntry.getValue();
					break;
				case "7":
					sum7 += subEntry.getValue();
					break;
				}
			}
		}
		List<Integer> data = new ArrayList<Integer>();
		List<Integer> sum = new ArrayList<Integer>();
		sum.addAll(Arrays.asList(sum1,sum2,sum3,sum4,sum5,sum6,sum7));
		int length=week.size();
		if(length!=0){
			for(int i : sum){
				int avg = i/length;
				data.add(avg);
			}
		}
		logger.debug("queryActiveWeekGameDays:" + data);
		return data;
	}
	
	private void increaseValue(String key, Map<String, Integer> map) {
		int value = map.get(key);
		value++;
		map.put(key, value);
	}
}
