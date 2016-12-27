package common.controllers.operations;

import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.ext.interceptor.GET;
import common.interceptor.GmInterceptor;

@Clear
public class PaidRecoverController extends Controller {
	/**
	 * 处理异常订单页面
	 * @author chris
	 */
	@Before({GET.class, GmInterceptor.class})
	@ActionKey("/operation/paidRecover")
	public void paidRecover() {
		render("paid-recover.html");
	}
}
