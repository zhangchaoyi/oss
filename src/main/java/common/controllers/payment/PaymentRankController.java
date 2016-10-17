package common.controllers.payment;

import java.util.ArrayList;
import java.util.HashMap;
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
import common.service.PaymentRankService;
import common.service.impl.PaymentRankServiceImpl;
import common.utils.DateUtils;
import common.utils.StringUtils;

@Clear(AuthInterceptor.class)
//@Before(AuthInterceptor.class)
public class PaymentRankController extends Controller{
	private PaymentRankService paymentRankService = new PaymentRankServiceImpl();
	
	
	@Before(GET.class)
	@ActionKey("/payment/rank")
	public void paymentIndex() {
		render("paymentRank.html");
	}
	
	@Before(POST.class)
	@ActionKey("/api/payment/rank/players")
	public void queryPaymentRank() {
		String icons = StringUtils.arrayToQueryString(getParaValues("icon[]"));
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		
		Map<String, Object> data = new LinkedHashMap<String, Object>();
		List<String> header = new ArrayList<String>();
		
		List<List<String>> queryData = paymentRankService.queryRank(icons, startDate, endDate);
		
		data.put("data", queryData);
		renderJson(data);
	}
	
	@Before(POST.class)
	@ActionKey("/api/payment/rank/account/detail")
	public void queryPaymentAccount() {
		String account = getPara("account");
		String icons = StringUtils.arrayToQueryString(getParaValues("icon[]"));
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		List<String> categories = DateUtils.getDateList(startDate, endDate);
		String[] accountArray = new String[] {account}; 
		
		
		Map<String, Object> category = new LinkedHashMap<String, Object>();
		Map<String, Object> seriesMap = new LinkedHashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();
		
		Map<String, Object> queryData = paymentRankService.queryAccountDetail(accountArray, categories, icons, startDate, endDate);
		seriesMap.put("在线时长", queryData.get("oTList"));
		seriesMap.put("登录次数", queryData.get("lTList"));
		seriesMap.put("付费金额", queryData.get("pRList"));
		category.put("日期", categories);
		
		Set<String> type = seriesMap.keySet();
		data.put("type", type.toArray());
		data.put("category", category);
		data.put("data", seriesMap);
		data.put("tableData", queryData.get("tableData"));
		renderJson(data);
	}
}
