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
import common.util.DateUtils;

/**
 * 在线习惯页
 * 
 * @author chris
 *
 */
public class OnlineHabitsServiceImpl implements OnlineHabitsService {
	private static Logger logger = Logger.getLogger(OnlineHabitsServiceImpl.class);
	/** 
	 * 新增玩家日平均时长和次数
	 * @param categories 日期列表
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间 
	 */
	public Map<String, Object> queryAddpDayAvgGP(List<String> categories, String icons, String startDate,
			String endDate, String db, String versions, String chId) {
		String timesSql = "select DATE_FORMAT(A.date_time,'%Y-%m-%d')date,count(*)count,count(distinct A.account)ap from (select A.account,A.date_time from create_role A join device_info B on A.openudid = B.openudid where A.date_time between ? and ? and B.os in ("+ icons + ") and B.script_version in ("+versions+") and B.ch_id in ("+chId+")) A join login B on A.account = B.account and A.date_time = B.date group by A.date_time";
		String timeSql = "select DATE_FORMAT(A.date_time,'%Y-%m-%d')date,sum(B.online_time)online_time,count(distinct A.account)ap from (select A.account,A.date_time from create_role A join device_info B on A.openudid = B.openudid where A.date_time between ? and ? and B.os in ("+ icons + ") and B.script_version in ("+versions+") and B.ch_id in ("+chId+")) A join logout B on A.account = B.account and A.date_time = B.date group by A.date_time;";

		List<Login> qTimes = Login.dao.use(db).find(timesSql, startDate, endDate);
		List<Logout> qTime = Logout.dao.use(db).find(timeSql, startDate, endDate);

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
			logger.info("Error queryAddpDayAvgGP:", e);
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
		logger.info("data:" + data);
		return data;
	}

	/**
	 *  新增玩家周平均时长和次数
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间  
	 */
	public Map<String, Object> queryAddpWeekAvgGP(String icons, String startDate, String endDate, String db, String versions, String chId) {
		Map<String, String> week = DateUtils.divideDateToWeek(startDate, endDate);
		String timesSql = "select count(*)count,count(distinct A.account)ap from (select A.account,A.date_time from create_role A join device_info B on A.openudid = B.openudid where A.date_time between ? and ? and B.os in ("+ icons + ") and B.script_version in ("+versions+") and B.ch_id in ("+chId+")) A join login B on A.account = B.account and A.date_time = B.date";
		String timeSql = "select sum(B.online_time)online_time,count(distinct A.account)ap from (select A.account,A.date_time from create_role A join device_info B on A.openudid = B.openudid where A.date_time between ? and ? and B.os in ("+ icons + ") and B.script_version in ("+versions+") and B.ch_id in ("+chId+")) A join logout B on A.account = B.account and A.date_time = B.date;";

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
				List<Login> qTimes = Login.dao.use(db).find(timesSql, start, end);
				List<Logout> qTime = Logout.dao.use(db).find(timeSql, start, end);
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
			logger.info("Error queryAddpWeekAvgGP:", e);
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

		List<String> categories = new ArrayList<String>(sort.keySet());
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("times", times);
		data.put("time", time);
		data.put("categories", categories);
		logger.info("data:" + data);
		return data;
	}

	/**
	 *  新增玩家月平均时长和次数
	 * @param categories 日期列表
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间  
	 */
	public Map<String, Object> queryAddpMonthAvgGP(List<String> categories, String icons, String startDate,
			String endDate, String db, String versions, String chId) {
		String start = DateUtils.monthToStr(DateUtils.strToDate(startDate));
		String end = DateUtils.monthToStr(DateUtils.strToDate(endDate));

		String timesSql = "select DATE_FORMAT(A.date_time,'%Y-%m')month,count(*)count,count(distinct A.account)ap from (select A.account,A.date_time from create_role A join device_info B on A.openudid = B.openudid where DATE_FORMAT(A.date_time,'%Y-%m') between ? and ? and B.os in ("+ icons + ") and B.script_version in ("+versions+") and B.ch_id in ("+chId+")) A join login B on A.account = B.account and A.date_time = B.date group by month;";
		String timeSql = "select DATE_FORMAT(A.date_time,'%Y-%m')month,sum(B.online_time)online_time,count(distinct A.account)ap from (select A.account,A.date_time from create_role A join device_info B on A.openudid = B.openudid where DATE_FORMAT(A.date_time,'%Y-%m') between ? and ? and B.os in ("+ icons + ") and B.script_version in ("+versions+") and B.ch_id in ("+chId+")) A join logout B on A.account = B.account and A.date_time = B.date group by month;";

		List<Login> qTimes = Login.dao.use(db).find(timesSql, start, end);
		List<Logout> qTime = Logout.dao.use(db).find(timeSql, start, end);
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
			logger.info("Error queryAddpMonthAvgGP:", e);
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
		logger.info("data:" + data);
		return data;
	}

