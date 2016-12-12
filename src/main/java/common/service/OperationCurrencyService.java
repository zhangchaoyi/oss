package common.service;

import java.util.List;

public interface OperationCurrencyService {
	public List<List<String>> queryAllCurrency(String startDate, String endDate);
	
	public List<List<String>> querySingleCurrency(String startDate, String endDate, String account);
}
