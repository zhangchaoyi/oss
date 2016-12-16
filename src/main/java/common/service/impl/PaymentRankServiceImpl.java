package common.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import common.model.LogCharge;
import common.model.Logout;
import common.pojo.AccountDetail;
import common.pojo.PaymentRank;
import common.service.PaymentRankService;
import common.utils.DateUtils;
import common.utils.StringUtils;

/**
 * 付费排行页
 * @author chris
 *
 */
public class PaymentRankServiceImpl implements PaymentRankService {
	private static Logger logger = Logger.getLogger(PaymentRankServiceImpl.class);
	/**
	 * 排名详情
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间
	 */
	public List<List<String>> queryRank(String icons, String startDate, String endDate, String db) {
		//得到付费排行等信息
		String pSql = "select A.*,DATE_FORMAT(B.timestamp,'%Y-%m-%d')fpt from (select A.account, sum(A.count)revenue, count(*)count from log_charge A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where A.is_product = 1 and DATE_FORMAT(A.timestamp,'%Y-%m-%d') between ? and ? and C.os in (" + icons + ") group by A.account) A join log_charge B on A.account = B.account where B.charge_times=1 and B.is_product = 1 order by revenue desc";
		//Map<account, PaymentRank>
		Map<String, PaymentRank> sort = new LinkedHashMap<String, PaymentRank>();
		List<LogCharge> logCharge = LogCharge.dao.use(db).find(pSql, startDate, endDate);
		List<String> accounts = new ArrayList<String>();
		
		for(LogCharge lc : logCharge){
			String account = lc.getStr("account");
			PaymentRank pr = new PaymentRank();
			pr.setFirstPaidTime(lc.getStr("fpt"));
			pr.setRevenue(lc.getDouble("revenue"));
			pr.setPaidTimes(lc.getLong("count"));
			sort.put(account, pr);
			accounts.add(account);
		}
		List<List<String>> data = new ArrayList<List<String>>();
		if(accounts.isEmpty()){
			return data;
		}
		//得到剩余信息
		String queryAccounts = StringUtils.arrayToQueryString(accounts.toArray(new String[accounts.size()]));
		String dSql = "select A.*,B.level,DATE_FORMAT(C.create_time,'%Y-%m-%d')create_time from (select account,sum(online_time)online_time,count(*)times,count(distinct date)online_days from logout group by account) A join (select account,max(level)level from level_up group by account) B on A.account = B.account join create_role C on A.account = C.account where A.account in ("+ queryAccounts +")";
		
		List<Logout> logout = Logout.dao.use(db).find(dSql);
		for(Logout l : logout){
			String account = l.getStr("account");
			long oT = l.getBigDecimal("online_time").longValue();
			PaymentRank pr = sort.get(account);
			pr.setOnlineTimes(DateUtils.getTimeFromSecond(oT));
			pr.setGameTimes(l.getLong("times"));
			pr.setOnlineDays(l.getLong("online_days"));
			pr.setCreateTime(l.getStr("create_time"));
			pr.setLevel(l.getInt("level"));
			sort.put(account, pr);
		}
		int num = 1;
		for(Map.Entry<String, PaymentRank> entry : sort.entrySet()){
			PaymentRank pr = entry.getValue();
			List<String> per = new ArrayList<String>();
			per.add(String.valueOf(num));
			per.add(entry.getKey());
			per.add(pr.getCreateTime());
			per.add(pr.getFirstPaidTime());
			per.add(String.valueOf(pr.getRevenue()));
			per.add(String.valueOf(pr.getPaidTimes()));
			per.add(String.valueOf(pr.getOnlineDays()));
			per.add(String.valueOf(pr.getOnlineTime()));
			per.add(String.valueOf(pr.getGameTimes()));
			per.add(String.valueOf(pr.getLevel()));
			per.add(entry.getKey());
			num++;
			data.add(per);
		}
		return data;
	}
	/**
	 * 帐号详情
	 * @param accountArray 帐号列表
	 * @param categories 日期列表
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间
	 */
	public Map<String, Object> queryAccountDetail(String[] accountArray, List<String> categories, String icons, String startDate, String endDate, String db) {
		logger.info("params:{"+"accountArray:"+accountArray+"}");
		String account = StringUtils.arrayToQueryString(accountArray);
		String lSql = "select DATE_FORMAT(A.date,'%Y-%m-%d')date,sum(A.online_time)online_time,count(*)count from logout A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where A.account=" + account + " and A.date between ? and ? and C.os in (" + icons + ") group by A.date";
		String pSql = "select DATE_FORMAT(A.timestamp,'%Y-%m-%d')date,sum(A.count)revenue from log_charge A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where A.is_product = 1 and A.account = " + account + " and DATE_FORMAT(A.timestamp,'%Y-%m-%d') between ? and ? and C.os in (" + icons + ") group by date";
		
		Map<String, AccountDetail> sort = new LinkedHashMap<String, AccountDetail>();
		//init
		for(String date : categories) {
			AccountDetail ad = new AccountDetail();
			sort.put(date, ad);
		}
		
		List<Logout> logout = Logout.dao.use(db).find(lSql, startDate, endDate);
		for(Logout l : logout){
			String date = l.getStr("date");
			AccountDetail ad = sort.get(date);
			ad.setOnlineTime(l.getBigDecimal("online_time").longValue());
			ad.setLoginTimes(l.getLong("count"));
			sort.put(date, ad);
		}
		List<LogCharge> logCharge = LogCharge.dao.use(db).find(pSql, startDate, endDate);
		for(LogCharge lc : logCharge){
			String date = lc.getStr("date");
			AccountDetail ad = sort.get(date);
			ad.setRevenue(lc.getDouble("revenue"));
			sort.put(date, ad);
		}
		
		List<Long> oTList = new ArrayList<Long>();
		List<Long> lTList = new ArrayList<Long>();
		List<Double> pRList = new ArrayList<Double>();
		List<List<String>> tableData = new ArrayList<List<String>>();
		
		//collect data
		for(Map.Entry<String, AccountDetail> entry : sort.entrySet()){
			AccountDetail ad = entry.getValue();
			long oT = ad.getOnlineTime();
			BigDecimal oTBg = new BigDecimal(oT*1.0/60);
			oT = oTBg.setScale(0, BigDecimal.ROUND_HALF_UP).longValue();
			long lT = ad.getLoginTimes();
			double pR = ad.getRevenue();
			oTList.add(oT);
			lTList.add(lT);
			pRList.add(pR);
			List<String> per = new ArrayList<String>();
			per.addAll(Arrays.asList(entry.getKey(), String.valueOf(oT), String.valueOf(lT), String.valueOf(pR)));
			tableData.add(per);
		}
		
		Map<String, Object> data = new HashMap<String, Object>(); 
		data.put("oTList", oTList);
		data.put("lTList", lTList);
		data.put("pRList", pRList);
		data.put("tableData", tableData);
		logger.info("data:" +data);
		return data;
	}
}
