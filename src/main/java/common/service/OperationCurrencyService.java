package common.service;

import java.util.Map;
/**
 * 货币获取消耗接口
 * @author chris
 */
public interface OperationCurrencyService {
	public Map<String, Object> queryAllCurrency(String startDate, String endDate, String currency, int start, int length, String db);
	
	public Map<String, Object> querySingleCurrency(String startDate, String endDate, String currency, String account, String db);
}
