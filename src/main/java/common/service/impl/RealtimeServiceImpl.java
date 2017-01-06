package common.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import common.model.CreateRole;
import common.model.DeviceInfo;
import common.model.LogCharge;
import common.model.Login;
import common.model.Logout;
import common.model.OnlineCount;
import common.service.RealtimeService;
import common.utils.DateUtils;

/**
 * 查询实时数据页 --包括上方表格 和 下方echart
 * 指标说明
 * eT=equipmentToday   aPT=activePlayersToday  pPT=paidPlayersToday  rT=revenueToday   (gTT=gameTimesToday)==>(firstPp=firstPaidPlayers)
 * nPT=newPlayersToday  oPT=oldPlayersToday    pTT=paidTimesToday    rSum=revenueSum   lGPT=loginGamePeriodToday    
 * @author chris
 *
 */
public class RealtimeServiceImpl implements RealtimeService{
	private static Logger logger = Logger.getLogger(RealtimeServiceImpl.class);
	
	/**
	 * 查询realtime 实时数据  付费使用设备数计算
	 * activateDev 今日-总计 激活设备数   dActivateDev 今日激活设备数   allActivateDev 总激活设备
	 * newAccount  今日-总计 新增帐号    dNewAccount 今日新增帐号     allNewAccount 总新增帐号
	 * dau 今日活跃玩家数 
	 * dauOld 今日活跃老玩家数
	 * newPaidDev 今日新增设备中的付费设备
	 * dActiveDev 今日活跃设备数
	 * allActiveDev 总活跃设备数
	 * revenue 今日-总计 收入   dRevenue 今日收入   allRevenue 总收入
	 * paidDetail  dPaidDev今日付费设备-dFirstPaid今日首次付费人数-dPaidTimes今日付费次数-allPaidDev总计付费设备-allPaidTimes总计付费次数
	 * 今日新增付费率dnewPaidRate = newPaidDev/dActivateDev
	 * 今日付费率dPaidRate = dPaidDev/dActiveDev
	 * 总计付费率allPaidRate = allPaidDev/allActiveDev
	 * 今日DAU ARPU --dDAUARPU = dRevenue/dau
	 * 总均DAU ARPU --allDAUARPU Anone
	 * 付费ARPU = allRevenue/allActiveDev
	 * 付费ARPPU = allRevenue/allPaidDev
	 * 今日人均付费次数 dAvgPaidTimes = dPaidTimes/dPaidDev  
	 * 总均人均付费次数 allAvgPaidTimes = allPaidTimes/allPaidDev
	 * onlineLoginTimesSql  计算今日平均单次时长 dOnlineTime 今日总游戏时长 dLoginTimes今日登录次数
	 * 今日平均单次时长 dAvgSinglePeriod = dOnlineTime/dLoginTimes
	 * 今日人均登录次数 dAvgLoginTimes= dLoginTimes/dau
	 */
	public Map<String, String> realtimeData(String icons, String db) {
		String activateDevSql = "select count(*)all_count,count(case when DATE_FORMAT(create_time,'%Y-%m-%d')=DATE_FORMAT(now(),'%Y-%m-%d') then openudid end)t_count from device_info where os in ("+icons+")"; 
		String newAccountSql = "select count(*)all_count,count(case when DATE_FORMAT(A.create_time,'%Y-%m-%d')=DATE_FORMAT(now(),'%Y-%m-%d') then A.account end)count from create_role A join device_info B on A.openudid = B.openudid where B.os in ("+icons+")";
		String dauSql = "select count(distinct A.account)count from login A join device_info B on A.openudid = B.openudid where DATE_FORMAT(A.login_time,'%Y-%m-%d')=DATE_FORMAT(now(),'%Y-%m-%d') and B.os in (" + icons + ")";
		String dauOldSql = "select count(*)count from (select distinct C.account from login C join device_info D on C.openudid = D.openudid where D.os in (" + icons + ") and DATE_FORMAT(C.login_time, '%Y-%m-%d')=DATE_FORMAT(now(), '%Y-%m-%d')) A left join (select E.account from create_role E join device_info F on E.openudid = F.openudid where F.os in (" + icons + ") and DATE_FORMAT(E.create_time,'%Y-%m-%d')=DATE_FORMAT(now(), '%Y-%m-%d')) B on A.account = B.account where B.account is null";
		String newPaidDevSql = "select count(distinct A.openudid)count from (select openudid from device_info where DATE_FORMAT(create_time,'%Y-%m-%d')=DATE_FORMAT(now(),'%Y-%m-%d') and os in (" + icons + "))A join create_role B on A.openudid = B.openudid join log_charge C on B.account = C.account where DATE_FORMAT(C.timestamp,'%Y-%m-%d')=DATE_FORMAT(now(),'%Y-%m-%d') and C.is_product=1";
		String dActiveDevSql = "select count(*)count from (select distinct openudid from login where date = DATE_FORMAT(now(),'%Y-%m-%d')) A join device_info B on A.openudid = B.openudid where B.os in ("+ icons +")";
		String allActiveDevSql = "select count(*)count from (select distinct openudid from login) A join device_info B on A.openudid = B.openudid where B.os in ("+ icons +");";
		String revenueSql = "select sum(A.count)allRevenue,sum(case when DATE_FORMAT(A.timestamp,'%Y-%m-%d')=DATE_FORMAT(now(),'%Y-%m-%d') then A.count else 0 end)t_r from log_charge A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where A.is_product = 1 and C.os in ("+ icons +")";
		String paidDetailSql = "select count(distinct case when DATE_FORMAT(A.timestamp,'%Y-%m-%d')=DATE_FORMAT(now(),'%Y-%m-%d') then C.openudid end)paidDevice,count(distinct case when A.charge_times=1 and DATE_FORMAT(A.timestamp,'%Y-%m-%d')=DATE_FORMAT(now(),'%Y-%m-%d') then C.openudid end)firstPaidD,count(case when DATE_FORMAT(A.timestamp,'%Y-%m-%d')=DATE_FORMAT(now(),'%Y-%m-%d') then A.account end)t_times,count(distinct C.openudid)allPaidDevice,count(A.account)allPaidTimes from log_charge A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where A.is_product = 1 and C.os in ("+ icons +")";
		String onlineLoginTimesSql = "select sum(case when A.online_time<86400 then A.online_time else 86400 end)online_time,count(A.account)count from logout A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where A.date=DATE_FORMAT(now(),'%Y-%m-%d') and C.os in (" + icons + ")";
		
		DeviceInfo activateDevDao = DeviceInfo.dao.use(db).findFirst(activateDevSql);
		CreateRole newAccountDao = CreateRole.dao.use(db).findFirst(newAccountSql);
		Login dauDao = Login.dao.use(db).findFirst(dauSql);
		Login dauOldDao = Login.dao.use(db).findFirst(dauOldSql);
		DeviceInfo newPaidDevDao = DeviceInfo.dao.use(db).findFirst(newPaidDevSql);
		Login dActiveDevDao = Login.dao.use(db).findFirst(dActiveDevSql);
		Login allActiveDevDao = Login.dao.use(db).findFirst(allActiveDevSql);
		LogCharge revenueDao = LogCharge.dao.use(db).findFirst(revenueSql);
		LogCharge paidDetailDao = LogCharge.dao.use(db).findFirst(paidDetailSql);
		Logout onlineLoginTimesDao = Logout.dao.use(db).findFirst(onlineLoginTimesSql);
		
		long dActivateDev = activateDevDao.getLong("t_count");
		long allActivateDev = activateDevDao.getLong("all_count");
		long dNewAccount = newAccountDao.getLong("count");
		long allNewAccount = newAccountDao.getLong("all_count");
		long dau = dauDao.getLong("count");
		long dauOld = dauOldDao.getLong("count");
		long newPaidDev = newPaidDevDao.getLong("count");
		long dActiveDev = dActiveDevDao.getLong("count");
		long allActiveDev = allActiveDevDao.getLong("count");
		double dRevenue = revenueDao.getDouble("t_r")==null?0.0:revenueDao.getDouble("t_r");
		double allRevenue = revenueDao.getDouble("allRevenue")==null?0.0:revenueDao.getDouble("allRevenue");
		long dPaidDev = paidDetailDao.getLong("paidDevice");
		long dFirstPaid = paidDetailDao.getLong("firstPaidD");
		long dPaidTimes = paidDetailDao.getLong("t_times");
		long allPaidDev = paidDetailDao.getLong("allPaidDevice");
		long allPaidTimes = paidDetailDao.getLong("allPaidTimes");
		double dOnlineTime = onlineLoginTimesDao.getBigDecimal("online_time")==null?0.0:onlineLoginTimesDao.getBigDecimal("online_time").doubleValue();
		long dLoginTimes = onlineLoginTimesDao.getLong("count");
		
		double dnewPaidRate = 0.000;
		double dPaidRate = 0.000;
		double allPaidRate = 0.000;
		double dDAUARPU = 0.00;
		double ARPU = 0.00;
		double ARPPU =0.00;
		double dAvgPaidTimes = 0.0;
		double allAvgPaidTimes = 0.0;
		double dAvgSinglePeriod = 0.0;
		double dAvgLoginTimes = 0.0;
		
		if(dActivateDev!=0){
			dnewPaidRate = (double)newPaidDev/(double)dActivateDev*100;
			BigDecimal bg = new BigDecimal(dnewPaidRate);
			dnewPaidRate = bg.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
		}
		if(dActiveDev!=0){
			dPaidRate = (double)dPaidDev/(double)dActiveDev*100;
			BigDecimal bg = new BigDecimal(dPaidRate);
			dPaidRate = bg.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
		}
		if(allActiveDev!=0){
			allPaidRate = (double)allPaidDev/(double)allActiveDev*100;
			BigDecimal bg = new BigDecimal(allPaidRate);
			allPaidRate = bg.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
		}
		if(dau!=0){
			dDAUARPU = dRevenue/(double)dau;
			BigDecimal bg = new BigDecimal(dDAUARPU);
			dDAUARPU = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		}
		if(allActiveDev!=0){
			ARPU = allRevenue/(double)allActiveDev;
			BigDecimal bg = new BigDecimal(ARPU);
			ARPU = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		}
		if(allPaidDev!=0){
			ARPPU = allRevenue/(double)allPaidDev;
			BigDecimal bg = new BigDecimal(ARPPU);
			ARPPU = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		}
		if(dPaidDev!=0){
			dAvgPaidTimes = (double)dPaidTimes/(double)dPaidDev;
			BigDecimal bg = new BigDecimal(dAvgPaidTimes);
			dAvgPaidTimes = bg.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
		}
		if(allPaidDev!=0){
			allAvgPaidTimes =(double) allPaidTimes/(double)allPaidDev;
			BigDecimal bg = new BigDecimal(allAvgPaidTimes);
			allAvgPaidTimes = bg.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
		}
		if(dLoginTimes!=0){
			dAvgSinglePeriod = dOnlineTime/(double)dLoginTimes;
			dAvgSinglePeriod = dAvgSinglePeriod / 60;
			BigDecimal bg = new BigDecimal(dAvgSinglePeriod);
			dAvgSinglePeriod = bg.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
		}
		if(dau!=0){
			dAvgLoginTimes= dLoginTimes/dau;
			BigDecimal bg = new BigDecimal(dAvgLoginTimes);
			dAvgLoginTimes = bg.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
		}
		
		Map<String, String> data = new HashMap<String, String>();
		data.put("dActivateDev", String.valueOf(dActivateDev));
		data.put("dNewAccount", String.valueOf(dNewAccount));
		data.put("dau", String.valueOf(dau));
		data.put("dauOld", String.valueOf(dauOld));
		data.put("dnewPaidRate", String.valueOf(dnewPaidRate)+"%");
		data.put("dPaidRate", String.valueOf(dPaidRate)+"%");
		data.put("allPaidRate", String.valueOf(allPaidRate)+"%");
		data.put("dRevenue", String.valueOf((long)Math.floor(dRevenue)));
		data.put("allRevenue", String.valueOf((long)Math.floor(allRevenue)));
		data.put("dFirstPaid", String.valueOf(dFirstPaid));
		data.put("dPaidDev", String.valueOf(dPaidDev));
		data.put("allActivateDev", String.valueOf(allActivateDev));
		data.put("allNewAccount", String.valueOf(allNewAccount));
		data.put("dDAUARPU", String.valueOf(dDAUARPU));
		data.put("ARPU", String.valueOf(ARPU));
		data.put("ARPPU", String.valueOf(ARPPU));
		data.put("dAvgPaidTimes", String.valueOf(dAvgPaidTimes));
		data.put("allAvgPaidTimes", String.valueOf(allAvgPaidTimes));
		data.put("dAvgSinglePeriod", String.valueOf(dAvgSinglePeriod)+"分钟");
		data.put("dAvgLoginTimes", String.valueOf(dAvgLoginTimes)+"次");
		
		return data;
	}
	
