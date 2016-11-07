package common.controllers.players;

import java.util.Map;

import org.apache.log4j.Logger;

import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.ext.interceptor.GET;
import com.jfinal.ext.interceptor.POST;
import common.interceptor.DataGuestInterceptor;
import common.service.AccdetailService;
import common.service.impl.AccdetailServiceImpl;

@Clear
public class AccdetailController extends Controller{
	private static Logger logger = Logger.getLogger(AccdetailController.class);
	private AccdetailService accdetailService = new AccdetailServiceImpl();
	
	@Before({GET.class, DataGuestInterceptor.class})
	@ActionKey("/players/accdetail")
	public void activePlayer() {
		render("accdetail.html");
	}
	
	@Before({POST.class, DataGuestInterceptor.class})
	@ActionKey("/api/players/accdetail")
	public void queryActivePlayer() {
		String accountId = getPara("accountId","");
		Map<String, Object> data = accdetailService.queryAccdetail(accountId);
		logger.debug("<AccdetailController> queryActivePlayer:" + data);
		renderJson(data);
	}
}
