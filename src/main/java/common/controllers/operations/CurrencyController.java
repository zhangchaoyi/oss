package common.controllers.operations;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.ext.interceptor.GET;
import com.jfinal.ext.interceptor.POST;
import common.service.OperationCurrencyService;
import common.service.impl.OperationCurrencyServiceImpl;

@Clear
public class CurrencyController extends Controller {
	private static Logger logger = Logger.getLogger(CurrencyController.class);
	private OperationCurrencyService ocs = new OperationCurrencyServiceImpl();

	/**
	 * 货币消耗和获取页
	 * 
	 * @author chris
	 */
	@Before(GET.class)
	@ActionKey("/operation/currency")
	public void currencyIndex() {
		render("currency-obtain-consume.html");
	}

	/**
	 * 查询全服货币获取消耗情况
	 * 
	 * @getPara startDate
	 * @getPara endDate
	 * @author chris
	 */
	@Before(POST.class)
	@ActionKey("/api/operation/currency/all")
	public void queryAllServerCurrency() {
		String startDate = getPara("startDate", "");
		String endDate = getPara("endDate", "");
		String currency = getPara("currency", "gold");
		int draw = getParaToInt("draw", 0);
		int start = getParaToInt("start", 0);
		int length = getParaToInt("length", 10);
		logger.info("getParam:" + "{startDate:" + startDate + ",endDate:" + endDate + "" + ",currency:" + currency
				+ ",draw:" + draw + ",start:" + start + ",length:" + length + "}");

		// mysql分 RMB 和 金币
		try {
			String db = URLDecoder.decode(getCookie("server"), "GBK");

			Map<String, Object> queryData = ocs.queryAllCurrency(startDate, endDate, currency, start, length, db);
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("draw", draw);
			data.put("recordsTotal", queryData.get("count"));
			data.put("recordsFiltered", queryData.get("count"));
			data.put("data", queryData.get("tableData"));
			renderJson(data);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			logger.info("cookie decoder failed", e);
		}
	}

	/**
	 * 查询个人货币获取消耗情况
	 * 
	 * @param account
	 *            查询帐号
	 * @author chris
	 */
	@Before(POST.class)
	@ActionKey("/api/operation/currency/player")
	public void querySingleCurrency() {
		String account = getPara("account", "");
		String startDate = getPara("startDate", "");
		String endDate = getPara("endDate", "");
		String currency = getPara("currency", "gold");
		logger.info("getParam:" + "{startDate:" + startDate + ",endDate:" + endDate + ",currency:" + currency
				+ ",account:" + account + "}");
		String db;
		try {
			db = URLDecoder.decode(getCookie("server"), "GBK");

			List<List<String>> queryData = ocs.querySingleCurrency(startDate, endDate, currency, account, db);
			renderJson(queryData);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			logger.info("cookie decoder failed", e);
		}
	}
}
