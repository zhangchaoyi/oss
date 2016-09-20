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
import common.service.DashboardService;
/* T=Total  Y=Yesterday  N=Nowadays
 * eT=equipmentTotal  eY=equipmentYesterday  p=players  aP=activePlayers  pP=paidPlayers  pT=paidTimes  r=revenue  lT=loginTimes lP=loginPeriod
 * aGT=averageGameTimes  aGP=averageGamePeriod 
 * D=device GT=gameTimes  
 */
public class DashboardServiceImpl implements DashboardService{
	public Map<String, String>queryDashboardData(){
		//表格一
		String eTSql = "select count(*)count from device_info";
		String eYSql = "select count(*)count from device_info where DATE_FORMAT(create_time,'%Y-%m-%d')=DATE_FORMAT(date_sub(now(),interval 1 day),'%Y-%m-%d')";
		String pTSql = "select count(*)count from create_role";
		String pYSql = "select count(*)count from create_role where DATE_FORMAT(create_time,'%Y-%m-%d')=DATE_FORMAT(date_sub(now(),interval 1 day),'%Y-%m-%d')";
		String aPTSql = "select count(*)count from(select count(account)times from login group by account having count(account)>1) A";
		String aPYSql = "select count(distinct account)count from login where DATE_FORMAT(login_time,'%Y-%m-%d')=DATE_FORMAT(date_sub(now(),interval 1 day),'%Y-%m-%d')";
		String pPTSql = "select count(distinct account)count from log_charge";
		String pPYSql = "select count(distinct account)count from log_charge where DATE_FORMAT(timestamp,'%Y-%m-%d')=DATE_FORMAT(date_sub(now(),interval 1 day),'%Y-%m-%d')";
		String pTTSql = "select count(*)count from log_charge";
		String pTYSql = "select count(*)count from log_charge where DATE_FORMAT(timestamp,'%Y-%m-%d')=DATE_FORMAT(date_sub(now(),interval 1 day),'%Y-%m-%d')";
		String rTSql = "select sum(count)revenue from log_charge";
		String rYSql = "select sum(count)revenue from log_charge where DATE_FORMAT(timestamp,'%Y-%m-%d')=DATE_FORMAT(date_sub(now(),interval 1 day),'%Y-%m-%d')";
		String lTTSql = "select count(*)count from login";
		String lTYSql = "select count(*)count from login where DATE_FORMAT(login_time,'%Y-%m-%d')=DATE_FORMAT(date_sub(now(),interval 1 day),'%Y-%m-%d')";
		String lPTSql = "select sum(online_time)online_time from logout";
		String lPYSql = "select sum(online_time)online_time from logout where date=DATE_FORMAT(date_sub(now(),interval 1 day),'%Y-%m-%d') and online_time < 86400";
				
		List<DeviceInfo> eT = DeviceInfo.dao.find(eTSql);
		List<DeviceInfo> eY = DeviceInfo.dao.find(eYSql);
		List<CreateRole> pT = CreateRole.dao.find(pTSql);
		List<CreateRole> pY = CreateRole.dao.find(pYSql);
		List<Login> apT = Login.dao.find(aPTSql);
		List<Login> apY = Login.dao.find(aPYSql);
		List<LogCharge> pPT = LogCharge.dao.find(pPTSql);
		List<LogCharge> pPY = LogCharge.dao.find(pPYSql);
		List<LogCharge> pTT = LogCharge.dao.find(pTTSql);
		List<LogCharge> pTY = LogCharge.dao.find(pTYSql);
		List<LogCharge> rT = LogCharge.dao.find(rTSql);
		List<LogCharge> rY = LogCharge.dao.find(rYSql);
		List<Login> lTT = Login.dao.find(lTTSql);
		List<Login> lTY = Login.dao.find(lTYSql);
		List<Logout> lPT = Logout.dao.find(lPTSql);
		List<Logout> lPY = Logout.dao.find(lPYSql);
		
		long activePlayerTotal = apT.get(0).getLong("count");
	    long activePlayerYesterday = apY.get(0).getLong("count");
		double revenueTotal = rT.get(0).getDouble("revenue")==null?0.0:rT.get(0).getDouble("revenue");
		double revenueYesterday = rY.get(0).getDouble("revenue")==null?0.0:rY.get(0).getDouble("revenue").doubleValue();
	    long loginTimesTotal = lTT.get(0).getLong("count");
	    long loginTimesYesterday = lTY.get(0).getLong("count");
	    double loginPeriodTotal = lPT.get(0).getBigDecimal("online_time")==null?0.0:lPT.get(0).getBigDecimal("online_time").doubleValue();
	    double loginGamePeriodYesterday = lPY.get(0).getBigDecimal("online_time")==null?0.0:lPY.get(0).getBigDecimal("online_time").doubleValue();
	    
	    double arpuTotal=0.0;
	    double aGTTotal=0.0;
	    double aGPTotal=0.0;
	    double arpuY=0.0;
	    double aGTY=0.0;
	    double aGPY=0.0;
	    
	    if(activePlayerTotal!=0){
	    	arpuTotal=revenueTotal/(double)activePlayerTotal;
	    	aGTTotal=(double)loginTimesTotal/(double)activePlayerTotal;
	    	aGPTotal=loginPeriodTotal/(double)activePlayerTotal;
	    	BigDecimal bgArpuTotal = new BigDecimal(arpuTotal);
	    	BigDecimal bgAGTTotal = new BigDecimal(aGTTotal);
	    	BigDecimal bgAGPTotal = new BigDecimal(aGPTotal);
	    	arpuTotal = bgArpuTotal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
	    	aGTTotal = bgAGTTotal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
	    	aGPTotal = bgAGPTotal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
	    }
	    if(activePlayerYesterday!=0){
	    	arpuY=revenueYesterday/(double)activePlayerYesterday;
	    	aGTY=(double)loginTimesYesterday/(double)activePlayerYesterday;
	    	aGPY=loginGamePeriodYesterday/(double)activePlayerYesterday;
	    	BigDecimal bgArpuY = new BigDecimal(arpuY);
	    	BigDecimal bgAGTY = new BigDecimal(aGTY);
	    	BigDecimal bgAGPY = new BigDecimal(aGPY);
	    	arpuY = bgArpuY.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
	    	aGTY = bgAGTY.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
	    	aGPY = bgAGPY.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
	    }
	    
	    
		Map<String, String> data = new HashMap<String, String>();
		data.put("eT", eT.get(0).getLong("count").toString());
		data.put("eY", eY.get(0).getLong("count").toString());
		data.put("pT", pT.get(0).getLong("count").toString());
		data.put("pY", pY.get(0).getLong("count").toString());
		data.put("apT", String.valueOf(activePlayerTotal));
		data.put("apY", String.valueOf(activePlayerYesterday));
		data.put("pPT", pPT.get(0).getLong("count").toString());
		data.put("pPY", pPY.get(0).getLong("count").toString());
		data.put("pTT", pTT.get(0).getLong("count").toString());
		data.put("pTY", pTY.get(0).getLong("count").toString());
		data.put("rT", String.valueOf(revenueTotal));
		data.put("rY", String.valueOf(revenueYesterday));
		data.put("arpuTotal", String.valueOf(arpuTotal));
		data.put("arpuY", String.valueOf(arpuY));
		data.put("aGTTotal", String.valueOf(aGTTotal));
		data.put("aGTY", String.valueOf(aGTY));
		data.put("aGPTotal", String.valueOf(aGPTotal));
		data.put("aGPY", String.valueOf(aGPY));
		
		//处理表格二

		String osDTSql = "select count(*)count from device_info where os = ?";
		String osDNSql = "select count(*)count from device_info where DATE_FORMAT(create_time,'%Y-%m-%d')=DATE_FORMAT(now(),'%Y-%m-%d') and os = ?";
		String osPTSql = "select count(*)count from create_role A join device_info B on A.openudid=B.openudid where B.os= ?";
		String osPNSql = "select count(*)count from create_role A join device_info B on A.openudid=B.openudid where DATE_FORMAT(A.create_time,'%Y-%m-%d')=DATE_FORMAT(now(),'%Y-%m-%d') and B.os= ?";
		String osGTSql = "select count(*)count from login A join device_info B on A.openudid = B.openudid where B.os = ?";
		String osGNSql = "select count(*)count from login A join device_info B on A.openudid = B.openudid where B.os = ? and A.date=DATE_FORMAT(now(),'%Y-%m-%d')";
		String osRTSql = "select sum(A.count)revenue from log_charge A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where C.os = ?";
		String osRNSql = "select sum(A.count)revenue from log_charge A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where C.os = ? and DATE_FORMAT(A.timestamp,'%Y-%m-%d')=DATE_FORMAT(now(),'%Y-%m-%d')";
		
		String[] os = {"iOS", "android", "windows"};
		long deviceTSum = 0;
		long deviceNSum = 0;
		long playersTSum = 0;
		long playersNSum = 0;	
		long gameTimesTSum = 0;
		long gameTimesNSum = 0;
		double revenueTSum = 0.0;
		double revenueNSum = 0.0;
		for(String s:os){
			List<DeviceInfo> osDT = DeviceInfo.dao.find(osDTSql, s);
			List<DeviceInfo> osDN = DeviceInfo.dao.find(osDNSql, s);
			List<CreateRole> osPT = CreateRole.dao.find(osPTSql, s); 
			List<CreateRole> osPN = CreateRole.dao.find(osPNSql, s);
			List<Login> osGT = Login.dao.find(osGTSql, s);
			List<Login> osGN = Login.dao.find(osGNSql, s);
			List<LogCharge> osRT = LogCharge.dao.find(osRTSql, s);
			List<LogCharge> osRN = LogCharge.dao.find(osRNSql, s);		
			
			deviceTSum += osDT.get(0).getLong("count");
			deviceNSum += osDN.get(0).getLong("count");
			playersTSum += osPT.get(0).getLong("count");
			playersNSum += osPN.get(0).getLong("count");
			gameTimesTSum += osGT.get(0).getLong("count");
			gameTimesNSum += osGN.get(0).getLong("count");
			revenueTSum += osRT.get(0).getDouble("revenue")==null?0.0:osRT.get(0).getDouble("revenue");
			revenueNSum += osRN.get(0).getDouble("revenue")==null?0.0:osRN.get(0).getDouble("revenue");
			
			data.put(s+"DT", osDT.get(0).getLong("count").toString());
			data.put(s+"DN", osDN.get(0).getLong("count").toString());
			data.put(s+"PT", osPT.get(0).getLong("count").toString());
			data.put(s+"PN", osPN.get(0).getLong("count").toString());
			data.put(s+"GT", osGT.get(0).getLong("count").toString());
			data.put(s+"GN", osGN.get(0).getLong("count").toString());
			data.put(s+"RT", osRT.get(0).getDouble("revenue")==null?"0.0":osRT.get(0).getDouble("revenue").toString());
			data.put(s+"RN", osRN.get(0).getDouble("revenue")==null?"0.0":osRN.get(0).getDouble("revenue").toString());
		}
		
		data.put("deviceTSum", String.valueOf(deviceTSum));
		data.put("deviceNSum", String.valueOf(deviceNSum));
		data.put("playersTSum", String.valueOf(playersTSum));
		data.put("playersNSum", String.valueOf(playersNSum));
		data.put("gameTimesTSum", String.valueOf(gameTimesTSum));
		data.put("gameTimesNSum", String.valueOf(gameTimesNSum));
		data.put("revenueTSum", String.valueOf(revenueTSum));
		data.put("revenueNSum", String.valueOf(revenueNSum));
		
		return data;
	}
}
