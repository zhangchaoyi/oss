package common.controllers;

import org.apache.log4j.Logger;

import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.ext.interceptor.GET;
import com.jfinal.ext.interceptor.POST;
import common.interceptor.*;

@Clear(AuthInterceptor.class)
public class LoginController extends Controller {
	private static Logger logger = Logger.getLogger(LoginController.class);

	@Before(GET.class)
	public void index() {
		setAttr("from", getPara("from"));

		render("login.html");
	}

	@Before(POST.class)
	@ActionKey("/api/login")
	public void loginValidate() {
		String userName = getPara("username");
		String passWord = getPara("password");

		logger.debug("username:" + userName + "password:" + passWord);
		if ("admin".equals(userName) && "admin".equals(passWord)) {
			setCookie("login",userName, 86400, "/", true);
//			getSession().setAttribute("login_flag", true);
			logger.debug("login successfully");
			renderJson("{\"message\":\"success\"}");
			return;
		}
		logger.debug("login failed");
		renderJson("{\"message\":\"fail\"}");
	}

	@Before(POST.class)
	@ActionKey("/api/logout")
	public void logout() {
		logger.debug("logout succefully");
//		getSession().setAttribute("login_flag", false);
		removeCookie("login");
	}

}
