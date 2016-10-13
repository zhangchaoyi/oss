package common.service;

import java.util.List;
import java.util.Map;

public interface PaymentTransformService {
	
	public Map<String, Object> queryAddPaymentAnalyze(List<String> categories, String icons, String startDate, String endDate);

	public Map<String, Object> queryDayPaidRate(List<String>categories, String icons, String startDate, String endDate);
	
	public Map<String, Object> queryWeekPaidRate(List<String>categories, String icons, String startDate, String endDate);
}
