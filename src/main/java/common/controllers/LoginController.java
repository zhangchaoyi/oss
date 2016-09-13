package common.controllers;

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
		setAttr("from", getPara("from"));

		render("login.html");
	}

//	@ActionKey("/login")
//	@Before(GET.class)
//	public void login() { 
//		
//	}

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
	
}
