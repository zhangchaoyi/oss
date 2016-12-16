package common.controllers.players;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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
import common.service.ActivePlayersService;
import common.service.AddPlayersService;
import common.service.impl.ActivePlayersServiceImpl;
import common.service.impl.AddPlayersServiceImpl;
import common.utils.DateUtils;
import common.utils.StringUtils;

@Clear
public class ActiveController extends Controller {
	private static Logger logger = Logger.getLogger(ActiveController.class);
	private AddPlayersService addPlayersService = new AddPlayersServiceImpl();
	private ActivePlayersService activePlayersService = new ActivePlayersServiceImpl();

	/**
	 * 活跃玩家页
	 * 
	 * @author chris role data_guest
	 */
	@Before({ GET.class, DataGuestInterceptor.class })
	@ActionKey("/players/active")
	public void activePlayer() {
		render("active.html");
	}

	/**
	 * 获取活跃玩家的 DAU/WAU/MAU
	 * 
	 * @author chris
	 * @getPara playerTag 页面所选中的 tag ----dau/duawaumau/daumau
	 * @getPara icon[] 当前的icon ---apple/android/windows
	 * @getPara startDate 所选起始时间
	 * @getPara endDate 所选结束时间
	 * @role data_guest
	 */
	@Before({ POST.class, DataGuestInterceptor.class })
	@ActionKey("/api/players/active")
	public void queryActivePlayer() {
		String playerTag = getPara("playerTag", "dau");
		String icons = StringUtils.arrayToQueryString(getParaValues("icon[]"));
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		logger.info("params:{" + "playerTag:" + playerTag + ",icons:" + icons + ",startDate:" + startDate + ",endDate:"
				+ endDate + "}");

		startDate = startDate + " 00:00:00";
		endDate = endDate + " 23:59:59";

		Map<String, Object> data = new LinkedHashMap<String, Object>();
		Map<String, List<String>> category = new LinkedHashMap<String, List<String>>();
		// 保存chart中数据
		Map<String, Object> seriesMap = new LinkedHashMap<String, Object>();
		List<String> categories = DateUtils.getDateList(startDate, endDate);

		try {
			String db = URLDecoder.decode(getCookie("server"), "GBK");
			switch (playerTag) {
			case "dau": {
				List<Long> addPlayers = addPlayersService.queryAddPlayersData(categories, icons, startDate, endDate,
						db);
				List<Long> dau = activePlayersService.queryDau(categories, icons, startDate, endDate, db);
				Map<String, List<Long>> dataMap = activePlayersService.queryPaidInActiveUser(categories, icons,
						startDate, endDate, db);
				List<Long> paid = dataMap.get("paid");
				List<Long> notpaid = dataMap.get("notpaid");

				seriesMap.put("新增玩家", addPlayers);
				seriesMap.put("DAU", dau);
				seriesMap.put("付费玩家", paid);
				seriesMap.put("非付费玩家", notpaid);

				break;
			}
			case "dauwaumau": {
				seriesMap = activePlayersService.queryActivePlayersInfo(categories, icons, startDate, endDate, db);
				break;
			}
			case "daumau": {
				List<Double> dauMauRate = activePlayersService.queryActivePlayersDauMauRate(categories, icons,
						startDate, endDate, db);
				seriesMap.put("DAU/MAU", dauMauRate);
				break;
			}
			}

			Set<String> type = seriesMap.keySet();
			category.put("日期", categories);
			data.put("type", type.toArray());
			data.put("category", category);
			data.put("data", seriesMap);
			logger.info("data:" + data);
			renderJson(data);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			logger.info("cookie decoder failed", e);
		}
	}

	/**
	 * 活跃玩家详细页 页面所选中的 tag ---played-days/rank/area/country/account
	 * 
	 * @author chris
	 * @getParam detailTagInfo
	 * @getPara icon[] 当前的icon ---apple/android/windows
	 * @getPara startDate 所选起始时间
	 * @getPara endDate 所选结束时间
	 * @role data_guest
	 */
	@SuppressWarnings("unchecked")
	@Before({ POST.class, DataGuestInterceptor.class })
	@ActionKey("/api/players/active/details")
	public void queryActiveDetail() {
		String detailTagInfo = getPara("detailTagInfo", "played-days");
		String icons = StringUtils.arrayToQueryString(getParaValues("icon[]"));
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		startDate = startDate + " 00:00:00";
		endDate = endDate + " 23:59:59";
		logger.info("params:{" + "detailTagfInfo:" + ",icons:" + icons + ",startDate:" + startDate + ",endDate:"
				+ endDate + "}");

		Map<String, Object> data = new LinkedHashMap<String, Object>();
		Map<String, List<String>> category = new LinkedHashMap<String, List<String>>();
		// 保存chart中数据
		Map<String, Object> seriesMap = new LinkedHashMap<String, Object>();

		String db;
		try {
			db = URLDecoder.decode(getCookie("server"), "GBK");
			switch (detailTagInfo) {
			case "played-days": {
				List<String> playedDays = Arrays.asList("1 天", "2~3 天", "4~7 天", "8~14 天", "15~30 天", "31~90 天",
						"91~180 天", "181~365 天", "365+ 天");
				List<Long> peopleCount = activePlayersService.queryPlayDays(playedDays, icons, startDate, endDate, db);
				seriesMap.put("活跃玩家", peopleCount);
				category.put("已玩天数", playedDays);
				break;
			}
			case "rank": {
				Map<Integer, Long> map = activePlayersService.queryRank(icons, startDate, endDate, db);
				List<Integer> rankPeriod = new ArrayList<Integer>(map.keySet());
				List<Long> peopleCount = new ArrayList<Long>(map.values());
				List<String> rankPeriodString = new ArrayList<String>();
				for (Integer i : rankPeriod) {
					rankPeriodString.add(i.toString());
				}
				seriesMap.put("活跃玩家", peopleCount);
				category.put("等级", rankPeriodString);
				break;
			}
			case "area": {
				Map<String, Object> map = activePlayersService.queryArea(icons, startDate, endDate, db);
				seriesMap.put("活跃玩家", map.get("activePlayer"));
				category.put("地区", (List<String>) map.get("area"));
				break;
			}
			case "country": {
				Map<String, Object> map = activePlayersService.queryCountry(icons, startDate, endDate, db);
				seriesMap.put("活跃玩家", map.get("activePlayer"));
				category.put("国家", (List<String>) map.get("country"));
				break;
			}
			case "account": {
				Map<String, Object> map = activePlayersService.queryAccountType(icons, startDate, endDate, db);
				seriesMap.put("活跃玩家", map.get("activePlayer"));
				category.put("账户类型", (List<String>) map.get("accountType"));
				break;
			}
			}
			Set<String> type = seriesMap.keySet();
			data.put("type", type.toArray());
			data.put("category", category);
			data.put("data", seriesMap);
			logger.info("data:" + data);
			renderJson(data);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			logger.info("cookie decoder failed", e);
		}
	}
}
