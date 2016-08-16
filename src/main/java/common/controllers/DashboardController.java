package common.controllers;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.ext.interceptor.GET;


public class DashboardController extends Controller{
	
	
	@Before(GET.class)
	@ActionKey("/dashboard")
	public void test() {
		render("dashboard.html");
	}
}
