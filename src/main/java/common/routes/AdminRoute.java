package common.routes;

import com.jfinal.config.Routes;

import common.controllers.AdminController;
import common.controllers.DashboardController;
import common.controllers.LoginController;
import common.controllers.RealtimeController;
import common.controllers.loss.LossController;
import common.controllers.online.AnalysisController;
import common.controllers.online.HabitsController;
import common.controllers.payment.PaymentBehaviorController;
import common.controllers.payment.PaymentDataController;
import common.controllers.payment.PaymentRankController;
import common.controllers.payment.PaymentTransformController;
import common.controllers.players.AccdetailController;
import common.controllers.players.ActiveController;
import common.controllers.players.AddController;
import common.controllers.players.EffectiveController;
import common.controllers.players.EquipmentController;
import common.controllers.players.RetainController;

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
		add("/paymentData", PaymentDataController.class, "/WEB-INF/views/paid");
		add("/paymentBehavior", PaymentBehaviorController.class, "/WEB-INF/views/paid");
		add("/paymentTransform", PaymentTransformController.class, "/WEB-INF/views/paid");
		add("/paymentRank", PaymentRankController.class, "/WEB-INF/views/paid");
		add("/onlineAnalysis", AnalysisController.class, "/WEB-INF/views/online");
		add("/onlineHabit", HabitsController.class, "/WEB-INF/views/online");
		add("/loss", LossController.class, "/WEB-INF/views/loss");
		add("/admin", AdminController.class, "/WEB-INF/views/admin");
	}

}
