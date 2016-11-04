package common.interceptor;

import javax.servlet.http.Cookie;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;

public class AuthInterceptor implements Interceptor {

	public void intercept(Invocation invocation) {
		Controller controller = invocation.getController();
//		Boolean loginUser = controller.getSessionAttr("login_flag");
		Cookie cookie = controller.getCookieObject("login");
		String cookieValue = "";
		if(cookie!=null){
			cookieValue = cookie.getValue();
		}
		if (cookieValue!=null && cookieValue!=""){
			invocation.invoke();
		}else{		
			String actionKey = invocation.getActionKey();
			if(actionKey.startsWith("/api")){
				controller.redirect("/login");
				return;
			}

			String queryString = controller.getRequest().getQueryString();
			String from = "/oss" + actionKey + (queryString==null? "":"?"+queryString);
			
			controller.redirect("/login?from="+from);
		}
	}
}
