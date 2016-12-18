package common.controllers.operations;

import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.ext.interceptor.GET;

import common.interceptor.GmInterceptor;

@Clear
public class MailManagementController extends Controller {
	/**
	 * 邮件管理页
	 * @author chris
	 */
	@Before({GET.class,GmInterceptor.class})
	@ActionKey("/operation/mailManagement")
	public void objectIndex() {
		render("mailManagement.html");
	}
}
