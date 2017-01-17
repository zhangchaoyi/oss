package common.service.impl;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.apache.log4j.Logger;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;
import common.model.ActiveUser;
import common.model.CreateRole;
import common.model.LevelUp;
import common.model.LogCharge;
import common.model.Login;
import common.service.ActivePlayersService;

/**
 * 查询活跃玩家页数据
 * @author chris
 *
 */
public class ActivePlayersServiceImpl implements ActivePlayersService {
	private static Logger logger = Logger.getLogger(ActivePlayersServiceImpl.class);
	/**
	 * 查询dau
	 * @param categories 日期列表
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间 
	 */
	
	public List<Long> queryDau(List<String> categories, String icons, String startDate, String endDate, String db, String versions, String chId) {
		List<Long> data = new ArrayList<Long>();
		String sql = "select count(*)dau,DATE_FORMAT(A.date,'%Y-%m-%d')date from (select account,date from login where date between ? and ? group by date,account) A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where C.os in ("+icons+") and C.script_version in ("+versions+") and B.ch_id in ("+chId+") group by A.date; ";
		List<ActiveUser> dau = ActiveUser.dao.use(db).find(sql, startDate, endDate);

		Map<String, Long> sort = new TreeMap<String, Long>();
		for (String category : categories) {
			sort.put(category, 0L);
		}
		for (ActiveUser cr : dau) {
			sort.put(cr.getStr("date"), cr.getLong("dau"));
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
	public Map<String, List<Long>> queryPaidInActiveUser(List<String> categories, String icons, String startDate, String endDate, String db, String versions, String chId){
		Map<String, List<Long>> data = new HashMap<String, List<Long>>();
		String sql = "select DATE_FORMAT(E.date,'%Y-%m-%d') date,sum(case when F.account is not null then 1 else 0 end)paid,sum(case when F.account is null then 1 else 0 end)notpaid from(select A.date,A.account from (select distinct account,date from login where login_time >= ? and login_time <= ?) A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where C.os in("+ icons +")) E left join (select distinct account from log_charge where is_product = 1) F on E.account=F.account group by DATE_FORMAT(E.date,'%Y-%m-%d')";
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
	public Map<String, Object> queryDAUWAUMAU(final List<String> categories, String icons, final String db, String versions, String chId) {
		final String sql = "select count(distinct case when A.date=? then A.account else 0 end)dau,count(distinct case when A.date between date_sub(?,interval 6 day) and ? then A.account else 0 end)wau,count(distinct case when A.date between date_sub(?,interval 30 day) and ? then A.account else 0 end)mau from login A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where C.os in ("+icons+") and C.script_version in ("+versions+") and C.ch_id in ("+chId+");";
		// Map<Date,Map<name,value>> --Map<"2016-08-20",Map<"dau",1>>
		final Map<String, Map<String, Long>> sort = new TreeMap<String, Map<String, Long>>();
		// 初始化
		for (String category : categories) {
			Map<String, Long> subMap = new HashMap<String, Long>();
			subMap.put("dau", 0L);
			subMap.put("wau", 0L);
			subMap.put("mau", 0L);
			sort.put(category, subMap);
		}
		boolean succeed = Db.tx(new IAtom(){
			public boolean run() throws SQLException{
				for(String d : categories){
					Record l = Db.use(db).findFirst(sql, d, d, d, d, d);
					long dau = l.getLong("dau");
					long wau = l.getLong("wau");
					long mau = l.getLong("mau");
					Map<String, Long> subMap = sort.get(d);
					subMap.put("dau",dau);
					subMap.put("wau",wau);
					subMap.put("mau",mau);
					sort.put(d,subMap);
				}
				return true;
			}
		});
		if(succeed==false){
			logger.info("transaction failed");
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
	public List<Double> queryActivePlayersDauMauRate(final List<String> categories, String icons, final String db, String versions, String chId) {
		List<Double> data = new ArrayList<Double>();
		final String sql = "select count(distinct case when A.date=? then A.account else 0 end)dau,count(distinct case when A.date between date_sub(?,interval 30 day) and ? then A.account else 0 end)mau from login A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where C.os in ("+icons+") and C.script_version in ("+versions+") and C.ch_id in ("+chId+");";
		final Map<String, Double> sort = new LinkedHashMap<String, Double>();
		// 初始化Map
		for (String category : categories) {
			sort.put(category, 0.0);			
		}
		boolean succeed = Db.tx(new IAtom(){
			public boolean run() throws SQLException{
				for(String d : categories){
					Record l = Db.use(db).findFirst(sql, d, d, d);
					long dau = l.getLong("dau");
					long mau = l.getLong("mau");
					double rate = (double)dau/(double)mau;
					BigDecimal bg = new BigDecimal(rate);
					rate = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
					sort.put(d, rate);
				}
				return true;
			}
		});
		if(succeed==false){
			logger.info("transaction failed");
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
	public List<Long> queryPlayDays(List<String> playDaysPeriod, String icons, String startDate, String endDate, String db, String versions, String chId) {
		List<Long> data = new ArrayList<Long>();
		String sql = "select count(distinct E.date) count from login E join (select A.account from(select distinct account from login where login_time >= ? and login_time <= ? ) A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where C.os in(" + icons + ") and C.script_version in ("+versions+") and C.ch_id in ("+chId+")) F on E.account = F.account where E.login_time <= ? group by E.account";
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
	public Map<Integer, Long> queryRank(String icons, String startDate, String endDate, String db, String versions, String chId) {
		String sql = "select F.level,count(F.level) count from (select D.account,max(D.level)level from level_up D join(select A.account from (select distinct account from login where login_time >= ? and login_time <= ?) A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where C.os in (" + icons + ") and C.script_version in ("+versions+") and C.ch_id in ("+chId+")) E on D.account = E.account group by D.account) F group by F.level";
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
	public Map<String, Object> queryArea(String icons, String startDate, String endDate, String db, String versions, String chId) {
		Map<String, Object> data = new HashMap<String, Object>();
		String sql = "select C.province province,count(C.province) count from create_role A join (select distinct account from login where login_time >= ? and login_time <= ?) B on A.account=B.account join device_info C on A.openudid=C.openudid where C.os in (" + icons + ") and C.script_version in ("+versions+") and C.ch_id in ("+chId+") group by C.province";
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
	public Map<String, Object> queryCountry(String icons, String startDate, String endDate, String db, String versions, String chId) {
		Map<String, Object> data = new HashMap<String, Object>();
		String sql = "select C.country country,count(C.country) count from create_role A join (select distinct account from login where login_time >= ? and login_time <= ?) B on A.account=B.account join device_info C on A.openudid=C.openudid where C.os in (" + icons + ") and C.script_version in ("+versions+") and C.ch_id in ("+chId+") group by C.country";
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
	public Map<String, Object> queryAccountType(String icons, String startDate, String endDate, String db, String versions, String chId) {
		Map<String, Object> data = new HashMap<String, Object>();
		String sql = "select A.account_type account_type,count(A.account_type)count from create_role A join (select distinct account from login where login_time >= ? and login_time <= ?) B on A.account=B.account join device_info C on A.openudid = C.openudid where C.os in (" + icons + ") and C.script_version in ("+versions+") and C.ch_id in ("+chId+") group by A.account_type";
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
