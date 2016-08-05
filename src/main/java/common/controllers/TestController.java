package common.controllers;

import java.util.List;

import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;

import common.model.User;


public class TestController extends Controller {

	@ActionKey("/test")
	public void test() {
		renderText("hello");
	}
	
	@ActionKey("/users")
	public void queryUsers() {
		List<User> users = User.dao.find("select * from user");
		renderJson(users);
	}
}