	/** 
	 * 活跃玩家日平均时长和次数
	 * @param categories 日期列表
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间 
	 */
	public Map<String, Object> queryActivepDayAvgGP(List<String> categories, String icons, String startDate,
			String endDate, String db, String versions, String chId) {
		String timesSql = "select DATE_FORMAT(A.date,'%Y-%m-%d')date,count(*)count,count(distinct A.account)ap from (select account,openudid,date from login where date between ? and ? ) A join device_info B on A.openudid = B.openudid where B.os in ("+ icons + ") and B.script_version in ("+versions+") and B.ch_id in ("+chId+") group by A.date";
		String timeSql = "select DATE_FORMAT(A.date,'%Y-%m-%d')date,sum(A.online_time)online_time,count(distinct A.account)ap from (select account,online_time,date from logout where date between ? and ?) A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where C.os in ("+ icons + ") and C.script_version in ("+versions+") and C.ch_id in ("+chId+") group by A.date";

		List<Login> qTimes = Login.dao.use(db).find(timesSql, startDate, endDate);
		List<Logout> qTime = Logout.dao.use(db).find(timeSql, startDate, endDate);

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
			logger.info("Error queryActivepDayAvgGP:", e);
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
		logger.info("data:" + data);
		return data;
	}

	/** 
	 * 活跃玩家周平均时长和次数
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间
	 */
	public Map<String, Object> queryActivepWeekAvgGP(String icons, String startDate, String endDate, String db, String versions, String chId) {
		Map<String, String> week = DateUtils.divideDateToWeek(startDate, endDate);
		String timesSql = "select count(*)count,count(distinct A.account)ap from (select account,openudid,date from login where date between ? and ? ) A join device_info B on A.openudid = B.openudid where B.os in ("+ icons + ") and B.script_version in ("+versions+") and B.ch_id in ("+chId+")";
		String timeSql = "select sum(A.online_time)online_time,count(distinct A.account)ap from (select account,online_time,date from logout where date between ? and ?) A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where C.os in ("+ icons + ") and C.script_version in ("+versions+") and C.ch_id in ("+chId+")";

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
				List<Login> qTimes = Login.dao.use(db).find(timesSql, start, end);
				List<Logout> qTime = Logout.dao.use(db).find(timeSql, start, end);
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
			logger.info("Error queryActivepWeekAvgGP:", e);
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

		List<String> categories = new ArrayList<String>(sort.keySet());
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("times", times);
		data.put("time", time);
		data.put("categories", categories);
		logger.info("data:" + data);
		return data;
	}

	/**
	 * 活跃玩家月平均时长和次数
	 * @param categories 日期列表
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间 
	 */
	public Map<String, Object> queryActivepMonthAvgGP(List<String> categories, String icons, String startDate,
			String endDate, String db, String versions, String chId) {
		String start = DateUtils.monthToStr(DateUtils.strToDate(startDate));
		String end = DateUtils.monthToStr(DateUtils.strToDate(endDate));

		String timesSql = "select A.month,count(*)count,count(distinct A.account)ap from (select account,openudid,DATE_FORMAT(date,'%Y-%m')month from login where DATE_FORMAT(date,'%Y-%m') between ? and ? ) A join device_info B on A.openudid = B.openudid where B.os in ("+ icons + ") and B.script_version in ("+versions+") and B.ch_id in ("+chId+") group by A.month;";
		String timeSql = "select A.month,sum(A.online_time)online_time,count(distinct A.account)ap from (select account,online_time,DATE_FORMAT(date,'%Y-%m')month from logout where DATE_FORMAT(date,'%Y-%m') between ? and ?) A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where C.os in ("+ icons + ") and C.script_version in ("+versions+") and C.ch_id in ("+chId+") group by A.month;";

		List<Login> qTimes = Login.dao.use(db).find(timesSql, start, end);
		List<Logout> qTime = Logout.dao.use(db).find(timeSql, start, end);
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
			logger.info("Error queryActivepMonthAvgGP:", e);
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
		logger.info("data:" + data);
		return data;
	}

	/**
	 *  付费玩家日平均时长和次数
	 * @param categories 日期列表
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间
	 */
	public Map<String, Object> queryPpDayAvgGP(List<String> categories, String icons, String startDate,
			String endDate, String db, String versions, String chId) {
		String timesSql = "select DATE_FORMAT(A.date,'%Y-%m-%d')date,count(*)count,count(distinct A.account)pp from (select distinct A.account,DATE_FORMAT(A.timestamp,'%Y-%m-%d')date from log_charge A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where A.is_product = 1 and DATE_FORMAT(A.timestamp,'%Y-%m-%d') between ? and ? and C.os in ("+ icons + ") and C.script_version in ("+versions+") and C.ch_id in ("+chId+")) A join login B on A.account = B.account and A.date = B.date group by A.date";
		String timeSql = "select DATE_FORMAT(A.date,'%Y-%m-%d')date,sum(B.online_time)online_time,count(distinct A.account)pp from (select distinct A.account,DATE_FORMAT(A.timestamp,'%Y-%m-%d')date from log_charge A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where A.is_product = 1 and DATE_FORMAT(A.timestamp,'%Y-%m-%d') between ? and ? and C.os in ("+ icons + ") and C.script_version in ("+versions+") and C.ch_id in ("+chId+")) A join logout B on A.account = B.account and A.date = B.date group by A.date;";

		List<Login> qTimes = Login.dao.use(db).find(timesSql, startDate, endDate);
		List<Logout> qTime = Logout.dao.use(db).find(timeSql, startDate, endDate);

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
			logger.info("Error queryPpDayAvgGP:", e);
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
		logger.info("data:" + data);
		return data;
	}

