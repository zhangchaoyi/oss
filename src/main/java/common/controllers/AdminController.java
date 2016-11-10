package common.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.ext.interceptor.GET;
import com.jfinal.ext.interceptor.POST;

import common.interceptor.RootInterceptor;
import common.service.AdminService;
import common.service.impl.AdminServiceImpl;
import common.utils.EncryptUtils;
import common.utils.StringUtils;

@Clear
public class AdminController extends Controller {
	private static Logger logger = Logger.getLogger(AdminController.class);
	private AdminService as = new AdminServiceImpl();
	
	/**
	 * 没有权限页
	 * @author chris 
	 */
	@Before(GET.class)
	@ActionKey("/admin/authority/error")
	public void authorityError() {
		render("authorityError.html");
	}
	/**
	 *  新增用户页
	 *  @author chris
	 */
	@Before({GET.class, RootInterceptor.class})
	@ActionKey("/admin/createUser")
	public void createUserIndex() {
		render("createUser.html");
	}
	/** 
	 * 管理用户页 
	 *@author chris
	 */
	@Before({GET.class, RootInterceptor.class})
	@ActionKey("/admin/manageUsers")
	public void manageUsersIndex() {
		render("userManagement.html");
	}
	/**
	 *  新增用户接口 
	 * @author chris
	 * @getParam username 用户名(加密后)
	 * @getParam password 密码(加密后)
	 * @getParam role 角色(加密后)
	 * @getParam key 加密密钥
	 * @return 返回成功/失败信息
	 */
	@Before({POST.class, RootInterceptor.class})
	@ActionKey("/api/admin/createUser")
	public void createUser() {
		String username = getPara("username");
		String password = getPara("password");
		String role = getPara("role", "data_guest");
		String key = getPara("key");
		
		Map<String, String> data = new HashMap<String, String>();
		
		try {
			username = EncryptUtils.aesDecrypt(username,key);
			password = EncryptUtils.aesDecrypt(password,key);
			role = EncryptUtils.aesDecrypt(role,key);
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug("<AdminInterceptor> Exception:", e);
		}
		
		//username 是否已经存在
		boolean exist = as.existUser(username);
		if(exist==true) {
			data.put("message", "exist");
			renderJson(data);
			return;
		}
		
		logger.debug("username:" + username + "password:" + password + "role:" + role);
		boolean succeed = as.signupUser(username, password, role);
		
		if(succeed==true){
			logger.debug("signup successfully");
			data.put("message", "successfully");
			renderJson(data);
			return;
		}
		logger.debug("createUser failed");
		data.put("message", "failed");
		renderJson(data);
	}
	/**
	 * 用户管理接口
	 * @author chris
	 * @return 返回所有用户列表
	 */
	@Before({POST.class, RootInterceptor.class})
	@ActionKey("/api/admin/manageUsers")
	public void manageUsers() {
		List<List<String>> data = as.queryAllUsers();
		logger.debug("<AdminController> manageUsers:" + data);
		renderJson(data);
	}
	/**
	 * 删除用户接口
	 * @author chris
	 * @getParam users[] 所选删除用户
	 */
	@Before({POST.class, RootInterceptor.class})
	@ActionKey("/api/admin/deleteUsers")
	public void deleteUsers() {
		String users = StringUtils.arrayToQueryString(getParaValues("users[]"));
		int deleted = as.deleteByUserName(users);
		Map<String, String> data = new HashMap<String, String>();
		data.put("message", String.valueOf(deleted));
		logger.debug("<AdminController> deleteUsers:" + deleted);
		renderJson(data);
	}
	/**
	 * 修改用户角色接口
	 * @author chris
	 * @getParam username 用户名
	 * @getParam roles[] 所需修改角色
	 */
	@Before({POST.class, RootInterceptor.class})
	@ActionKey("/api/admin/changeRole")
	public void changeRole(){
		String username = getPara("username");
		String[] queryRole = getParaValues("roles[]");
		Map<String, String> data = new HashMap<String, String>();
		as.changeRoles(username, queryRole);
		data.put("message", "successfully");
		logger.debug("<AdminController> changeRole:" + data);
		renderJson(data);
	}
}
