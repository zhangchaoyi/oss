package common.controllers;

import java.util.Map;

import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.ext.interceptor.GET;
import com.jfinal.ext.interceptor.POST;

import common.interceptor.AuthInterceptor;
import common.service.DashboardService;
import common.service.impl.DashboardServiceImpl;

@Before(AuthInterceptor.class)
//@Clear(AuthInterceptor.class)
public class DashboardController extends Controller{
	private DashboardService ds = new DashboardServiceImpl();
	@Before(GET.class)
	@ActionKey("/dashboard")
	public void test() {
		render("dashboard.html");
	}
	
	@Before(POST.class)
	@ActionKey("/api/dashboard")
	public void queryDashboard() {
		Map<String, String> data = ds.queryDashboardData();
		renderJson(data);
	}
}
