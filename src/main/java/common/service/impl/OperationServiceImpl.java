package common.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.jfinal.plugin.activerecord.Db;

import common.model.UserFeedback;
import common.service.OperationService;

/**
 * 用户运营页接口
 * @author chris
 */
public class OperationServiceImpl implements OperationService {
	private static Logger logger = Logger.getLogger(OperationServiceImpl.class);
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
		boolean succeed = false;
		succeed  = new UserFeedback().set("account", account).set("title", title).set("content", content).set("server", server).set("port", port).set("create_time", new Date()).set("reply",0).save();
		logger.debug("<OperationServiceImpl> addFeedback:" + succeed);
		return succeed;
	}
	
	/**
	 * 查询用户反馈列表
	 * @return List<List<String>> 直接填充datatable
	 */
	public List<List<String>> queryFeedback(String startDate, String endDate) {
		String sql = "select account,server from user_feedback where DATE_FORMAT(create_time,'%Y-%m-%d') between ? and ? group by account,server";
		List<UserFeedback> userFeedback = UserFeedback.dao.find(sql, startDate, endDate);
		List<List<String>> data = new ArrayList<List<String>>();
		for(UserFeedback uf : userFeedback){
			String account = uf.getStr("account");
			String server = uf.getStr("server");
			List<String> subList = new ArrayList<String>(Arrays.asList(account, server, ""));
			data.add(subList);
		}
		logger.debug("<OperationServiceImpl> queryFeedback:" + data);
		return data;
	}
	
	/**
	 * 查询某个用户的所有反馈
	 * @param queryAccount 帐号id
	 * @param queryServer 服务器
	 * @return List<List<String>> 直接填充datatable
	 */
	public List<List<String>> queryFeedbackDetail(String queryAccount, String queryServer, String startDate, String endDate) {
		String sql = "select * from user_feedback where account = ? and server = ? and DATE_FORMAT(create_time,'%Y-%m-%d') between ? and ?";
		List<UserFeedback> userFeedback = UserFeedback.dao.find(sql, queryAccount, queryServer, startDate, endDate);
		List<List<String>> data = new ArrayList<List<String>>();
		for(UserFeedback uf : userFeedback){
			String account = uf.getStr("account");
			String title = uf.getStr("title");
			String content = uf.getStr("content")==null ? "": uf.getStr("content");
			String server = uf.getStr("server");
			String port = uf.getStr("port");
			String createTime = uf.getDate("create_time").toString();
			String reply = uf.getInt("reply").toString();
			String id = uf.getInt("id").toString();
			List<String> subList = new ArrayList<String>(Arrays.asList(id,account,title,content,server,port,createTime,reply,id));
			data.add(subList);
		}
		logger.debug("<OperationServiceImpl> queryFeedbackDetail:" + data);
		return data;
	}
	
	/**
	 * 回复反馈成功后根据mysql id 将reply字段修改为1 表示已回复
	 * @param id --mysql row id
	 * @return row id   /0表示失败
	 */
	public int completeReply(int id) {
		String sql = "update user_feedback set reply = 1 where id = ?";
		int succeed = Db.update(sql, id);
		logger.debug("<OperationServiceImpl> completeReply:" + succeed);
		return succeed;
	}
	
	/**
	 * 删除所选的feedback记录
	 * @para ids  所选的id
	 * @return int  row id  / 0表示失败
	 */
	public int deleteFeedback(String ids) {
		String sql = "delete from user_feedback where id in (" + ids +")";
		int deleted = Db.update(sql);
		logger.debug("<OperationServiceImpl> deleteFeedback:" + deleted);
		return deleted;
	}
}
