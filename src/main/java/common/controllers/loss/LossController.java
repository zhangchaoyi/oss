package common.controllers.loss;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
import common.service.LossAnalysisService;
import common.service.impl.LossAnalysisServiceImpl;
import common.utils.DateUtils;
import common.utils.StringUtils;

@Clear
public class LossController extends Controller {
	private static Logger logger = Logger.getLogger(LossController.class);
	private LossAnalysisService las = new LossAnalysisServiceImpl();
	/**
	 * 流失页
	 * @author chris
	 * @role data_guest
	 */
	@Before({GET.class, DataGuestInterceptor.class})
	@ActionKey("/loss")
	public void analyse() {
		render("loss.html");
	}
	/**
	 * 流失接口
	 * @author chris
	 * @getPara playerTag 玩家tag
	 * @getPara tag 流失/回流 tag
	 * @getPara icon[]  当前的icon   ---apple/android/windows
	 * @getPara startDate  所选起始时间
	 * @getPara endDate  所选结束时间
	 * @role data_guest
	 */
	@Before({POST.class, DataGuestInterceptor.class})
	@ActionKey("/api/loss")
	public void queryLoss() {
		String playerTag = getPara("playerTag", "active-players");
		String tag = getPara("tag", "day-loss");
		String icons = StringUtils.arrayToQueryString(getParaValues("icon[]"));
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		logger.info("params: {" + "playerTag:"+playerTag+",tag:"+tag+",icons:"+icons+",startDate:"+startDate+",endDate:"+endDate+"}");
		
		Map<String, Object> data = new LinkedHashMap<String, Object>();
		Map<String, List<String>> category = new LinkedHashMap<String, List<String>>();
		// 保存chart中数据
		Map<String, Object> seriesMap = new LinkedHashMap<String, Object>();
		List<String> categories = DateUtils.getDateList(startDate, endDate);
		List<String> header = new ArrayList<String>();
		String playerType = "";
		Map<String, Object> queryDayData = new HashMap<String, Object>();

		switch (playerTag) {
		case "active-players":
			playerType = "active";
			switch (tag) {
			case "day-loss":
				queryDayData = las.queryDayLoss(categories, icons, startDate, endDate, playerType);
				header.addAll(Arrays.asList("日期", "活跃用户", "7日流失数 (率)", "14日流失数 (率)", "30日流失数 (率)"));
				break;
			case "week-back":
				queryDayData = las.queryDayReturn(categories, icons, startDate, endDate, playerType);
				header.addAll(Arrays.asList("日期", "活跃用户", "7日回流人数", "14日回流人数", "30日回流人数"));
				break;
			}
			break;
		case "paid-players":
			playerType = "paid";
			switch (tag) {
			case "day-loss":
				queryDayData = las.queryDayLoss(categories, icons, startDate, endDate, playerType);
				header.addAll(Arrays.asList("日期", "付费用户", "7日流失数 (率)", "14日流失数 (率)", "30日流失数 (率)"));
				break;
			case "week-back":
				queryDayData = las.queryDayReturn(categories, icons, startDate, endDate, playerType);
				header.addAll(Arrays.asList("日期", "付费用户", "7日回流人数", "14日回流人数", "30日回流人数"));
				break;
			}
			break;
		case "no-paid-players":
			playerType = "nonpaid";
			switch (tag) {
			case "day-loss":
				queryDayData = las.queryDayLoss(categories, icons, startDate, endDate, playerType);
				header.addAll(Arrays.asList("日期", "非付费用户", "7日流失数 (率)", "14日流失数 (率)", "30日流失数 (率)"));
				break;
			case "week-back":
				queryDayData = las.queryDayReturn(categories, icons, startDate, endDate, playerType);
				header.addAll(Arrays.asList("日期", "非付费用户", "7日回流人数", "14日回流人数", "30日回流人数"));
				break;
			}
			break;
		}

		if ("day-loss".equals(tag)) {
			seriesMap.put("7日流失率", queryDayData.get("sdLR"));
			seriesMap.put("14日流失率", queryDayData.get("fdLR"));
			seriesMap.put("30日流失率", queryDayData.get("tdLR"));
		}
		if ("week-back".equals(tag)) {
			seriesMap.put("7日回流人数", queryDayData.get("sdL"));
			seriesMap.put("14日回流人数", queryDayData.get("fdL"));
			seriesMap.put("30日回流人数", queryDayData.get("tdL"));
		}

		Set<String> type = seriesMap.keySet();
		category.put("日期", categories);
		data.put("type", type.toArray());
		data.put("category", category);
		data.put("data", seriesMap);
		data.put("header", header);
		data.put("tableData", queryDayData.get("tableData"));
		logger.info("data:" + data);
		renderJson(data);
	}
}
