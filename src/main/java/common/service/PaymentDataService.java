package common.service;

import java.util.List;
import java.util.Map;

public interface PaymentDataService {
	
	public Map<String, Map<String,Object>> queryMoneyPayment(List<String> categories, String startDate, String endDate, String icons);
	
	public Map<String, Map<String,Object>> queryPeoplePayment(List<String> categories, String startDate, String endDate, String icons);
	
	public Map<String, Map<String,Object>> queryNumPayment(List<String> categories, String startDate, String endDate, String icons);
}
