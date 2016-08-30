package common.service.impl;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import common.model.Login;
import common.service.ActivePlayersService;

public class ActivePlayersServiceImpl implements ActivePlayersService {
	
	public List<Long> queryDau(List<String> categories, String startDate, String endDate){
		List<Long> data = new ArrayList<Long>();
		String sql = "select DATE_FORMAT(login_time,'%Y-%m-%d') date,count(distinct account) count from login where login_time >= ? and login_time <= ? group by DATE_FORMAT(login_time,'%Y-%m-%d')";
		List<Login> dau = Login.dao.find(sql, startDate, endDate);
		
		Map<String, Long> sort = new TreeMap<String, Long>();
		for (String category : categories) {
			sort.put(category, 0L);
		}
		for (Login cr : dau) {
			sort.put(cr.getStr("date"), cr.getLong("count"));
		}
		for (Map.Entry<String, Long> entry : sort.entrySet()) {
			data.add(entry.getValue());
		}
		System.out.println(data);
		return data;
	}
}