	/**
	 *  付费玩家周平均时长和次数
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间
	 */
	public Map<String, Object> queryPpWeekAvgGP(String icons, String startDate, String endDate, String db, String versions, String chId) {
		Map<String, String> week = DateUtils.divideDateToWeek(startDate, endDate);
		String timesSql = "select count(*)count,count(distinct A.account)pp from (select distinct A.account,DATE_FORMAT(A.timestamp,'%Y-%m-%d')date from log_charge A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where A.is_product = 1 and DATE_FORMAT(A.timestamp,'%Y-%m-%d') between ? and ? and C.os in ("+ icons + ") and C.script_version in ("+versions+") and C.ch_id in ("+chId+")) A join login B on A.account = B.account and A.date = B.date";
		String timeSql = "select sum(B.online_time)online_time,count(distinct A.account)pp from (select distinct A.account,DATE_FORMAT(A.timestamp,'%Y-%m-%d')date from log_charge A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where A.is_product = 1 and DATE_FORMAT(A.timestamp,'%Y-%m-%d') between ? and ? and C.os in ("+ icons + ") and C.script_version in ("+versions+") and C.ch_id in ("+chId+")) A join logout B on A.account = B.account and A.date = B.date;";

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
				List<Login> qTimes = Login.dao.use(db).find(timesSql, start, end);
				List<Logout> qTime = Logout.dao.use(db).find(timeSql, start, end);
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
			logger.info("Error queryPpWeekAvgGP:", e);
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

		List<String> categories = new ArrayList<String>(sort.keySet());
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("times", times);
		data.put("time", time);
		data.put("categories", categories);
		logger.info("data:" + data);
		return data;
	}

	/**
	 *  付费玩家月平均时长和次数
	 * @param categories 日期列表
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间
	 */
	public Map<String, Object> queryPpMonthAvgGP(List<String> categories, String icons, String startDate,
			String endDate, String db, String versions, String chId) {
		String start = DateUtils.monthToStr(DateUtils.strToDate(startDate));
		String end = DateUtils.monthToStr(DateUtils.strToDate(endDate));

		String timesSql = "select DATE_FORMAT(A.date,'%Y-%m')month,count(*)count,count(distinct A.account)pp from (select distinct A.account,DATE_FORMAT(A.timestamp,'%Y-%m-%d')date from log_charge A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where A.is_product = 1 and DATE_FORMAT(A.timestamp,'%Y-%m') between ? and ? and C.os in ("+ icons + ") and C.script_version in ("+versions+") and C.ch_id in ("+chId+")) A join login B on A.account = B.account and A.date = B.date group by month";
		String timeSql = "select DATE_FORMAT(A.date,'%Y-%m')month,sum(B.online_time)online_time,count(distinct A.account)pp from (select distinct A.account,DATE_FORMAT(A.timestamp,'%Y-%m-%d')date from log_charge A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where A.is_product = 1 and DATE_FORMAT(A.timestamp,'%Y-%m') between ? and ? and C.os in ("+ icons + ") and C.script_version in ("+versions+") and C.ch_id in ("+chId+")) A join logout B on A.account = B.account and A.date = B.date group by month;";

		List<Login> qTimes = Login.dao.use(db).find(timesSql, start, end);
		List<Logout> qTime = Logout.dao.use(db).find(timeSql, start, end);
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
			logger.info("Error queryPpMonthAvgGP:", e);
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
		logger.info("data:" + data);
		return data;
	}

	// detail
	/**
	 *  新增玩家日游戏次数 --新增玩家新增当天日游戏次数
	 * @param categories 日期列表
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间
	 */
	public List<Integer> queryAddDayGameTimes(List<String> categories, String icons, String startDate, String endDate, String db, String versions, String chId) {
		String sql = "select count(*)count from (select A.account,A.date_time from create_role A join device_info B on A.openudid = B.openudid where A.date_time between ? and ? and B.os in ("+ icons + ") and B.script_version in ("+versions+") and B.ch_id in ("+chId+")) A join login B on A.account = B.account and A.date_time = B.date group by A.account";
		List<CreateRole> dGT = CreateRole.dao.use(db).find(sql, startDate, endDate);
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

		List<Integer> data = new ArrayList<Integer>(sort.values());
		logger.info("data:" + data);
		return data;
	}

