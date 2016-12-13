package common.service;

import java.util.List;
import java.util.Map;

public interface OperationCurrencyService {
	public Map<String, Object> queryAllCurrency(String startDate, String endDate, String currency, int start, int length);
	
	public List<List<String>> querySingleCurrency(String startDate, String endDate, String currency, String account);
}
