package common.routes;

import com.jfinal.config.Routes;

import common.controllers.DashboardController;
import common.controllers.LoginController;
import common.controllers.TestController;
import common.controllers.players.ActiveController;
import common.controllers.players.EffectiveController;
import common.controllers.players.RetainController;

public class AdminRoute extends Routes {

	@Override
	public void config() {
		add("/test", TestController.class);
		add("/", LoginController.class, "/views");
		add("/dashboard", DashboardController.class, "/views");
		add("/effective", EffectiveController.class);
		add("/retain", RetainController.class);
		add("/active", ActiveController.class);
	}

}
