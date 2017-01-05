package common.controllers.operations;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.ext.interceptor.GET;
import com.jfinal.ext.interceptor.POST;
import common.interceptor.GmInterceptor;
import common.service.PaidRecoverService;
import common.service.impl.PaidRecoverServiceImpl;

@Clear
public class PaidRecoverController extends Controller {
	private PaidRecoverService prs = new PaidRecoverServiceImpl(); 
	/**
	 * 处理异常订单页面
	 * @author chris
	 */
	@Before({GET.class, GmInterceptor.class})
	@ActionKey("/operation/paidRecover")
	public void paidRecover() {
		render("paid-recover.html");
	}
	
	/**
	 * 根据account查询订单号
	 * @author chris
	 */
	@Before(POST.class)
	@ActionKey("/api/operation/order")
	public void queryOrder(){
		String account = getPara("account", "");
		try {
			String db = URLDecoder.decode(getCookie("server"), "GBK");
			List<List<String>> tableData = prs.queryOrderByAccount(account, db);
			Map<String, Object> data = new HashMap<String, Object>();
			if(tableData.size()==0){
				data.put("result", "0");
				renderJson(data);
				return;
			}
			data.put("reslut", "1");
			data.put("tableData", tableData);
			renderJson(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
