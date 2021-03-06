package common.controllers.payment;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
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
import common.service.PaymentBehaviorService;
import common.service.impl.PaymentBehaviorServiceImpl;
import common.util.Contants;
import common.util.StringUtils;

@Clear
public class PaymentBehaviorController extends Controller {
	private static Logger logger = Logger.getLogger(PaymentBehaviorController.class);
	private PaymentBehaviorService paymentBehaviorService = new PaymentBehaviorServiceImpl();
	private String currency = Contants.getCurrency();
	/**
	 * 付费行为页
	 * 
	 * @author chris
	 * @role vip
	 */
	@Before({ GET.class, VipInterceptor.class })
	@ActionKey("/payment/behavior")
	public void paymentIndex() {
		render("paymentBehavior.html");
	}

	/**
	 * 付费等级接口
	 * 
	 * @author chris
	 * @getPara 付费金额/次数
	 * @getPara icon[] 当前的icon ---apple/android/windows
	 * @getPara startDate 所选起始时间
	 * @getPara endDate 所选结束时间
	 * @role vip
	 */
	@Before({ POST.class, VipInterceptor.class })
	@ActionKey("/api/payment/behavior/rank")
	public void queryPaymentBehaviorMoney() {
		String tag = getPara("tag", "rank-paymentBehavior-money");
		String icons = StringUtils.arrayToQueryString(getParaValues("icon[]"));
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		String versions = StringUtils.arrayToQueryString(getParaValues("versions[]"));
		String chId = StringUtils.arrayToQueryString(getParaValues("chId[]"));
		logger.info("params: {" + "tag:" + tag + ",icons:" + icons + ",startDate:" + startDate + ",endDate:" + endDate
				+ "}");

		Map<String, Object> data = new LinkedHashMap<String, Object>();
		Map<String, Object> category = new LinkedHashMap<String, Object>();
		Map<String, Object> seriesMap = new LinkedHashMap<String, Object>();
		List<String> header = new ArrayList<String>();

		try {
			String db = URLDecoder.decode(getCookie("server"), "GBK");

			switch (tag) {
			case "rank-paymentBehavior-money":
				Map<String, Object> rankMoney = paymentBehaviorService.queryRankMoney(icons, startDate, endDate, db, versions, chId);
				category.put("付费等级", rankMoney.get("level"));
				seriesMap.put("付费金额("+currency+")", rankMoney.get("revenue"));
				header.addAll(Arrays.asList("付费等级", "付费金额("+currency+")"));
				break;
			case "rank-paymentBehavior-times":
				Map<String, Object> rankTimes = paymentBehaviorService.queryRankTimes(icons, startDate, endDate, db, versions, chId);
				category.put("付费等级", rankTimes.get("level"));
				seriesMap.put("付费次数", rankTimes.get("count"));
				header.addAll(Arrays.asList("付费等级", "付费次数"));
				break;
			}

			Set<String> type = seriesMap.keySet();
			data.put("table", "rank");
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

	/**
	 * 付费间隔接口
	 * 
	 * @author chris
	 * @getPara tag 首充时间分布/二充到首充时间分布/三充到二充时间分布
	 * @getPara icon[] 当前的icon ---apple/android/windows
	 * @getPara startDate 所选起始时间
	 * @getPara endDate 所选结束时间
	 * @role vip
	 */
	@Before({ POST.class, VipInterceptor.class })
	@ActionKey("/api/payment/behavior/period")
	public void queryPaymentBehaviorPeriod() {
		String tag = getPara("tag", "fp-period");
		String icons = StringUtils.arrayToQueryString(getParaValues("icon[]"));
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		String versions = StringUtils.arrayToQueryString(getParaValues("versions[]"));
		String chId = StringUtils.arrayToQueryString(getParaValues("chId[]"));
		logger.info("params:{" + "tag:" + tag + ",icons:" + icons + ",startDate:" + startDate + ",endDate:" + endDate
				+ "}");

		Map<String, Object> data = new LinkedHashMap<String, Object>();
		Map<String, Object> category = new LinkedHashMap<String, Object>();
		Map<String, Object> seriesMap = new LinkedHashMap<String, Object>();
		List<String> header = new ArrayList<String>();
		List<String> categories = new ArrayList<String>();
		Map<String, String> period = initGamePeriodMap();

		categories.addAll(period.keySet());

		String db;
		try {
			db = URLDecoder.decode(getCookie("server"), "GBK");

			switch (tag) {
			case "fp-period":
				List<Integer> fpCount = paymentBehaviorService.queryFirstPeriod(categories, icons, startDate, endDate, db, versions, chId);
				categories.clear();
				categories.addAll(period.values());
				category.put("首充时间", categories);
				seriesMap.put("人数", fpCount);
				header.addAll(Arrays.asList("首充时间", "人数", "百分比"));
				break;
			case "stfp-period":
				List<Integer> stfCount = paymentBehaviorService.querySTFPeriod(categories, icons, startDate, endDate, db, versions, chId);
				categories.clear();
				categories.addAll(period.values());
				category.put("二充到首充时间", categories);
				seriesMap.put("人数", stfCount);
				header.addAll(Arrays.asList("二充到首充时间", "人数", "百分比"));
				break;
			case "ttsp-period":
				List<Integer> ttsCount = paymentBehaviorService.queryTTSPeriod(categories, icons, startDate, endDate, db, versions, chId);
				categories.clear();
				categories.addAll(period.values());
				category.put("三充到二充时间", categories);
				seriesMap.put("人数", ttsCount);
				header.addAll(Arrays.asList("三充到二充时间", "人数", "百分比"));
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

	/**
	 * 详细栏接口
	 * 
	 * @author chris
	 * @getPara tag 详细栏tag
	 * @getPara subTag 子选项栏tag
	 * @getPara icon[] 当前的icon ---apple/android/windows
	 * @getPara startDate 所选起始时间
	 * @getPara endDate 所选结束时间
	 * @role vip
	 */
	@Before({ POST.class, VipInterceptor.class })
	@ActionKey("/api/payment/behavior/fp/detail")
	public void queryPaymentBehaviorDetail() {
		String tag = getPara("tag", "fp-cycle");
		String subTag = getPara("subTag", "game-days");
		String icons = StringUtils.arrayToQueryString(getParaValues("icon[]"));
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		String versions = StringUtils.arrayToQueryString(getParaValues("versions[]"));
		String chId = StringUtils.arrayToQueryString(getParaValues("chId[]"));
		logger.info("params:{" + "tag:" + tag + ",subTag:" + subTag + ",icons:" + icons + ",startDate:" + startDate
				+ ",endDate:" + endDate + "}");

		Map<String, Object> data = new LinkedHashMap<String, Object>();
		Map<String, Object> category = new LinkedHashMap<String, Object>();
		Map<String, Object> seriesMap = new LinkedHashMap<String, Object>();
		List<String> header = new ArrayList<String>();
		List<String> categories = new ArrayList<String>();
		Map<String, String> period;

		try {
			String db = URLDecoder.decode(getCookie("server"), "GBK");

			switch (tag) {
			case "fp-cycle":
				data.put("chartType", "bar");
				switch (subTag) {
				case "game-days":
					period = initGameDaysMap();
					categories.addAll(period.keySet());
					List<Integer> gdCount = paymentBehaviorService.queryFpGameDays(categories, icons, startDate,
							endDate, db, versions, chId);
					categories.clear();
					categories.addAll(period.values());
					category.put("游戏天数", categories);
					seriesMap.put("付费玩家", gdCount);
					header.addAll(Arrays.asList("游戏天数", "付费玩家", "百分比"));
					break;
				case "game-period":
					period = initGamePeriodMap();
					categories.addAll(period.keySet());
					List<Integer> gpCount = paymentBehaviorService.queryFpGamePeriod(categories, icons, startDate,
							endDate, db, versions, chId);
					categories.clear();
					categories.addAll(period.values());
					category.put("累计游戏时长", categories);
					seriesMap.put("付费玩家", gpCount);
					header.addAll(Arrays.asList("累计游戏时长", "付费玩家", "百分比"));
					break;
				}
				break;
			case "fp-rank":
				Map<String, Object> fpRank = paymentBehaviorService.queryFpRank(icons, startDate, endDate, db, versions, chId);
				category.put("玩家首付等级", fpRank.get("level"));
				seriesMap.put("付费玩家", fpRank.get("count"));
				header.addAll(Arrays.asList("玩家首付等级", "付费玩家", "百分比"));
				data.put("chartType", "line");
				break;
			case "fp-money":
				period = initPaidPeriod();
				categories.addAll(period.keySet());
				List<Integer> mCount = paymentBehaviorService.queryFpMoney(categories, icons, startDate, endDate, db, versions, chId);
				categories.clear();
				categories.addAll(period.values());
				category.put("玩家首付金额", categories);
				seriesMap.put("付费玩家", mCount);
				header.addAll(Arrays.asList("玩家首付金额", "付费玩家", "百分比"));
				data.put("chartType", "bar");
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

	private Map<String, String> initGameDaysMap() {
		Map<String, String> period = new LinkedHashMap<String, String>();
		period.put("d1", "首日");
		period.put("d2", "2~3 天");
		period.put("d4", "4~7 天");
		period.put("w2", "2 周");
		period.put("w3", "3 周");
		period.put("w4", "4 周");
		period.put("w5", "5 周");
		period.put("w6", "6 周");
		period.put("w7", "7 周");
		period.put("w8", "8 周");
		period.put("w9", "9~12 周");
		period.put("w12", ">12 周");
		return period;
	}

	private Map<String, String> initGamePeriodMap() {
		Map<String, String> period = new LinkedHashMap<String, String>();
		period.put("l10min", "<10 分钟");
		period.put("l30min", "10~30 分钟");
		period.put("l60min", "30~60 分钟");
		period.put("l2h", "1~2 小时");
		period.put("l4h", "2~4 小时");
		period.put("l6h", "4~6 小时");
		period.put("l10h", "6~10 小时");
		period.put("l15h", "10~15 小时");
		period.put("l20h", "15~20 小时");
		period.put("l30h", "20~30 小时");
		period.put("l40h", "30~40 小时");
		period.put("l60h", "40~60 小时");
		period.put("l100h", "60~100 小时");
		period.put("m100h", ">100 小时");
		return period;
	}

	private Map<String, String> initPaidPeriod() {
		Map<String, String> period = new LinkedHashMap<String, String>();
		period.put("m1", "1~10 "+currency);
		period.put("m11", "11~50 "+currency);
		period.put("m51", "51~100 "+currency);
		period.put("m101", "101~200 "+currency);
		period.put("m201", "201~500 "+currency);
		period.put("m501", "501~1000 "+currency);
		period.put("m1000", ">1000 "+currency);
		return period;
	}
}
