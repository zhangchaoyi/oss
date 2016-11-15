package common.controllers.operations;

import java.util.List;

import org.apache.log4j.Logger;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.ext.interceptor.GET;
import com.jfinal.ext.interceptor.POST;
import common.interceptor.VipInterceptor;
import common.service.OperationService;
import common.service.impl.OperationServiceImpl;
import common.utils.StringUtils;

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
		String account = getPara("account", "");
		String title = getPara("title", "");
		String content = getPara("content", "");
		String server = getPara("server", "");
		String port = getPara("port", "");
		if("".equals(account)){
			renderText("提交成功");
			return;
		}
		boolean succeed = os.addFeedback(account, title, content, server, port);
		if(succeed == true){
			logger.debug("<FeedbackController> feedback:" + "succeed");
			renderText("提交成功");
		}
	}
	
	/**
	 * 查询用户反馈列表
	 * @author chris
	 * @role vip
	 */
	@Before({POST.class, VipInterceptor.class})
	@ActionKey("/api/operation/feedback/list")
	public void queryFeedback() {
		String startDate = getPara("startDate", "");
		String endDate = getPara("endDate", "");
		List<List<String>> data = os.queryFeedback(startDate, endDate);
		renderJson(data);
	}
	
	/**
	 * 查询某个用户所有的反馈
	 * @author chris
	 * @getPara account 帐号 
	 * @getPara server 服务器
	 * @role vip
	 */
	@Before({POST.class, VipInterceptor.class})
	@ActionKey("/api/operation/feedback/user")
	public void queryFeedbackUser() {
		String account = getPara("account", "");
		String server = getPara("server", "");
		String startDate = getPara("startDate","");
		String endDate = getPara("endDate", "");
		List<List<String>> data = os.queryFeedbackDetail(account, server, startDate, endDate);
		renderJson(data);
	}
	
	/**
	 * 回复反馈成功修改 reply 为 1 
	 * @author chris
	 * @getPara id  -- mysql row id
	 * @role vip 
	 */
	@Before({POST.class, VipInterceptor.class})
	@ActionKey("/api/operation/feedback/user/reply")
	public void modifyFeedbackUserReply() {
		String id = getPara("id");
		try{
			int succeed = os.completeReply(Integer.parseInt(id));
			renderText(String.valueOf(succeed));
		}catch(Exception e){
			logger.debug("<FeedbackController> modifyFeedbackUserReply:", e);
		}
	}
	/**
	 * 删除所选的feedback
	 * @author chris
	 * @getPara id[]  获取row id列表
	 */
	@Before({POST.class, VipInterceptor.class})
	@ActionKey("/api/operation/feedback/user/delete")
	public void deleteFeedbackUserReply() {
		String ids = StringUtils.arrayToQueryString(getParaValues("ids[]"));
		int deleted = os.deleteFeedback(ids);
		logger.debug("<FeedbackController> deleteFeedbackUserReply:" + deleted);
		renderText(String.valueOf(deleted));
	}
}
