package common.controllers;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import org.apache.log4j.Logger;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.ext.interceptor.GET;
import com.jfinal.ext.interceptor.POST;
import com.jfinal.kit.JsonKit;

import common.model.SecUser;
import common.mysql.DbSelector;
import common.service.AdminService;
import common.service.impl.AdminServiceImpl;
import common.util.DateUtils;
import common.util.EncryptUtils;
import common.util.JsonToMap;

@Clear
public class LoginController extends Controller {
	private static Logger logger = Logger.getLogger(LoginController.class);
	private AdminService as = new AdminServiceImpl();

	/**
	 * 登录页
	 * 
	 * @author chris
	 * @getPara from 跳转登录页前的 页面url
	 * @role 所有角色
	 */
	@Before(GET.class)
	public void index() {
		render("login.html");
	}

	/**
	 * 登录校验接口,先根据username查询mysql的salt,将(传来的password经解密后 + salt) md5 得到摘要 比对
	 * 数据库的password摘要
	 * 登录成功后设置cookie 包括用户名,menu的选项,用户拥有的服务器列表以及设置当前的服务器列表
	 * @author chris
	 * @param username
	 *            用户名(加密后)
	 * @param password
	 *            密码(加密后)
	 * @param key
	 *            加密密钥
	 * @role 所有角色
	 */
	@Before(POST.class)
	@ActionKey("/api/login")
	public void loginValidate() {
		String username = getPara("username");
		String password = getPara("password");
		String key = getPara("key");
		logger.info("paras: {" + "username:" + username + ",password:" + password + ",key:" + key + "}");
		try {
			username = EncryptUtils.aesDecrypt(username, key);
			password = EncryptUtils.aesDecrypt(password, key);
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("After decrypt:--" + "username:" + username + " password:" + password + " key:" + key);

		SecUser secUser = as.getUser(username);
		if (secUser == null) {
			renderJson("{\"message\":\"failed\"}");
			return;
		}
		String queryPasswd = secUser.getStr("password");
		String salt = secUser.getStr("salt");
		password += salt;
		try {
			if (EncryptUtils.checkpassword(password, queryPasswd)) {
				int code = DbSelector.setUserDbs(secUser.getStr("server"));
				if(code==0){
					logger.info("login failed");
					renderJson("{\"message\":\"failed\"}");
					return;
				}
				Map<String,String> menu = initUserMap(secUser);
				setCookie("menu", URLEncoder.encode(JsonKit.toJson(menu),"GBK"), -1, "/", false);
				setCookie("icons", URLEncoder.encode("iOS", "GBK"), -1, "/", false);
				setCookie("startDate", URLEncoder.encode(DateUtils.getSevenAgoDate(), "GBK"), -1, "/", false);
				setCookie("endDate", URLEncoder.encode(DateUtils.getTodayDate(), "GBK"), -1, "/", false);
				setCookie("login", username, -1, "/", true);
				setCookie("server", URLEncoder.encode(DbSelector.getDbName(), "GBK"), -1, "/", false);
				setCookie("serverList", URLEncoder.encode(DbSelector.getUserDbs().toString(), "GBK"), -1, "/", true);
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
	 * 登出接口,清理所有的cookie
	 * 清空内存里的userDbs
	 * @author chris
	 * @role 所有角色
	 */
	@Before(POST.class)
	@ActionKey("/api/logout")
	public void logout() {
		logger.info("logout succefully");
		removeCookie("login");
		removeCookie("menu");
		removeCookie("icons");
		removeCookie("startDate");
		removeCookie("endDate");
		DbSelector.clearUserDbs();
		renderJson("{\"message\":\"success\"}");
	}

	/**
	 * 得到cookie信息 --用户名 --用户具有的服务器列表  --用户当前的服务器
	 * 不存放在cookie的主要原因在于需要获取服务器当前正使用的服
	 * @author chris
	 * @role data_guest
	 */
	@Before(POST.class)
	@ActionKey("/api/cookie/info")
	public void getCookieInfo() {
		Cookie userCookie = getCookieObject("login");
		Cookie serverCookie = getCookieObject("server");
		Cookie serverListCookie = getCookieObject("serverList");

		String username = "";
		String server = "";
		String serverList = "";
		String message = "true";
		
		Map<String, Object> data = new HashMap<String, Object>();
		if (userCookie==null || serverCookie==null || serverListCookie==null) {
			message = "false";
			data.put("message", message);
			renderJson(data);
			return;
		}
		username = userCookie.getValue();
		server = serverCookie.getValue();
		serverList = serverListCookie.getValue();
		try {
			String dbs = URLDecoder.decode(serverList, "GBK");
			String db = URLDecoder.decode(server, "GBK");
			Map<String,Object> jsonMap = JsonToMap.toMap(dbs);
			Map<String,String> dbsMap = new LinkedHashMap<String, String>();
			for(Map.Entry<String, Object> entry : jsonMap.entrySet()){
				dbsMap.put(entry.getKey(),entry.getValue().toString().replace("\"",""));
			}
			data.put("username", username);
			data.put("db", db);
			data.put("dbName", DbSelector.getDbName(db));
			data.put("dbs", dbsMap);
			data.put("message", message);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		logger.info("cookie info" + data);
		renderJson(data);
	}

	/**
	 * 对secUser 源数据进行处理,将不包含"1"的key-value 去除
	 * 返回最终包含"1"的map
	 * @param secUser
	 * @return
	 */
	public static Map<String, String> initUserMap(SecUser secUser) {
		Map<String, String> map = new LinkedHashMap<String, String>();
		String realtime = secUser.getStr("realtime");
		String form = secUser.getStr("form");
		String playerAnalyse = secUser.getStr("player_analyse");
		String paidAnalyse = secUser.getStr("paid_analyse");
		String loss = secUser.getStr("loss");
		String onlineAnalyse = secUser.getStr("online_analyse");
		String channelAnalyse = secUser.getStr("channel_analyse");
		String systemAnalyse = secUser.getStr("system_analyse");
		String versionAnalyse = secUser.getStr("version_analyse");
		String customEvent = secUser.getStr("custom_event");
		String opSupport = secUser.getStr("op_support");
		String dataDig = secUser.getStr("data_dig");
		String marketAnalyse = secUser.getStr("market_analyse");
		String techSupport = secUser.getStr("tech_support");
		String managementCenter = secUser.getStr("management_center");
		String server = secUser.getStr("server");

		map.put("realtime", realtime);
		map.put("form", form);
		map.put("playerAnalyse", playerAnalyse);
		map.put("paidAnalyse", paidAnalyse);
		map.put("loss", loss);
		map.put("onlineAnalyse", onlineAnalyse);
		map.put("channelAnalyse", channelAnalyse);
		map.put("systemAnalyse", systemAnalyse);
		map.put("versionAnalyse", versionAnalyse);
		map.put("customEvent", customEvent);
		map.put("opSupport", opSupport);
		map.put("dataDig", dataDig);
		map.put("marketAnalyse", marketAnalyse);
		map.put("techSupport", techSupport);
		map.put("managementCenter", managementCenter);
		map.put("server", server);
		Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();  
		while(it.hasNext()){
			Map.Entry<String, String> entry = it.next();
			if(!entry.getValue().contains("1")){
				it.remove();
			}
		}
		return map;
	}
}
