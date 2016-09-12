package common.controllers.players;

import java.util.Map;

import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.ext.interceptor.GET;
import com.jfinal.ext.interceptor.POST;

import common.interceptor.AuthInterceptor;
import common.service.AccdetailService;
import common.service.impl.AccdetailServiceImpl;

//@Clear(AuthInterceptor.class)
@Before(AuthInterceptor.class)
public class AccdetailController extends Controller{
	private AccdetailService accdetailService = new AccdetailServiceImpl();
	
	@Before(GET.class)
	@ActionKey("/players/accdetail")
	public void activePlayer() {
		render("accdetail.html");
	}
	
	@Before(POST.class)
	@ActionKey("/api/players/accdetail")
	public void queryActivePlayer() {
		String accountId = getPara("accountId","");
		Map<String, Object> data = accdetailService.queryAccdetail(accountId);
		renderJson(data);
	}
}
