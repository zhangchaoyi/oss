package common.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.model.CreateRole;
import common.model.DeviceInfo;
import common.model.LogCharge;
import common.model.Login;
import common.model.Logout;
import common.service.RealtimeService;

/*
 * eT=equipmentToday   aPT=activePlayersToday  pPT=paidPlayersToday  rT=revenueToday        gTT=gameTimesToday
 * nPT=newPlayersToday  oPT=oldPlayersToday    pTT=paidTimesToday    rTT=revenueTotalToday   aGPT=averageGamePeriodToday    
 * 
 *
 */
public class RealtimeServiceImpl implements RealtimeService{
	public Map<String, String> queryBeforeData(){
		String eSql = "select count(*)count from device_info where DATE_FORMAT(create_time,'%Y-%m-%d')=DATE_FORMAT(date_sub(now(),interval ? day),'%Y-%m-%d')";
		String aPSql = "select count(distinct account)count from login where DATE_FORMAT(login_time,'%Y-%m-%d')=DATE_FORMAT(date_sub(now(),interval ? day),'%Y-%m-%d')";
		String pPSql = "select count(distinct account)count from log_charge where DATE_FORMAT(timestamp,'%Y-%m-%d')=DATE_FORMAT(date_sub(now(),interval ? day),'%Y-%m-%d')";
		String rSql = "select sum(count)revenue from log_charge where DATE_FORMAT(timestamp,'%Y-%m-%d')=DATE_FORMAT(date_sub(now(),interval ? day),'%Y-%m-%d')";
		String gTSql = "select count(*)count from login where DATE_FORMAT(login_time,'%Y-%m-%d')=DATE_FORMAT(date_sub(now(),interval ? day),'%Y-%m-%d')";
		String nPSql = "select count(*)count from create_role where DATE_FORMAT(create_time,'%Y-%m-%d')=DATE_FORMAT(date_sub(now(),interval ? day),'%Y-%m-%d')";
		String oPSql = "select count(*)count from (select distinct account from login where DATE_FORMAT(login_time, '%Y-%m-%d')=DATE_FORMAT(date_sub(now(),interval ? day), '%Y-%m-%d')) A left join (select account from create_role where DATE_FORMAT(create_time,'%Y-%m-%d')=DATE_FORMAT(date_sub(now(),interval ? day), '%Y-%m-%d')) B on A.account = B.account where B.account is null";
		String pTSql = "select count(*)count from log_charge where DATE_FORMAT(timestamp,'%Y-%m-%d')=DATE_FORMAT(date_sub(now(),interval ? day),'%Y-%m-%d')";
		String rSumSql = "select sum(count)revenue from log_charge where timestamp < DATE_FORMAT(date_sub(now(),interval ? day),'%Y-%m-%d')";
		//登陆时长
		String lGPSql = "select sum(case when online_time<86400 then online_time else 86400 end)online_time,count(account)count from logout where date=DATE_FORMAT(date_sub(now(),interval ? day),'%Y-%m-%d')";

		Map<String, String> data = new HashMap<String, String>();
		int[] day = {1,7,30};
		
		for(int d : day){
			List<DeviceInfo> e = DeviceInfo.dao.find(eSql, d);
			List<Login> aP = Login.dao.find(aPSql, d);
			List<LogCharge> pP = LogCharge.dao.find(pPSql, d);
			List<LogCharge> r = LogCharge.dao.find(rSql, d);
			List<Login> gT = Login.dao.find(gTSql, d);
			List<CreateRole> nP = CreateRole.dao.find(nPSql, d);
			List<Login> oP = Login.dao.find(oPSql, d, d);
			List<LogCharge> pT = LogCharge.dao.find(pTSql, d);
			List<LogCharge> rSum = LogCharge.dao.find(rSumSql, d-1);
			List<Logout> lGP = Logout.dao.find(lGPSql,d);
			
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
			data.put("lGP"+d, String.valueOf(avgPerGamePeroid));
		}
		return data;
	} 
}
