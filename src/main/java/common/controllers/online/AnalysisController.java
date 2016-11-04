package common.controllers.online;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.ext.interceptor.GET;
import com.jfinal.ext.interceptor.POST;
import common.interceptor.DataGuestInterceptor;
import common.service.OnlineAnalysisService;
import common.service.impl.OnlineAnalysisServiceImpl;
import common.utils.DateUtils;
import common.utils.StringUtils;

@Clear
public class AnalysisController extends Controller{
	private static Logger logger = Logger.getLogger(AnalysisController.class);
	private OnlineAnalysisService onlineService = new OnlineAnalysisServiceImpl();
	
	@Before({GET.class, DataGuestInterceptor.class})
	@ActionKey("/online/analysis")
	public void analyse() {
		render("analysis.html");
	}
	
	@Before(POST.class)
	@ActionKey("/api/online/analysis/startTimes")
	public void queryStartTimes() {
		String tag = getPara("tag", "distributed-period");
		String icons = StringUtils.arrayToQueryString(getParaValues("icon[]"));
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");

		Map<String, Object> data = new LinkedHashMap<String,Object>();	
		Map<String, List<String>> category = new LinkedHashMap<String, List<String>>();	
		//保存chart中数据
		Map<String, Object> seriesMap = new LinkedHashMap<String, Object>();
		List<String> categories = DateUtils.getDateList(startDate, endDate);
		
		switch(tag){
		case "distributed-period":
			int days = categories.size();
			categories = Arrays.asList("00:00~01:00", "01:00~02:00", "02:00~03:00", "03:00~04:00",
					"04:00~05:00", "05:00~06:00", "06:00~07:00", "07:00~08:00", "08:00~09:00", "09:00~10:00", "10:00~11:00",
					"11:00~12:00", "12:00~13:00", "13:00~14:00", "14:00~15:00", "15:00~16:00", "16:00~17:00", "17:00~18:00",
					"18:00~19:00", "19:00~20:00", "20:00~21:00", "21:00~22:00", "22:00~23:00", "23:00~24:00");
			category.put("时间段", categories);
			List<Long> dp = onlineService.queryPeriodDistribution(days,icons, startDate, endDate);
			seriesMap.put("启动次数", dp);
			break;
		case "start-times":
			category.put("日期", categories);
			List<Long> st = onlineService.queryStartTimes(categories, icons, startDate, endDate);
			seriesMap.put("启动次数", st);
			break;
		}
		
		Set<String> type = seriesMap.keySet();
			
		data.put("type", type.toArray());
		data.put("category", category);
		data.put("data", seriesMap);
		
		logger.debug("<AnalysisController> queryStartTimes:" + data);
		renderJson(data);
	}
	
	@Before(POST.class)
	@ActionKey("/api/online/analysis/neightbor")
	public void queryNeightborPeriod() {
		String icons = StringUtils.arrayToQueryString(getParaValues("icon[]"));
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		
		Map<String, Object> data = new LinkedHashMap<String,Object>();	
		Map<String, List<String>> category = new LinkedHashMap<String, List<String>>();	
		//保存chart中数据
		Map<String, Object> seriesMap = new LinkedHashMap<String, Object>();
		List<String> categories = new ArrayList<String>();
		categories.addAll(Arrays.asList("0~60 min","1~2 h","2~3 h","3~4 h","4~5 h","5~8 h","8~12 h","12~24 h","1~2 D","2~3 D"));
		category.put("启动间隔", categories);
		List<String> header = new ArrayList<String>();
		header.addAll(Arrays.asList("启动间隔", "次数", "人数"));
		
		Map<String, Object> nbp = onlineService.queryNeighborStartPeriod(categories, icons, startDate, endDate);
		seriesMap.put("次数", nbp.get("count"));
		seriesMap.put("人数", nbp.get("people"));
		
		Set<String> type = seriesMap.keySet();
		
		data.put("header", header);
		data.put("type", type.toArray());
		data.put("category", category);
		data.put("data", seriesMap);
		
		logger.debug("<AnalysisController> queryNeightborPeriod:" + data);
		renderJson(data);
	}
}
