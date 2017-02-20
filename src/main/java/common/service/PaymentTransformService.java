package common.service;

import java.util.List;
import java.util.Map;
/**
 * 付费转化接口
 * @author chris
 */
public interface PaymentTransformService {
	
	public Map<String, Object> queryAddPaymentAnalyze(List<String> categories, String icons, String startDate, String endDate, String db);

	public Map<String, Object> queryDayPaidRate(List<String>categories, String icons, String startDate, String endDate, String db);
	
	public Map<String, Object> queryWeekPaidRate(String icons, String startDate, String endDate, String db);
	
	public Map<String, Object> queryMonthPaidRate(List<String>categories, String icons, String startDate, String endDate, String db);
	
	public Map<String, Object> queryAreaPaidRate(String icons, String startDate, String endDate, String tag, String db);
}
