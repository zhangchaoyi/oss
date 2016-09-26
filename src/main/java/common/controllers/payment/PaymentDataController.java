package common.controllers.payment;

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
import common.service.PaymentDataService;
import common.service.impl.PaymentDataServiceImpl;
import common.utils.DateUtils;
import common.utils.StringUtils;

@Clear(AuthInterceptor.class)
public class PaymentDataController extends Controller{
	private PaymentDataService paymentDataService = new PaymentDataServiceImpl();
	
	@Before(GET.class)
	@ActionKey("/payment/data")
	public void addIndex() {
		render("payment.html");
	}
	
	@Before(POST.class)
	@ActionKey("/api/payment/data")
	public void queryPaymentData() {
		String tag = getPara("tag", "data-payment-money");
		String icons = StringUtils.arrayToQueryString(getParaValues("icon[]"));
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		
		Map<String, Object> data = new LinkedHashMap<String, Object>();
		Map<String, List<String>> category = new LinkedHashMap<String, List<String>>();
		Map<String, Object> seriesMap = new LinkedHashMap<String, Object>();
		Map<String, Object> sum = new LinkedHashMap<String, Object>();
		Map<String, Map<String,Object>> recData = new LinkedHashMap<String, Map<String,Object>>();
		
		List<String> categories = DateUtils.getDateList(startDate, endDate);
		switch(tag){
		case "data-payment-money":
			recData = paymentDataService.queryMoneyPayment(categories, startDate, endDate, icons);
			sum = recData.get("sum");
			seriesMap = recData.get("series");
			break;
		case "data-payment-people":
			recData = paymentDataService.queryPeoplePayment(categories, startDate, endDate, icons);
			sum = recData.get("sum");
			seriesMap = recData.get("series");
			break;
		case "data-payment-times":
			recData = paymentDataService.queryNumPayment(categories, startDate, endDate, icons);
			sum = recData.get("sum");
			seriesMap = recData.get("series");
			break;
		}
		
		Set<String> type = seriesMap.keySet();

		category.put("日期", categories);
		
		data.put("sum", sum);
		data.put("type", type.toArray());
		data.put("category", category);
		data.put("data", seriesMap);
		renderJson(data);
	}
}
