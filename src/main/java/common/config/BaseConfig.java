package common.config;

import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.c3p0.C3p0Plugin;

import common.controllers.IndexController;
import common.interceptor.AdminInterceptor;
import common.interceptor.DataGuestInterceptor;
import common.interceptor.RootInterceptor;
import common.interceptor.VipInterceptor;
import common.model.ActiveUser;
import common.model.CreateRole;
import common.model.DeviceInfo;
import common.model.LevelUp;
import common.model.LogCharge;
import common.model.Login;
import common.model.Logout;
import common.model.LossUser;
import common.model.PaymentDetail;
import common.model.RetainEquipment;
import common.model.RetainUser;
import common.model.ReturnUser;
import common.model.SecRole;
import common.model.SecUser;
import common.model.SecUserRole;
import common.model.UserFeedback;
import common.mysql.DbSelector;
import common.routes.AdminRoute;

public class BaseConfig extends JFinalConfig {

	@Override
	public void configConstant(Constants me) {
		me.setEncoding("UTF-8");
	}

	@Override
	public void configRoute(Routes me) {
		me.add("/", IndexController.class, "/WEB-INF/views");
		me.add(new AdminRoute());
	}

	@Override
	public void configPlugin(Plugins me) {
		PropKit.use("config.txt");

		C3p0Plugin malaiCp = new C3p0Plugin(PropKit.get("jdbcUrl"), PropKit.get("user"),
				PropKit.get("password").trim());
		me.add(malaiCp);
		ActiveRecordPlugin malaiArp = new ActiveRecordPlugin("malai", malaiCp);
		me.add(malaiArp);
		malaiArp.addMapping("create_role", CreateRole.class);
		malaiArp.addMapping("device_info", DeviceInfo.class);
		malaiArp.addMapping("logout", Logout.class);
		malaiArp.addMapping("login", Login.class);
		malaiArp.addMapping("active_user", ActiveUser.class);
		malaiArp.addMapping("log_charge", LogCharge.class);
		malaiArp.addMapping("level_up", LevelUp.class);
		malaiArp.addMapping("retain_user", RetainUser.class);
		malaiArp.addMapping("retain_equipment", RetainEquipment.class);
		malaiArp.addMapping("payment_detail", PaymentDetail.class);
		malaiArp.addMapping("loss_user", LossUser.class);
		malaiArp.addMapping("return_user", ReturnUser.class);
		malaiArp.addMapping("sec_role", "role_id", SecRole.class);
		malaiArp.addMapping("sec_user", "user_id", SecUser.class);
		malaiArp.addMapping("sec_user_role", SecUserRole.class);
		malaiArp.addMapping("user_feedback", UserFeedback.class);

		C3p0Plugin ucCp = new C3p0Plugin(PropKit.get("jdbcUcUrl"), PropKit.get("user"), PropKit.get("password").trim());
		me.add(ucCp);
		ActiveRecordPlugin ucArp = new ActiveRecordPlugin("uc", ucCp);
		me.add(ucArp);
		ucArp.addMapping("create_role", CreateRole.class);
		ucArp.addMapping("device_info", DeviceInfo.class);
		ucArp.addMapping("logout", Logout.class);
		ucArp.addMapping("login", Login.class);
		ucArp.addMapping("active_user", ActiveUser.class);
		ucArp.addMapping("log_charge", LogCharge.class);
		ucArp.addMapping("level_up", LevelUp.class);
		ucArp.addMapping("retain_user", RetainUser.class);
		ucArp.addMapping("retain_equipment", RetainEquipment.class);
		ucArp.addMapping("payment_detail", PaymentDetail.class);
		ucArp.addMapping("loss_user", LossUser.class);
		ucArp.addMapping("return_user", ReturnUser.class);
		ucArp.addMapping("sec_role", "role_id", SecRole.class);
		ucArp.addMapping("sec_user", "user_id", SecUser.class);
		ucArp.addMapping("sec_user_role", SecUserRole.class);
		ucArp.addMapping("user_feedback", UserFeedback.class);
		
		DbSelector.setDbName("malai");
	}

	@Override
	public void configInterceptor(Interceptors me) {
		me.addGlobalActionInterceptor(new AdminInterceptor());
		me.addGlobalActionInterceptor(new VipInterceptor());
		me.addGlobalActionInterceptor(new DataGuestInterceptor());
		me.addGlobalActionInterceptor(new RootInterceptor());
	}

	@Override
	public void configHandler(Handlers me) {

	}

	public static C3p0Plugin createC3p0Plugin() {
		PropKit.use("config.txt");
		return new C3p0Plugin(PropKit.get("jdbcUrl"), PropKit.get("user"), PropKit.get("password").trim());
	}
}
