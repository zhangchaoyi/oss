package common.controllers.payment;

import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.ext.interceptor.GET;

import common.interceptor.AuthInterceptor;

//@Clear(AuthInterceptor.class)
@Before(AuthInterceptor.class)
public class PaymentBehaviorController extends Controller {
	@Before(GET.class)
	@ActionKey("/payment/behavior")
	public void paymentIndex() {
		render("paymentBehavior.html");
	}
}
