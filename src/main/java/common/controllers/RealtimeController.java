package common.controllers;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.ext.interceptor.GET;
import com.jfinal.ext.interceptor.POST;
import common.interceptor.AuthInterceptor;
import common.service.RealtimeService;
import common.service.impl.RealtimeServiceImpl;
import common.utils.StringUtils;

/**
 * 实时数据页 --逻辑控制层
 * 目前实时在线为假数据
 * @author chris
 *
 */
//@Clear(AuthInterceptor.class)
@Before(AuthInterceptor.class)
public class RealtimeController extends Controller {
	private RealtimeService realtimeService = new RealtimeServiceImpl();

	@Before(GET.class)
	@ActionKey("/realtime/info")
	public void activePlayer() {
		render("info.html");
	}

	@Before(POST.class)
	@ActionKey("/api/realtime/beforedata")
	public void queryBeforeData() {
		String icons = StringUtils.arrayToQueryString(getParaValues("icon[]"));
		Map<String, String> data = realtimeService.queryBeforeData(icons);
		renderJson(data);
	}

	@Before(POST.class)
	@ActionKey("/api/realtime/realtimedata")
	public void queryRealtimeData() {
		String icons = StringUtils.arrayToQueryString(getParaValues("icon[]"));
		Map<String, String> data = realtimeService.queryRealtimeData(icons);
		renderJson(data);
	}

	@Before(POST.class)
	@ActionKey("/api/realtime/info")
	public void queryRealtimeInfo() {
		String detailTag = getPara("detailTag", "rto");
		String icons = StringUtils.arrayToQueryString(getParaValues("icon[]"));
		String[] date = getParaValues("startDate[]");

		Map<String, Object> data = new LinkedHashMap<String, Object>();
		List<String> categories = Arrays.asList("00:00~01:00", "01:00~02:00", "02:00~03:00", "03:00~04:00",
				"04:00~05:00", "05:00~06:00", "06:00~07:00", "07:00~08:00", "08:00~09:00", "09:00~10:00", "10:00~11:00",
				"11:00~12:00", "12:00~13:00", "13:00~14:00", "14:00~15:00", "15:00~16:00", "16:00~17:00", "17:00~18:00",
				"18:00~19:00", "19:00~20:00", "20:00~21:00", "21:00~22:00", "22:00~23:00", "23:00~24:00");
		Map<String, List<String>> category = new LinkedHashMap<String, List<String>>();
		Map<String,Object> seriesMap = new HashMap<String,Object>();
		
		switch (detailTag) {
		case "rto": {
			for (String s : date) {
				Random d = new Random();
				List<Long> list = new ArrayList<Long>();
			for (int i = 0; i < 24; i++) {
				list.add((long) d.nextInt(100));
			}
			seriesMap.put(s, list);
		}
			break;
		}
		case "equ": {
			seriesMap = realtimeService.queryRealtimeDevice(icons, date);
			break;
		}
		case "adp": {
			seriesMap = realtimeService.queryRealtimeAddPlayers(icons, date);
			break;
		}
		case "pay": {
			seriesMap = realtimeService.queryRealtimeRevenue(icons, date);
			break;
		}
		}

		Set<String> type = seriesMap.keySet();

		category.put("时间段", categories);
		data.put("category", category);
		data.put("type", type.toArray());
		data.put("data", seriesMap);
		renderJson(data);
	}
}
