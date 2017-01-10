package common.service;

import java.util.List;
import java.util.Map;

public interface OperationCurrencyService {
	public Map<String, Object> queryAllCurrency(String startDate, String endDate, String currency, int start, int length, String db);
	
	public Map<String, Object> querySingleCurrency(String startDate, String endDate, String currency, String account, String db);
}
