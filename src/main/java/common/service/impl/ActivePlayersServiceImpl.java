package common.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.apache.log4j.Logger;
import common.model.ActiveUser;
import common.model.CreateRole;
import common.model.LevelUp;
import common.model.LogCharge;
import common.model.Login;
import common.mysql.DbSelector;
import common.service.ActivePlayersService;

/**
 * 查询活跃玩家页数据
 * @author chris
 *
 */
public class ActivePlayersServiceImpl implements ActivePlayersService {
	private static Logger logger = Logger.getLogger(ActivePlayersServiceImpl.class);
	private String db = DbSelector.getDbName();
	/**
	 * 查询dau
	 * @param categories 日期列表
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间 
	 */
	
	public List<Long> queryDau(List<String> categories, String icons, String startDate, String endDate) {
		List<Long> data = new ArrayList<Long>();
		String sql = "select DATE_FORMAT(date,'%Y-%m-%d') date, sum(dau)dau from active_user where date between ? and ? and os in ("
				+ icons + ") group by DATE_FORMAT(date,'%Y-%m-%d')";
		List<ActiveUser> dau = ActiveUser.dao.use(db).find(sql, startDate, endDate);

		Map<String, Long> sort = new TreeMap<String, Long>();
		for (String category : categories) {
			sort.put(category, 0L);
		}
		for (ActiveUser cr : dau) {
			sort.put(cr.getStr("date"), cr.getBigDecimal("dau").longValue());
		}
		data.addAll(sort.values());
		logger.info("data:" + data);
		return data;
	}
	
