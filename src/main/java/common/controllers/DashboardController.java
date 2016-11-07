package common.controllers;

import java.util.Map;

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

	@Before({GET.class, DataGuestInterceptor.class})
	@ActionKey("/dashboard")
	public void dashboard() {
		render("dashboard.html");
	}

	@Before({POST.class, DataGuestInterceptor.class})
	@ActionKey("/api/dashboard")
	public void queryDashboard() {
		Map<String, String> data = ds.queryDashboardData();
		logger.debug("<DashboardController> queryDashboard:" + data);
		renderJson(data);
	}
}
