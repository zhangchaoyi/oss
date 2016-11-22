package common.controllers.operations;

import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.ext.interceptor.GET;
import com.jfinal.ext.interceptor.POST;
import common.interceptor.GmInterceptor;
import common.mysql.DbSelector;
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
	@Before({GET.class, GmInterceptor.class})
	@ActionKey("/operation/feedback")
	public void feedbackIndex() {
		DbSelector.setDbName("malai");
		render("userFeedback.html");
	}
	
	/**
	 * 玩家填写反馈页
	 * @author kola
	 * @role all
	 */
	 @Before(GET.class)
	 @ActionKey("/operation/feedback/players")
	 public void feedbackPlayers() {
	 	render("feedBack.html");
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
		logger.info("params:{ " + "account:" + account + " title:" + title + " content:" + content + " server:" + server + " port:" + port + "}");
		if("".equals(account)){
			renderText("提交成功");
			return;
		}
		boolean succeed = os.addFeedback(account, title, content, server, port);
		if(succeed == true){
			logger.info("return:" + "succeed");
			renderText("提交成功");
		}
	}
	
	/**
	 * 查询用户反馈列表
	 * @author chris
	 * @role vip
	 */
	@Before({POST.class, GmInterceptor.class})
	@ActionKey("/api/operation/feedback/list")
	public void queryFeedback() {
		String startDate = getPara("startDate", "");
		String endDate = getPara("endDate", "");
		String server = getPara("server", "");
		logger.info("params {"+"startDate:"+startDate+",endDate:"+endDate+",server:"+server+"}");
		List<List<String>> data = os.queryFeedback(startDate, endDate, server);
		logger.info("return:" + data);
		renderJson(data);
	}
	
	/**
	 * 回复反馈成功修改 reply 为 1 
	 * @author chris
	 * @getPara id  -- mysql row id
	 * @role vip 
	 */
	@Before({POST.class, GmInterceptor.class})
	@ActionKey("/api/operation/feedback/user/reply")
	public void modifyFeedbackUserReply() {
		String id = getPara("id","");
		logger.info("params {" + "id:" + id + "}");
		if("".equals(id)){
			renderText("0");
			return;
		}
		try{
			int succeed = os.completeReply(Integer.parseInt(id));
			renderText(String.valueOf(succeed));
		}catch(Exception e){
			logger.info("Exception:", e);
		}
	}
	/**
	 * 删除所选的feedback
	 * @author chris
	 * @getPara id[]  获取row id列表
	 */
	@Before({POST.class, GmInterceptor.class})
	@ActionKey("/api/operation/feedback/user/delete")
	public void deleteFeedbackUserReply() {
		String ids = StringUtils.arrayToQueryString(getParaValues("ids[]"));
		logger.info("params: {" + "ids:" + ids + "}");
		int deleted = os.deleteFeedback(ids);
		logger.info("return:" + deleted);
		renderText(String.valueOf(deleted));
	}
	
	/**
	 * 根据某个row id 查询反馈信息
	 * @author chris
	 * @getPara id 获取row id
	 */
	@Before({POST.class,GmInterceptor.class})
	@ActionKey("/api/operation/feedback/user/detail")
	public void deleteFeedbackUser() {
		String id = getPara("id");
		logger.info("params: {" + "id:" + id + "}");
		Map<String, String> data = os.queryFeedbackById(id);
		renderJson(data);
	}
}
