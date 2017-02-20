package common.controllers.payment;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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
import common.util.DateUtils;
import common.util.StringUtils;

@Clear
public class PaymentRankController extends Controller {
	private static Logger logger = Logger.getLogger(PaymentRankController.class);
	private PaymentRankService paymentRankService = new PaymentRankServiceImpl();

	/**
	 * 付费排行页
	 * 
	 * @author chris
	 * @role vip
	 */
	@Before({ GET.class, VipInterceptor.class })
	@ActionKey("/payment/rank")
	public void paymentIndex() {
		render("paymentRank.html");
	}

	/**
	 * 付费排行接口
	 * 
	 * @author chris
	 * @param icons
	 *            当前的icon ---apple/android/windows
	 * @param startDate
	 *            所选起始时间
	 * @param endDate
	 *            所选结束时间
	 * @role vip
	 */
	@Before({ POST.class, VipInterceptor.class })
	@ActionKey("/api/payment/rank/players")
	public void queryPaymentRank() {
		String icons = StringUtils.arrayToQueryString(getParaValues("icon[]"));
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		String versions = StringUtils.arrayToQueryString(getParaValues("versions[]"));
		String chId = StringUtils.arrayToQueryString(getParaValues("chId[]"));
		logger.info("params:{" + "icons:" + icons + ",startDate:" + startDate + ",endDate:" + endDate + "}");

		try {
			String db = URLDecoder.decode(getCookie("server"), "GBK");
			Map<String, Object> data = new LinkedHashMap<String, Object>();
			List<List<String>> queryData = paymentRankService.queryRank(icons, startDate, endDate, db, versions, chId);
			data.put("data", queryData);
			logger.info("data:" + data);
			renderJson(data);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			logger.info("cookie decoder failed", e);
		}
	}

	/**
	 * 详细栏接口
	 * 
	 * @author chris account 帐号id
	 * @param icons
	 *            当前的icon ---apple/android/windows
	 * @param startDate
	 *            所选起始时间
	 * @param endDate
	 *            所选结束时间
	 * @role vip
	 */
	@Before({ POST.class, VipInterceptor.class })
	@ActionKey("/api/payment/rank/account/detail")
	public void queryPaymentAccount() {
		String account = getPara("account");
		String icons = StringUtils.arrayToQueryString(getParaValues("icon[]"));
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		logger.info("params:{" + "account:" + account + ",icons:" + icons + ",startDate:" + startDate + ",endDate:"
				+ endDate + "}");

		List<String> categories = DateUtils.getDateList(startDate, endDate);
		String[] accountArray = new String[] { account };

		Map<String, Object> category = new LinkedHashMap<String, Object>();
		Map<String, Object> seriesMap = new LinkedHashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();

		try {
			String db = URLDecoder.decode(getCookie("server"), "GBK");
			Map<String, Object> queryData = paymentRankService.queryAccountDetail(accountArray, categories, icons,
					startDate, endDate, db);
			seriesMap.put("在线时长", queryData.get("oTList"));
			seriesMap.put("登录次数", queryData.get("lTList"));
			seriesMap.put("付费金额", queryData.get("pRList"));
			category.put("日期", categories);

			Set<String> type = seriesMap.keySet();
			data.put("type", type.toArray());
			data.put("category", category);
			data.put("data", seriesMap);
			data.put("tableData", queryData.get("tableData"));
			logger.info("data:" + data);
			renderJson(data);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			logger.info("cookie decoder failed", e);
		}
	}
}
