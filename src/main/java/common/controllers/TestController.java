package common.controllers;

import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;

public class TestController extends Controller {

	@ActionKey("/test")
	public void test() {
		render("/index.html");
	}

	@ActionKey("/users")
	public void queryUsers() {
//		List<User> users = User.dao.find("select * from user");
//		renderJson(users);
	}
}
