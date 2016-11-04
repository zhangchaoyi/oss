package common.controllers.payment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.ext.interceptor.GET;
import com.jfinal.ext.interceptor.POST;
import common.model.LogCharge;
import common.service.PaymentDataService;
import common.service.impl.PaymentDataServiceImpl;
import common.utils.DateUtils;
import common.utils.StringUtils;
import common.interceptor.AdminInterceptor;

@Clear
public class PaymentDataController extends Controller{
	private static Logger logger = Logger.getLogger(PaymentDataController.class);
	private PaymentDataService paymentDataService = new PaymentDataServiceImpl();
	
	@Before({GET.class, AdminInterceptor.class})
	@ActionKey("/payment/data")
	public void paymentIndex() {
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
		logger.debug("<PaymentDataController> queryPaymentData:" + data);
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
		logger.debug("<PaymentDataController> queryPaymentDataTable:" + paymentDetail);
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
		data.put("chartType", "bar");
		switch(tag){
		case "analyze-payment-money":
			switch(subTag){
			case "day":
				categories = Arrays.asList("1","2","3","4","5","6~10","11~50","51~100","101~500","501~1000","1001~2000",">2000");
				List<Integer> queryPeriod = paymentDataService.queryDayPaymentMoney(categories, icons, startDate, endDate);
				categories = Arrays.asList("1($)","2($)","3($)","4($)","5($)","6~10($)","11~50($)","51~100($)","101~500($)","501~1000($)","1001~2000($)",">2000($)");
				category.put("付费金额区间", categories);
				seriesMap.put("人数", queryPeriod);
				break;
			case "week":
				categories = Arrays.asList("1($)","2($)","3($)","4($)","5($)","6~10($)","11~50($)","51~100($)","101~500($)","501~1000($)","1001~2000($)",">2000($)");
				category.put("付费金额区间", categories);
				seriesMap.put("人数", Arrays.asList(0,0,0,0,0,0,0,0,0,0,0,0));
				break;
			case "month":
				categories = Arrays.asList("1($)","2($)","3($)","4($)","5($)","6~10($)","11~50($)","51~100($)","101~500($)","501~1000($)","1001~2000($)",">2000($)");
				category.put("付费金额区间", categories);
				seriesMap.put("人数", Arrays.asList(0,0,0,0,0,0,0,0,0,0,0,0));
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
				seriesMap.put("人数", Arrays.asList(0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0));
				break;
			case "month":
				categories = Arrays.asList("1","2","3","4","5","6","7","8","9","10","11~20","21~30","31~40","41~50","51~100",">100");
				category.put("付费次数区间", categories);
				seriesMap.put("人数", Arrays.asList(0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0));
				break;
			}
			break;
		case "analyze-payment-arpu":
			data.put("chartType", "line");
			categories = DateUtils.getDateList(startDate, endDate);
			category.put("日期", categories);
			switch(subTag){
			case "ARPU-D":
				List<Double> AD = paymentDataService.queryDayARPU(categories, icons, startDate, endDate);
				seriesMap.put("ARPU(日)", AD);
				break;
			case "ARPU-M":
				List<Double> list = new ArrayList<Double>();
				for(String date:categories){
					list.add(0.0);
				}
				seriesMap.put("ARPU(月)",list);
				break;
			case "ARPPU-D":
				List<Double> ARD = paymentDataService.queryDayARPPU(categories, icons, startDate, endDate);
				seriesMap.put("ARPPU(日)", ARD);
				break;
			case "ARPPU-M":
				List<Double> lista = new ArrayList<Double>();
				for(String date:categories){
					lista.add(0.0);
				}
				seriesMap.put("ARPPU(月)",lista);
				break;
			}
			break;
		}
		
		Set<String> type = seriesMap.keySet();
		data.put("type", type.toArray());
		data.put("category", category);
		data.put("data", seriesMap);
		logger.debug("<PaymentDataController> queryAnalyzePayment:" + data);
		renderJson(data);
	}
	@Before(POST.class)
	@ActionKey("/api/payment/analyze/table")
	public void queryPaymentAnalyzeTable() {
		String icons = StringUtils.arrayToQueryString(getParaValues("icon[]"));
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		String tag = getPara("tag");
		String subTag = getPara("subTag","");
		List<String> categories = new ArrayList<String>();
		List<List<Object>> queryData = new ArrayList<List<Object>>();
		List<String> header = new ArrayList<String>();
		Map<String, Object> data = new HashMap<String, Object>();
		
		switch(tag){
		case "analyze-payment-money":
			header.addAll(Arrays.asList("付费金额区间","每日付费玩家","每周付费玩家","每月付费玩家"));
			categories = Arrays.asList("1","2","3","4","5","6~10","11~50","51~100","101~500","501~1000","1001~2000",">2000");
			queryData = paymentDataService.queryAllPaymentMoney(categories, icons, startDate, endDate);
			break;
		case "analyze-payment-times":
			header.addAll(Arrays.asList("付费次数区间","每日付费玩家","每周付费玩家","每月付费玩家"));
			categories = Arrays.asList("1","2","3","4","5","6","7","8","9","10","11~20","21~30","31~40","41~50","51~100",">100");
			queryData = paymentDataService.queryAllPaymentTimes(categories, icons, startDate, endDate);
			break;
		case "analyze-payment-arpu":
			header.addAll(Arrays.asList("日期",subTag));
			categories = DateUtils.getDateList(startDate, endDate);
			switch(subTag){
			case "ARPU-D":
				queryData = paymentDataService.queryArpu(categories, icons, startDate, endDate);
				break;
			case "ARPU-M":
				List<List<Object>> d = new ArrayList<List<Object>>();
				for(String date : categories){
					List<Object> dd = new ArrayList<Object>();
					dd.add(date);
					dd.add(0);
					d.add(dd);
				}
				queryData = d;
				break;
			case "ARPPU-D":
				queryData = paymentDataService.queryArppu(categories, icons, startDate, endDate);
				break;
			case "ARPPU-M":
				List<List<Object>> e = new ArrayList<List<Object>>();
				for(String date : categories){
					List<Object> dd = new ArrayList<Object>();
					dd.add(date);
					dd.add(0);
					e.add(dd);
				}
				queryData = e;
				break;
			}
			break;
		}
		data.put("header", header);
		data.put("data", queryData);
		logger.debug("<PaymentDataController> queryPaymentAnalyzeTable:" + data);
		renderJson(data);
	}
	
