package common.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.model.LogCharge;
import common.service.PaymentBehaviorService;

public class PaymentBehaviorServiceImpl implements PaymentBehaviorService{
	
	public Map<String,Object> queryRankMoney(String icons, String startDate, String endDate) {
		String sql = "select A.level, sum(A.count)revenue from log_charge A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where C.os in (" + icons + ") and DATE_FORMAT(timestamp,'%Y-%m-%d') between ? and ? group by A.level";
		List<LogCharge> rankMoney = LogCharge.dao.find(sql, startDate, endDate);

		Map<String, Object> data = new HashMap<String, Object>();
		List<Integer> level = new ArrayList<Integer>();
		List<Double> revenue = new ArrayList<Double>();
		
		for(LogCharge lc : rankMoney) {
			level.add(lc.getInt("level"));
			revenue.add(lc.getDouble("revenue"));
		}
		data.put("level", level);
		data.put("revenue", revenue);
		
		return data;
	}
	
	public Map<String, Object> queryRankTimes(String icons, String startDate, String endDate) {
		String sql = "select A.level, count(*)count from log_charge A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where C.os in (" + icons + ") and DATE_FORMAT(timestamp,'%Y-%m-%d') between ? and ? group by A.level";
		List<LogCharge> rankTimes = LogCharge.dao.find(sql, startDate, endDate);
		
		Map<String, Object> data = new HashMap<String, Object>();
		List<Integer> level = new ArrayList<Integer>();
		List<Long> count = new ArrayList<Long>();
		
		for(LogCharge lc : rankTimes) {
			level.add(lc.getInt("level"));
			count.add(lc.getLong("count"));
		}
		data.put("level", level);
		data.put("count", count);
		
		return data;
	}
}
