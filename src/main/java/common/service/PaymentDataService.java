package common.service;

import java.util.List;
import java.util.Map;


public interface PaymentDataService {
	
	public Map<String, Map<String,Object>> queryMoneyPayment(List<String> categories, String startDate, String endDate, String icons);
	
	public Map<String, Map<String,Object>> queryPeoplePayment(List<String> categories, String startDate, String endDate, String icons);
	
	public Map<String, Map<String,Object>> queryNumPayment(List<String> categories, String startDate, String endDate, String icons);
	
	public List<List<Object>> queryDataPayment(List<String> categories, String startDate, String endDate, String icons);
	
	public List<Integer> queryDayPaymentMoney(List<String> categories, String icons, String startDate, String endDate);
	
	public List<Integer> queryDayPaymentTimes(List<String> categories, String icons, String startDate, String endDate);
	
	public Map<String,Object> queryDayARPU(List<String> categories, String icons, String startDate, String endDate);
}
