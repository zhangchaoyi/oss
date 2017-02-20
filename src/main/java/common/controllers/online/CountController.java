package common.controllers.online;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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
import common.service.OnlineCountService;
import common.service.impl.OnlineCountServiceImpl;
import common.util.DateUtils;

@Clear
public class CountController extends Controller {
	private static Logger logger = Logger.getLogger(CountController.class);
	private OnlineCountService oc = new OnlineCountServiceImpl();

	/**
	 * 在线人数页
	 * @author chris
	 * @role data_guest
	 */
	@Before({ GET.class, DataGuestInterceptor.class })
	@ActionKey("/online/count")
	public void count() {
		render("count.html");
	}

	/**
	 * 在线人数ccu接口
	 * 
	 * @author chris
	 * @role data_guest
	 */
	@Before({ POST.class, DataGuestInterceptor.class })
	@ActionKey("/api/online/count/ccu")
	public void ccu() {
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		logger.info("params: {" + ",startDate:" + startDate + ",endDate:" + endDate + "}");
		try {
			String db = URLDecoder.decode(getCookie("server"), "GBK");

			Map<String, Object> queryData = oc.queryCCU(startDate, endDate, db);
			Map<String, Object> data = new LinkedHashMap<String, Object>();
			Map<String, Object> seriesMap = new LinkedHashMap<String, Object>();
			Map<String, Object> category = new LinkedHashMap<String, Object>();

			seriesMap.put("CCU", queryData.get("ccu"));
			category.put("时间", queryData.get("onlineDatetime"));

			Set<String> type = seriesMap.keySet();
			data.put("type", type.toArray());
			data.put("category", category);
			data.put("data", seriesMap);
			data.put("periodPcu", queryData.get("periodPcu"));
			data.put("historyPcu", queryData.get("historyPcu"));
			data.put("latestCcu", queryData.get("latestCcu"));
			logger.info("data:" + data);
			renderJson(data);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			logger.info("cookie decoder failed", e);
		}
	}

	/**
	 * 在线人数pcu接口
	 * 
	 * @author chris
	 * @role data_guest
	 */
	@Before({ POST.class, DataGuestInterceptor.class })
	@ActionKey("/api/online/count/pcu")
	public void pcu() {
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		logger.info("params: {" + ",startDate:" + startDate + ",endDate:" + endDate + "}");
		List<String> categories = DateUtils.getDateList(startDate, endDate);
		try {
			String db = URLDecoder.decode(getCookie("server"), "GBK");
			Map<String, Object> queryData = oc.queryPCU(categories, startDate, endDate, db);
			Map<String, Object> data = new LinkedHashMap<String, Object>();
			Map<String, Object> seriesMap = new LinkedHashMap<String, Object>();
			Map<String, Object> category = new LinkedHashMap<String, Object>();
			seriesMap.put("PCU", queryData.get("pcus"));
			category.put("日期", categories);

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
