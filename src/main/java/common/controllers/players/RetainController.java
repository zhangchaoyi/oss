package common.controllers.players;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
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
import common.service.RetainPlayersService;
import common.service.impl.RetainPlayersServiceImpl;
import common.util.DateUtils;
import common.util.StringUtils;

/**
 * 处理留存用户和留存设备,逻辑控制层 自定义留存使用假数据
 * 
 * @author chris
 */
@Clear
public class RetainController extends Controller {
	private static Logger logger = Logger.getLogger(RetainController.class);
	private RetainPlayersService retainPlayersService = new RetainPlayersServiceImpl();

	/**
	 * 留存页
	 * 
	 * @role data_guest
	 */
	@Before({ GET.class, DataGuestInterceptor.class })
	@ActionKey("/players/retain")
	public void retain() {
		render("retain.html");
	}

	/**
	 * 设备留存页
	 * 
	 * @role data_guest
	 */
	@Before({ GET.class, DataGuestInterceptor.class })
	@ActionKey("/players/retain-equipment")
	public void retainEquipment() {
		render("retain-equipment.html");
	}

	/**
	 * 留存接口
	 * 
	 * @getPara icon[] 当前的icon ---apple/android/windows
	 * @getPara startDate 所选起始时间
	 * @getPara endDate 所选结束时间
	 * @role data_guest
	 */
	@Before({ POST.class, DataGuestInterceptor.class })
	@ActionKey("/api/players/retain")
	public void queryRetain() {
		String icons = StringUtils.arrayToQueryString(getParaValues("icon[]"));
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		logger.info("params:{" + "icons:" + icons + ",startDate:" + startDate + ",endDate:" + endDate + "}");

		Map<String, Object> data = new LinkedHashMap<String, Object>();
		List<String> categories = DateUtils.getDateList(startDate, endDate);

		try {
			String db = URLDecoder.decode(getCookie("server"), "GBK");
			Map<String, Object> queryData = retainPlayersService.queryRetainUser(categories, icons, startDate, endDate,
					db);
			Map<String, List<String>> category = new HashMap<String, List<String>>();
			Map<String, Object> addPlayer = new HashMap<String, Object>();
			Map<String, Object> activeDevice = new HashMap<String, Object>();
			Map<String, Object> addDevice = new HashMap<String, Object>();

			// 保存chart中数据
			Map<String, Object> seriesMap = new LinkedHashMap<String, Object>();
			seriesMap.put("+1日", queryData.get("firstDR"));
			seriesMap.put("+2日", queryData.get("secondDR"));
			seriesMap.put("+3日", queryData.get("thirdDR"));
			seriesMap.put("+4日", queryData.get("forthDR"));
			seriesMap.put("+5日", queryData.get("fifthDR"));
			seriesMap.put("+6日", queryData.get("sixthDR"));
			seriesMap.put("+7日", queryData.get("sevenDR"));
			seriesMap.put("+8日", queryData.get("eighthDR"));
			seriesMap.put("+9日", queryData.get("ninthDR"));
			seriesMap.put("+10日", queryData.get("tenthDR"));
			seriesMap.put("+11日", queryData.get("eleventhDR"));
			seriesMap.put("+12日", queryData.get("twelfthDR"));
			seriesMap.put("+13日", queryData.get("thirteenthDR"));
			seriesMap.put("+14日", queryData.get("fourteenthDR"));
			seriesMap.put("+30日", queryData.get("mR"));

			Set<String> type = seriesMap.keySet();

			category.put("日期", categories);
			addPlayer.put("新增玩家", queryData.get("add"));
			activeDevice.put("激活设备", queryData.get("activeDevice"));
			addDevice.put("新增设备", queryData.get("addDevice"));

			data.put("type", type.toArray());
			data.put("category", category);
			data.put("addPlayer", addPlayer);
			data.put("activeDevice", activeDevice);
			data.put("addDevice", addDevice);
			data.put("data", seriesMap);
			data.put("nDRRateAvg", queryData.get("nDRRateAvg"));
			data.put("sDRRateAvg", queryData.get("sDRRateAvg"));
			data.put("mRRateAvg", queryData.get("mRRateAvg"));
			logger.info("data:" + data);
			renderJson(data);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			logger.info("cookie decoder failed", e);
		}
	}

	/**
	 * 设备留存率接口
	 * 
	 * @getPara icon[] 当前的icon ---apple/android/windows
	 * @getPara startDate 所选起始时间
	 * @getPara endDate 所选结束时间
	 * @role data_guest
	 */
	@Before({ POST.class, DataGuestInterceptor.class })
	@ActionKey("/api/players/retain-equipment/rate")
	public void queryRetainEquipmentRate() {
		String icons = StringUtils.arrayToQueryString(getParaValues("icon[]"));
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		logger.info("params:{" + "icons:" + icons + ",startDate:" + startDate + ",endDate:" + endDate + "}");

		List<String> categories = DateUtils.getDateList(startDate, endDate);
		try {
			String db = URLDecoder.decode(getCookie("server"), "GBK");
			Map<String, Object> queryData = retainPlayersService.queryRetainEquipment(categories, icons, startDate,
					endDate, db);

			Map<String, Object> data = new HashMap<String, Object>();
			Map<String, List<String>> category = new HashMap<String, List<String>>();

			// 保存chart中数据
			Map<String, Object> seriesMap = new HashMap<String, Object>();

			seriesMap.put("次日留存率", queryData.get("fD"));
			seriesMap.put("7日留存率", queryData.get("sevenD"));
			seriesMap.put("30日留存率", queryData.get("ttD"));

			// 处理表数据
			List<String> tableHeader = new LinkedList<String>();
			Map<String, Object> tableData = new LinkedHashMap<String, Object>();

			tableHeader.addAll(Arrays.asList("首次使用日", "激活设备", "新增设备", "第N天后 保留设备"));
			for (int i = 1; i < 8; i++) {
				tableHeader.add("+" + i + "日");
			}
			tableHeader.add("+14" + "日");
			tableHeader.add("+30" + "日");

			tableData.put("activeDevice", queryData.get("activeDevice"));
			tableData.put("addEquipment", queryData.get("addEquipment"));
			tableData.put("fD", queryData.get("fD"));
			tableData.put("sD", queryData.get("sD"));
			tableData.put("tD", queryData.get("tD"));
			tableData.put("fourD", queryData.get("fourD"));
			tableData.put("fifD", queryData.get("fifD"));
			tableData.put("sixD", queryData.get("sixD"));
			tableData.put("sevenD", queryData.get("sevenD"));
			tableData.put("ftD", queryData.get("ftD"));
			tableData.put("ttD", queryData.get("ttD"));

			Set<String> type = seriesMap.keySet();
			category.put("日期", categories);
			data.put("type", type.toArray());
			data.put("category", category);
			data.put("data", seriesMap);
			data.put("tableData", tableData);
			data.put("header", tableHeader);
			logger.info("data:" + data);
			renderJson(data);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			logger.info("cookie decoder failed", e);
		}
	}
}
