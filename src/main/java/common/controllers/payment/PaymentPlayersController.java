package common.controllers.payment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.ext.interceptor.GET;
import com.jfinal.ext.interceptor.POST;

import common.interceptor.VipInterceptor;
import common.service.PaymentPlayersService;
import common.service.impl.PaymentPlayersServiceImpl;
import common.utils.StringUtils;

@Clear
public class PaymentPlayersController extends Controller {
	private static Logger logger = Logger.getLogger(PaymentPlayersController.class);
	private PaymentPlayersService pps = new PaymentPlayersServiceImpl();
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
	
	/**
	 * 付费玩家列表
	 * @author chris
	 * @role vip
	 */
	@Before(POST.class)
	@ActionKey("/api/payment/players")
	public void queryPlayersList(){
		String icons = StringUtils.arrayToQueryString(getParaValues("icon[]"));
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		logger.info("params:{"+"icons:"+icons+",startDate:"+startDate+",endDate:"+endDate+"}");
		
		List<List<String>> tableData = pps.queryPLayersList(startDate, endDate, icons);
		Map<String,Object> data = new HashMap<String, Object>();
		data.put("tableData", tableData);
		renderJson(data);
	}
	
	/**
	 * 根据account查询付费玩家记录
	 * @author chris
	 * @role vip
	 */
	@Before(POST.class)
	@ActionKey("/api/payment/player")
	public void queryPlayerByAccount(){
		String account = getPara("account", "");
		logger.info("params:{"+"account:"+account+"}");
		List<List<String>> tableData = pps.queryPlayerByAccount(account);
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("tableData", tableData);
		renderJson(data);
	}
	
}
