package common.service;

import java.util.List;
import java.util.Map;

import common.model.LogCharge;


public interface PaymentDataService {
	
	public Map<String, Map<String,Object>> queryMoneyPayment(List<String> categories, String startDate, String endDate, String icons);
	
	public Map<String, Map<String,Object>> queryPeoplePayment(List<String> categories, String startDate, String endDate, String icons);
	
	public Map<String, Map<String,Object>> queryNumPayment(List<String> categories, String startDate, String endDate, String icons);
	
	public List<List<Object>> queryDataPayment(List<String> categories, String startDate, String endDate, String icons);
	
	public List<Integer> queryDayPaymentMoney(List<String> categories, String icons, String startDate, String endDate);
	
	public List<Integer> queryDayPaymentTimes(List<String> categories, String icons, String startDate, String endDate);
	
	public List<Double> queryDayARPU(List<String> categories, String icons, String startDate, String endDate);
	
	public List<Double> queryDayARPPU(List<String> categories, String icons, String startDate, String endDate);
	
	public List<List<Object>> queryAllPaymentMoney(List<String> categories, String icons, String startDate, String endDate);

	public List<List<Object>> queryAllPaymentTimes(List<String> categories, String icons, String startDate, String endDate);
	
	public List<List<Object>> queryArpu(List<String> categories, String icons, String startDate, String endDate);
	
	public List<List<Object>> queryArppu(List<String> categories, String icons, String startDate, String endDate);
	
	public List<LogCharge> queryAreaRevenue(String icons, String startDate, String endDate);
	
	public Map<String, Object> queryAreaARPU(String icons, String startDate, String endDate);

	public Map<String,Object> queryAreaARPPU(String icons, String startDate, String endDate);
	
	public List<LogCharge> queryCountryRevenue(String icons, String startDate, String endDate);
	
	public Map<String, Object> queryCountryARPU(String icons, String startDate, String endDate);
	
	public Map<String,Object> queryCountryARPPU(String icons, String startDate, String endDate);
}
