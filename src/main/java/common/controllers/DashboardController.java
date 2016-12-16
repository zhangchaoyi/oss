package common.controllers;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.ext.interceptor.GET;
import com.jfinal.ext.interceptor.POST;
import common.service.DashboardService;
import common.service.impl.DashboardServiceImpl;

@Clear
public class DashboardController extends Controller {
	private static Logger logger = Logger.getLogger(DashboardController.class);
	private DashboardService ds = new DashboardServiceImpl();
	
	/**
	 * dashboard 页
	 * @author chris
	 * @role data_guest
	 */
	@Before(GET.class)
	@ActionKey("/dashboard")
	public void dashboard() {
		render("dashboard.html");
	}
	/**
	 * dashboard 接口
	 * @author chris
	 * @role data_guest
	 */
	@Before(POST.class)
	@ActionKey("/api/dashboard")
	public void queryDashboard() {
		Map<String, String> data = new HashMap<String, String>();
		try {
			String db = URLDecoder.decode(getCookie("server"), "GBK");
			data = ds.queryDashboardData(db);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			logger.info("cookie decoder failed", e);
		}
		
		logger.info("data:" + data);
		renderJson(data);
	}
}
