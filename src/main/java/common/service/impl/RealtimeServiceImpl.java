package common.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import common.model.CreateRole;
import common.model.DeviceInfo;
import common.model.LogCharge;
import common.model.Login;
import common.model.Logout;
import common.mysql.DbSelector;
import common.service.RealtimeService;
import common.utils.DateUtils;

/**
 * 查询实时数据页 --包括上方表格 和 下方echart
 * 指标说明
 * eT=equipmentToday   aPT=activePlayersToday  pPT=paidPlayersToday  rT=revenueToday        gTT=gameTimesToday
 * nPT=newPlayersToday  oPT=oldPlayersToday    pTT=paidTimesToday    rSum=revenueSum   lGPT=loginGamePeriodToday    
 * @author chris
 *
 */
public class RealtimeServiceImpl implements RealtimeService{
	private static Logger logger = Logger.getLogger(RealtimeServiceImpl.class);
	private String db = DbSelector.getDbName();
	//实时查询十个数据,用于动态更新
	public Map<String, String> queryRealtimeData(String icons){
		String eSql = "select count(*)count from device_info where DATE_FORMAT(create_time,'%Y-%m-%d')=DATE_FORMAT(now(),'%Y-%m-%d') and os in (" + icons + ")";
		String aPSql = "select count(distinct A.account)count from login A join device_info B on A.openudid = B.openudid where DATE_FORMAT(A.login_time,'%Y-%m-%d')=DATE_FORMAT(now(),'%Y-%m-%d') and B.os in (" + icons + ")";
		String pPSql = "select count(distinct A.account)count from log_charge A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where DATE_FORMAT(A.timestamp,'%Y-%m-%d')=DATE_FORMAT(now(),'%Y-%m-%d') and C.os in (" + icons + ")";
		String rTSql = "select sum(A.count)revenue from log_charge A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where DATE_FORMAT(A.timestamp,'%Y-%m-%d')=DATE_FORMAT(now(),'%Y-%m-%d') and C.os in (" + icons + ")";
		String gTSql = "select count(*)count from login A join device_info B on A.openudid = B.openudid where DATE_FORMAT(A.login_time,'%Y-%m-%d')=DATE_FORMAT(now(),'%Y-%m-%d') and B.os in (" + icons + ")";
		String nPSql = "select count(*)count from create_role A join device_info B on A.openudid = B.openudid where DATE_FORMAT(A.create_time,'%Y-%m-%d')=DATE_FORMAT(now(),'%Y-%m-%d') and B.os in (" + icons + ")";
		String oPSql = "select count(*)count from (select distinct C.account from login C join device_info D on C.openudid = D.openudid where D.os in (" + icons + ") and DATE_FORMAT(C.login_time, '%Y-%m-%d')=DATE_FORMAT(now(), '%Y-%m-%d')) A left join (select E.account from create_role E join device_info F on E.openudid = F.openudid where F.os in (" + icons + ") and DATE_FORMAT(E.create_time,'%Y-%m-%d')=DATE_FORMAT(now(), '%Y-%m-%d')) B on A.account = B.account where B.account is null";
		String pTSql = "select count(*)count from log_charge A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where C.os in (" + icons + ") and DATE_FORMAT(A.timestamp,'%Y-%m-%d')=DATE_FORMAT(now(),'%Y-%m-%d')";
		String rSumSql = "select sum(A.count)revenue from log_charge A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where C.os in (" + icons + ")";
		String lGPTSql = "select sum(case when online_time<86400 then A.online_time else 86400 end)online_time,count(A.account)count from logout A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where A.date=DATE_FORMAT(now(),'%Y-%m-%d') and C.os in (" + icons + ")";
		
		List<DeviceInfo> e = DeviceInfo.dao.use(db).find(eSql);
		List<Login> aP = Login.dao.use(db).find(aPSql);
		List<LogCharge> pP = LogCharge.dao.use(db).find(pPSql);
		List<LogCharge> rT = LogCharge.dao.use(db).find(rTSql);
		List<Login> gT = Login.dao.use(db).find(gTSql);
		List<CreateRole> nP = CreateRole.dao.use(db).find(nPSql);
		List<Login> oP = Login.dao.use(db).find(oPSql);
		List<LogCharge> pT = LogCharge.dao.use(db).find(pTSql);
		List<LogCharge> rSum = LogCharge.dao.use(db).find(rSumSql);
		List<Logout> lGPT = Logout.dao.use(db).find(lGPTSql);
		
		Map<String, String> data = new HashMap<String, String>();
		data.put("e", e.get(0).getLong("count").toString());
		data.put("aP", aP.get(0).getLong("count").toString());
		data.put("pP", pP.get(0).getLong("count").toString());
		data.put("rT", rT.get(0).getDouble("revenue")==null?"0.0":rT.get(0).getDouble("revenue").toString());
		data.put("gT", gT.get(0).getLong("count").toString());
		data.put("nP", nP.get(0).getLong("count").toString());
		data.put("oP", oP.get(0).getLong("count").toString());
		data.put("pT", pT.get(0).getLong("count").toString());
		data.put("rSum", rSum.get(0).getDouble("revenue")==null?"0.0":rSum.get(0).getDouble("revenue").toString());
		double onlineTime = lGPT.get(0).getBigDecimal("online_time")==null?0.0:lGPT.get(0).getBigDecimal("online_time").doubleValue();
		long loPlayers = lGPT.get(0).getLong("count");
		double avgPerGamePeroid = 0.0;
		if(loPlayers!=0){
			avgPerGamePeroid = onlineTime/(double)loPlayers;
			BigDecimal bg = new BigDecimal(avgPerGamePeroid);
			avgPerGamePeroid = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		}
		data.put("aGPT", String.valueOf(avgPerGamePeroid));
		logger.info("data:" + data);
		return data;
	}
	//查询昨日,七日,三十日过往不变数据
	public Map<String, String> queryBeforeData(String icons){
		String eSql = "select count(*)count from device_info where DATE_FORMAT(create_time,'%Y-%m-%d')=DATE_FORMAT(date_sub(now(),interval ? day),'%Y-%m-%d') and os in (" + icons + ")";
		String aPSql = "select count(distinct A.account)count from login A join device_info B on A.openudid = B.openudid where DATE_FORMAT(A.login_time,'%Y-%m-%d')=DATE_FORMAT(date_sub(now(),interval ? day),'%Y-%m-%d') and B.os in (" + icons + ")";
		String pPSql = "select count(distinct A.account)count from log_charge A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where DATE_FORMAT(A.timestamp,'%Y-%m-%d')=DATE_FORMAT(date_sub(now(),interval ? day),'%Y-%m-%d') and C.os in (" + icons + ")";
		String rSql = "select sum(A.count)revenue from log_charge A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where DATE_FORMAT(A.timestamp,'%Y-%m-%d')=DATE_FORMAT(date_sub(now(),interval ? day),'%Y-%m-%d') and C.os in (" + icons + ")";
		String gTSql = "select count(*)count from login A join device_info B on A.openudid = B.openudid where DATE_FORMAT(A.login_time,'%Y-%m-%d')=DATE_FORMAT(date_sub(now(),interval ? day),'%Y-%m-%d') and B.os in (" + icons + ")";
		String nPSql = "select count(*)count from create_role A join device_info B on A.openudid = B.openudid where DATE_FORMAT(A.create_time,'%Y-%m-%d')=DATE_FORMAT(date_sub(now(),interval ? day),'%Y-%m-%d') and B.os in (" + icons + ")";
		String oPSql = "select count(*)count from (select distinct C.account from login C join device_info D on C.openudid = D.openudid where D.os in (" + icons + ") and DATE_FORMAT(C.login_time, '%Y-%m-%d')=DATE_FORMAT(date_sub(now(),interval ? day), '%Y-%m-%d')) A left join (select E.account from create_role E join device_info F on E.openudid = F.openudid where F.os in (" + icons + ") and DATE_FORMAT(E.create_time,'%Y-%m-%d')=DATE_FORMAT(date_sub(now(),interval ? day), '%Y-%m-%d')) B on A.account = B.account where B.account is null";
		String pTSql = "select count(*)count from log_charge A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where DATE_FORMAT(A.timestamp,'%Y-%m-%d')=DATE_FORMAT(date_sub(now(),interval ? day),'%Y-%m-%d') and C.os in (" + icons + ")";
		String rSumSql = "select sum(A.count)revenue from log_charge A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where A.timestamp < DATE_FORMAT(date_sub(now(),interval ? day),'%Y-%m-%d') and C.os in (" + icons + ")";
		//登陆时长
		String lGPSql = "select sum(case when A.online_time<86400 then A.online_time else 86400 end)online_time,count(A.account)count from logout A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where A.date=DATE_FORMAT(date_sub(now(),interval ? day),'%Y-%m-%d') and C.os in ("+ icons + ")";

		Map<String, String> data = new HashMap<String, String>();
		int[] day = {1,7,30};
		//分别处理 昨日 七日 三十日
		for(int d : day){
			List<DeviceInfo> e = DeviceInfo.dao.use(db).find(eSql, d);
			List<Login> aP = Login.dao.use(db).find(aPSql, d);
			List<LogCharge> pP = LogCharge.dao.use(db).find(pPSql, d);
			List<LogCharge> r = LogCharge.dao.use(db).find(rSql, d);
			List<Login> gT = Login.dao.use(db).find(gTSql, d);
			List<CreateRole> nP = CreateRole.dao.use(db).find(nPSql, d);
			List<Login> oP = Login.dao.use(db).find(oPSql, d, d);
			List<LogCharge> pT = LogCharge.dao.use(db).find(pTSql, d);
			List<LogCharge> rSum = LogCharge.dao.use(db).find(rSumSql, d-1);
			List<Logout> lGP = Logout.dao.use(db).find(lGPSql,d);
			
			data.put("e"+d, e.get(0).getLong("count").toString());
			data.put("aP"+d, aP.get(0).getLong("count").toString());
			data.put("pP"+d, pP.get(0).getLong("count").toString());
			data.put("r"+d, r.get(0).getDouble("revenue")==null?"0.0":r.get(0).getDouble("revenue").toString());
			data.put("gT"+d, gT.get(0).getLong("count").toString());
			data.put("nP"+d, nP.get(0).getLong("count").toString());
			data.put("oP"+d, oP.get(0).getLong("count").toString());
			data.put("pT"+d, pT.get(0).getLong("count").toString());
			data.put("rSum"+d, rSum.get(0).getDouble("revenue")==null?"0.0":rSum.get(0).getDouble("revenue").toString());
			double onlineTime = lGP.get(0).getBigDecimal("online_time")==null?0.0:lGP.get(0).getBigDecimal("online_time").doubleValue();
			long loPlayers = lGP.get(0).getLong("count");
			double avgPerGamePeroid = 0.0;
			if(loPlayers!=0){
				avgPerGamePeroid = onlineTime/(double)loPlayers;
				BigDecimal bg = new BigDecimal(avgPerGamePeroid);
				avgPerGamePeroid = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			}
			data.put("aGP"+d, String.valueOf(avgPerGamePeroid));
		}
		logger.info("data:" + data);
		return data;
	} 
	
	//查询实时设备
	public Map<String, Object> queryRealtimeDevice(String icons, String[] date){
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
	public Map<String, Object> queryRealtimeAddPlayers(String icons, String[] date){
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
	public Map<String, Object> queryRealtimeRevenue(String icons, String[] date){
		String sql = "select sum(A.count)revenue,hour(A.timestamp)hour from log_charge A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where DATE_FORMAT(A.timestamp,'%Y-%m-%d')= ? and C.os in (" + icons + ") group by hour(A.timestamp)";
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
