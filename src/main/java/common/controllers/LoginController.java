package common.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.ext.interceptor.GET;
import com.jfinal.ext.interceptor.POST;
import common.interceptor.*;

@Clear(AuthInterceptor.class)
public class LoginController extends Controller {

	@Before(GET.class)
	public void index() {
		render("login.html");
	}

	@ActionKey("/login")
	@Before(GET.class)
	public void login() {
		render("login.html");
	}

	@Before(POST.class)
	@ActionKey("/api/login")
	public void loginValidate() {
		String userName = getPara("username");
		String passWord = getPara("password");

		if ("admin".equals(userName) && "admin".equals(passWord)) {
			getSession().setAttribute("login_flag", true);
			renderJson("{\"message\":\"success\"}");
			return;
		}
		renderJson("{\"message\":\"fail\"}");
	}

	@Before(POST.class)
	@ActionKey("/api/logout")
	public void logout() {
		getSession().setAttribute("login_flag", false);
	}
	
	
	@Before(GET.class)
	@ActionKey("/api/testTableData")
	public void testTableData() {
		Map<String,Object> map = new HashMap<String,Object>();
		
		List<Object> l = new ArrayList<Object>();
		l.add(Arrays.asList("ri","ri","ri","ri"));
		map.put("data", l);
		renderJson(map);
	}
}
