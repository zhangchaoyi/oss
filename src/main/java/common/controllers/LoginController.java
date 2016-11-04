package common.controllers;

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
import common.model.SecUser;
import common.service.AdminService;
import common.service.impl.AdminServiceImpl;

@Clear
public class LoginController extends Controller {
	private static Logger logger = Logger.getLogger(LoginController.class);
	private AdminService as = new AdminServiceImpl();
	
	@Before(GET.class)
	public void index() {
		setAttr("from", getPara("from"));

		render("login.html");
	}

	@Before(POST.class)
	@ActionKey("/api/login")
	public void loginValidate() {
		String username = getPara("username");
		String password = getPara("password");

		logger.debug("username:" + username + "password:" + password);
		SecUser secUser = as.getUser(username);
		if(secUser!=null && secUser.getStr("password").equals(password)){
			setCookie("login",username, -1, "/", true);
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
		removeCookie("login");
		renderJson("{\"message\":\"success\"}");
	}
	
	@Before(POST.class)
	@ActionKey("/api/cookie/info")
	public void getCookieInfo(){
		Cookie cookie = getCookieObject("login");
		String username = "";
		String message = "false";
		if(cookie!=null){
			username = cookie.getValue();
			message = "true";
		}
		Map<String, String> data = new HashMap<String, String>();
		data.put("username", username);
		data.put("message", message);
		logger.debug("cookie info" + data);
		renderJson(data);
	}

}
