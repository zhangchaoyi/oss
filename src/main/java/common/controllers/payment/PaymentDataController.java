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
	
	@Before(POST.class)
	@ActionKey("/api/payment/data/table")
	public void queryPaymentDataTable() {
		String icons = StringUtils.arrayToQueryString(getParaValues("icon[]"));
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		List<String> categories = DateUtils.getDateList(startDate, endDate);
		List<List<Object>> paymentDetail = paymentDataService.queryDataPayment(categories, startDate, endDate, icons);
		renderJson(paymentDetail);
	}
	
	@Before(POST.class)
	@ActionKey("/api/payment/analyze")
	public void queryAnalyzePayment() {
		String icons = StringUtils.arrayToQueryString(getParaValues("icon[]"));
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		String tag = getPara("tag");
		String subTag = getPara("subTag");
		
		Map<String, Object> data = new LinkedHashMap<String, Object>();
		Map<String, List<String>> category = new LinkedHashMap<String, List<String>>();
		Map<String, Object> seriesMap = new LinkedHashMap<String, Object>();
		List<String> categories = new ArrayList<String>();
		switch(tag){
		case "analyze-payment-money":
			switch(subTag){
			case "day":
				categories = Arrays.asList("1","2","3","4","5","6~10","11~50","51~100","101~500","501~1000","1001~2000",">2000");
				category.put("付费金额区间", categories);
				List<Integer> queryPeriod = paymentDataService.queryDayPaymentMoney(categories, icons, startDate, endDate);
				seriesMap.put("人数", queryPeriod);
				break;
			case "week":
				categories = Arrays.asList("1","2","3","4","5","6~10","11~50","51~100","101~500","501~1000","1001~2000",">2000");
				category.put("付费金额区间", categories);
				seriesMap.put("人数", Arrays.asList(1,1,1,1,1,1,1,1,1,1,1,1));
				break;
			case "month":
				categories = Arrays.asList("1","2","3","4","5","6~10","11~50","51~100","101~500","501~1000","1001~2000",">2000");
				category.put("付费金额区间", categories);
				seriesMap.put("人数", Arrays.asList(1,1,1,1,1,1,1,1,1,1,1,1));
				break;
			}
			break;
		case "analyze-payment-times":
			switch(subTag){
			case "day":
				categories = Arrays.asList("1","2","3","4","5","6","7","8","9","10","11~20","21~30","31~40","41~50","51~100",">100");
				category.put("付费次数区间", categories);
				List<Integer> queryPeriod = paymentDataService.queryDayPaymentTimes(categories, icons, startDate, endDate);
				seriesMap.put("人数", queryPeriod);
				break;
			case "week":
				categories = Arrays.asList("1","2","3","4","5","6","7","8","9","10","11~20","21~30","31~40","41~50","51~100",">100");
				category.put("付费次数区间", categories);
				seriesMap.put("人数", Arrays.asList(1,1,1,1,1,1,1,1,1,1,1,1));
				break;
			case "month":
				categories = Arrays.asList("1","2","3","4","5","6","7","8","9","10","11~20","21~30","31~40","41~50","51~100",">100");
				category.put("付费次数区间", categories);
				seriesMap.put("人数", Arrays.asList(1,1,1,1,1,1,1,1,1,1,1,1));
				break;
			}
			break;
		case "analyze-payment-arpu":
			switch(subTag){
			case "ARPU-D":
				break;
			case "ARPU-M":
				break;
			case "ARPPU-D":
				break;
			case "ARPPU-M":
				break;
			}
			break;
		}
		
		Set<String> type = seriesMap.keySet();
		data.put("type", type.toArray());
		data.put("category", category);
		data.put("data", seriesMap);
		renderJson(data);
	}
	
}
