package common.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import common.model.LogCharge;
import common.service.PaymentPlayersService;

/**
 * 查询付费玩家 包括帐号 付费金额 时间
 * 
 * @author chris
 *
 */
public class PaymentPlayersServiceImpl implements PaymentPlayersService {
	private static Logger logger = Logger.getLogger(PaymentPlayersServiceImpl.class);
	/**
	 * 付费玩家列表 包括帐号 付费金额 时间
	 * 
	 * @param startDate
	 * @param endDate
	 * @param icons
	 * @return
	 */
	public List<List<String>> queryPLayersList(String startDate, String endDate, String icons, String db) {
		String sql = "select A.account,A.count,A.currency,A.timestamp,A.team_name from log_charge A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where A.is_product = 1 and DATE_FORMAT(A.timestamp,'%Y-%m-%d') between ? and ? and C.os in ("
				+ icons + ")";
		List<LogCharge> players = LogCharge.dao.use(db).find(sql, startDate, endDate);
		List<List<String>> data = new ArrayList<List<String>>();
		for (LogCharge lc : players) {
			String account = lc.getStr("account");
			String roleName = lc.getStr("team_name")==null?"-":lc.getStr("team_name");
			double count = lc.getDouble("count");
			String currency = lc.getStr("currency");
			Date timestamp = lc.getDate("timestamp");
			List<String> subList = new ArrayList<String>();
			subList.add(account);
			subList.add(roleName);
			if (currency.trim().equals("RMB")) {
				subList.add("￥" + String.valueOf(count));
			} else {
				subList.add("$" + String.valueOf(count));
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			subList.add(sdf.format(timestamp));
			data.add(subList);
		}
		logger.info("playersList:" + data);
		return data;
	}

	/**
	 * 根据玩家account/roleName 查询所有付费情况
	 * 
	 * @param account
	 * @return
	 */
	public List<List<String>> queryPlayerByAccount(String account, String db) {
		String sql = "";
		if(account.length()==8 && isNumeric(account)){
			sql = "select account,count,currency,timestamp,team_name from log_charge where account = ? and is_product = 1";
		}else{
			sql = "select account,count,currency,timestamp,team_name from log_charge where team_name = ? and is_product = 1;";
		}
		List<LogCharge> player = LogCharge.dao.use(db).find(sql, account);
		List<List<String>> data = new ArrayList<List<String>>();
		for (LogCharge lc : player) {
			String queryAccount = lc.getStr("account");
			double count = lc.getDouble("count");
			String currency = lc.getStr("currency");
			Date timestamp = lc.getDate("timestamp");
			String roleName = lc.getStr("team_name")==null?"-":lc.getStr("team_name");
			List<String> subList = new ArrayList<String>();
			subList.add(queryAccount);
			subList.add(roleName);
			if (currency.trim().equals("RMB")) {
				subList.add("￥" + String.valueOf(count));
			} else {
				subList.add("$" + String.valueOf(count));
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			subList.add(sdf.format(timestamp));
			data.add(subList);
		}
		return data;
	}
	
	public boolean isNumeric(String str){ 
		Pattern pattern = Pattern.compile("[0-9]*"); 
		Matcher isNum = pattern.matcher(str);
		if( !isNum.matches() ){
		   return false; 
		} 
		return true; 
	}
}
