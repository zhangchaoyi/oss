package common.controllers.payment;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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
import common.util.Contants;
import common.util.DateUtils;
import common.util.StringUtils;
import common.interceptor.VipInterceptor;

@Clear
public class PaymentDataController extends Controller {
	private static Logger logger = Logger.getLogger(PaymentDataController.class);
	private PaymentDataService paymentDataService = new PaymentDataServiceImpl();
	private String currency = Contants.getCurrency();
	/**
	 * 付费数据页
	 * 
	 * @author chris
	 * @role vip
	 */
	@Before({ GET.class, VipInterceptor.class })
	@ActionKey("/payment/data")
	public void paymentIndex() {
		render("payment.html");
	}

	/**
	 * 付费数据栏接口
	 * 
	 * @author chris
	 * @getPara tag 付费金额/人数/次数
	 * @param icons
	 *            当前的icon ---apple/android/windows
	 * @param startDate
	 *            所选起始时间
	 * @param endDate
	 *            所选结束时间
	 * @role vip
	 */
	@Before({ POST.class, VipInterceptor.class })
	@ActionKey("/api/payment/data")
	public void queryPaymentData() {
		String tag = getPara("tag", "data-payment-money");
		String icons = StringUtils.arrayToQueryString(getParaValues("icon[]"));
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		String versions = StringUtils.arrayToQueryString(getParaValues("versions[]"));
		String chId = StringUtils.arrayToQueryString(getParaValues("chId[]"));
		logger.info(
				"params:{" + "tag" + tag + ",icons:" + icons + ",startDate:" + startDate + ",endDate:" + endDate + "}");

		Map<String, Object> data = new LinkedHashMap<String, Object>();
		Map<String, List<String>> category = new LinkedHashMap<String, List<String>>();
		Map<String, Object> seriesMap = new LinkedHashMap<String, Object>();
		Map<String, Object> sum = new LinkedHashMap<String, Object>();
		Map<String, Map<String, Object>> recData = new LinkedHashMap<String, Map<String, Object>>();

		List<String> categories = DateUtils.getDateList(startDate, endDate);

		try {
			String db = URLDecoder.decode(getCookie("server"), "GBK");
			switch (tag) {
			case "data-payment-money":
				recData = paymentDataService.queryMoneyPayment(categories, startDate, endDate, icons, db, versions, chId);
				sum = recData.get("sum");
				seriesMap = recData.get("series");
				break;
			case "data-payment-people":
				recData = paymentDataService.queryPeoplePayment(categories, startDate, endDate, icons, db, versions, chId);
				sum = recData.get("sum");
				seriesMap = recData.get("series");
				break;
			case "data-payment-times":
				recData = paymentDataService.queryNumPayment(categories, startDate, endDate, icons, db, versions, chId);
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
			logger.info("data:" + data);
			renderJson(data);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			logger.info("cookie decoder failed", e);
		}
	}

	/**
	 * 付费数据表格
	 * 
	 * @param icons
	 *            当前的icon ---apple/android/windows
	 * @param startDate
	 *            所选起始时间
	 * @param endDate
	 *            所选结束时间
	 */
	@Before({ POST.class, VipInterceptor.class })
	@ActionKey("/api/payment/data/table")
	public void queryPaymentDataTable() {
		String icons = StringUtils.arrayToQueryString(getParaValues("icon[]"));
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		String versions = StringUtils.arrayToQueryString(getParaValues("versions[]"));
		String chId = StringUtils.arrayToQueryString(getParaValues("chId[]"));
		logger.info("params:{" + "icons:" + icons + ",startDate:" + startDate + ",endDate:" + endDate + "}");
		List<String> categories = DateUtils.getDateList(startDate, endDate);

		try {
			String db = URLDecoder.decode(getCookie("server"), "GBK");
			List<List<Object>> paymentDetail = paymentDataService.queryDataPayment(categories, startDate, endDate,
					icons, db, versions, chId);
			logger.info("data:" + paymentDetail);
			renderJson(paymentDetail);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			logger.info("cookie decoder failed", e);
		}
	}

	/**
	 * 付费分析接口
	 * 
	 * @param icons
	 *            当前的icon ---apple/android/windows
	 * @param startDate
	 *            所选起始时间
	 * @param endDate
	 *            所选结束时间
	 * @param tag
	 *            付费金额/次数/ARPUARPPU
	 * @param subTag
	 *            日/周/月
	 */
	@Before({ POST.class, VipInterceptor.class })
	@ActionKey("/api/payment/analyze")
	public void queryAnalyzePayment() {
		String icons = StringUtils.arrayToQueryString(getParaValues("icon[]"));
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		String tag = getPara("tag");
		String subTag = getPara("subTag");
		String versions = StringUtils.arrayToQueryString(getParaValues("versions[]"));
		String chId = StringUtils.arrayToQueryString(getParaValues("chId[]"));
		logger.info("params:{" + "tag:" + tag + ",subTag:" + subTag + ",icons:" + icons + ",startDate:" + startDate
				+ ",endDate:" + endDate + "}");

		Map<String, Object> data = new LinkedHashMap<String, Object>();
		Map<String, List<String>> category = new LinkedHashMap<String, List<String>>();
		Map<String, Object> seriesMap = new LinkedHashMap<String, Object>();
		List<String> categories = new ArrayList<String>();
		data.put("chartType", "bar");
		try {
			String db = URLDecoder.decode(getCookie("server"), "GBK");
			switch (tag) {
			case "analyze-payment-money":
				switch (subTag) {
				case "day":
					categories = Arrays.asList("1", "2", "3", "4", "5", "6~10", "11~50", "51~100", "101~500", "501~1000", "1001~2000", ">2000");
					List<Integer> queryPeriod = paymentDataService.queryDayPaymentMoney(categories, icons, startDate,
							endDate, db, versions, chId);
					categories = Arrays.asList("1("+currency+")", "2("+currency+")", "3("+currency+")", "4("+currency+")", "5("+currency+")", "6~10("+currency+")", "11~50("+currency+")",
							"51~100("+currency+")", "101~500("+currency+")", "501~1000("+currency+")", "1001~2000("+currency+")", ">2000("+currency+")");
					category.put("付费金额区间", categories);
					seriesMap.put("人数", queryPeriod);
					break;
				}
				break;
			case "analyze-payment-times":
				switch (subTag) {
				case "day":
					categories = Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11~20", "21~30",
							"31~40", "41~50", "51~100", ">100");
					category.put("付费次数区间", categories);
					List<Integer> queryPeriod = paymentDataService.queryDayPaymentTimes(categories, icons, startDate,
							endDate, db, versions, chId);
					seriesMap.put("人数", queryPeriod);
					break;
				}
				break;
			case "analyze-payment-arpu":
				data.put("chartType", "line");
				switch (subTag) {
				case "ARPU-D":
					categories = DateUtils.getDateList(startDate, endDate);
					List<Double> AD = paymentDataService.queryDayARPU(categories, icons, startDate, endDate, db, versions, chId);
					seriesMap.put("ARPU(日)", AD);
					break;
				case "ARPU-M":
					categories = DateUtils.getMonthList(startDate, endDate);
					List<Double> AM = paymentDataService.queryMonthARPU(icons, startDate, endDate, db, versions, chId);
					seriesMap.put("ARPU(月)", AM);
					break;
				case "ARPPU-D":
					categories = DateUtils.getDateList(startDate, endDate);
					List<Double> ARD = paymentDataService.queryDayARPPU(categories, icons, startDate, endDate, db,versions,chId);
					seriesMap.put("ARPPU(日)", ARD);
					break;
				case "ARPPU-M":
					categories = DateUtils.getMonthList(startDate, endDate);
					List<Double> ARM = paymentDataService.queryMonthARPPU(icons, startDate, endDate, db, versions, chId);
					seriesMap.put("ARPPU(月)", ARM);
					break;
				}
				break;
			}
			category.put("日期", categories);
			Set<String> type = seriesMap.keySet();
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
	 * 付费分析表格
	 * 
	 * @param icons
	 *            当前的icon ---apple/android/windows
	 * @param startDate
	 *            所选起始时间
	 * @param endDate
	 *            所选结束时间
	 * @param tag
	 *            付费金额/次数/ARPUARPPU
	 * @param subTag
	 *            日/周/月
	 */
	@Before({ POST.class, VipInterceptor.class })
	@ActionKey("/api/payment/analyze/table")
	public void queryPaymentAnalyzeTable() {
		String icons = StringUtils.arrayToQueryString(getParaValues("icon[]"));
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		String tag = getPara("tag");
		String subTag = getPara("subTag", "");
		String versions = StringUtils.arrayToQueryString(getParaValues("versions[]"));
		String chId = StringUtils.arrayToQueryString(getParaValues("chId[]"));
		logger.info("params:{" + "tag:" + tag + ",subTag" + subTag + ",icons:" + icons + ",startDate:" + startDate
				+ ",endDate:" + endDate + "}");

		List<String> categories = new ArrayList<String>();
		List<List<Object>> queryData = new ArrayList<List<Object>>();
		List<String> header = new ArrayList<String>();
		Map<String, Object> data = new HashMap<String, Object>();

		try {
			String db = URLDecoder.decode(getCookie("server"), "GBK");
			switch (tag) {
			case "analyze-payment-money":
				header.addAll(Arrays.asList("付费金额区间", "每日付费玩家"));
				categories = Arrays.asList("1", "2", "3", "4", "5", "6~10", "11~50", "51~100", "101~500", "501~1000",
						"1001~2000", ">2000");
				queryData = paymentDataService.queryAllPaymentMoney(categories, icons, startDate, endDate, db, versions, chId);
				break;
			case "analyze-payment-times":
				header.addAll(Arrays.asList("付费次数区间", "每日付费玩家"));
				categories = Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11~20", "21~30", "31~40",
						"41~50", "51~100", ">100");
				queryData = paymentDataService.queryAllPaymentTimes(categories, icons, startDate, endDate, db, versions, chId);
				break;
			case "analyze-payment-arpu":
				header.addAll(Arrays.asList("日期", subTag));
				categories = DateUtils.getDateList(startDate, endDate);
				switch (subTag) {
				case "ARPU-D":
					queryData = paymentDataService.queryArpu(categories, icons, startDate, endDate, db, versions, chId);
					break;
				case "ARPU-M":
					queryData = paymentDataService.queryMonthArpuTable(icons, startDate, endDate, db, versions, chId);
					break;
				case "ARPPU-D":
					queryData = paymentDataService.queryArppu(categories, icons, startDate, endDate, db, versions, chId);
					break;
				case "ARPPU-M":
					queryData = paymentDataService.queryMonthArppuTable(icons, startDate, endDate, db, versions, chId);
					break;
				}
				break;
			}
			data.put("header", header);
			data.put("data", queryData);
			logger.info("data:" + data);
			renderJson(data);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			logger.info("cookie decoder failed", e1);
		}
	}

	/**
	 * 详细栏接口
	 * 
	 * @param icons
	 *            当前的icon ---apple/android/windows
	 * @param startDate
	 *            所选起始时间
	 * @param endDate
	 *            所选结束时间
	 * @param tag
	 *            地区/国家/.....
	 * @param subTag
	 *            子选项栏
	 */
	@Before({ POST.class, VipInterceptor.class })
	@ActionKey("/api/payment/detail")
	public void queryDetailTable() {
		String icons = StringUtils.arrayToQueryString(getParaValues("icon[]"));
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		String tag = getPara("tag");
		String subTag = getPara("subTag", "");
		String versions = StringUtils.arrayToQueryString(getParaValues("versions[]"));
		String chId = StringUtils.arrayToQueryString(getParaValues("chId[]"));
		logger.info("params:{" + "tag:" + tag + ",subTag:" + subTag + ",icons:" + icons + ",startDate:" + startDate
				+ ",endDate:" + endDate + "}");

		List<String> header = new ArrayList<String>();
		Map<String, Object> category = new LinkedHashMap<String, Object>();
		Map<String, Object> seriesMap = new LinkedHashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();

		try {
			String db = URLDecoder.decode(getCookie("server"), "GBK");

			switch (tag) {
			case "area":
				switch (subTag) {
				case "revenue":
					List<String> provinces = new ArrayList<String>();
					List<Double> revenue = new ArrayList<Double>();
					List<LogCharge> logCharge = paymentDataService.queryAreaRevenue(icons, startDate, endDate, db, versions, chId);
					for (LogCharge lc : logCharge) {
						provinces.add(lc.getStr("province"));
						revenue.add(lc.getDouble("revenue"));
					}
					header.addAll(Arrays.asList("地区", "收入", "百分比"));
					category.put("地区", provinces);
					seriesMap.put("付费金额", revenue);
					break;
				case "avg-arpu":
					Map<String, Object> arpu = paymentDataService.queryAreaARPU(icons, startDate, endDate, db, versions, chId);
					header.addAll(Arrays.asList("地区", "日均ARPU", "百分比"));
					category.put("地区", arpu.get("area"));
					seriesMap.put("日均ARPU", arpu.get("data"));
					break;
				case "avg-arppu":
					Map<String, Object> arppu = paymentDataService.queryAreaARPPU(icons, startDate, endDate, db, versions, chId);
					header.addAll(Arrays.asList("地区", "日均ARPPU", "百分比"));
					category.put("地区", arppu.get("area"));
					seriesMap.put("日均ARPPU", arppu.get("data"));
					break;
				}
				break;
			case "country":
				switch (subTag) {
				case "revenue":
					List<String> countries = new ArrayList<String>();
					List<Double> revenue = new ArrayList<Double>();
					List<LogCharge> logCharge = paymentDataService.queryCountryRevenue(icons, startDate, endDate, db, versions, chId);
					for (LogCharge lc : logCharge) {
						countries.add(lc.getStr("country"));
						revenue.add(lc.getDouble("revenue"));
					}
					header.addAll(Arrays.asList("国家", "收入", "百分比"));
					category.put("国家", countries);
					seriesMap.put("付费金额", revenue);
					break;
				case "avg-arpu":
					Map<String, Object> arpu = paymentDataService.queryCountryARPU(icons, startDate, endDate, db, versions, chId);
					header.addAll(Arrays.asList("国家", "日均ARPU", "百分比"));
					category.put("国家", arpu.get("country"));
					seriesMap.put("日均ARPU", arpu.get("data"));
					break;
				case "avg-arppu":
					Map<String, Object> arppu = paymentDataService.queryCountryARPPU(icons, startDate, endDate, db, versions, chId);
					header.addAll(Arrays.asList("国家", "日均ARPPU", "百分比"));
					category.put("国家", arppu.get("country"));
					seriesMap.put("日均ARPPU", arppu.get("data"));
					break;
				}
				break;
			case "mobileoperator":
				List<String> carrier = new ArrayList<String>();
				List<Double> revenue = new ArrayList<Double>();
				List<LogCharge> logCharge = paymentDataService.queryMobile(icons, startDate, endDate, db, versions, chId);
				for (LogCharge lc : logCharge) {
					carrier.add(lc.getStr("carrier"));
					revenue.add(lc.getDouble("revenue"));
				}
				header.addAll(Arrays.asList("移动运营商", "收入", "百分比"));
				category.put("移动运营商", carrier);
				seriesMap.put("付费金额", revenue);
				break;
			}
			Set<String> type = seriesMap.keySet();
			data.put("type", type.toArray());
			data.put("header", header);
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
