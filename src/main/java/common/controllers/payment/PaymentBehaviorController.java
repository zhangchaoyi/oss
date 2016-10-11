package common.controllers.payment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.ext.interceptor.GET;
import com.jfinal.ext.interceptor.POST;

import common.interceptor.AuthInterceptor;
import common.service.PaymentBehaviorService;
import common.service.impl.PaymentBehaviorServiceImpl;
import common.utils.StringUtils;

@Clear(AuthInterceptor.class)
//@Before(AuthInterceptor.class)
public class PaymentBehaviorController extends Controller {
	private PaymentBehaviorService paymentBehaviorService = new PaymentBehaviorServiceImpl();
	
	@Before(GET.class)
	@ActionKey("/payment/behavior")
	public void paymentIndex() {
		render("paymentBehavior.html");
	}
	
	@Before(POST.class)
	@ActionKey("/api/payment/behavior/rank")
	public void queryPaymentBehaviorMoney() {
		String tag = getPara("tag", "rank-paymentBehavior-money");
		String icons = StringUtils.arrayToQueryString(getParaValues("icon[]"));
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		
		Map<String, Object> data = new LinkedHashMap<String, Object>();
		Map<String, Object> category = new LinkedHashMap<String, Object>();
		Map<String, Object> seriesMap = new LinkedHashMap<String, Object>();
		List<String> header = new ArrayList<String>();
		
		switch(tag){
		case "rank-paymentBehavior-money":
			Map<String, Object> rankMoney = paymentBehaviorService.queryRankMoney(icons, startDate, endDate);
			category.put("付费等级", rankMoney.get("level"));
			seriesMap.put("付费金额($)", rankMoney.get("revenue"));
			header.addAll(Arrays.asList("付费等级", "付费金额($)"));
			break;
		case "rank-paymentBehavior-times":
			Map<String, Object> rankTimes = paymentBehaviorService.queryRankTimes(icons, startDate, endDate);
			category.put("付费等级", rankTimes.get("level"));
			seriesMap.put("付费次数", rankTimes.get("count"));
			header.addAll(Arrays.asList("付费等级", "付费次数"));
			break;
		}
		
		Set<String> type = seriesMap.keySet();
		data.put("type", type.toArray());
		data.put("header", header);
		data.put("category", category);
		data.put("data", seriesMap);
		renderJson(data);
	}
}
