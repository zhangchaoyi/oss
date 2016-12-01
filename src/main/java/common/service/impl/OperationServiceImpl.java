package common.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.jfinal.plugin.activerecord.Db;

import common.model.GmRecord;
import common.model.UserFeedback;
import common.mysql.DbSelector;
import common.service.OperationService;

/**
 * 用户运营页接口
 * @author chris
 */
public class OperationServiceImpl implements OperationService {
	private static Logger logger = Logger.getLogger(OperationServiceImpl.class);
	private String db = DbSelector.getDbName();
	/**
	 * 接收玩家反馈,并将其插入到mysql中
	 * @param account 帐号id
	 * @param title 标题
	 * @param content 内容
	 * @param server 服务器
	 * @param port 端口
	 * @return true/false
	 */
	public boolean addFeedback(String account, String title, String content, String server, String port) {
		logger.info("params:{"+"account:"+account+",title:"+title+",content:"+content+",server:"+server+",port:"+port+"}");
		boolean succeed = false;
		succeed  = new UserFeedback().use(db).set("account", account).set("title", title).set("content", content).set("server", server).set("port", port).set("create_time", new Date()).set("reply",0).save();
		logger.info("return:" + succeed);
		return succeed;
	}
	
	/**
	 * 查询用户反馈列表
	 * @return List<List<String>> 直接填充datatable
	 */
	public List<List<String>> queryFeedback(String startDate, String endDate, String server) {
		logger.info("params:{"+"server:"+server+"}");
		String sql = "select * from user_feedback where DATE_FORMAT(create_time,'%Y-%m-%d') between ? and ? and server = ?";
		List<UserFeedback> userFeedback = UserFeedback.dao.use(db).find(sql, startDate, endDate, server);
		List<List<String>> data = new ArrayList<List<String>>();
		for(UserFeedback uf : userFeedback){
			String account = uf.getStr("account");
			String content = uf.getStr("content")==null ? "": uf.getStr("content");
			String createTime = uf.getDate("create_time").toString();
			String reply = uf.getInt("reply").toString();
			String id = uf.getInt("id").toString();
			List<String> subList = new ArrayList<String>(Arrays.asList(id,account,content,createTime,id,reply,id));
			data.add(subList);
		}
		logger.info("data:" + data);
		return data;
	}
	
	/**
	 * 回复反馈成功后根据mysql id 将reply字段修改为1 表示已回复
	 * @param id --mysql row id
	 * @return row id   /0表示失败
	 */
	public int completeReply(int id) {
		logger.info("params:{"+"id"+id+"}");
		String sql = "update user_feedback set reply = 1 where id = ?";
		int succeed = Db.use(db).update(sql, id);
		logger.info("return:" + succeed);
		return succeed;
	}
	
	/**
	 * 删除所选的feedback记录
	 * @para ids  所选的id
	 * @return int  row id  / 0表示失败
	 */
	public int deleteFeedback(String ids) {
		logger.info("params:{"+"ids"+ids+"}");
		String sql = "delete from user_feedback where id in (" + ids +")";
		int deleted = Db.use(db).update(sql);
		logger.info("return:" + deleted);
		return deleted;
	}
	
	/**
	 * 根据某个row id 查询反馈信息
	 * @param id --row id
	 */
	public Map<String, String> queryFeedbackById(String id) {
		logger.info("params:{"+"id:"+id+"}");
		String sql = "select * from user_feedback where id = ?";
		UserFeedback uf = UserFeedback.dao.use(db).findFirst(sql, id);
		Map<String, String> data = new HashMap<String, String>();
		if(uf==null){
			data.put("message", "failed");
			logger.info("<OperationServiceImpl> queryFeedbackById: null" );
			return data;
		}
		String account = uf.getStr("account");
		String content = uf.getStr("content")==null ? "": uf.getStr("content");
		data.put("account",account);
		data.put("content", content);
		logger.info("data:" + data);
		return data;
	}
	
	/**
	 * 插入gm操作
	 * @param account 管理员帐号
	 * @param operation gm指令
	 */
	public boolean insertGmRecord(String account, String operation, String emailAddress){
		logger.info("params:{"+"account:"+account+",operation:"+operation+",emailAddress:"+emailAddress+"}");
		boolean succeed = new GmRecord().use(db).set("account", account).set("operation", operation).set("create_time", new Date()).set("email_address", emailAddress).save();
		return succeed;
	}
}
