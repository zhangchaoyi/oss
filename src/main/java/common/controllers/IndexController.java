package common.controllers;

import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.Controller;
import com.jfinal.ext.interceptor.GET;

@Clear
public class IndexController extends Controller{
	@Before(GET.class)
	public void index() {
		render("login.html");
	}
}
