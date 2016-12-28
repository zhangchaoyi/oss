package common.controllers.players;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

import org.apache.log4j.Logger;

import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.ext.interceptor.GET;
import com.jfinal.ext.interceptor.POST;
import common.interceptor.DataGuestInterceptor;
import common.service.AccdetailService;
import common.service.impl.AccdetailServiceImpl;

@Clear
public class AccdetailController extends Controller {
	private static Logger logger = Logger.getLogger(AccdetailController.class);
	private AccdetailService accdetailService = new AccdetailServiceImpl();

	/**
	 * 生命轨迹页
	 * 
	 * @author chris
	 * @role data_guest
	 */
	@Before({ GET.class, DataGuestInterceptor.class })
	@ActionKey("/players/accdetail")
	public void activePlayer() {
		render("accdetail.html");
	}

	/**
	 * 生命轨迹接口
	 * 
	 * @author chris
	 * @getPara accountId
	 * @role data_guest
	 */
	@Before({ POST.class, DataGuestInterceptor.class })
	@ActionKey("/api/players/accdetail")
	public void queryActivePlayer() {
		String accountId = getPara("accountId", "");
		logger.info("params:{" + "accountId" + accountId + "}");
		try {
			String db = URLDecoder.decode(getCookie("server"), "GBK");
			Map<String, Object> data = accdetailService.queryAccdetail(accountId.trim(), db);
			logger.info("data:" + data);
			renderJson(data);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			logger.info("cookie decoder failed", e);
		}
	}
}
