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
import common.model.CreateRole;
import common.service.AddPlayersService;
import common.service.impl.AddPlayersServiceImpl;
import common.util.DateUtils;
import common.util.StringUtils;

@Clear
public class AddController extends Controller {
	private static Logger logger = Logger.getLogger(AddController.class);
	private AddPlayersService addPlayersService = new AddPlayersServiceImpl();

	/**
	 * 新增用户页
	 * 
	 * @author chris
	 * @role data_guest
	 */
	@Before({ GET.class, DataGuestInterceptor.class })
	@ActionKey("/players/add")
	public void addIndex() {
		render("add.html");
	}

	/**
	 * 新增玩家栏
	 * 
	 * @author chris
	 * @getPara addTagInfo 新增玩家选择tag
	 * @getPara icon[] 当前的icon ---apple/android/windows
	 * @getPara startDate 所选起始时间
	 * @getPara endDate 所选结束时间
	 * @role data_guest
	 */
	@Before({ POST.class, DataGuestInterceptor.class })
	@ActionKey("/api/players/add")
	public void queryAddPlayer() {
		String addTagInfo = getPara("addTagInfo", "new-activate");
		String icons = StringUtils.arrayToQueryString(getParaValues("icon[]"));
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		String versions = StringUtils.arrayToQueryString(getParaValues("versions[]"));
		String chId = StringUtils.arrayToQueryString(getParaValues("chId[]"));
		startDate = startDate + " 00:00:00";
		endDate = endDate + " 23:59:59";
		logger.info("params:{" + "addTagInfo:" + addTagInfo + ",icons:" + icons + ",startDate:" + startDate
				+ ",endDate:" + endDate + "}");

		// 返回数据格式
		Map<String, Object> data = new LinkedHashMap<String, Object>();
		Map<String, List<String>> category = new LinkedHashMap<String, List<String>>();
		List<String> categories = DateUtils.getDateList(startDate, endDate);
		// 保存chart中数据
		Map<String, List<Long>> seriesMap = new LinkedHashMap<String, List<Long>>();

		try {
			String db = URLDecoder.decode(getCookie("server"), "GBK");
			switch (addTagInfo) {
			case "new-activate": {
				List<Long> addPlayers = addPlayersService.queryAddPlayersData(categories, icons, startDate, endDate,
						db, versions, chId);
				List<Long> activateEquipment = addPlayersService.queryDeviceInfoData(categories, icons, startDate,
						endDate, db, versions, chId);
				List<Long> addEquipment = addPlayersService.queryAddEquipmentData(categories, icons, startDate, endDate,
						db, versions, chId);

				seriesMap.put("新增账户", addPlayers);
				seriesMap.put("设备激活", activateEquipment);
				seriesMap.put("新增设备", addEquipment);
				break;
			}
			case "players-change-rate": {
				List<Long> activateEquipment = addPlayersService.queryDeviceInfoData(categories, icons, startDate,
						endDate, db, versions, chId);
				List<Long> addEquipment = addPlayersService.queryAddEquipmentData(categories, icons, startDate, endDate,
						db, versions, chId);
				List<Long> playersChangeRate = addPlayersService.dealQueryPlayersChangeRate(activateEquipment,
						addEquipment);

				seriesMap.put("玩家转化率", playersChangeRate);
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
	 * 新增玩家详细栏
	 * 
	 * @getPara adddetailTagInfo 页面tag
	 * @getPara icon[] 当前的icon ---apple/android/windows
	 * @getPara startDate 所选起始时间
	 * @getPara endDate 所选结束时间
	 * @role data_guest
	 */
	@Before({ POST.class, DataGuestInterceptor.class })
	@ActionKey("/api/players/add/detail")
	public void queryaddPlayerDetails() {
		String addDetailTagInfo = getPara("addDetailTagInfo", "first-game-period");
		String icons = StringUtils.arrayToQueryString(getParaValues("icon[]"));
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		String versions = StringUtils.arrayToQueryString(getParaValues("versions[]"));
		String chId = StringUtils.arrayToQueryString(getParaValues("chId[]"));
		startDate = startDate + " 00:00:00";
		endDate = endDate + " 23:59:59";

		Map<String, Object> data = new LinkedHashMap<String, Object>();
		Map<String, List<String>> category = new LinkedHashMap<String, List<String>>();
		// 保存chart中数据
		Map<String, List<Long>> seriesMap = new LinkedHashMap<String, List<Long>>();

		try {
			String db = URLDecoder.decode(getCookie("server"), "GBK");
			switch (addDetailTagInfo) {
			case "first-game-period": {
				List<String> gamePeriod = Arrays.asList("1~4 s", "5~10 s", "11~30 s", "31~60 s", "1~3 min", "3~10 min",
						"10~30 min", "30~60 min", ">60 min");
				List<Long> peopleCount = addPlayersService.queryFirstGamePeriod(gamePeriod, icons, startDate, endDate,
						db, versions, chId);

				seriesMap.put("玩家数", peopleCount);
				category.put("首次游戏时长", gamePeriod);
				break;
			}
			case "subsidiary-account-analyze": {
				List<String> accountPeriod = Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8~10", ">10");
				List<Long> equipmentCount = addPlayersService.querySubsidiaryAccount(accountPeriod, icons, startDate,
						endDate, db, versions, chId);

				seriesMap.put("设备数", equipmentCount);
				category.put("小号分析", accountPeriod);
				break;
			}
			case "area": {
				List<CreateRole> queryData = addPlayersService.queryArea(icons, startDate, endDate, db, versions, chId);
				List<String> provinces = new ArrayList<String>();
				List<Long> provinceCount = new ArrayList<Long>();
				for (CreateRole cr : queryData) {
					provinces.add(cr.getStr("province"));
					provinceCount.add(cr.getLong("count"));
				}
				seriesMap.put("新增人数", provinceCount);
				category.put("地区", provinces);
				break;
			}
			case "country": {
				List<CreateRole> queryData = addPlayersService.queryCountry(icons, startDate, endDate, db, versions, chId);
				List<String> countries = new ArrayList<String>();
				List<Long> countryCount = new ArrayList<Long>();
				for (CreateRole cr : queryData) {
					countries.add(cr.getStr("country"));
					countryCount.add(cr.getLong("count"));
				}
				seriesMap.put("新增人数", countryCount);
				category.put("国家", countries);
				break;
			}
			case "account": {
				List<CreateRole> queryData = addPlayersService.queryAccountType(icons, startDate, endDate, db, versions, chId);
				List<String> accountType = new ArrayList<String>();
				List<Long> accountTypeCount = new ArrayList<Long>();
				for (CreateRole cr : queryData) {
					accountType.add(cr.getStr("account_type"));
					accountTypeCount.add(cr.getLong("count"));
				}
				seriesMap.put("新增人数", accountTypeCount);
				category.put("账户类型", accountType);
				break;
			}
			}
			Set<String> type = seriesMap.keySet();
			data.put("type", type.toArray());
			data.put("category", category);
			data.put("data", seriesMap);
			logger.info("<AddController> queryaddPlayerDetails:" + data);
			renderJson(data);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			logger.info("cookie decoder failed", e);
		}

	}
}
