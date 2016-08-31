package common.service.impl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import common.model.ActiveUser;
import common.service.ActivePlayersService;

public class ActivePlayersServiceImpl implements ActivePlayersService {
	
	public List<Long> queryDau(List<String> categories, String startDate, String endDate){
		List<Long> data = new ArrayList<Long>();
		String sql = "select DATE_FORMAT(time,'%Y-%m-%d') date, dau from active_user where time >= ? and time <=?";
		List<ActiveUser> dau = ActiveUser.dao.find(sql, startDate, endDate);
		
		Map<String, Long> sort = new TreeMap<String, Long>();
		for (String category : categories) {
			sort.put(category, 0L);
		}
		for (ActiveUser cr : dau) {
			sort.put(cr.getStr("date"), Long.parseLong(String.valueOf(cr.getInt("dau"))));
		}
//		data.addAll(sort.values());
		for (Map.Entry<String, Long> entry : sort.entrySet()) {
			data.add(entry.getValue());
		}

		return data;
	}
	
	public Map<String, List<Long>> queryActivePlayersInfo(List<String> categories, String startDate, String endDate){
		String sql = "select DATE_FORMAT(time,'%Y-%m-%d') date, dau, wau, mau from active_user where time >= ? and time <=?";
		List<ActiveUser> activeUser = ActiveUser.dao.find(sql, startDate, endDate);
		
		Map<String,Map<String, Long>> sort = new TreeMap<String,Map<String, Long>>();
		//初始化
		for(String category : categories){
			Map<String, Long> subMap = new HashMap<String, Long>();
			subMap.put("dau", 0L);
			subMap.put("wau", 0L);
			subMap.put("mau", 0L);
			sort.put(category, subMap);
		}
		//向map中插入数据
		for(ActiveUser au : activeUser){
			String date = au.getStr("date");
			Map<String, Long> subMap = sort.get(date);
			subMap.put("dau", Long.parseLong(String.valueOf(au.getInt("dau"))));
			subMap.put("wau", Long.parseLong(String.valueOf(au.getInt("wau"))));
			subMap.put("mau", Long.parseLong(String.valueOf(au.getInt("mau"))));
			sort.put(date, subMap);
		}
		
		List<Long> dauData = new ArrayList<Long>();
		List<Long> wauData = new ArrayList<Long>();
		List<Long> mauData = new ArrayList<Long>();
		
		for(Map.Entry<String, Map<String, Long>> entry : sort.entrySet()){
			for(Map.Entry<String, Long> subEntry : entry.getValue().entrySet()){
				switch(subEntry.getKey()){
					case "dau":{
						dauData.add(subEntry.getValue());
						break;
					}
					case "wau":{
						wauData.add(subEntry.getValue());
						break;
					}
					case "mau":{
						mauData.add(subEntry.getValue());
						break;
					}
				}						
			}
		}

		Map<String, List<Long>> data = new HashMap<String, List<Long>>();
		data.put("DAU", dauData);
		data.put("WAU", wauData);
		data.put("MAU", mauData);
		
		return data;
	}
	
//	public Map<String, List<Long>> queryActivePlayersInfo(List<String> categories, String startDate, String endDate){
//		
//	}
	
	
	
}
