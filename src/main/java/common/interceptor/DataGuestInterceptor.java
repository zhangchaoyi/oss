package common.interceptor;

import java.util.List;

import javax.servlet.http.Cookie;

import org.apache.log4j.Logger;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;

import common.service.AdminService;
import common.service.impl.AdminServiceImpl;
/**
 * 要求用户的角色至少为data_guest
 * @author chris
 */
public class DataGuestInterceptor implements Interceptor {
	private static Logger logger = Logger.getLogger(DataGuestInterceptor.class);
	private AdminService as = new AdminServiceImpl();
	@Override
	public void intercept(Invocation invocation) {
		Controller controller = invocation.getController();
		Cookie cookie = controller.getCookieObject("login");
		Cookie serverCookie = controller.getCookieObject("server");
		String username = "";
		boolean permission = false;
		
		if(serverCookie==null){
			controller.redirect("/login");
			logger.info("serverCookie is null");
			return;
		}
		
		if (cookie != null) {
			username = cookie.getValue();
			// 获取role列表
			if (!"".equals(username)) {
				List<String> roles = as.queryRoleByUsername(username);
				permission = as.validateRolePermission(roles, "data_guest");
			}
			if(permission==true){
				invocation.invoke();
				return;
			}
		}

		String actionKey = invocation.getActionKey();
		// 接口校验不通过直接返回/login
		if (actionKey.startsWith("/api")) {
			if (cookie == null) {
				controller.redirect("/login");
				logger.info("cookie is null,permission denied");
				return;
			}
			controller.redirect("/admin/authority/error");
			return;
		}
		// 页面校验不通过需要记录来时的url再返回/login
		String queryString = controller.getRequest().getQueryString();
		String from = "/oss" + actionKey + (queryString == null ? "" : "?" + queryString);
		if(cookie==null){
			controller.redirect("/login?from="+from);
			logger.info("cookie is null,permission denied");
			return;
		}
		logger.info("cookie is not null,permission denied");
		controller.redirect("/admin/authority/error?from=" + from);
	}

}
