package common.config;

import java.util.Set;

import org.apache.log4j.Logger;

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
import common.interceptor.GmInterceptor;
import common.interceptor.RootInterceptor;
import common.interceptor.VipInterceptor;
import common.model.ActiveUser;
import common.model.CreateRole;
import common.model.DeviceInfo;
import common.model.GmRecord;
import common.model.LevelUp;
import common.model.LogCharge;
import common.model.LogGold;
import common.model.LogRmb;
import common.model.Login;
import common.model.Logout;
import common.model.LossUser;
import common.model.OnlineCount;
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
import common.utils.Contants;

public class BaseConfig extends JFinalConfig {
	private static Logger logger = Logger.getLogger(BaseConfig.class);

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
	/**
	 * 配置多个数据源,每个数据源对应一个C3p0Plugin和ActiveRecordPlugin实例
	 * @author chris
	 */
	public void configPlugin(Plugins me) {
		PropKit.use("config.txt");
		Contants.initPropMap();
		DbSelector.initDbs();
		Set<String> dbs = DbSelector.getDbs().keySet();
		try {
			for (String db : dbs) {
				String key = db + "Url";
				String jdbc = PropKit.get(key);
				logger.info("jdbcUrl:" + jdbc);
				if (jdbc == null) {
					logger.error("------------config.txt prop db wrong, mysql source error-------");
					return;
				}
				C3p0Plugin cp = new C3p0Plugin(jdbc, PropKit.get("user"), PropKit.get("password").trim());
				me.add(cp);
				// arp 实例的名称为 后续切换数据源的名称
				ActiveRecordPlugin arp = new ActiveRecordPlugin(db, cp);
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
				arp.addMapping("user_feedback", UserFeedback.class);
				arp.addMapping("gm_record", GmRecord.class);
				arp.addMapping("online_count", OnlineCount.class);
				arp.addMapping("log_gold", LogGold.class);
				arp.addMapping("log_RMB", LogRmb.class);
				
				// 设置默认的数据库
				DbSelector.setDbName(PropKit.get("jdbcDefault"));
			}
		} catch (Exception e) {
			logger.error("BaseConfigError", e);
		}

	}

	@Override
	public void configInterceptor(Interceptors me) {
		me.addGlobalActionInterceptor(new AdminInterceptor());
		me.addGlobalActionInterceptor(new VipInterceptor());
		me.addGlobalActionInterceptor(new DataGuestInterceptor());
		me.addGlobalActionInterceptor(new RootInterceptor());
		me.addGlobalActionInterceptor(new GmInterceptor());
	}

	@Override
	public void configHandler(Handlers me) {

	}

	//用于jfinal Generator 中重新生成表model
	public static C3p0Plugin createC3p0Plugin() {
		PropKit.use("config.txt");
		return new C3p0Plugin(PropKit.get("malaiUrl"), PropKit.get("user"), PropKit.get("password").trim());
	}
}
