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

import common.model.SecUser;
import common.mysql.DbSelector;
import common.service.AdminService;
import common.service.impl.AdminServiceImpl;
import common.utils.EncryptUtils;

@Clear
public class LoginController extends Controller {
	private static Logger logger = Logger.getLogger(LoginController.class);
	private AdminService as = new AdminServiceImpl();
	
	/**
	 * 登录页
	 * @author chris
	 * @getPara from  跳转登录页前的 页面url
	 * @role  所有角色
	 */
	@Before(GET.class)
	public void index() {
		render("login.html");
	}
	
	@Before(POST.class)
	@ActionKey("/api/login/serverInfo")
	public void loginServer() {
		renderJson("dbs", DbSelector.getDbs());
	}
	
    /**
     * 登录校验接口,先根据username查询mysql的salt,将(传来的password经解密后 + salt) md5 得到摘要 比对 数据库的password摘要  
     * @author chris
     * @param username 用户名(加密后)
     * @param password 密码(加密后)
     * @param key 加密密钥
     * @role 所有角色
     */
	@Before(POST.class)
	@ActionKey("/api/login")
	public void loginValidate() {
		String username = getPara("username");
		String password = getPara("password");
		String db = getPara("db", "malai");
		String key = getPara("key");
		logger.info("paras: {" + "username:"+username+",password:"+password+",db:"+db+",key:"+key+"}");
		try {
			username = EncryptUtils.aesDecrypt(username,key);
			password = EncryptUtils.aesDecrypt(password,key);
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("After decrypt:--"+"username:" + username + " password:" + password + " key:" + key);
		
		SecUser secUser = as.getUser(username);
		if(secUser==null){
			renderJson("{\"message\":\"failed\"}");
			return;
		}
		String queryPasswd = secUser.getStr("password");
		String salt = secUser.getStr("salt");
		password += salt;
		try {
			if(EncryptUtils.checkpassword(password, queryPasswd)){
				setCookie("login",username, -1, "/", true);
				DbSelector.setDbName(db);
				logger.info("login successfully");
				renderJson("{\"message\":\"success\"}");
				return;
			}
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			logger.info("<LoginController> Exception:", e);
			e.printStackTrace();
		}
		
		logger.info("login failed");
		renderJson("{\"message\":\"failed\"}");
	}
	/**
	 * 登出接口,清理cookie
	 * @author chris
	 * @role 所有角色
	 */
	@Before(POST.class)
	@ActionKey("/api/logout")
	public void logout() {
		logger.info("logout succefully");
		removeCookie("login");
		renderJson("{\"message\":\"success\"}");
	}
	/**
	 * 得到cookie信息 --用户名  --服务器
	 * @author chris
	 * @role  data_guest
	 */
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
		String db = DbSelector.getDbName();
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("username", username);
		data.put("db", db);
		data.put("dbName", DbSelector.getDbName(db));
		data.put("dbs", DbSelector.getDbs());
		data.put("message", message);
		logger.info("cookie info" + data);
		renderJson(data);
	}
}
