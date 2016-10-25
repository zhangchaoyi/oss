package common.controllers.loss;

import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.ext.interceptor.GET;

import common.interceptor.AuthInterceptor;

@Clear(AuthInterceptor.class)
public class LossController extends Controller{
	@Before(GET.class)
	@ActionKey("/loss")
	public void analyse() {
		render("loss.html");
	}
}
