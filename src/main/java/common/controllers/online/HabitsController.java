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

import common.interceptor.DataGuestInterceptor;
import common.service.OnlineHabitsService;
import common.service.impl.OnlineHabitsServiceImpl;
import common.utils.DateUtils;
import common.utils.StringUtils;

@Clear
public class HabitsController extends Controller {
	private static Logger logger = Logger.getLogger(HabitsController.class);
	private OnlineHabitsService ohs = new OnlineHabitsServiceImpl();
	/**
	 * 在线习惯页
	 * @author chris
	 * @role data_guest
	 */
	@Before({GET.class, DataGuestInterceptor.class})
	@ActionKey("/online/habits")
	public void analyse() {
		render("habits.html");
	}
	/**
	 * 在线习惯平均游戏时长和次数接口
	 * @author chris
	 * @getPara playerTag 玩家类型 tag
	 * @getPara tag 每日/周/月 tag
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间
	 * @role data_guest  
	 */
	@SuppressWarnings("unchecked")
	@Before({POST.class, DataGuestInterceptor.class})
	@ActionKey("/api/online/habits/avgGP")
	public void queryAvgGP() {
		String playerTag = getPara("playerTag", "add-players");
		String tag = getPara("tag", "day");
		String icons = StringUtils.arrayToQueryString(getParaValues("icon[]"));
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");

		Map<String, Object> data = new LinkedHashMap<String, Object>();
		Map<String, List<String>> category = new LinkedHashMap<String, List<String>>();
		// 保存chart中数据
		Map<String, Object> seriesMap = new LinkedHashMap<String, Object>();
		List<String> categories = DateUtils.getDateList(startDate, endDate);
		List<String> header = new ArrayList<String>();
		header.addAll(Arrays.asList("日期", "每玩家游戏次数", "每玩家游戏时长(分钟)"));

		switch (playerTag) {
		case "add-players":
			switch (tag) {
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
			switch (tag) {
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
			switch (tag) {
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
	/**
	 * 在线习惯详细栏接口
	 * @author chris
	 * @getPara playerTag 玩家类型 tag
	 * @getPara tag 子选项栏 
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间
	 * @role data_guest 
	 */
	@Before({POST.class, DataGuestInterceptor.class})
	@ActionKey("/api/online/habits/detail")
	public void queryPeriodDetail() {
		String playerTag = getPara("playerTag", "add-players");
		String tag = getPara("tag", "day-times");
		String icons = StringUtils.arrayToQueryString(getParaValues("icon[]"));
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");

		Map<String, Object> data = new LinkedHashMap<String, Object>();
		Map<String, List<String>> category = new LinkedHashMap<String, List<String>>();
		// 保存chart中数据
		Map<String, Object> seriesMap = new LinkedHashMap<String, Object>();
		List<String> categories = new ArrayList<String>();
		List<String> header = new ArrayList<String>();
		List<Integer> players = new ArrayList<Integer>();

		switch (playerTag) {
		case "add-players":
			switch (tag) {
			case "day-times":
				categories.addAll(Arrays.asList("1", "2~3", "4~5", "6~10", "11~20", "21~50", "50+"));
				category.put("日游戏次数", categories);
				header.addAll(Arrays.asList("日游戏次数", "玩家数量", "百分比"));
				players = ohs.queryAddDayGameTimes(categories, icons, startDate, endDate);
				break;
			case "day-time":
				categories.addAll(Arrays.asList("<10 s", "10~60 s", "1~3 min", "3~10 min", "10~30 min", "30~60 min",
						"1~2 h", "2~4 h", ">4 h"));
				category.put("日游戏时长", categories);
				header.addAll(Arrays.asList("日游戏时长", "玩家数量", "百分比"));
				players = ohs.queryAddDayGameTime(categories, icons, startDate, endDate);
				break;
			case "single-time":
				categories.addAll(Arrays.asList("1~4 s", "5~10 s", "11~30 s", "31~60 s", "1~3 min", "3~10 min",
						"10~30 min", "30~60 min", ">60 min"));
				category.put("单次游戏时长", categories);
				header.addAll(Arrays.asList("单次游戏时长", "玩家数量", "百分比", "游戏次数", "百分比"));
				Map<String, List<Integer>> queryData = ohs.queryAddDaySinglePeriod(categories, icons, startDate,
						endDate);
				players = queryData.get("players");
				data.put("times", queryData.get("times"));
				break;
			case "period":
				categories.addAll(Arrays.asList("0:00~0:59", "1:00~1:59", "2:00~2:59", "3:00~3:59", "4:00~4:59",
						"5:00~5:59", "6:00~6:59", "7:00~7:59", "8:00~8:59", "9:00~9:59", "10:00~10:59", "11:00~11:59",
						"12:00~12:59", "13:00~13:59", "14:00~14:59", "15:00~15:59", "16:00~16:59", "17:00~17:59",
						"18:00~18:59", "19:00~19:59", "20:00~20:59", "21:00~21:59", "22:00~22:59", "23:00~23:59"));
				category.put("游戏时段",categories);
				header.addAll(Arrays.asList("游戏时段","玩家数量","百分比"));
				players = ohs.queryAddDayPeriod(icons, startDate, endDate);
				break;
			}
			seriesMap.put("新增玩家", players);
			break;
		case "active-players":
			switch (tag) {
			case "day-times":
				categories.addAll(Arrays.asList("1", "2~3", "4~5", "6~10", "11~20", "21~50", "50+"));
				category.put("日游戏次数", categories);
				header.addAll(Arrays.asList("日游戏次数", "玩家数量", "百分比"));
				players = ohs.queryActiveDayGameTimes(categories, icons, startDate, endDate);
				break;
			case "week-times":
				categories.addAll(Arrays.asList("1", "2~3", "4~5", "6~10", "11~20", "21~50", "50+"));
				category.put("周游戏次数", categories);
				header.addAll(Arrays.asList("周游戏次数", "玩家数量", "百分比"));
				players = ohs.queryActiveWeekGameTimes(categories, icons, startDate, endDate);
				break;
			case "week-days":
				categories.addAll(Arrays.asList("1", "2", "3", "4", "5", "6", "7"));
				category.put("周游戏天数", categories);
				header.addAll(Arrays.asList("周游戏天数", "玩家数量", "百分比"));
				players = ohs.queryActiveWeekGameDays(categories, icons, startDate, endDate);
				break;
			case "month-days":
				categories.addAll(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8~14", "15~21", "22~31"));
				category.put("月游戏天数", categories);
				header.addAll(Arrays.asList("月游戏天数", "玩家数量", "百分比"));
				players = ohs.queryActiveMonthGameDays(categories, icons, startDate, endDate);
				break;
			case "day-time":
				categories.addAll(Arrays.asList("<10 s", "10~60 s", "1~3 min", "3~10 min", "10~30 min", "30~60 min",
						"1~2 h", "2~4 h", ">4 h"));
				category.put("日游戏时长", categories);
				header.addAll(Arrays.asList("日游戏时长", "玩家数量", "百分比"));
				players = ohs.queryActiveDayGameTime(categories, icons, startDate, endDate);
				break;
			case "week-time":
				categories.addAll(Arrays.asList("0~60 s", "1~3 min", "3~10 min", "10~60 min", "1~2 h", "2~4 h", "4~6 h",
						"6~10 h", "10~15 h", "15~20 h", ">20 h"));
				category.put("周游戏时长", categories);
				header.addAll(Arrays.asList("周游戏时长", "玩家数量", "百分比"));
				players = ohs.queryActiveWeekGameTime(categories, icons, startDate, endDate);
				break;
			case "single-time":
				categories.addAll(Arrays.asList("1~4 s", "5~10 s", "11~30 s", "31~60 s", "1~3 min", "3~10 min",
						"10~30 min", "30~60 min", ">60 min"));
				category.put("单次游戏时长", categories);
				header.addAll(Arrays.asList("单次游戏时长", "玩家数量", "百分比", "游戏次数", "百分比"));
				Map<String, List<Integer>> queryData = ohs.queryActiveDaySinglePeriod(categories, icons, startDate,
						endDate);
				players = queryData.get("players");
				data.put("times", queryData.get("times"));
				break;
			case "period":
				categories.addAll(Arrays.asList("0:00~0:59", "1:00~1:59", "2:00~2:59", "3:00~3:59", "4:00~4:59",
						"5:00~5:59", "6:00~6:59", "7:00~7:59", "8:00~8:59", "9:00~9:59", "10:00~10:59", "11:00~11:59",
						"12:00~12:59", "13:00~13:59", "14:00~14:59", "15:00~15:59", "16:00~16:59", "17:00~17:59",
						"18:00~18:59", "19:00~19:59", "20:00~20:59", "21:00~21:59", "22:00~22:59", "23:00~23:59"));
				category.put("游戏时段",categories);
				header.addAll(Arrays.asList("游戏时段","玩家数量","百分比"));
				players = ohs.queryActiveDayPeriod(icons, startDate, endDate);
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
