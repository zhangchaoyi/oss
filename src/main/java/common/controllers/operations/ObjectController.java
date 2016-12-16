package common.controllers.operations;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import org.apache.log4j.Logger;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.ext.interceptor.GET;
import com.jfinal.ext.interceptor.POST;
import common.service.OperationObjectService;
import common.service.impl.OperationObjectServiceImpl;

@Clear
public class ObjectController extends Controller {
	private static Logger logger = Logger.getLogger(ObjectController.class);
	private OperationObjectService oo = new OperationObjectServiceImpl();

	/**
	 * 物品消耗和获取页
	 * 
	 * @author chris
	 */
	@Before(GET.class)
	@ActionKey("/operation/object")
	public void objectIndex() {
		render("object-obtain-consume.html");
	}

	/**
	 * 查询个人物品获取消耗情况
	 * 
	 * @param account
	 *            查询帐号
	 * @author chris
	 */
	@Before(POST.class)
	@ActionKey("/api/operation/object/player")
	public void querySingleCurrency() {
		String account = getPara("account", "");
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		logger.info("getParam:" + "{startDate:" + startDate + ",endDate:" + endDate + ",account:" + account + "}");
		String db;
		try {
			db = URLDecoder.decode(getCookie("server"), "GBK");
			List<List<String>> queryData = oo.querySingleObject(startDate, endDate, account, db);
			renderJson(queryData);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			logger.info("cookie decoder failed", e);
		}
	}
}
