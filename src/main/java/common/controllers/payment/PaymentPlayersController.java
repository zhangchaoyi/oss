package common.controllers.payment;

import org.apache.log4j.Logger;

import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.ext.interceptor.GET;
import common.interceptor.VipInterceptor;

@Clear
public class PaymentPlayersController extends Controller {
	private static Logger logger = Logger.getLogger(PaymentPlayersController.class);
	
	/**
	 * 付费玩家情况
	 * @author chris
	 * @role vip
	 */
	@Before({GET.class, VipInterceptor.class})
	@ActionKey("/payment/players")
	public void paymentPlayersIndex(){
		render("payment-players.html");
	}
	
	
}
