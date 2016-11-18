package common.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.Cookie;

import org.apache.log4j.Logger;

import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.ext.interceptor.GET;
import com.jfinal.ext.interceptor.POST;

import common.interceptor.RootInterceptor;
import common.mysql.DbSelector;
import common.service.AdminService;
import common.service.impl.AdminServiceImpl;
import common.utils.EncryptUtils;
import common.utils.StringUtils;

@Clear
public class AdminController extends Controller {
	private static Logger logger = Logger.getLogger(AdminController.class);
	private AdminService as = new AdminServiceImpl();
	private Set<String> dbs = DbSelector.getDbs().keySet();
	
	/**
	 * 没有权限页
	 * @author chris
	 * @role  所有 
	 */
	@Before(GET.class)
	@ActionKey("/admin/authority/error")
	public void authorityError() {
		renderError(401,"authorityError.html");
	}
	/**
	 *  新增用户页
	 *  @author chris
	 *  @role  root
	 */
	@Before({GET.class, RootInterceptor.class})
	@ActionKey("/admin/createUser")
	public void createUserIndex() {
		render("createUser.html");
	}
	/** 
	 * 管理用户页 
	 * @author chris
	 * @role  root
	 */
	@Before({GET.class, RootInterceptor.class})
	@ActionKey("/admin/manageUsers")
	public void manageUsersIndex() {
		render("userManagement.html");
	}
	/**
	 * 用户个人权限页
	 * @author chris
	 * @role all 
	 */
	@Before(GET.class)
	@ActionKey("/admin/account")
	public void accountIndex(){
		render("account-permission.html");
	}
	
	/**
	 *  新增用户接口 
	 * @author chris
	 * @getParam username 用户名(加密后)
	 * @getParam password 密码(加密后)
	 * @getParam role 角色(加密后)
	 * @getParam key 加密密钥
	 * @return 返回成功/失败信息
	 * @role  root
	 */
	@Before({POST.class, RootInterceptor.class})
	@ActionKey("/api/admin/createUser")
	public void createUser() {
		String username = getPara("username");
		String password = getPara("password");
		String role = getPara("role", "data_guest");
		String key = getPara("key");
		logger.info("params: {" + "username:"+username+",password:"+password+",role:"+role+",key:"+key+"}");
		Map<String, String> data = new HashMap<String, String>();
		
		try {
			username = EncryptUtils.aesDecrypt(username,key);
			password = EncryptUtils.aesDecrypt(password,key);
			role = EncryptUtils.aesDecrypt(role,key);
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Exception:", e);
		}
		
		//username 是否已经存在
		boolean exist = as.existUser(username);
		if(exist==true) {
			data.put("message", "exist");
			renderJson(data);
			return;
		}
		
		logger.info("username:" + username + "password:" + password + "role:" + role);
		logger.info("dbs:"+dbs);
		
		//对各个数据库同时进行用户管理,修改完成恢复原数据库
		String originDb = DbSelector.getDbName();
		boolean succeed = false;
		for(String db : dbs){
			DbSelector.setDbName(db);
			succeed = as.signupUser(username, password, role);
		}
		DbSelector.setDbName(originDb);
		
		if(succeed==true){
			logger.info("signup successfully");
			data.put("message", "successfully");
			renderJson(data);
			return;
		}
		logger.info("createUser failed");
		data.put("message", "failed");
		renderJson(data);
	}
	/**
	 * 用户管理接口 由于多个数据源的用户是一致的,只需要查询其中一个
	 * @author chris
	 * @return 返回所有用户列表
	 * @role  root 
	 */
	@Before({POST.class, RootInterceptor.class})
	@ActionKey("/api/admin/manageUsers")
	public void manageUsers() {
		List<List<String>> data = as.queryUsers();
		logger.info("data:" + data);
		renderJson(data);
	}
	/**
	 * 删除用户接口
	 * @author chris
	 * @getParam users[] 所选删除用户
	 * @role  root 
	 */
	@Before({POST.class, RootInterceptor.class})
	@ActionKey("/api/admin/deleteUsers")
	public void deleteUsers() {
		String users = StringUtils.arrayToQueryString(getParaValues("users[]"));
		logger.info("params:{"+"users[]:"+users+"}");
		logger.info("dbs:"+dbs);
		
		//对各个数据库同时进行用户管理,修改完成恢复原数据库
		String originDb = DbSelector.getDbName();
		int deleted = 0;
		for(String db : dbs){
			DbSelector.setDbName(db);
			deleted = as.deleteByUserName(users);
		}
		DbSelector.setDbName(originDb);
		
		Map<String, String> data = new HashMap<String, String>();
		data.put("message", String.valueOf(deleted));
		logger.info("return:" + deleted);
		renderJson(data);
	}
	/**
	 * 修改用户角色接口
	 * @author chris
	 * @getParam username 用户名
	 * @getParam roles[] 所需修改角色
	 * @role  root 
	 */
	@Before({POST.class, RootInterceptor.class})
	@ActionKey("/api/admin/changeRole")
	public void changeRole(){
		String username = getPara("username");
		String[] queryRole = getParaValues("roles[]");
		logger.info("params:{"+"username:"+username+",queryRole:"+queryRole+"}");
		logger.info("dbs:"+dbs);
		
		Map<String, String> data = new HashMap<String, String>();
		
		//对各个数据库同时进行用户管理,修改完成恢复原数据库
		String originDb = DbSelector.getDbName();
		for(String db : dbs){
			DbSelector.setDbName(db);
			as.changeRoles(username, queryRole);
		}
		DbSelector.setDbName(originDb);
		
		data.put("message", "successfully");
		logger.info("data:" + data);
		renderJson(data);
	}
	
	/**
	 * 用户个人帐号权限页
	 * 从cookie中获取用户名,cookie不存在则返回空数据
	 * @author chris
	 * @role  all
	 */
	@Before(POST.class)
	@ActionKey("/api/admin/account")
	public void accountPermission(){
		Cookie cookie = getCookieObject("login");
		String username = "chris";
		if(cookie!=null){
			username = cookie.getValue();
		}
		logger.info("get username from cookie:" + "{"+username+"}");
		List<List<String>> data = new ArrayList<List<String>>();
		if("".equals(username)){
			logger.info("cookie 不存在");
			renderJson(data);
			return;
		}
		data = as.queryUsers(username);
		renderJson(data);
	}
}
