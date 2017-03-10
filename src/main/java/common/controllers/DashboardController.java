package common.controllers;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;

import org.apache.log4j.Logger;

import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.ext.interceptor.GET;
import com.jfinal.ext.interceptor.POST;

import common.interceptor.DataGuestInterceptor;
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
	@Before({GET.class, DataGuestInterceptor.class})
	@ActionKey("/dashboard")
	public void dashboard() {
		render("dashboard.html");
	}
	/**
	 * dashboard 接口
	 * @author chris
	 * @role data_guest
	 */
	@Before({POST.class,DataGuestInterceptor.class})
	@ActionKey("/api/dashboard")
	public void queryDashboard() {
		Map<String, String> data = new HashMap<String, String>();
		Cookie serverCookie = getCookieObject("server");
		if(serverCookie==null){
			renderText("cookie服务器不存在,请求非法");
			return;
		}
		try {
			String db = URLDecoder.decode(serverCookie.getValue(), "GBK");
			data = ds.queryDashboardData(db);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			logger.info("cookie decoder failed", e);
		}
		
		logger.info("data:" + data);
		renderJson(data);
	}
}