	/** 
	 * 查询 付费 / 非付费
	 * @param categories 日期列表
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间 
	 */
	public Map<String, List<Long>> queryPaidInActiveUser(List<String> categories, String icons, String startDate, String endDate){
		Map<String, List<Long>> data = new HashMap<String, List<Long>>();
		String sql = "select DATE_FORMAT(E.date,'%Y-%m-%d') date,sum(case when F.account is not null then 1 else 0 end)paid,sum(case when F.account is null then 1 else 0 end)notpaid from(select A.date,A.account from (select distinct account,date from login where login_time >= ? and login_time <= ?) A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where C.os in("+ icons +")) E left join (select distinct account from log_charge) F on E.account=F.account group by DATE_FORMAT(E.date,'%Y-%m-%d')";
		List<LogCharge> activePlayers = LogCharge.dao.use(db).find(sql, startDate, endDate);
		//Map<Date,Map<paid/notpaid,value>>
		Map<String, Map<String,Long>> sort = new TreeMap<String, Map<String, Long>>();
		//initial
		for(String category : categories){
			Map<String, Long> subMap = new HashMap<String, Long>();
			subMap.put("paid", 0L);
			subMap.put("notpaid", 0L);
			sort.put(category, subMap);
		}
		for(LogCharge lc : activePlayers){
			String date = lc.getStr("date");
			Map<String, Long> subMap = sort.get(date);
			subMap.put("paid", lc.getBigDecimal("paid").longValue());
			subMap.put("notpaid", lc.getBigDecimal("notpaid").longValue());
			sort.put(date, subMap);
		}
		
		List<Long> paid = new ArrayList<Long>();
		List<Long> notpaid = new ArrayList<Long>();

		for(Map.Entry<String, Map<String, Long>> entry : sort.entrySet()){
			for(Map.Entry<String, Long> subEntry : entry.getValue().entrySet()){
				switch(subEntry.getKey()){
					case "paid":{
						paid.add(subEntry.getValue());
						break;
					}
					case "notpaid":{
						notpaid.add(subEntry.getValue());
						break;
					}
				}
			}
		}

		data.put("paid", paid);
		data.put("notpaid", notpaid);
		logger.info("data:" + data);
		return data;
	}
	/** 
	 * 查询 dau | wau | mau
	 * @param categories 日期列表
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间 
	 */
	public Map<String, Object> queryActivePlayersInfo(List<String> categories, String icons, String startDate, String endDate) {
		String sql = "select DATE_FORMAT(date,'%Y-%m-%d') date, sum(dau) dau, sum(wau) wau, sum(mau) mau from active_user where date between ? and ? and os in (" + icons + ") group by  DATE_FORMAT(date,'%Y-%m-%d')";
		List<ActiveUser> activeUser = ActiveUser.dao.use(db).find(sql, startDate, endDate);

		// Map<Date,Map<name,value>> --Map<"2016-08-20",Map<"dau",1>>
		Map<String, Map<String, Long>> sort = new TreeMap<String, Map<String, Long>>();
		// 初始化
		for (String category : categories) {
			Map<String, Long> subMap = new HashMap<String, Long>();
			subMap.put("dau", 0L);
			subMap.put("wau", 0L);
			subMap.put("mau", 0L);
			sort.put(category, subMap);
		}
		// 向map中插入数据
		for (ActiveUser au : activeUser) {
			String date = au.getStr("date");
			Map<String, Long> subMap = sort.get(date);
			subMap.put("dau", au.getBigDecimal("dau").longValue());
			subMap.put("wau", au.getBigDecimal("wau").longValue());
			subMap.put("mau", au.getBigDecimal("mau").longValue());
			sort.put(date, subMap);
		}

		List<Long> dauData = new ArrayList<Long>();
		List<Long> wauData = new ArrayList<Long>();
		List<Long> mauData = new ArrayList<Long>();

		for (Map.Entry<String, Map<String, Long>> entry : sort.entrySet()) {
			for (Map.Entry<String, Long> subEntry : entry.getValue().entrySet()) {
				switch (subEntry.getKey()) {
				case "dau": {
					dauData.add(subEntry.getValue());
					break;
				}
				case "wau": {
					wauData.add(subEntry.getValue());
					break;
				}
				case "mau": {
					mauData.add(subEntry.getValue());
					break;
				}
				}
			}
		}

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("DAU", dauData);
		data.put("WAU", wauData);
		data.put("MAU", mauData);
		logger.info("data:" + data);
		return data;
	}
	/**
	 * 查询 dau/mau
	 * @param categories 日期列表
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间 
	 */
	public List<Double> queryActivePlayersDauMauRate(List<String> categories, String icons, String startDate, String endDate) {
		List<Double> data = new ArrayList<Double>();
		String sql = "select DATE_FORMAT(date,'%Y-%m-%d') date, sum(dau) dau, sum(mau) mau from active_user where date between ? and ? and os in (" + icons + ") group by  DATE_FORMAT(date,'%Y-%m-%d')";
		List<ActiveUser> activeUser = ActiveUser.dao.use(db).find(sql, startDate, endDate);

		Map<String, Double> sort = new TreeMap<String, Double>();
		// 初始化Map
		for (String category : categories) {
			sort.put(category, 0.0);
		}

		for (ActiveUser au : activeUser) {
			int dau = au.getBigDecimal("dau").intValue();
			int mau = au.getBigDecimal("mau").intValue();
			if (mau == 0) {
				continue;
			}
			BigDecimal bg = new BigDecimal(dau * 1.0 / mau);
			double rate = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			sort.put(au.getStr("date"), rate);
		}

		data.addAll(sort.values());
		logger.info("data:" + data);
		return data;
	}
	/**
	 * 查询 已玩天数
	 * @param playDaysPeriod 时间区间分布
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间 
	 */
	public List<Long> queryPlayDays(List<String> playDaysPeriod, String icons, String startDate, String endDate) {
		List<Long> data = new ArrayList<Long>();
		String sql = "select count(distinct E.date) count from login E join (select A.account from(select distinct account from login where login_time >= ? and login_time <= ? ) A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where C.os in(" + icons + ")) F on E.account = F.account where E.login_time <= ? group by E.account";
		List<Login> playDays = Login.dao.use(db).find(sql, startDate, endDate, endDate);
		Map<String, Integer> record = new LinkedHashMap<String, Integer>();
		for (String playDayString : playDaysPeriod) {
			record.put(playDayString, 0);
		}

		for (Login login : playDays) {
			int num = login.getLong("count").intValue();
			if (num <= 1) {
				increaseValue("1 天", record);
				continue;
			}
			if (num >= 2 && num <= 3) {
				increaseValue("2~3 天", record);
				continue;
			}
			if (num >= 4 && num <= 7) {
				increaseValue("4~7 天", record);
				continue;
			}
			if (num >= 8 && num <= 14) {
				increaseValue("8~14 天", record);
				continue;
			}
			if (num >= 15 && num <= 30) {
				increaseValue("15~30 天", record);
				continue;
			}
			if (num >= 31 && num <= 90) {
				increaseValue("31~90 天", record);
				continue;
			}
			if (num >= 91 && num <= 180) {
				increaseValue("91~180 天", record);
				continue;
			}
			if (num >= 181 && num <= 365) {
				increaseValue("181~365 天", record);
				continue;
			}
			increaseValue("365+ 天", record);
		}

		for (Integer i : record.values()) {
			data.add(i.longValue());
		}
		logger.info("data:" + data);
		return data;
	}
	/**
	 * 查询 等级
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间
	 */
	public Map<Integer, Long> queryRank(String icons, String startDate, String endDate) {
		String sql = "select F.level,count(F.level) count from (select D.account,max(D.level)level from level_up D join(select A.account from (select distinct account from login where login_time >= ? and login_time <= ?) A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where C.os in (" + icons + ")) E on D.account = E.account group by D.account) F group by F.level";
		List<LevelUp> rank = LevelUp.dao.use(db).find(sql, startDate, endDate);
		// Map<level,count>
		Map<Integer, Long> data = new HashMap<Integer, Long>();
		for (LevelUp lu : rank) {
			data.put(lu.getInt("level"), lu.getLong("count"));
		}
		List<Integer> categories = new ArrayList<Integer>(data.keySet());
		int max = max(categories);
		for (int i = 0; i <= max; i++) {
			if (!data.containsKey(i)) {
				data.put(i, 0L);
			}
		}
		logger.info("data:" + data);
		return data;
	}
	/**
	 * 查询 地区 --省份
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间 
	 */
	public Map<String, Object> queryArea(String icons, String startDate, String endDate) {
		Map<String, Object> data = new HashMap<String, Object>();
		String sql = "select C.province province,count(C.province) count from create_role A join (select distinct account from login where login_time >= ? and login_time <= ?) B on A.account=B.account join device_info C on A.openudid=C.openudid where C.os in (" + icons + ") group by C.province";
		List<CreateRole> queryData = CreateRole.dao.use(db).find(sql, startDate, endDate);
		List<String> province = new ArrayList<String>();
		List<Long> areaData = new ArrayList<Long>();
		for (CreateRole cr : queryData) {
			province.add(cr.getStr("province"));
			areaData.add(cr.getLong("count"));
		}
		data.put("activePlayer", areaData);
		data.put("area", province);
		logger.info("data:" + data);
		return data;
	}
	/**
	 * 查询 国家
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间 
	 */
	public Map<String, Object> queryCountry(String icons, String startDate, String endDate) {
		Map<String, Object> data = new HashMap<String, Object>();
		String sql = "select C.country country,count(C.country) count from create_role A join (select distinct account from login where login_time >= ? and login_time <= ?) B on A.account=B.account join device_info C on A.openudid=C.openudid where C.os in (" + icons + ") group by C.country";
		List<CreateRole> queryData = CreateRole.dao.use(db).find(sql, startDate, endDate);
		List<String> country = new ArrayList<String>();
		List<Long> countryData = new ArrayList<Long>();
		for (CreateRole cr : queryData) {
			country.add(cr.getStr("country"));
			countryData.add(cr.getLong("count"));
		}
		data.put("activePlayer", countryData);
		data.put("country", country);
		logger.info("data:" + data);
		return data;
	}
	/**
	 * 查询 账户类型
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间 
	 */
	public Map<String, Object> queryAccountType(String icons, String startDate, String endDate) {
		Map<String, Object> data = new HashMap<String, Object>();
		String sql = "select A.account_type account_type,count(A.account_type)count from create_role A join (select distinct account from login where login_time >= ? and login_time <= ?) B on A.account=B.account join device_info C on A.openudid = C.openudid where C.os in (" + icons + ") group by A.account_type";
		List<CreateRole> queryData = CreateRole.dao.use(db).find(sql, startDate, endDate);
		List<String> accountType = new ArrayList<String>();
		List<Long> accountTypeData = new ArrayList<Long>();
		for (CreateRole cr : queryData) {
			accountType.add(cr.getStr("account_type"));
			accountTypeData.add(cr.getLong("count"));
		}
		data.put("activePlayer", accountTypeData);
		data.put("accountType", accountType);
		logger.info("data:" + data);
		return data;
	}
	//map计数器
	private void increaseValue(String key, Map<String, Integer> map) {
		int value = map.get(key);
		value++;
		map.put(key, value);
	}
    //获取list的 max值下标
	private int max(List<Integer> list) {
		if (list == null || list.size() == 0) {
			return 0;
		}
		int max = 0;
		for (int i = 0; i < list.size() - 1; i++) {
			if (list.get(i) > list.get(i + 1)) {
				max = list.get(i);
			} else {
				max = list.get(i + 1);
			}
		}
		return max;
	}

}
