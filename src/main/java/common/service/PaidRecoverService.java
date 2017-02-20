package common.service;

import java.util.List;
/**
 * 订单恢复接口
 * @author chris
 */
public interface PaidRecoverService {
	public List<List<String>> queryOrderByAccount(String account, String db);
}
