package common.controllers;

import org.apache.log4j.Logger;

import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;

public class TestController extends Controller {

	private static final Logger logger = Logger.getLogger(TestController.class);
	@ActionKey("/test")
	public void test() {
		logger.debug("sadfsdaf");
		render("/index.html");
	}

	@ActionKey("/users")
	public void queryUsers() {
//		List<User> users = User.dao.find("select * from user");
//		renderJson(users);
	}

//	public static void main(String args[]){
//		this.getContextClassLoader().getResource();
//	}
}
