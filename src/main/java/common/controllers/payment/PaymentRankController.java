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
import common.interceptor.VipInterceptor;
import common.service.PaymentRankService;
import common.service.impl.PaymentRankServiceImpl;
import common.utils.DateUtils;
import common.utils.StringUtils;

@Clear
public class PaymentRankController extends Controller{
	private static Logger logger = Logger.getLogger(PaymentRankController.class);
	private PaymentRankService paymentRankService = new PaymentRankServiceImpl();
	
	
	@Before({GET.class, VipInterceptor.class})
	@ActionKey("/payment/rank")
	public void paymentIndex() {
		render("paymentRank.html");
	}
	
	@Before({POST.class, VipInterceptor.class})
	@ActionKey("/api/payment/rank/players")
	public void queryPaymentRank() {
		String icons = StringUtils.arrayToQueryString(getParaValues("icon[]"));
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		
		Map<String, Object> data = new LinkedHashMap<String, Object>();
		List<List<String>> queryData = paymentRankService.queryRank(icons, startDate, endDate);
//		List<List<String>> queryData = new ArrayList<List<String>>();
//		List<String> subList = new ArrayList<String>(Arrays.asList("1","20091204","2016-09-01","2016-09-01","1","1","1","1","1","10","20091204"));
//		queryData.add(subList);
		
		data.put("data", queryData);
		logger.debug("<PaymentRankController> queryPaymentRank:" + data);
		renderJson(data);
	}
	
	@Before({POST.class, VipInterceptor.class})
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
		logger.debug("<PaymentRankController> queryPaymentAccount:" + data);
		renderJson(data);
	}
}
