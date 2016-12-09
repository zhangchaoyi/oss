package common.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import common.model.LogCharge;
import common.mysql.DbSelector;
import common.service.PaymentPlayersService;

/**
 * 查询付费玩家 包括帐号 付费金额 时间
 * 
 * @author chris
 *
 */
public class PaymentPlayersServiceImpl implements PaymentPlayersService {
	private static Logger logger = Logger.getLogger(PaymentPlayersServiceImpl.class);
	private String db = DbSelector.getDbName();
	/**
	 * 付费玩家列表 包括帐号 付费金额 时间
	 * 
	 * @param startDate
	 * @param endDate
	 * @param icons
	 * @return
	 */
	public List<List<String>> queryPLayersList(String startDate, String endDate, String icons) {
		String sql = "select A.account,A.count,A.currency,A.timestamp from log_charge A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where A.is_product = 1 and DATE_FORMAT(A.timestamp,'%Y-%m-%d') between ? and ? and C.os in ("
				+ icons + ")";
		List<LogCharge> players = LogCharge.dao.use(db).find(sql, startDate, endDate);
		List<List<String>> data = new ArrayList<List<String>>();
		for (LogCharge lc : players) {
			String account = lc.getStr("account");
			double count = lc.getDouble("count");
			String currency = lc.getStr("currency");
			Date timestamp = lc.getDate("timestamp");
			List<String> subList = new ArrayList<String>();
			subList.add(account);
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
	 * 根据玩家account 查询所有付费情况
	 * 
	 * @param account
	 * @return
	 */
	public List<List<String>> queryPlayerByAccount(String account) {
		String sql = "select account,count,currency,timestamp from log_charge where account = ? and is_product = 1";
		List<LogCharge> player = LogCharge.dao.use(db).find(sql, account);
		List<List<String>> data = new ArrayList<List<String>>();
		for (LogCharge lc : player) {
			double count = lc.getDouble("count");
			String currency = lc.getStr("currency");
			Date timestamp = lc.getDate("timestamp");
			List<String> subList = new ArrayList<String>();
			subList.add(account);
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

}
