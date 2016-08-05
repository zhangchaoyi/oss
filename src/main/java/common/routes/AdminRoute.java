package common.routes;

import com.jfinal.config.Routes;

import common.controllers.TestController;

public class AdminRoute extends Routes {

	@Override
	public void config() {
		add("/test", TestController.class);
	}

}