	/**
	 *  新增玩家日游戏时长 --新增玩家新增当天日游戏时长
	 * @param categories 日期列表
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间
	 */
	public List<Integer> queryAddDayGameTime(List<String> categories, String icons, String startDate, String endDate, String db, String versions, String chId) {
		String sql = "select sum(B.online_time)online_time from (select A.account,A.date_time from create_role A join device_info B on A.openudid = B.openudid where A.date_time between ? and ? and B.os in ("+ icons + ") and B.script_version in ("+versions+") and B.ch_id in ("+chId+")) A join logout B on A.account = B.account and A.date_time = B.date group by A.account";
		List<Logout> dGT = Logout.dao.use(db).find(sql, startDate, endDate);
		// init
		Map<String, Integer> sort = new LinkedHashMap<String, Integer>();
		for (String c : categories) {
			sort.put(c, 0);
		}
		try {
			for (Logout l : dGT) {
				int s = l.getBigDecimal("online_time") == null ? 0 : l.getBigDecimal("online_time").intValue();
				if (s < 10) {
					increaseValue("<10 s", sort);
				} else if (s >= 10 && s <= 60) {
					increaseValue("10~60 s", sort);
				} else if (s > 60 && s <= 60 * 3) {
					increaseValue("1~3 min", sort);
				} else if (s > 60 * 3 && s <= 60 * 10) {
					increaseValue("3~10 min", sort);
				} else if (s > 60 * 10 && s <= 60 * 30) {
					increaseValue("10~30 min", sort);
				} else if (s > 60 * 30 && s <= 60 * 60) {
					increaseValue("30~60 min", sort);
				} else if (s > 60 * 60 && s <= 60 * 60 * 2) {
					increaseValue("1~2 h", sort);
				} else if (s > 60 * 60 * 2 && s <= 60 * 60 * 4) {
					increaseValue("2~4 h", sort);
				} else if (s > 60 * 60 * 4) {
					increaseValue(">4 h", sort);
				}
			}
		} catch (Exception e) {
			logger.info("Error queryAddDayGameTime:", e);
		}

		List<Integer> data = new ArrayList<Integer>(sort.values());
		logger.info("data:" + data);
		return data;
	}

	/**
	 *  新增玩家单次游戏时长 --新增玩家新增当天 日游戏时长 / 日游戏次数
	 * @param categories 日期列表
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间
	 */
	public Map<String, List<Integer>> queryAddDaySinglePeriod(List<String> categories, String icons, String startDate,
			String endDate, String db, String versions, String chId) {
		String sql = "select sum(B.online_time)online_time,count(*)count from (select A.account,A.date_time from create_role A join device_info B on A.openudid = B.openudid where A.date_time between ? and ? and B.os in ("+ icons + ") and B.script_version in ("+versions+") and B.ch_id in ("+chId+")) A join logout B on A.account = B.account and A.date_time = B.date group by A.account";
		List<Logout> dSP = Logout.dao.use(db).find(sql, startDate, endDate);
		// init
		Map<String, Integer> sort = new LinkedHashMap<String, Integer>();
		Map<String, Integer> timesSort = new LinkedHashMap<String, Integer>();
		for (String c : categories) {
			sort.put(c, 0);
			timesSort.put(c, 0);
		}
		try {
			for (Logout l : dSP) {
				int s = l.getBigDecimal("online_time") == null ? 0 : l.getBigDecimal("online_time").intValue();
				int count = l.getLong("count").intValue();
				String key = "";
				if (count != 0) {
					s = s / count;
				}
				if (s >= 1 && s <= 4) {
					key = "1~4 s";
					increaseValue(key, sort);
				} else if (s >= 5 && s <= 10) {
					key = "5~10 s";
					increaseValue(key, sort);
				} else if (s >= 11 && s <= 30) {
					key = "11~30 s";
					increaseValue(key, sort);
				} else if (s >= 31 && s <= 60) {
					key = "31~60 s";
					increaseValue(key, sort);
				} else if (s > 60 && s <= 60 * 3) {
					key = "1~3 min";
					increaseValue(key, sort);
				} else if (s > 60 * 3 && s <= 60 * 10) {
					key = "3~10 min";
					increaseValue(key, sort);
				} else if (s > 60 * 10 && s <= 60 * 30) {
					key = "10~30 min";
					increaseValue(key, sort);
				} else if (s > 60 * 30 && s <= 60 * 60) {
					key = "30~60 min";
					increaseValue(key, sort);
				} else if (s > 60 * 60) {
					key = ">60 min";
					increaseValue(key, sort);
				}
				// 不满足时间区间的key
				if ("".equals(key)) {
					continue;
				}
				int value = timesSort.get(key);
				value += count;
				timesSort.put(key, value);
			}
		} catch (Exception e) {
			logger.info("Error queryAddDaySinglePeriod:", e);
		}
		List<Integer> players = new ArrayList<Integer>(sort.values());
		List<Integer> times = new ArrayList<Integer>(timesSort.values());
		Map<String, List<Integer>> data = new HashMap<String, List<Integer>>();
		data.put("players", players);
		data.put("times", times);
		logger.info("data:" + data);
		return data;
	}

	//新增玩家游戏时段
	public List<Integer> queryAddDayPeriod(String icons, String startDate,
			String endDate, String db, String versions, String chId){
		String sql = "select hour(B.login_time)hour,count(*)count from (select A.account,A.date_time from create_role A join device_info B on A.openudid = B.openudid where A.date_time between ? and ? and B.os in (" + icons + ") and B.script_version in ("+versions+") and B.ch_id in ("+chId+")) A join login B on A.account = B.account and A.date_time = B.date group by hour";
		List<Login> aDP = Login.dao.use(db).find(sql, startDate, endDate);
		//init
		Map<Integer, Integer> sort = new LinkedHashMap<Integer, Integer>();
		for(int i=0;i<24;i++){
			sort.put(i, 0);
		}
		//load
		for(Login l : aDP){
			int h = l.getInt("hour");
			int count = l.getLong("count").intValue();
			sort.put(h, count);
		}
		List<Integer> data = new ArrayList<Integer>(sort.values());
		logger.info("data:" + data);
		return data;
	}
	
