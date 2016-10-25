package common.controllers.online;

import java.util.Collection;
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

import common.interceptor.AuthInterceptor;
import common.service.OnlineHabitsService;
import common.service.impl.OnlineHabitsServiceImpl;
import common.utils.DateUtils;
import common.utils.StringUtils;

@Clear(AuthInterceptor.class)
public class HabitsController extends Controller{
	private static Logger logger = Logger.getLogger(HabitsController.class);
	private OnlineHabitsService ohs = new OnlineHabitsServiceImpl(); 
	
	@Before(GET.class)
	@ActionKey("/online/habits")
	public void analyse() {
		render("habits.html");
	}
	
	@SuppressWarnings("unchecked")
	@Before(POST.class)
	@ActionKey("/api/online/habits/avgGP")
	public void queryAvgGP() {
		String playerTag = getPara("playerTag", "add-players");
		String tag = getPara("tag", "day");
		String icons = StringUtils.arrayToQueryString(getParaValues("icon[]"));
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		
		Map<String, Object> data = new LinkedHashMap<String,Object>();	
		Map<String, List<String>> category = new LinkedHashMap<String, List<String>>();	
		//保存chart中数据
		Map<String, Object> seriesMap = new LinkedHashMap<String, Object>();
		List<String> categories = DateUtils.getDateList(startDate, endDate);
		
		switch(playerTag) {
		case "add-players":
			switch(tag) {
			case "day":
				Map<String, Object> addpAvgD = ohs.queryAddpDayAvgGP(categories, icons, startDate, endDate);
				seriesMap.put("每玩家游戏次数", addpAvgD.get("times"));
				seriesMap.put("每玩家游戏时长(分钟)", addpAvgD.get("time"));
				break;
			case "week":
				Map<String, Object> addpAvgW = ohs.queryAddpWeekAvgGP(icons, startDate, endDate);
				seriesMap.put("每玩家游戏次数", addpAvgW.get("times"));
				seriesMap.put("每玩家游戏时长(分钟)", addpAvgW.get("time"));
				categories.clear();
				categories.addAll((Collection<? extends String>) addpAvgW.get("categories"));
				break;
			case "month":
				break;
			}
			break;
		case "active-players":
			switch(tag) {
			case "day":
				Map<String, Object> activepAvgD = ohs.queryActivepDayAvgGP(categories, icons, startDate, endDate);
				seriesMap.put("每玩家游戏次数", activepAvgD.get("times"));
				seriesMap.put("每玩家游戏时长(分钟)", activepAvgD.get("time"));
				break;
			case "week":
				Map<String, Object> activepAvgW = ohs.queryActivepWeekAvgGP(icons, startDate, endDate);
				seriesMap.put("每玩家游戏次数", activepAvgW.get("times"));
				seriesMap.put("每玩家游戏时长(分钟)", activepAvgW.get("time"));
				categories.clear();
				categories.addAll((Collection<? extends String>) activepAvgW.get("categories"));
				break;
			case "month":
				break;
			}
			break;
		case "paid-players":
			switch(tag) {
			case "day":
				Map<String, Object> ppAvgD = ohs.queryPpDayAvgGP(categories, icons, startDate, endDate);
				seriesMap.put("每玩家游戏次数", ppAvgD.get("times"));
				seriesMap.put("每玩家游戏时长(分钟)", ppAvgD.get("time"));
				break;
			case "week":
				Map<String, Object> ppAvgW = ohs.queryActivepWeekAvgGP(icons, startDate, endDate);
				seriesMap.put("每玩家游戏次数", ppAvgW.get("times"));
				seriesMap.put("每玩家游戏时长(分钟)", ppAvgW.get("time"));
				categories.clear();
				categories.addAll((Collection<? extends String>) ppAvgW.get("categories"));
				break;
			case "month":
				break;
			}
			break;
		}
		
		Set<String> type = seriesMap.keySet();
		category.put("日期", categories);	
		data.put("type", type.toArray());
		data.put("category", category);
		data.put("data", seriesMap);
		logger.debug("<HabitsController> queryAvgGP:" + data);
		renderJson(data);
	}
}