	@Before(POST.class)
	@ActionKey("/api/payment/detail")
	public void queryDetailTable(){
		String icons = StringUtils.arrayToQueryString(getParaValues("icon[]"));
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		String tag = getPara("tag");
		String subTag = getPara("subTag","");
		List<String> header = new ArrayList<String>();
		Map<String, Object> category = new LinkedHashMap<String, Object>();
		Map<String, Object> seriesMap = new LinkedHashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();
		
		switch(tag){
		case "area":
			switch(subTag){
			case "revenue":
				List<String> provinces = new ArrayList<String>();
				List<Double> revenue = new ArrayList<Double>();
				List<LogCharge> logCharge = paymentDataService.queryAreaRevenue(icons, startDate, endDate);
				for(LogCharge lc : logCharge){
					provinces.add(lc.getStr("province"));
					revenue.add(lc.getDouble("revenue"));
				}
				header.addAll(Arrays.asList("地区", "收入", "百分比"));
				category.put("地区", provinces);
				seriesMap.put("付费金额", revenue);
				break;
			case "avg-arpu":
				Map<String, Object> arpu = paymentDataService.queryAreaARPU(icons, startDate, endDate);
				header.addAll(Arrays.asList("地区", "日均ARPU", "百分比"));
				category.put("地区", arpu.get("area"));
				seriesMap.put("日均ARPU", arpu.get("data"));
				break;
			case "avg-arppu":
				Map<String, Object> arppu = paymentDataService.queryAreaARPPU(icons, startDate, endDate);
				header.addAll(Arrays.asList("地区", "日均ARPPU", "百分比"));
				category.put("地区", arppu.get("area"));
				seriesMap.put("日均ARPPU", arppu.get("data"));
				break;
			}
			break;
		case "country":
			switch(subTag){
			case "revenue":
				List<String> countries = new ArrayList<String>();
				List<Double> revenue = new ArrayList<Double>();
				List<LogCharge> logCharge = paymentDataService.queryCountryRevenue(icons, startDate, endDate);
				for(LogCharge lc : logCharge){
					countries.add(lc.getStr("country"));
					revenue.add(lc.getDouble("revenue"));
				}
				header.addAll(Arrays.asList("国家", "收入", "百分比"));
				category.put("国家", countries);
				seriesMap.put("付费金额", revenue);
				break;
			case "avg-arpu":
				Map<String, Object> arpu = paymentDataService.queryCountryARPU(icons, startDate, endDate);
				header.addAll(Arrays.asList("国家", "日均ARPU", "百分比"));
				category.put("国家", arpu.get("country"));
				seriesMap.put("日均ARPU", arpu.get("data"));
				break;
			case "avg-arppu":
				Map<String, Object> arppu = paymentDataService.queryCountryARPPU(icons, startDate, endDate);
				header.addAll(Arrays.asList("国家", "日均ARPPU", "百分比"));
				category.put("国家", arppu.get("country"));
				seriesMap.put("日均ARPPU", arppu.get("data"));
				break;
			}
			break;
		case "mobileoperator":
			List<String> carrier = new ArrayList<String>();
			List<Double> revenue = new ArrayList<Double>();
			List<LogCharge> logCharge = paymentDataService.queryMobile(icons, startDate, endDate);
			for(LogCharge lc : logCharge){
				carrier.add(lc.getStr("carrier"));
				revenue.add(lc.getDouble("revenue"));
			}
			header.addAll(Arrays.asList("移动运营商","收入","百分比"));
			category.put("移动运营商", carrier);
			seriesMap.put("付费金额", revenue);
			break;
		case "comsume-package":
			switch(subTag){
			case "revenue":
				break;
			case "recharge-people":
				break;
			}
			break;
		}
		Set<String> type = seriesMap.keySet();
		data.put("type", type.toArray());
		data.put("header", header);
		data.put("category", category);
		data.put("data", seriesMap);
		logger.debug("<PaymentDataController> queryDetailTable:" + data);
		renderJson(data);
	}
}
