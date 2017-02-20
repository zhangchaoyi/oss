package common.service;

import java.util.List;
/**
 * 物品获取消耗接口
 * @author chris
 */
public interface OperationObjectService {
	public List<List<String>> querySingleObject(String startDate, String endDate, String account, String db);
}
