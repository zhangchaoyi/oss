package common.controllers;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
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
import common.model.SecUser;
import common.service.AdminService;
import common.service.impl.AdminServiceImpl;
import common.utils.EncryptUtils;

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
		String key = getPara("key");

		try {
			username = EncryptUtils.aesDecrypt(username,key);
			password = EncryptUtils.aesDecrypt(password,key);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		logger.debug("username:" + username + " password:" + password + " key:" + key);
		
		SecUser secUser = as.getUser(username);
		if(secUser==null){
			renderJson("{\"message\":\"failed\"}");
		}
		String queryPasswd = secUser.getStr("password");
		String salt = secUser.getStr("salt");
		password += salt;
		try {
			if(EncryptUtils.checkpassword(password, queryPasswd)){
				setCookie("login",username, -1, "/", true);
				logger.debug("login successfully");
				renderJson("{\"message\":\"success\"}");
				return;
			}
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			logger.debug("<LoginController> Exception:", e);
			e.printStackTrace();
		}
		
		logger.debug("login failed");
		renderJson("{\"message\":\"failed\"}");
	}

	@Before(POST.class)
	@ActionKey("/api/logout")
	public void logout() {
		logger.debug("logout succefully");
		removeCookie("login");
		renderJson("{\"message\":\"success\"}");
	}
	
	@Before({POST.class, DataGuestInterceptor.class})
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