	/**
	 * 查询过往1 7 30日 数据
	 */
	public Map<String, String> beforeData(String icons, String db){
		String activateDevSql = "select count(*)all_count,count(case when DATE_FORMAT(create_time,'%Y-%m-%d')=DATE_FORMAT(date_sub(now(),interval ? day),'%Y-%m-%d') then openudid end)count from device_info where DATE_FORMAT(create_time,'%Y-%m-%d')<=DATE_FORMAT(date_sub(now(),interval ? day),'%Y-%m-%d') and os in ("+ icons +")";
		String newAccountSql = "select count(*)all_count,count(case when DATE_FORMAT(A.create_time,'%Y-%m-%d')=DATE_FORMAT(date_sub(now(),interval ? day),'%Y-%m-%d') then A.account end)count from create_role A join device_info B on A.openudid = B.openudid where DATE_FORMAT(A.create_time,'%Y-%m-%d')<=DATE_FORMAT(date_sub(now(),interval ? day),'%Y-%m-%d') and B.os in ("+ icons +")";
		String dauSql = "select count(distinct A.account)count from login A join device_info B on A.openudid = B.openudid where DATE_FORMAT(A.login_time,'%Y-%m-%d')=DATE_FORMAT(date_sub(now(),interval ? day),'%Y-%m-%d') and B.os in (" + icons + ")";
		String dauOldSql = "select count(*)count from (select distinct C.account from login C join device_info D on C.openudid = D.openudid where D.os in (" + icons + ") and DATE_FORMAT(C.login_time, '%Y-%m-%d')=DATE_FORMAT(date_sub(now(),interval ? day), '%Y-%m-%d')) A left join (select E.account from create_role E join device_info F on E.openudid = F.openudid where F.os in (" + icons + ") and DATE_FORMAT(E.create_time,'%Y-%m-%d')=DATE_FORMAT(date_sub(now(),interval ? day), '%Y-%m-%d')) B on A.account = B.account where B.account is null";
		String newPaidDevSql = "select count(distinct A.openudid)count from (select openudid from device_info where DATE_FORMAT(create_time,'%Y-%m-%d')=DATE_FORMAT(date_sub(now(),interval ? day),'%Y-%m-%d') and os in (" + icons + "))A join create_role B on A.openudid = B.openudid join log_charge C on B.account = C.account where DATE_FORMAT(C.timestamp,'%Y-%m-%d')=DATE_FORMAT(date_sub(now(),interval ? day),'%Y-%m-%d') and C.is_product=1; ";
		String dActiveDevSql = "select count(*)count from (select distinct openudid from login where date = DATE_FORMAT(date_sub(now(),interval ? day),'%Y-%m-%d')) A join device_info B on A.openudid = B.openudid where B.os in ("+ icons +")";
		String allActiveDevSql = "select count(*)count from (select distinct openudid from login where date<=DATE_FORMAT(date_sub(now(),interval ? day),'%Y-%m-%d')) A join device_info B on A.openudid = B.openudid where B.os in ("+ icons +")";
		String revenueSql = "select sum(A.count)revenue,sum(case when DATE_FORMAT(A.timestamp,'%Y-%m-%d')=DATE_FORMAT(date_sub(now(),interval ? day),'%Y-%m-%d') then A.count else 0 end)y_r from (select * from log_charge where DATE_FORMAT(timestamp,'%Y-%m-%d')<=DATE_FORMAT(date_sub(now(),interval ? day),'%Y-%m-%d')) A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where A.is_product = 1 and C.os in ("+icons+");";
		String paidDetailSql = "select count(distinct case when DATE_FORMAT(A.timestamp,'%Y-%m-%d')=DATE_FORMAT(date_sub(now(),interval ? day),'%Y-%m-%d') then C.openudid end)paidDevice,count(distinct case when A.charge_times=1 and DATE_FORMAT(A.timestamp,'%Y-%m-%d')=DATE_FORMAT(date_sub(now(),interval ? day),'%Y-%m-%d') then C.openudid end)firstPaidD,count(case when DATE_FORMAT(A.timestamp,'%Y-%m-%d')=DATE_FORMAT(date_sub(now(),interval ? day),'%Y-%m-%d') then A.account end)times,count(distinct C.openudid)allPaidDevice,count(A.account)allTimes from log_charge A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where A.is_product = 1 and C.os in ("+ icons +") and DATE_FORMAT(A.timestamp,'%Y-%m-%d')<=DATE_FORMAT(date_sub(now(),interval ? day),'%Y-%m-%d')";
		String onlineLoginTimesSql = "select sum(case when A.online_time<86400 then A.online_time else 86400 end)online_time,count(A.account)count from logout A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where A.date=DATE_FORMAT(date_sub(now(),interval ? day),'%Y-%m-%d') and C.os in ("+ icons + ")";
		
		Map<String, String> data = new LinkedHashMap<String, String>();
		int[] day = {1,7,30};
		//分别处理 昨日 七日 三十日
		for(int d : day){
			DeviceInfo activateDevDao = DeviceInfo.dao.use(db).findFirst(activateDevSql, d, d);
			CreateRole newAccountDao = CreateRole.dao.use(db).findFirst(newAccountSql, d, d);
			Login dauDao = Login.dao.use(db).findFirst(dauSql, d);
			Login dauOldDao = Login.dao.use(db).findFirst(dauOldSql, d, d);
			DeviceInfo newPaidDevDao = DeviceInfo.dao.use(db).findFirst(newPaidDevSql, d, d);
			Login dActiveDevDao = Login.dao.use(db).findFirst(dActiveDevSql, d);
			Login allActiveDevDao = Login.dao.use(db).findFirst(allActiveDevSql, d);
			LogCharge revenueDao = LogCharge.dao.use(db).findFirst(revenueSql, d, d);
			LogCharge paidDetailDao = LogCharge.dao.use(db).findFirst(paidDetailSql, d, d, d, d);
			Logout onlineLoginTimesDao = Logout.dao.use(db).findFirst(onlineLoginTimesSql, d);
			
			long dActivateDev = activateDevDao.getLong("count");
			long allActivateDev = activateDevDao.getLong("all_count");
			long dNewAccount = newAccountDao.getLong("count");
			long allNewAccount = newAccountDao.getLong("all_count");
			long dau = dauDao.getLong("count");
			long dauOld = dauOldDao.getLong("count");
			long newPaidDev = newPaidDevDao.getLong("count");
			long dActiveDev = dActiveDevDao.getLong("count");
			long allActiveDev = allActiveDevDao.getLong("count");
			double dRevenue = revenueDao.getDouble("y_r")==null?0.0:revenueDao.getDouble("y_r");
			double allRevenue = revenueDao.getDouble("revenue")==null?0.0:revenueDao.getDouble("revenue");
			long dPaidDev = paidDetailDao.getLong("paidDevice");
			long dFirstPaid = paidDetailDao.getLong("firstPaidD");
			long dPaidTimes = paidDetailDao.getLong("times");
			long allPaidDev = paidDetailDao.getLong("allPaidDevice");
			long allPaidTimes = paidDetailDao.getLong("allTimes");
			double dOnlineTime = onlineLoginTimesDao.getBigDecimal("online_time")==null?0.0:onlineLoginTimesDao.getBigDecimal("online_time").doubleValue();
			long dLoginTimes = onlineLoginTimesDao.getLong("count");
			
			double dnewPaidRate = 0.000;
			double dPaidRate = 0.000;
			double allPaidRate = 0.000;
			double dDAUARPU = 0.00;
			double ARPU = 0.00;
			double ARPPU =0.00;
			double dAvgPaidTimes = 0.0;
			double allAvgPaidTimes = 0.0;
			double dAvgSinglePeriod = 0.0;
			double dAvgLoginTimes = 0.0;
			
			if(dActivateDev!=0){
				dnewPaidRate = (double)newPaidDev/(double)dActivateDev*100;
				BigDecimal bg = new BigDecimal(dnewPaidRate);
				dnewPaidRate = bg.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
			}
			if(dActiveDev!=0){
				dPaidRate = (double)dPaidDev/(double)dActiveDev*100;
				BigDecimal bg = new BigDecimal(dPaidRate);
				dPaidRate = bg.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
			}
			if(allActiveDev!=0){
				allPaidRate = (double)allPaidDev/(double)allActiveDev*100;
				BigDecimal bg = new BigDecimal(allPaidRate);
				allPaidRate = bg.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
			}
			if(dau!=0){
				dDAUARPU = dRevenue/(double)dau;
				BigDecimal bg = new BigDecimal(dDAUARPU);
				dDAUARPU = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			}
			if(allActiveDev!=0){
				ARPU = allRevenue/(double)allActiveDev;
				BigDecimal bg = new BigDecimal(ARPU);
				ARPU = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			}
			if(allPaidDev!=0){
				ARPPU = allRevenue/(double)allPaidDev;
				BigDecimal bg = new BigDecimal(ARPPU);
				ARPPU = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			}
			if(dPaidDev!=0){
				dAvgPaidTimes = (double)dPaidTimes/(double)dPaidDev;
				BigDecimal bg = new BigDecimal(dAvgPaidTimes);
				dAvgPaidTimes = bg.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
			}
			if(allPaidDev!=0){
				allAvgPaidTimes =(double) allPaidTimes/(double)allPaidDev;
				BigDecimal bg = new BigDecimal(allAvgPaidTimes);
				allAvgPaidTimes = bg.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
			}
			if(dLoginTimes!=0){
				dAvgSinglePeriod = dOnlineTime/(double)dLoginTimes;
				dAvgSinglePeriod = dAvgSinglePeriod / 60;
				BigDecimal bg = new BigDecimal(dAvgSinglePeriod);
				dAvgSinglePeriod = bg.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
			}
			if(dau!=0){
				dAvgLoginTimes= dLoginTimes/dau;
				BigDecimal bg = new BigDecimal(dAvgLoginTimes);
				dAvgLoginTimes = bg.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
			}
			data.put("dActivateDev"+d, String.valueOf(dActivateDev));
			data.put("dNewAccount"+d, String.valueOf(dNewAccount));
			data.put("dau"+d, String.valueOf(dau));
			data.put("dauOld"+d, String.valueOf(dauOld));
			data.put("dnewPaidRate"+d, String.valueOf(dnewPaidRate)+"%");
			data.put("dPaidRate"+d, String.valueOf(dPaidRate)+"%");
			data.put("allPaidRate"+d, String.valueOf(allPaidRate)+"%");
			data.put("dRevenue"+d, String.valueOf(Math.floor(dRevenue)));
			data.put("allRevenue"+d, String.valueOf(Math.floor(allRevenue)));
			data.put("dFirstPaid"+d, String.valueOf(dFirstPaid));
			data.put("dPaidDev"+d, String.valueOf(dPaidDev));
			data.put("allActivateDev"+d, String.valueOf(allActivateDev));
			data.put("allNewAccount"+d, String.valueOf(allNewAccount));
			data.put("dDAUARPU"+d, String.valueOf(dDAUARPU));
			data.put("ARPU"+d, String.valueOf(ARPU));
			data.put("ARPPU"+d, String.valueOf(ARPPU));
			data.put("dAvgPaidTimes"+d, String.valueOf(dAvgPaidTimes));
			data.put("allAvgPaidTimes"+d, String.valueOf(allAvgPaidTimes));
			data.put("dAvgSinglePeriod"+d, String.valueOf(dAvgSinglePeriod)+"分钟");
			data.put("dAvgLoginTimes"+d, String.valueOf(dAvgLoginTimes)+"次");
		}
		return data;
	}
	
