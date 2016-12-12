package common.controllers.operations;

import java.util.List;

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
	 * @author chris
	 */
	@Before(GET.class)
	@ActionKey("/operation/currency")
	public void currencyIndex() {
		render("currency-obtain-consume.html");
	}
	
	/**
	 * 查询全服货币获取消耗情况
	 * @getPara startDate
	 * @getPara endDate
	 * @author chris
	 */
	@Before(POST.class)
	@ActionKey("/api/operation/currency/all")
	public void queryAllServerCurrency() {
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		logger.info("getParam:"+"{startDate:"+startDate+",endDate:"+endDate+"}");
		List<List<String>> queryData = ocs.queryAllCurrency(startDate, endDate);
		renderJson(queryData);
	}
	
	/**
	 * 查询个人货币获取消耗情况
	 * @param account 查询帐号
	 * @author chris
	 */
	@Before(POST.class)
	@ActionKey("/api/operation/currency/player")
	public void querySingleCurrency() {
		String account = getPara("account", "");
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		logger.info("getParam:"+"{startDate:"+startDate+",endDate:"+endDate+",account:"+account+"}");
		List<List<String>> queryData = ocs.querySingleCurrency(startDate, endDate, account);
		renderJson(queryData);
	}
}
