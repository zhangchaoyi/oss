package common.service.impl;

import java.util.Date;

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
}
