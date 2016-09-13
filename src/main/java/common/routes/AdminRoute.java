package common.routes;

import com.jfinal.aop.Before;
import com.jfinal.config.Routes;

import common.controllers.DashboardController;
import common.controllers.LoginController;
import common.controllers.RealtimeController;
import common.controllers.players.AccdetailController;
import common.controllers.players.ActiveController;
import common.controllers.players.AddController;
import common.controllers.players.EffectiveController;
import common.controllers.players.EquipmentController;
import common.controllers.players.RetainController;
import common.interceptor.AuthInterceptor;

//@Clear(AuthInterceptor.class)
@Before(AuthInterceptor.class)
public class AdminRoute extends Routes {

	@Override
	public void config() {
		add("/login", LoginController.class, "/WEB-INF/views");
		add("/dashboard", DashboardController.class, "/WEB-INF/views");
		add("/effective", EffectiveController.class,"/WEB-INF/views/players");
		add("/retain", RetainController.class,"/WEB-INF/views/players");
		add("/active", ActiveController.class,"/WEB-INF/views/players");
		add("/add", AddController.class,"/WEB-INF/views/players");
		add("/equipment", EquipmentController.class, "/WEB-INF/views/players");
		add("/accdetail", AccdetailController.class, "/WEB-INF/views/players");
		add("/realtime", RealtimeController.class, "/WEB-INF/views");
	}

}
