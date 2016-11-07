package common.controllers;

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

@Clear
public class AdminController extends Controller {
	private static Logger logger = Logger.getLogger(AdminController.class);
	private AdminService as = new AdminServiceImpl();
	
	@Before(GET.class)
	@ActionKey("/admin/authority/error")
	public void authorityError() {
		render("authorityError.html");
	}
	
	@Before(GET.class)
	@ActionKey("/admin/createUser")
	public void createUser() {
		render("createUser.html");
	}
	
	@Before({POST.class, AdminInterceptor.class})
	@ActionKey("/api/admin/signup")
	public void loginValidate() {
		String username = getPara("username");
		String password = getPara("password");
		String role = getPara("role", "data_guest");
		
		logger.debug("username:" + username + "password:" + password);
		boolean succeed = as.signupUser(username, password, role);
		
		if(succeed==true){
			logger.debug("signup successfully");
			renderJson("{\"message\":\"successfully\"}");
			return;
		}
		logger.debug("signup failed");
		renderJson("{\"message\":\"fail\"}");
	}
}
