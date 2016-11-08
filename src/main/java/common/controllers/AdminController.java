package common.controllers;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.ext.interceptor.GET;
import com.jfinal.ext.interceptor.POST;

import common.interceptor.AdminInterceptor;
import common.service.AdminService;
import common.service.impl.AdminServiceImpl;
import common.utils.EncryptUtils;

@Clear
public class AdminController extends Controller {
	private static Logger logger = Logger.getLogger(AdminController.class);
	private AdminService as = new AdminServiceImpl();
	
	@Before(GET.class)
	@ActionKey("/admin/authority/error")
	public void authorityError() {
		render("authorityError.html");
	}
	
	@Before({GET.class, AdminInterceptor.class})
	@ActionKey("/admin/createUser")
	public void createUser() {
		render("createUser.html");
	}
	
	@Before({POST.class, AdminInterceptor.class})
	@ActionKey("/api/admin/createUser")
	public void loginValidate() {
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
}
