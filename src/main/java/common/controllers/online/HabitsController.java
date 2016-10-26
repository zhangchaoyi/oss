package common.controllers.online;

import java.util.ArrayList;
import java.util.Arrays;
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
		List<String> header = new ArrayList<String>();
		header.addAll(Arrays.asList("日期", "每玩家游戏次数", "每玩家游戏时长(分钟)"));
		
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
				categories = DateUtils.getMonthList(startDate, endDate);
				Map<String, Object> addAvgM = ohs.queryAddpMonthAvgGP(categories, icons, startDate, endDate);
				seriesMap.put("每玩家游戏次数", addAvgM.get("times"));
				seriesMap.put("每玩家游戏时长(分钟)", addAvgM.get("time"));
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
				categories = DateUtils.getMonthList(startDate, endDate);
				Map<String, Object> activeAvgM = ohs.queryActivepMonthAvgGP(categories, icons, startDate, endDate);
				seriesMap.put("每玩家游戏次数", activeAvgM.get("times"));
				seriesMap.put("每玩家游戏时长(分钟)", activeAvgM.get("time"));
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
				categories = DateUtils.getMonthList(startDate, endDate);
				Map<String, Object> ppAvgM = ohs.queryPpMonthAvgGP(categories, icons, startDate, endDate);
				seriesMap.put("每玩家游戏次数", ppAvgM.get("times"));
				seriesMap.put("每玩家游戏时长(分钟)", ppAvgM.get("time"));
				break;
			}
			break;
		}
		
		Set<String> type = seriesMap.keySet();
		category.put("日期", categories);	
		data.put("type", type.toArray());
		data.put("category", category);
		data.put("data", seriesMap);
		data.put("header", header);
		logger.debug("<HabitsController> queryAvgGP:" + data);
		renderJson(data);
	}
	
	@Before(POST.class)
	@ActionKey("/api/online/habits/detail")
	public void queryPeriodDetail() {
		String playerTag = getPara("playerTag", "add-players");
		String tag = getPara("tag", "day-times");
		String icons = StringUtils.arrayToQueryString(getParaValues("icon[]"));
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		
		Map<String, Object> data = new LinkedHashMap<String,Object>();	
		Map<String, List<String>> category = new LinkedHashMap<String, List<String>>();	
		//保存chart中数据
		Map<String, Object> seriesMap = new LinkedHashMap<String, Object>();
		List<String> categories = new ArrayList<String>();
		List<String> header = new ArrayList<String>();
		List<Integer> players = new ArrayList<Integer>();
		
		switch(playerTag){
		case "add-players":
			switch(tag){
			case "day-times":
				categories.addAll(Arrays.asList("1","2~3","4~5","6~10","11~20","21~50","50+"));
				category.put("日游戏次数", categories);	
				header.addAll(Arrays.asList("日游戏次数", "玩家数量", "百分比"));
				players = ohs.queryAddDayGameTimes(categories, icons, startDate, endDate);
				break;
			case "day-time":
				break;
			case "single-time":
				break;
			case "period":
				break;
			}
			seriesMap.put("新增玩家", players);
			break;
		case "active-players":
			switch(tag){
			case "day-times":
				categories.addAll(Arrays.asList("1","2~3","4~5","6~10","11~20","21~50","50+"));
				category.put("日游戏次数", categories);	
				header.addAll(Arrays.asList("日游戏次数", "玩家数量", "百分比"));
				players = ohs.queryActiveDayGameTimes(categories, icons, startDate, endDate);
				break;
			case "week-times":
				categories.addAll(Arrays.asList("1","2~3","4~5","6~10","11~20","21~50","50+"));
				category.put("周游戏次数", categories);	
				header.addAll(Arrays.asList("周游戏次数", "玩家数量", "百分比"));
				players = ohs.queryActiveWeekGameTimes(categories, icons, startDate, endDate);
				break;
			case "week-days":
				categories.addAll(Arrays.asList("1","2","3","4","5","6","7"));
				category.put("周游戏天数", categories);
				header.addAll(Arrays.asList("周游戏天数","玩家数量","百分比"));
				players = ohs.queryActiveWeekGameDays(categories, icons, startDate, endDate);
				break;
			case "month-days":
				break;
			case "day-time":
				break;
			case "week-time":
				break;
			case "single-time":
				break;
			case "period":
				break;
			}
			seriesMap.put("活跃玩家", players);
			break;
		}
		
		Set<String> type = seriesMap.keySet();
		
		data.put("type", type.toArray());
		data.put("category", category);
		data.put("data", seriesMap);
		data.put("header", header);
		logger.debug("<HabitsController> queryAvgGP:" + data);
		renderJson(data);
	}
}
