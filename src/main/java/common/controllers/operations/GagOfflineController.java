package common.controllers.operations;

import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.ext.interceptor.GET;

import common.interceptor.GmInterceptor;

@Clear
public class GagOfflineController extends Controller {
	/**
	 * 禁言强制下线管理
	 * @author chris
	 * @role 所有
	 */
	@Before({GET.class, GmInterceptor.class})
	@ActionKey("/operation/gagOffline")
	public void gagOffline() {
		render("gag-offline.html");
	}
}
