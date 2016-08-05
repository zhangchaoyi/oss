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

import common.model.User;
import common.routes.AdminRoute;

public class BaseConfig extends JFinalConfig {

	@Override
	public void configConstant(Constants me) {

	}

	@Override
	public void configRoute(Routes me) {
		me.add(new AdminRoute());
	}

	@Override
	public void configPlugin(Plugins me) {		
		C3p0Plugin cp = createC3p0Plugin();
		me.add(cp);
		ActiveRecordPlugin arp = new ActiveRecordPlugin(cp);
		me.add(arp);
		arp.addMapping("user", User.class);
	}

	@Override
	public void configInterceptor(Interceptors me) {

	}

	@Override
	public void configHandler(Handlers me) {

	}

	public static C3p0Plugin createC3p0Plugin() {
		PropKit.use("mysql.txt");
		return new C3p0Plugin(PropKit.get("jdbcUrl"), PropKit.get("user"), PropKit.get("password").trim());
	}

}
