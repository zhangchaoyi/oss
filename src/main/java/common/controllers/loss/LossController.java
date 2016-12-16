package common.controllers.loss;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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
	 * 
	 * @author chris
	 * @role data_guest
	 */
	@Before({ GET.class, DataGuestInterceptor.class })
	@ActionKey("/loss")
	public void analyse() {
		render("loss.html");
	}

	/**
	 * 流失接口
	 * 
	 * @author chris
	 * @getPara playerTag 玩家tag
	 * @getPara tag 流失/回流 tag
	 * @getPara icon[] 当前的icon ---apple/android/windows
	 * @getPara startDate 所选起始时间
	 * @getPara endDate 所选结束时间
	 * @role data_guest
	 */
	@Before({ POST.class, DataGuestInterceptor.class })
	@ActionKey("/api/loss")
	public void queryLoss() {
		String playerTag = getPara("playerTag", "active-players");
		String tag = getPara("tag", "day-loss");
		String icons = StringUtils.arrayToQueryString(getParaValues("icon[]"));
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		logger.info("params: {" + "playerTag:" + playerTag + ",tag:" + tag + ",icons:" + icons + ",startDate:"
				+ startDate + ",endDate:" + endDate + "}");

		Map<String, Object> data = new LinkedHashMap<String, Object>();
		Map<String, List<String>> category = new LinkedHashMap<String, List<String>>();
		// 保存chart中数据
		Map<String, Object> seriesMap = new LinkedHashMap<String, Object>();
		List<String> categories = DateUtils.getDateList(startDate, endDate);
		String playerType = "";
		Map<String, Object> queryDayData = new HashMap<String, Object>();

		String db;
		try {
			db = URLDecoder.decode(getCookie("server"), "GBK");

			switch (playerTag) {
			case "active-players":
				playerType = "active";
				switch (tag) {
				case "day-loss":
					queryDayData = las.queryDayLoss(categories, icons, startDate, endDate, playerType, db);
					break;
				case "week-back":
					queryDayData = las.queryDayReturn(categories, icons, startDate, endDate, playerType, db);
					break;
				}
				break;
			case "paid-players":
				playerType = "paid";
				switch (tag) {
				case "day-loss":
					queryDayData = las.queryDayLoss(categories, icons, startDate, endDate, playerType, db);
					break;
				case "week-back":
					queryDayData = las.queryDayReturn(categories, icons, startDate, endDate, playerType, db);
					break;
				}
				break;
			case "no-paid-players":
				playerType = "nonpaid";
				switch (tag) {
				case "day-loss":
					queryDayData = las.queryDayLoss(categories, icons, startDate, endDate, playerType, db);
					break;
				case "week-back":
					queryDayData = las.queryDayReturn(categories, icons, startDate, endDate, playerType, db);
					break;
				}
				break;
			}

			if ("day-loss".equals(tag)) {
				seriesMap.put("+1日", queryDayData.get("firstDayLR"));
				seriesMap.put("+2日", queryDayData.get("secondDayLR"));
				seriesMap.put("+3日", queryDayData.get("thirdDayLR"));
				seriesMap.put("+4日", queryDayData.get("forthDayLR"));
				seriesMap.put("+5日", queryDayData.get("fifthDayLR"));
				seriesMap.put("+6日", queryDayData.get("sixthDayLR"));
				seriesMap.put("+7日", queryDayData.get("sevenDayLR"));
				seriesMap.put("+8日", queryDayData.get("eighthDayLR"));
				seriesMap.put("+9日", queryDayData.get("ninthDayLR"));
				seriesMap.put("+10日", queryDayData.get("tenthDayLR"));
				seriesMap.put("+11日", queryDayData.get("eleventhDayLR"));
				seriesMap.put("+12日", queryDayData.get("twelfthDayLR"));
				seriesMap.put("+13日", queryDayData.get("thirteenthDayLR"));
				seriesMap.put("+14日", queryDayData.get("fourteenthDayLR"));
				seriesMap.put("+30日", queryDayData.get("thirtyDayLR"));
			}
			if ("week-back".equals(tag)) {
				seriesMap.put("+1日", queryDayData.get("firstDL"));
				seriesMap.put("+2日", queryDayData.get("secondDL"));
				seriesMap.put("+3日", queryDayData.get("thirdDL"));
				seriesMap.put("+4日", queryDayData.get("forthDL"));
				seriesMap.put("+5日", queryDayData.get("fifthDL"));
				seriesMap.put("+6日", queryDayData.get("sixthDL"));
				seriesMap.put("+7日", queryDayData.get("sevenDL"));
				seriesMap.put("+8日", queryDayData.get("eighthDL"));
				seriesMap.put("+9日", queryDayData.get("ninthDL"));
				seriesMap.put("+10日", queryDayData.get("tenthDL"));
				seriesMap.put("+11日", queryDayData.get("eleventhDL"));
				seriesMap.put("+12日", queryDayData.get("twelfthDL"));
				seriesMap.put("+13日", queryDayData.get("thirteenthDL"));
				seriesMap.put("+14日", queryDayData.get("fourteenthDL"));
				seriesMap.put("+30日", queryDayData.get("thirtyDL"));
			}

			Set<String> type = seriesMap.keySet();
			category.put("日期", categories);
			data.put("type", type.toArray());
			data.put("category", category);
			data.put("data", seriesMap);
			data.put("tableData", queryDayData.get("tableData"));
			logger.info("data:" + data);
			renderJson(data);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			logger.info("cookie decoder failed", e);
		}
	}
}