	/**
	 * 查询实时在线人数 online_count 
	 * @param date 时间列表
	 */
	public Map<String, Object> queryRealtimePlayerCount(String[] date, String db){
		String sql = "select sum(online_count)sum,hour(online_datetime)hour,count(*)count from online_count where online_date = ? group by hour";
		Map<String, Object> data = new TreeMap<String, Object>(new Comparator<String>(){
			@Override
			public int compare(String o1, String o2) {
				if("昨日".equals(o1)){
					return 1;
				}
				return o2.compareTo(o1);
			}});
		for(String d : date){
			List<OnlineCount> onlineCount = OnlineCount.dao.use(db).find(sql, d);
			Map<Integer, Long> sort = new TreeMap<Integer, Long>();
			for(int i=0;i<24;i++){
				sort.put(i, 0L);
			}
			//每小时的总在线人数sum/次数count 代表该时段均值
			for(OnlineCount oc : onlineCount){
				long count = oc.getLong("count");
				long sum = oc.getBigDecimal("sum").longValue();
				double result = (double)sum / (double)count;
				BigDecimal bg = new BigDecimal(result);
				result = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				long avg = (long)Math.ceil(result);
				sort.put(oc.getInt("hour"), avg);
			}
			List<Long> al = new ArrayList<Long>();
			al.addAll(sort.values());
			d = DateUtils.convertDate(d);
			data.put(d, al);
		}
		return data;
	}
	
