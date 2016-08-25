package common.controllers;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.ext.interceptor.GET;

import common.interceptor.AuthInterceptor;

@Before(AuthInterceptor.class)
public class DashboardController extends Controller{
	@Before(GET.class)
	@ActionKey("/dashboard")
	public void test() {
		render("dashboard.html");
	}
}
