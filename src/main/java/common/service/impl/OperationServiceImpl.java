package common.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import common.model.UserFeedback;
import common.service.OperationService;

/**
 * 用户运营页接口
 * @author chris
 */
public class OperationServiceImpl implements OperationService {
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
		succeed  = new UserFeedback().set("account", account).set("title", title).set("content", content).set("server", server).set("port", port).set("create_time", new Date()).save();
		return succeed;
	}
	
	/**
	 * 查询用户反馈列表
	 * @return List<List<String>> 直接填充datatable
	 */

	public List<List<String>> queryFeedback() {
		String sql = "select account,server from user_feedback group by account,server";
		List<UserFeedback> userFeedback = UserFeedback.dao.find(sql);
		List<List<String>> data = new ArrayList<List<String>>();
		for(UserFeedback uf : userFeedback){
			String account = uf.getStr("account");
			String server = uf.getStr("server");
			List<String> subList = new ArrayList<String>(Arrays.asList(account, server, ""));
			data.add(subList);
		}
		return data;
	}
	
	/**
	 * 查询某个用户的所有反馈
	 * @param queryAccount 帐号id
	 * @param queryServer 服务器
	 * @return List<List<String>> 直接填充datatable
	 */
	public List<List<String>> queryFeedbackDetail(String queryAccount, String queryServer) {
		String sql = "select * from user_feedback where account = ? and server = ?";
		List<UserFeedback> userFeedback = UserFeedback.dao.find(sql, queryAccount, queryServer);
		List<List<String>> data = new ArrayList<List<String>>();
		for(UserFeedback uf : userFeedback){
			String account = uf.getStr("account");
			String title = uf.getStr("title");
			String content = uf.getStr("content")==null ? "": uf.getStr("content");
			String server = uf.getStr("server");
			String port = uf.getStr("port");
			String createTime = uf.getDate("create_time").toString();
			List<String> subList = new ArrayList<String>(Arrays.asList(account,title,content,server,port,createTime,""));
			data.add(subList);
		}
		return data;
	}
}
