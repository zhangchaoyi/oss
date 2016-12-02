package common.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import common.model.OnlineCount;
import common.service.OnlineCountService;

public class OnlineCountServiceImpl implements OnlineCountService {
	
	/**
	 * 根据时间区间查询所有 ccu
	 * @author chris
	 * @param startDate 起始时间
	 * @param endDate 结束时间
	 */
	public Map<String, Object> queryCCU(String startDate, String endDate) {
		String ccuSql = "select online_count,online_datetime from online_count where online_date between ? and ? order by online_datetime";
		String maxPcuSql = "select max(online_count)max_pcu,max(case when online_date between ? and ? then online_count end)max_period_pcu from online_count";
		
		List<OnlineCount> onlineCount = OnlineCount.dao.find(ccuSql, startDate, endDate);
		OnlineCount pcu = OnlineCount.dao.findFirst(maxPcuSql, startDate, endDate);
		List<String> onlineDatetime = new ArrayList<String>();
		List<Long> ccu = new ArrayList<Long>();
		Map<String, Object> data = new HashMap<String, Object>();
		
		for(OnlineCount oc : onlineCount){
			long count = oc.getInt("online_count");
			Date datetime = oc.getDate("online_datetime");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm"); 
			ccu.add(count);
			onlineDatetime.add(sdf.format(datetime));
		}
		
		long periodPcu = 0;
		long historyPcu = 0;
		periodPcu = pcu.getLong("max_period_pcu")==null?0:pcu.getLong("max_period_pcu");
		historyPcu = pcu.getInt("max_pcu")==null?0:pcu.getInt("max_pcu");
		
		data.put("ccu", ccu);
		data.put("onlineDatetime", onlineDatetime);
		data.put("periodPcu", periodPcu);
		data.put("historyPcu", historyPcu);
		
		return data;
	}

	/**
	 * 根据时间区间查询每天的pcu
	 * @author chris
	 * @param startDate 起始时间
	 * @param endDate 结束时间
	 * @return
	 */
	public Map<String, Object> queryPCU(List<String> categories, String startDate, String endDate) {
		String sql = "select max(online_count)pcu,online_date from online_count where online_date between ? and ? group by online_date";
		List<OnlineCount> onlineCount = OnlineCount.dao.find(sql, startDate, endDate);
		Map<String, Integer> sort = new LinkedHashMap<String, Integer>();
		//init
		for(String date : categories){
			sort.put(date, 0);
		}
		//load data
		for(OnlineCount oc : onlineCount){
			String date = oc.getDate("online_date").toString();
			int pcu = oc.getInt("pcu")==null?0:oc.getInt("pcu");
			sort.put(date, pcu);
		}
		List<Integer> pcus = new ArrayList<Integer>(sort.values());
		
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("pcus", pcus);
		return data;
	}
}
