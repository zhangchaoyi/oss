package common.controllers.payment;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
import common.service.PaymentTransformService;
import common.service.impl.PaymentTransformServiceImpl;
import common.util.DateUtils;
import common.util.StringUtils;

@Clear
public class PaymentTransformController extends Controller {
	private static Logger logger = Logger.getLogger(PaymentTransformController.class);
	private PaymentTransformService paymentTransformService = new PaymentTransformServiceImpl();

	/**
	 * 付費转化页
	 * 
	 * @author chris
	 * @role vip
	 */
	@Before({ GET.class, VipInterceptor.class })
	@ActionKey("/payment/transform")
	public void paymentIndex() {
		render("paymentTransform.html");
	}

	/**
	 * 付费分析接口
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
	@ActionKey("/api/payment/transform/paidAnalyze")
	public void queryPaymentAddPaidAnalyze() {
		String icons = StringUtils.arrayToQueryString(getParaValues("icon[]"));
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		logger.info("params:{" + "icons:" + icons + ",startDate:" + startDate + ",endDate:" + endDate + "}");

		Map<String, Object> data = new LinkedHashMap<String, Object>();
		Map<String, Object> category = new LinkedHashMap<String, Object>();
		Map<String, Object> seriesMap = new LinkedHashMap<String, Object>();
		List<String> header = new ArrayList<String>();
		header.addAll(Arrays.asList("日期", "新增玩家", "首日付费人数(率)", "首周付费人数(率)", "首月付费人数(率)"));
		List<String> categories = DateUtils.getDateList(startDate, endDate);

		try {
			String db = URLDecoder.decode(getCookie("server"), "GBK");
			Map<String, Object> aPA = paymentTransformService.queryAddPaymentAnalyze(categories, icons, startDate,
					endDate, db);
			seriesMap.put("首日付费率", aPA.get("fdPP"));
			seriesMap.put("首周付费率", aPA.get("fwPP"));
			seriesMap.put("首月付费率", aPA.get("fmPP"));

			Set<String> type = seriesMap.keySet();
			data.put("chartType", "line");
			data.put("header", header);
			data.put("tableData", aPA.get("tableData"));
			category.put("日期", categories);
			data.put("type", type.toArray());
			data.put("category", category);
			data.put("data", seriesMap);
			logger.info("data:" + data);
			renderJson(data);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 付费率接口
	 * 
	 * @author chris
	 * @para tag 日/周/月付费率
	 * @param icons
	 *            当前的icon ---apple/android/windows
	 * @param startDate
	 *            所选起始时间
	 * @param endDate
	 *            所选结束时间
	 * @role vip
	 */
	@SuppressWarnings("unchecked")
	@Before({ POST.class, VipInterceptor.class })
	@ActionKey("/api/payment/transform/rate")
	public void queryPaymentRate() {
		String tag = getPara("tag");
		String icons = StringUtils.arrayToQueryString(getParaValues("icon[]"));
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		logger.info("params:{" + "tag:" + tag + ",icons:" + icons + ",startDate:" + startDate + ",endDate:" + endDate
				+ "}");

		Map<String, Object> data = new LinkedHashMap<String, Object>();
		Map<String, Object> category = new LinkedHashMap<String, Object>();
		Map<String, Object> seriesMap = new LinkedHashMap<String, Object>();
		List<String> header = new ArrayList<String>();
		List<String> categories = DateUtils.getDateList(startDate, endDate);

		String db;
		try {
			db = URLDecoder.decode(getCookie("server"), "GBK");

			switch (tag) {
			case "dpr":
				header.addAll(Arrays.asList("日期", "付费玩家", "活跃玩家", "日付费率"));
				Map<String, Object> dpr = paymentTransformService.queryDayPaidRate(categories, icons, startDate,
						endDate, db);
				seriesMap.put("日付费率", dpr.get("dpr"));
				data.put("tableData", dpr.get("tableData"));
				break;
			case "wpr":
				header.addAll(Arrays.asList("日期", "付费玩家", "活跃玩家", "周付费率"));
				Map<String, Object> wpr = paymentTransformService.queryWeekPaidRate(icons, startDate, endDate, db);
				seriesMap.put("周付费率", wpr.get("wpr"));
				data.put("tableData", wpr.get("tableData"));
				categories.clear();
				categories.addAll((Collection<? extends String>) wpr.get("categories"));
				break;
			case "mpr":
				categories = DateUtils.getMonthList(startDate, endDate);
				header.addAll(Arrays.asList("日期", "付费玩家", "活跃玩家", "月付费率"));
				Map<String, Object> mpr = paymentTransformService.queryMonthPaidRate(categories, icons, startDate,
						endDate, db);
				seriesMap.put("月付费率", mpr.get("mpr"));
				data.put("tableData", mpr.get("tableData"));
				break;
			}

			Set<String> type = seriesMap.keySet();
			data.put("chartType", "line");
			data.put("header", header);
			category.put("日期", categories);
			data.put("type", type.toArray());
			data.put("category", category);
			data.put("data", seriesMap);
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
	 * @author chris
	 * @param tag
	 *            详细栏tag
	 * @param icons
	 *            当前的icon ---apple/android/windows
	 * @param startDate
	 *            所选起始时间
	 * @param endDate
	 *            所选结束时间
	 * @role vip
	 */
	@Before({ POST.class, VipInterceptor.class })
	@ActionKey("/api/payment/transform/detail")
	public void queryPaymentTransformDetail() {
		String tag = getPara("tag");
		String icons = StringUtils.arrayToQueryString(getParaValues("icon[]"));
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		logger.info("params:{" + "tag:" + tag + ",icons:" + icons + ",startDate:" + startDate + ",endDate:" + endDate
				+ "}");

		Map<String, Object> data = new LinkedHashMap<String, Object>();
		Map<String, Object> category = new LinkedHashMap<String, Object>();
		Map<String, Object> seriesMap = new LinkedHashMap<String, Object>();
		List<String> header = new ArrayList<String>();

		try {
			String db = URLDecoder.decode(getCookie("server"), "GBK");

			switch (tag) {
			case "area":
				header.addAll(Arrays.asList("日期", "日均付费率"));
				Map<String, Object> province = paymentTransformService.queryAreaPaidRate(icons, startDate, endDate,
						"province", db);
				seriesMap.put("日均付费率", province.get("rate"));
				category.put("地区", province.get("categories"));
				data.put("tableData", province.get("tableData"));
				break;
			case "country":
				header.addAll(Arrays.asList("日期", "日均付费率"));
				Map<String, Object> country = paymentTransformService.queryAreaPaidRate(icons, startDate, endDate,
						"country", db);
				seriesMap.put("日均付费率", country.get("rate"));
				category.put("国家", country.get("categories"));
				data.put("tableData", country.get("tableData"));
				break;
			}

			Set<String> type = seriesMap.keySet();
			data.put("chartType", "bar");
			data.put("header", header);
			data.put("type", type.toArray());
			data.put("category", category);
			data.put("data", seriesMap);
			logger.info("data:" + data);
			renderJson(data);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			logger.info("cookie decoder failed", e);
		}
	}
}
