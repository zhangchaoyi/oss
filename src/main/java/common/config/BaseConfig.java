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
		C3p0Plugin cp = createC3p0Plugin();
		me.add(cp);
		ActiveRecordPlugin arp = new ActiveRecordPlugin(cp);
		me.add(arp);
		arp.addMapping("create_role", CreateRole.class);
		arp.addMapping("device_info", DeviceInfo.class);
		arp.addMapping("logout", Logout.class);
		arp.addMapping("login", Login.class);
		arp.addMapping("active_user", ActiveUser.class);
		arp.addMapping("log_charge", LogCharge.class);
		arp.addMapping("level_up", LevelUp.class);
		arp.addMapping("retain_user", RetainUser.class);
		arp.addMapping("retain_equipment", RetainEquipment.class);
		arp.addMapping("payment_detail", PaymentDetail.class);
		arp.addMapping("loss_user", LossUser.class);
		arp.addMapping("return_user", ReturnUser.class);
		arp.addMapping("sec_role", "role_id", SecRole.class);
		arp.addMapping("sec_user", "user_id", SecUser.class);
		arp.addMapping("sec_user_role", SecUserRole.class);
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