	//查询实时设备
	public Map<String, Object> queryRealtimeDevice(String icons, String[] date, String db){
		String sql ="select count(*)count,hour(create_time)hour from device_info where DATE_FORMAT(create_time,'%Y-%m-%d')= ?  and os in (" + icons + ") group by hour(create_time)";
		Map<String, Object> data = new TreeMap<String, Object>(new Comparator<String>(){
			@Override
			public int compare(String o1, String o2) {
				if("昨日".equals(o1)){
					return 1;
				}
				return o2.compareTo(o1);
			}});
		for(String d : date){
			List<DeviceInfo> deviceInfo = DeviceInfo.dao.use(db).find(sql,d);
			Map<Integer, Long> sort = new TreeMap<Integer, Long>();
			for(int i=0;i<24;i++){
				sort.put(i, 0L);
			}
			for(DeviceInfo di:deviceInfo){
				sort.put(di.getInt("hour"), di.getLong("count"));
			}
			List<Long> al = new ArrayList<Long>();
			al.addAll(sort.values());
			d = DateUtils.convertDate(d);
			data.put(d,al);
		}
		logger.info("data:" + data);
		return data;
	}
	
	//查询实时新增玩家
	public Map<String, Object> queryRealtimeAddPlayers(String icons, String[] date, String db){
		String sql = "select count(*)count,hour(A.create_time)hour from create_role A join device_info B on A.openudid  = B.openudid where DATE_FORMAT(A.create_time,'%Y-%m-%d')= ? and B.os in (" + icons + ") group by hour(A.create_time)";
		Map<String, Object> data = new TreeMap<String, Object>(new Comparator<String>(){
			@Override
			public int compare(String o1, String o2) {
				if("昨日".equals(o1)){
					return 1;
				}
				return o2.compareTo(o1);
			}});
		for(String d: date){
			List<CreateRole> createRole = CreateRole.dao.use(db).find(sql,d);
			Map<Integer, Long> sort = new TreeMap<Integer, Long>();
			for(int i=0;i<24;i++){
				sort.put(i, 0L);
			}
			for(CreateRole cr : createRole){
				sort.put(cr.getInt("hour"), cr.getLong("count"));
			}
			List<Long> al = new ArrayList<Long>();
			al.addAll(sort.values());
			d = DateUtils.convertDate(d);
			data.put(d, al);
		}
		logger.info("queryRealtimeAddPlayers:" + data);
		return data;
	}
	//查询实时收入金额
	public Map<String, Object> queryRealtimeRevenue(String icons, String[] date, String db){
		String sql = "select sum(A.count)revenue,hour(A.timestamp)hour from log_charge A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where A.is_product = 1 and DATE_FORMAT(A.timestamp,'%Y-%m-%d')= ? and C.os in (" + icons + ") group by hour(A.timestamp)";
		Map<String, Object> data = new TreeMap<String, Object>(new Comparator<String>(){
			@Override
			public int compare(String o1, String o2) {
				if("昨日".equals(o1)){
					return 1;
				}
				return o2.compareTo(o1);
			}});
		for(String d: date){
			List<LogCharge> logCharge = LogCharge.dao.use(db).find(sql,d);
			Map<Integer, Double> sort = new TreeMap<Integer, Double>();
			for(int i=0;i<24;i++){
				sort.put(i, 0.0);
			}
			for(LogCharge lc : logCharge){
				sort.put(lc.getInt("hour"),lc.getDouble("revenue"));
			}
			List<Double> al = new ArrayList<Double>();
			al.addAll(sort.values());
			d = DateUtils.convertDate(d);
			data.put(d, al);
		}
		logger.info("data:" + data);
		return data;
	}
}
