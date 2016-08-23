package common.routes;

import com.jfinal.config.Routes;

import common.controllers.DashboardController;
import common.controllers.LoginController;
import common.controllers.TestController;
import common.controllers.players.EffectiveController;

public class AdminRoute extends Routes {

	@Override
	public void config() {
		add("/test", TestController.class);
		add("/", LoginController.class, "/views");
		add("/dashboard", DashboardController.class, "/views");
		add("/effective", EffectiveController.class ,"/views");
	}

}
