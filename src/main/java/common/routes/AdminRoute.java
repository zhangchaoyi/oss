package common.routes;

import com.jfinal.config.Routes;

import common.controllers.AdminController;
import common.controllers.DashboardController;
import common.controllers.LoginController;
import common.controllers.RealtimeController;
import common.controllers.loss.LossController;
import common.controllers.online.AnalysisController;
import common.controllers.online.CountController;
import common.controllers.online.HabitsController;
import common.controllers.operations.AnnouncementController;
import common.controllers.operations.CurrencyController;
import common.controllers.operations.FeedbackController;
import common.controllers.operations.GagOfflineController;
import common.controllers.operations.GameAccountController;
import common.controllers.operations.GmRecordController;
import common.controllers.operations.MailManagementController;
import common.controllers.operations.ObjectController;
import common.controllers.operations.PlayerInfoController;
import common.controllers.payment.PaymentBehaviorController;
import common.controllers.payment.PaymentDataController;
import common.controllers.payment.PaymentPlayersController;
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
		add("/paymentPlayers", PaymentPlayersController.class, "/WEB-INF/views/paid");
		add("/onlineAnalysis", AnalysisController.class, "/WEB-INF/views/online");
		add("/onlineHabit", HabitsController.class, "/WEB-INF/views/online");
		add("/onlineCount", CountController.class, "/WEB-INF/views/online");
		add("/loss", LossController.class, "/WEB-INF/views/loss");
		add("/admin", AdminController.class, "/WEB-INF/views/admin");
		add("/feedback", FeedbackController.class, "/WEB-INF/views/operation");
		add("/gmRecord", GmRecordController.class, "/WEB-INF/views/operation");
		add("/currencyObtainConsume", CurrencyController.class, "/WEB-INF/views/operation");
		add("/objectObtainConsume", ObjectController.class, "/WEB-INF/views/operation");
		add("/mailManagement", MailManagementController.class, "/WEB-INF/views/operation");
		add("/playerInfo", PlayerInfoController.class, "/WEB-INF/views/operation");
		add("/announcement", AnnouncementController.class, "/WEB-INF/views/operation");
		add("/gameAccount", GameAccountController.class, "/WEB-INF/views/operation");
		add("/gagOffline", GagOfflineController.class, "/WEB-INF/views/operation");
	}

}
