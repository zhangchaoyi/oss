package common.controllers.operations;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.ext.interceptor.GET;
import com.jfinal.ext.interceptor.POST;

import common.interceptor.RootInterceptor;
import common.interceptor.VipInterceptor;
import common.service.OperationService;
import common.service.impl.OperationServiceImpl;

@Clear
public class FeedbackController extends Controller{
	private static Logger logger = Logger.getLogger(FeedbackController.class);
	private OperationService os = new OperationServiceImpl();
	/**
	 * 用户反馈页
	 * @author chris
	 * @role vip
	 */
	@Before({GET.class, VipInterceptor.class})
	@ActionKey("/operation/feedback")
	public void feedbackIndex() {
		render("userFeedback.html");
	}
	
	/**
	 * 用户反馈接口
	 * @author chris
	 * @getPara account 帐号id
	 * @getPara title 标题
	 * @getPara content 内容
	 * @getPara server 服务器
	 * @getPara port 端口
	 * @role vip 
	 */
	@Before(POST.class)
	@ActionKey("/api/operation/feedback")
	public void feedback() {
		String account = getPara("account");
		String title = getPara("title");
		String content = getPara("content");
		String server = getPara("server");
		String port = getPara("port");
		boolean succeed = os.addFeedback(account, title, content, server, port);
		Map<String, String> data = new HashMap<String, String>();
		data.put("message", String.valueOf(succeed));
		logger.debug("<FeedbackController> feedback:" + data);
		renderJson(data);
	}
}
