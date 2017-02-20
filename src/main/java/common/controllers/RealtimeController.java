package common.controllers;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.ext.interceptor.GET;
import com.jfinal.ext.interceptor.POST;
import common.service.RealtimeService;
import common.service.impl.RealtimeServiceImpl;
import common.util.StringUtils;
import common.interceptor.DataGuestInterceptor;

/**
 * 实时数据页 --逻辑控制层 目前实时在线为假数据
 * @author chris
 */
@Clear
public class RealtimeController extends Controller {
	private static Logger logger = Logger.getLogger(RealtimeController.class);
	private RealtimeService realtimeService = new RealtimeServiceImpl();

	/**
	 * 实时概况页
	 * 
	 * @role data_guest
	 */
	@Before({ GET.class, DataGuestInterceptor.class })
	@ActionKey("/realtime/info")
	public void activePlayer() {
		render("info.html");
	}

	/**
	 * 实时概况 昨日/七日/三十日 接口
	 * 
	 * @getPara icon[] 当前的icon ---apple/android/windows
	 * @role data_guest
	 */
	@Before({ POST.class, DataGuestInterceptor.class })
	@ActionKey("/api/realtime/beforedata")
	public void queryBeforeData() {
		String icons = StringUtils.arrayToQueryString(getParaValues("icon[]"));
		String versions = StringUtils.arrayToQueryString(getParaValues("versions[]"));
		String chId = StringUtils.arrayToQueryString(getParaValues("chId[]"));
		try {
			String db = URLDecoder.decode(getCookie("server"), "GBK");
			logger.info("params: {icons:" + icons + "}");
			Map<String, String> data = realtimeService.beforeData(icons, db, versions, chId);
			logger.info("data:" + data);
			renderJson(data);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			logger.info("cookie decoder failed",e);
		}
	}

	/**
	 * 实时接口
	 * 
	 * @getPara icon[] 当前的icon ---apple/android/windows
	 * @role data_guest
	 */
	@Before({ POST.class, DataGuestInterceptor.class })
	@ActionKey("/api/realtime/realtimedata")
	public void queryRealtimeData() {
		String icons = StringUtils.arrayToQueryString(getParaValues("icon[]"));
		String versions = StringUtils.arrayToQueryString(getParaValues("versions[]"));
		String chId = StringUtils.arrayToQueryString(getParaValues("chId[]"));
		logger.info("params: {icons:" + icons + "}");
		try {
			String db = URLDecoder.decode(getCookie("server"), "GBK");
			Map<String, String> data = realtimeService.realtimeData(icons, db, versions, chId);
			logger.info("data:" + data);
			renderJson(data);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			logger.info("cookie decoder failed",e);
		}
	}

	/**
	 * 详细栏接口
	 * 
	 * @getPara detailTag tag选择器
	 * @getPara startDate[] 对比时段
	 * @getPara icon[] 当前的icon ---apple/android/windows
	 * @role data_guest
	 */
	@Before({ POST.class, DataGuestInterceptor.class })
	@ActionKey("/api/realtime/info")
	public void queryRealtimeInfo() {
		String detailTag = getPara("detailTag", "rto");
		String icons = StringUtils.arrayToQueryString(getParaValues("icon[]"));
		String versions = StringUtils.arrayToQueryString(getParaValues("versions[]"));
		String chId = StringUtils.arrayToQueryString(getParaValues("chId[]"));
		String[] date = getParaValues("startDate[]");
		logger.info("params: {" + "detailTag:" + detailTag + ",icons:" + icons + ",date[]" + date + "}");

		String db;
		try {
			db = URLDecoder.decode(getCookie("server"), "GBK");

			Map<String, Object> data = new LinkedHashMap<String, Object>();
			List<String> categories = Arrays.asList("00:00~01:00", "01:00~02:00", "02:00~03:00", "03:00~04:00",
					"04:00~05:00", "05:00~06:00", "06:00~07:00", "07:00~08:00", "08:00~09:00", "09:00~10:00",
					"10:00~11:00", "11:00~12:00", "12:00~13:00", "13:00~14:00", "14:00~15:00", "15:00~16:00",
					"16:00~17:00", "17:00~18:00", "18:00~19:00", "19:00~20:00", "20:00~21:00", "21:00~22:00",
					"22:00~23:00", "23:00~24:00");
			Map<String, List<String>> category = new LinkedHashMap<String, List<String>>();
			Map<String, Object> seriesMap = new HashMap<String, Object>();

			switch (detailTag) {
			case "rto": {
				seriesMap = realtimeService.queryRealtimePlayerCount(date, db);
				break;
			}
			case "equ": {
				seriesMap = realtimeService.queryRealtimeDevice(icons, date, db, versions, chId);
				break;
			}
			case "adp": {
				seriesMap = realtimeService.queryRealtimeAddPlayers(icons, date, db, versions, chId);
				break;
			}
			case "pay": {
				seriesMap = realtimeService.queryRealtimeRevenue(icons, date, db, versions, chId);
				break;
			}
			}

			Set<String> type = seriesMap.keySet();

			category.put("时间段", categories);
			data.put("category", category);
			data.put("type", type.toArray());
			data.put("data", seriesMap);
			logger.info("data:" + data);
			renderJson(data);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			logger.info("cookie decoder failed",e);
		}
	}
}