	/**
	 *  活跃玩家日游戏次数 ---每天形成一个次数分布, 再将多天的次数分布取平均值
	 * @param categories 日期列表
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间
	 */
	public List<Integer> queryActiveDayGameTimes(List<String> categories, String icons, String startDate,
			String endDate, String db, String versions, String chId) {
		String sql = "select DATE_FORMAT(A.date,'%Y-%m-%d')date,count(*)count from (select account,openudid,date from login where date between ? and ?) A join device_info B on A.openudid = B.openudid where B.os in ("+ icons + ") and B.script_version in ("+versions+") and B.ch_id in ("+chId+") group by A.account,A.date;";
		List<Login> dGT = Login.dao.use(db).find(sql, startDate, endDate);
		// Map<date,Map<categories, Integer>>
		Map<String, Map<String, Integer>> sort = new LinkedHashMap<String, Map<String, Integer>>();
		// init
		List<String> dateList = DateUtils.getDateList(startDate, endDate);
		for (String date : dateList) {
			Map<String, Integer> subMap = new LinkedHashMap<String, Integer>();
			for (String c : categories) {
				subMap.put(c, 0);
			}
			sort.put(date, subMap);
		}
		// load data
		for (Login l : dGT) {
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

		for (Map.Entry<String, Map<String, Integer>> entry : sort.entrySet()) {
			for (Map.Entry<String, Integer> subEntry : entry.getValue().entrySet()) {
				switch (subEntry.getKey()) {
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
		List<Integer> sum = new ArrayList<Integer>(Arrays.asList(sum1, sum2, sum3, sum4, sum5, sum6, sum7));
		int length = dateList.size();
		if (length != 0) {
			for (int i : sum) {
				int avg = i / length;
				data.add(avg);
			}
		}
		logger.info("data:" + data);
		return data;
	}

	/**
	 *  活跃玩家周游戏次数 ---每周形成一个次数分布, 再将多周的次数分布取平均值
	 * @param categories 日期列表
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间
	 */
	public List<Integer> queryActiveWeekGameTimes(List<String> categories, String icons, String startDate,
			String endDate, String db, String versions, String chId) {
		Map<String, String> week = DateUtils.divideDateToWeek(startDate, endDate);
		String sql = "select count(*)count from (select account,openudid,date from login where date between ? and ?) A join device_info B on A.openudid = B.openudid where B.os in ("+ icons + ") and B.script_version in ("+versions+") and B.ch_id in ("+chId+") group by A.account";

		// init & load
		Map<String, Map<String, Integer>> sort = new LinkedHashMap<String, Map<String, Integer>>();
		for (Map.Entry<String, String> entry : week.entrySet()) {
			String start = entry.getKey();
			String end = entry.getValue();
			String period = start + "~" + end;
			Map<String, Integer> subMap = new LinkedHashMap<String, Integer>();
			for (String c : categories) {
				subMap.put(c, 0);
			}
			List<Login> wGT = Login.dao.use(db).find(sql, start, end);
			for (Login l : wGT) {
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

		for (Map.Entry<String, Map<String, Integer>> entry : sort.entrySet()) {
			for (Map.Entry<String, Integer> subEntry : entry.getValue().entrySet()) {
				switch (subEntry.getKey()) {
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
		List<Integer> sum = new ArrayList<Integer>(Arrays.asList(sum1, sum2, sum3, sum4, sum5, sum6, sum7));
		int length = week.size();
		if (length != 0) {
			for (int i : sum) {
				int avg = i / length;
				data.add(avg);
			}
		}
		logger.info("data:" + data);
		return data;
	}

	/**
	 *  活跃玩家周游戏天数 ---每周形成一个次数分布, 再将多周的次数分布取平均值
	 * @param categories 日期列表
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间
	 */
	public List<Integer> queryActiveWeekGameDays(List<String> categories, String icons, String startDate,
			String endDate, String db, String versions, String chId) {
		Map<String, String> week = DateUtils.divideDateToWeek(startDate, endDate);
		String sql = "select count(distinct A.date)count from (select account,openudid,date from login where date between ? and ?) A join device_info B on A.openudid = B.openudid where B.os in ("+ icons + ") and B.script_version in ("+versions+") and B.ch_id in ("+chId+") group by A.account;";

		// init & load
		// Map<week,Map<type,Integer>>
		Map<String, Map<String, Integer>> sort = new LinkedHashMap<String, Map<String, Integer>>();
		for (Map.Entry<String, String> entry : week.entrySet()) {
			String start = entry.getKey();
			String end = entry.getValue();
			String period = start + "~" + end;
			Map<String, Integer> subMap = new LinkedHashMap<String, Integer>();
			// init
			for (String c : categories) {
				subMap.put(c, 0);
			}
			List<Login> wGT = Login.dao.use(db).find(sql, start, end);
			for (Login l : wGT) {
				int count = l.getLong("count").intValue();
				switch (count) {
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

		for (Map.Entry<String, Map<String, Integer>> entry : sort.entrySet()) {
			for (Map.Entry<String, Integer> subEntry : entry.getValue().entrySet()) {
				switch (subEntry.getKey()) {
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
		List<Integer> sum = new ArrayList<Integer>(Arrays.asList(sum1, sum2, sum3, sum4, sum5, sum6, sum7));
		int length = week.size();
		if (length != 0) {
			for (int i : sum) {
				int avg = i / length;
				data.add(avg);
			}
		}
		logger.info("data:" + data);
		return data;
	}

	/**
	 *  活跃玩家月游戏天数 ---每月形成一个次数分布, 再将多月的次数分布取平均值
	 * @param categories 日期列表
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间
	 */
	public List<Integer> queryActiveMonthGameDays(List<String> categories, String icons, String startDate,
			String endDate, String db, String versions, String chId) {
		List<String> monthList = DateUtils.getMonthList(startDate, endDate);
		String start = DateUtils.monthToStr(DateUtils.strToDate(startDate));
		String end = DateUtils.monthToStr(DateUtils.strToDate(endDate));
		String sql = "select DATE_FORMAT(A.date,'%Y-%m')month,count(distinct A.date)count from (select account,openudid,date from login where DATE_FORMAT(date,'%Y-%m') between ? and ?) A join device_info B on A.openudid = B.openudid where B.os in ("+ icons + ") and B.script_version in ("+versions+") and B.ch_id in ("+chId+") group by A.account,month";

		// init
		// Map<month, Map<type,Integer>>
		Map<String, Map<String, Integer>> sort = new LinkedHashMap<String, Map<String, Integer>>();
		for (String m : monthList) {
			Map<String, Integer> subMap = new LinkedHashMap<String, Integer>();
			for (String c : categories) {
				subMap.put(c, 0);
			}
			sort.put(m, subMap);
		}
		// load data
		List<Login> mGT = Login.dao.use(db).find(sql, start, end);
		for (Login l : mGT) {
			String month = l.getStr("month");
			int count = l.getLong("count").intValue();
			Map<String, Integer> subMap = sort.get(month);
			switch (count) {
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
			default:
				if (count >= 8 && count <= 14) {
					increaseValue("8~14", subMap);
				} else if (count >= 15 && count <= 21) {
					increaseValue("15~21", subMap);
				} else if (count >= 22 && count <= 31) {
					increaseValue("22~31", subMap);
				}
				break;
			}
		}

		int sum1 = 0;
		int sum2 = 0;
		int sum3 = 0;
		int sum4 = 0;
		int sum5 = 0;
		int sum6 = 0;
		int sum7 = 0;
		int sum8 = 0;
		int sum9 = 0;
		int sum10 = 0;
		for (Map.Entry<String, Map<String, Integer>> entry : sort.entrySet()) {
			for (Map.Entry<String, Integer> subEntry : entry.getValue().entrySet()) {
				switch (subEntry.getKey()) {
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
				case "8~14":
					sum8 += subEntry.getValue();
					break;
				case "15~21":
					sum9 += subEntry.getValue();
					break;
				case "22~31":
					sum10 += subEntry.getValue();
					break;
				}
			}
		}

		List<Integer> data = new ArrayList<Integer>();
		List<Integer> sum = new ArrayList<Integer>(
				Arrays.asList(sum1, sum2, sum3, sum4, sum5, sum6, sum7, sum8, sum9, sum10));
		int length = monthList.size();
		if (length != 0) {
			for (int i : sum) {
				int avg = i / length;
				data.add(avg);
			}
		}
		logger.info("data:" + data);
		return data;
	}

	/**
	 *  活跃玩家日游戏时长 --每天形成一个次数分布, 再将多天的次数分布取平均值
	 * @param categories 日期列表
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间
	 */
	public List<Integer> queryActiveDayGameTime(List<String> categories, String icons, String startDate,
			String endDate, String db, String versions, String chId) {
		String sql = "select DATE_FORMAT(A.date,'%Y-%m-%d')date,sum(A.online_time)online_time from (select account,online_time,date from logout where date between ? and ?) A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where C.os in ("+ icons + ") and C.script_version in ("+versions+") and C.ch_id in ("+chId+") group by A.account,A.date";
		List<Logout> dGT = Logout.dao.use(db).find(sql, startDate, endDate);
		// init
		// Map<date, Map<type,Integer>>
		Map<String, Map<String, Integer>> sort = new LinkedHashMap<String, Map<String, Integer>>();
		List<String> dateList = DateUtils.getDateList(startDate, endDate);
		for (String d : dateList) {
			Map<String, Integer> subMap = new LinkedHashMap<String, Integer>();
			for (String c : categories) {
				subMap.put(c, 0);
			}
			sort.put(d, subMap);
		}
		// load data
		for (Logout l : dGT) {
			String date = l.getStr("date");
			int s = l.getBigDecimal("online_time") == null ? 0 : l.getBigDecimal("online_time").intValue();
			Map<String, Integer> subMap = sort.get(date);
			if (s < 10) {
				increaseValue("<10 s", subMap);
			} else if (s >= 10 && s <= 60) {
				increaseValue("10~60 s", subMap);
			} else if (s > 60 && s <= 60 * 3) {
				increaseValue("1~3 min", subMap);
			} else if (s > 60 * 3 && s <= 60 * 10) {
				increaseValue("3~10 min", subMap);
			} else if (s > 60 * 10 && s <= 60 * 30) {
				increaseValue("10~30 min", subMap);
			} else if (s > 60 * 30 && s <= 60 * 60) {
				increaseValue("30~60 min", subMap);
			} else if (s > 60 * 60 && s <= 60 * 60 * 2) {
				increaseValue("1~2 h", subMap);
			} else if (s > 60 * 60 * 2 && s <= 60 * 60 * 4) {
				increaseValue("2~4 h", subMap);
			} else if (s > 60 * 60 * 4) {
				increaseValue(">4 h", subMap);
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
		int sum8 = 0;
		int sum9 = 0;
		for (Map.Entry<String, Map<String, Integer>> entry : sort.entrySet()) {
			for (Map.Entry<String, Integer> subEntry : entry.getValue().entrySet()) {
				switch (subEntry.getKey()) {
				case "<10 s":
					sum1 += subEntry.getValue();
					break;
				case "10~60 s":
					sum2 += subEntry.getValue();
					break;
				case "1~3 min":
					sum3 += subEntry.getValue();
					break;
				case "3~10 min":
					sum4 += subEntry.getValue();
					break;
				case "10~30 min":
					sum5 += subEntry.getValue();
					break;
				case "30~60 min":
					sum6 += subEntry.getValue();
					break;
				case "1~2 h":
					sum7 += subEntry.getValue();
					break;
				case "2~4 h":
					sum8 += subEntry.getValue();
					break;
				case ">4 h":
					sum9 += subEntry.getValue();
					break;
				}
			}
		}

		List<Integer> data = new ArrayList<Integer>();
		List<Integer> sum = new ArrayList<Integer>(Arrays.asList(sum1, sum2, sum3, sum4, sum5, sum6, sum7, sum8, sum9));
		int length = dateList.size();
		if (length != 0) {
			for (int i : sum) {
				int avg = i / length;
				data.add(avg);
			}
		}
		logger.info("data:" + data);
		return data;
	}

	/**
	 *  活跃玩家周游戏时长 ---每周形成一个次数分布, 再将多周的次数分布取平均值
	 * @param categories 日期列表
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间
	 */
	public List<Integer> queryActiveWeekGameTime(List<String> categories, String icons, String startDate,
			String endDate, String db, String versions, String chId) {
		Map<String, String> week = DateUtils.divideDateToWeek(startDate, endDate);
		String sql = "select sum(A.online_time)online_time from (select account,online_time,date from logout where date between ? and ?) A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where C.os in ("+ icons + ") and C.script_version in ("+versions+") and C.ch_id in ("+chId+") group by A.account";
		// init & load
		// Map<date, Map<week,Integer>>
		Map<String, Map<String, Integer>> sort = new LinkedHashMap<String, Map<String, Integer>>();
		for (Map.Entry<String, String> entry : week.entrySet()) {
			String start = entry.getKey();
			String end = entry.getValue();
			String period = start + "~" + end;
			Map<String, Integer> subMap = new LinkedHashMap<String, Integer>();
			for (String c : categories) {
				subMap.put(c, 0);
			}
			List<Logout> wGT = Logout.dao.use(db).find(sql, startDate, endDate);
			for (Logout l : wGT) {
				int s = l.getBigDecimal("online_time") == null ? 0 : l.getBigDecimal("online_time").intValue();
				if (s >= 0 && s <= 60) {
					increaseValue("0~60 s", subMap);
				} else if (s > 60 && s <= 60 * 3) {
					increaseValue("1~3 min", subMap);
				} else if (s > 60 * 3 && s <= 60 * 10) {
					increaseValue("3~10 min", subMap);
				} else if (s > 60 * 10 && s <= 60 * 60) {
					increaseValue("10~60 min", subMap);
				} else if (s > 60 * 60 && s <= 60 * 60 * 2) {
					increaseValue("1~2 h", subMap);
				} else if (s > 60 * 60 * 2 && s <= 60 * 60 * 4) {
					increaseValue("2~4 h", subMap);
				} else if (s > 60 * 60 * 4 && s <= 60 * 60 * 6) {
					increaseValue("4~6 h", subMap);
				} else if (s > 60 * 60 * 6 && s <= 60 * 60 * 10) {
					increaseValue("6~10 h", subMap);
				} else if (s > 60 * 60 * 10 && s <= 60 * 60 * 15) {
					increaseValue("10~15 h", subMap);
				} else if (s > 60 * 60 * 15 && s <= 60 * 60 * 20) {
					increaseValue("15~20 h", subMap);
				} else if (s > 60 * 60 * 20) {
					increaseValue(">20 h", subMap);
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
		int sum8 = 0;
		int sum9 = 0;
		int sum10 = 0;
		int sum11 = 0;

		for (Map.Entry<String, Map<String, Integer>> entry : sort.entrySet()) {
			for (Map.Entry<String, Integer> subEntry : entry.getValue().entrySet()) {
				switch (subEntry.getKey()) {
				case "0~60 s":
					sum1 += subEntry.getValue();
					break;
				case "1~3 min":
					sum2 += subEntry.getValue();
					break;
				case "3~10 min":
					sum3 += subEntry.getValue();
					break;
				case "10~60 min":
					sum4 += subEntry.getValue();
					break;
				case "1~2 h":
					sum5 += subEntry.getValue();
					break;
				case "2~4 h":
					sum6 += subEntry.getValue();
					break;
				case "4~6 h":
					sum7 += subEntry.getValue();
					break;
				case "6~10 h":
					sum8 += subEntry.getValue();
					break;
				case "10~15 h":
					sum9 += subEntry.getValue();
					break;
				case "15~20 h":
					sum10 += subEntry.getValue();
					break;
				case ">20 h":
					sum11 += subEntry.getValue();
					break;
				}
			}
		}

		List<Integer> data = new ArrayList<Integer>();
		List<Integer> sum = new ArrayList<Integer>(
				Arrays.asList(sum1, sum2, sum3, sum4, sum5, sum6, sum7, sum8, sum9, sum10, sum11));
		int length = week.size();
		if (length != 0) {
			for (int i : sum) {
				int avg = i / length;
				data.add(avg);
			}
		}
		logger.info("data:" + data);
		return data;
	}

	/**
	 *  活跃玩家单次游戏时长 --活跃玩家 日游戏时长 / 日游戏次数
	 * @param categories 日期列表
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间
	 */
	public Map<String, List<Integer>> queryActiveDaySinglePeriod(List<String> categories, String icons,
			String startDate, String endDate, String db, String versions, String chId) {
		String sql = "select sum(A.online_time)online_time,count(*)count from (select account,online_time from logout where date between ? and ?) A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where C.os in ("+ icons + ") and C.script_version in ("+versions+") and C.ch_id in ("+chId+") group by A.account;";
		List<Logout> dSP = Logout.dao.use(db).find(sql, startDate, endDate);
		// init
		Map<String, Integer> sort = new LinkedHashMap<String, Integer>();
		Map<String, Integer> timesSort = new LinkedHashMap<String, Integer>();
		for (String c : categories) {
			sort.put(c, 0);
			timesSort.put(c, 0);
		}
		// load data
		try {
			for (Logout l : dSP) {
				int s = l.getBigDecimal("online_time") == null ? 0 : l.getBigDecimal("online_time").intValue();
				int count = l.getLong("count").intValue();
				if (count != 0) {
					s = s / count;
				}
				String key = "";
				if (s >= 1 && s <= 4) {
					key = "1~4 s";
					increaseValue(key, sort);
				} else if (s >= 5 && s <= 10) {
					key = "5~10 s";
					increaseValue(key, sort);
				} else if (s >= 11 && s <= 30) {
					key = "11~30 s";
					increaseValue(key, sort);
				} else if (s >= 31 && s <= 60) {
					key = "31~60 s";
					increaseValue(key, sort);
				} else if (s > 60 && s <= 60 * 3) {
					key = "1~3 min";
					increaseValue(key, sort);
				} else if (s > 60 * 3 && s <= 60 * 10) {
					key = "3~10 min";
					increaseValue(key, sort);
				} else if (s > 60 * 10 && s <= 60 * 30) {
					key = "10~30 min";
					increaseValue(key, sort);
				} else if (s > 60 * 30 && s <= 60 * 60) {
					key = "30~60 min";
					increaseValue(key, sort);
				} else if (s > 60 * 60) {
					key = ">60 min";
					increaseValue(key, sort);
				}
				// 不满足时间区间的key
				if ("".equals(key)) {
					continue;
				}
				int value = timesSort.get(key);
				value += count;
				timesSort.put(key, value);
			}
		} catch (Exception e) {
			logger.info("Error queryActiveDaySinglePeriod:", e);
		}
		List<Integer> players = new ArrayList<Integer>(sort.values());
		List<Integer> times = new ArrayList<Integer>(timesSort.values());
		Map<String, List<Integer>> data = new HashMap<String, List<Integer>>();
		data.put("players", players);
		data.put("times", times);
		logger.info("data:" + data);
		return data;
	}

	/**
	 * 活跃玩家游戏时段
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间
	 */
	public List<Integer> queryActiveDayPeriod(String icons, String startDate, String endDate, String db, String versions, String chId) {
		String sql = "select hour(A.login_time)hour,count(*)count from (select account,openudid,login_time from login where date between ? and ?) A join device_info B on A.openudid = B.openudid where B.os in (" + icons + ") and B.script_version in ("+versions+") and B.ch_id in ("+chId+") group by hour";
		List<Login> aDP = Login.dao.use(db).find(sql, startDate, endDate);
		//init
		Map<Integer, Integer> sort = new LinkedHashMap<Integer, Integer>();
		for(int i=0;i<24;i++){
			sort.put(i, 0);
		}
		//load
		for(Login l : aDP){
			int h = l.getInt("hour");
			int count = l.getLong("count").intValue();
			sort.put(h, count);
		}
		List<Integer> data = new ArrayList<Integer>(sort.values());
		logger.info("data:" + data);
		return data;
	}
	
	private void increaseValue(String key, Map<String, Integer> map) {
		int value = map.get(key);
		value++;
		map.put(key, value);
	}
}
