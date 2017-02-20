package common.service;

import java.util.Map;
/**
 * 生命轨迹查询接口
 * @author chris
 */
public interface AccdetailService {
	public Map<String, Object> queryAccdetail(String accountId, String db);
}
