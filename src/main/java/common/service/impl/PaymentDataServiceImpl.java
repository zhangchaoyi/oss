package common.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import common.model.PaymentDetail;
import common.service.PaymentDataService;

public class PaymentDataServiceImpl implements PaymentDataService {
	public Map<String, Map<String,Object>> queryMoneyPayment(List<String> categories, String startDate, String endDate, String icons){
		String sql = "select DATE_FORMAT(date,'%Y-%m-%d')date,sum(paid_money)paid_money, sum(ft_paid_money)ft_paid_money, sum(fd_paid_money)fd_paid_money from payment_detail where date between ? and ? and os in (" + icons + ") group by date";
		List<PaymentDetail> paymentDetail = PaymentDetail.dao.find(sql, startDate, endDate);
		//每个map分别处理 金额/人数/次数
		Map<String, Double> paid = new LinkedHashMap<String, Double>();
		Map<String, Double> ftPaid = new LinkedHashMap<String, Double>();
		Map<String, Double> fdPaid = new LinkedHashMap<String, Double>();
		Map<String, Object> sum = new LinkedHashMap<String, Object>();
		Map<String, Object> series = new LinkedHashMap<String, Object>();
		//初始化防止日期空值
		for(String c : categories){
			paid.put(c, 0.0);
			ftPaid.put(c, 0.0);
			fdPaid.put(c, 0.0);
		}
		Double pDSum = 0.00;
		Double ftPDSum = 0.00;
		Double fdPDSum = 0.00;
		//分别载入数据
		for(PaymentDetail pd : paymentDetail){
			Double pD = pd.getDouble("paid_money");
			Double ftPD = pd.getDouble("ft_paid_money");
			Double fdPD = pd.getDouble("fd_paid_money");
			paid.put(pd.getStr("date"), pD);
			ftPaid.put(pd.getStr("date"), ftPD);
			fdPaid.put(pd.getStr("date"), fdPD);
			pDSum += pD;
			ftPDSum += ftPD;
			fdPDSum += fdPD;
		}
		BigDecimal bgPDSum = new BigDecimal(pDSum);
		BigDecimal bgftPDSum = new BigDecimal(ftPDSum);
		BigDecimal bgfdPDSum = new BigDecimal(fdPDSum);
		pDSum = bgPDSum.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		ftPDSum = bgftPDSum.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		fdPDSum = bgfdPDSum.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		
		sum.put("活跃", "¥" + String.valueOf(pDSum));
		sum.put("首次付费", "¥" + String.valueOf(ftPDSum));
		sum.put("首日付费", "¥" + String.valueOf(fdPDSum));
		List<Double> paidList = new ArrayList<Double>();
		List<Double> ftPaidList = new ArrayList<Double>();
		List<Double> fdPaidList = new ArrayList<Double>();
		paidList.addAll(paid.values());
		ftPaidList.addAll(ftPaid.values());
		fdPaidList.addAll(fdPaid.values());
		
		Map<String, Map<String,Object>> data = new HashMap<String, Map<String, Object>>();
		series.put("活跃玩家(¥)", paidList);
		series.put("首次付费玩家(¥)", ftPaidList);
		series.put("首日付费玩家(¥)", fdPaidList);
		data.put("sum", sum);
		data.put("series", series);
		return data;
	}
	
	public Map<String, Map<String,Object>> queryPeoplePayment(List<String> categories, String startDate, String endDate, String icons) {
		String sql = "select DATE_FORMAT(date,'%Y-%m-%d')date,sum(paid_people)paid_people, sum(ft_paid_people)ft_paid_people, sum(fd_paid_people)fd_paid_people from payment_detail where date between ? and ? and os in (" + icons + ") group by date";
		List<PaymentDetail> paymentDetail = PaymentDetail.dao.find(sql, startDate, endDate);
		
		Map<String, Long> paid = new LinkedHashMap<String, Long>();
		Map<String, Long> ftPaid = new LinkedHashMap<String, Long>();
		Map<String, Long> fdPaid = new LinkedHashMap<String, Long>();
		Map<String, Object> sum = new LinkedHashMap<String, Object>();
		Map<String, Object> series = new LinkedHashMap<String, Object>();
		
		for(String c : categories){
			paid.put(c, 0L);
			ftPaid.put(c, 0L);
			fdPaid.put(c, 0L);
		}
		long pPSum = 0L;
		long ftPSum = 0L;
		long fdPSum = 0L;
		for(PaymentDetail pd : paymentDetail){
			long pP = pd.getBigDecimal("paid_people").longValue();
			long ftpP = pd.getBigDecimal("ft_paid_people").longValue();
			long fdpP = pd.getBigDecimal("fd_paid_people").longValue();
			paid.put(pd.getStr("date"), pP);
			ftPaid.put(pd.getStr("date"), ftpP);
			fdPaid.put(pd.getStr("date"), fdpP);
			pPSum += pP;
			ftPSum += ftpP;
			fdPSum += fdpP;
		}
		
		sum.put("活跃", pPSum);
		sum.put("首次付费", ftPSum);
		sum.put("首日付费", fdPSum);
		
		List<Long> paidList = new ArrayList<Long>();
		List<Long> ftPaidList = new ArrayList<Long>();
		List<Long> fdPaidList = new ArrayList<Long>();
		paidList.addAll(paid.values());
		ftPaidList.addAll(ftPaid.values());
		fdPaidList.addAll(fdPaid.values());
		
		Map<String, Map<String,Object>> data = new HashMap<String, Map<String,Object>>();
		series.put("活跃玩家", paidList);
		series.put("首次付费玩家", ftPaidList);
		series.put("首日付费玩家", fdPaidList);
		
		data.put("sum", sum);
		data.put("series", series);
		return data;
	}
	
	public Map<String, Map<String,Object>> queryNumPayment(List<String> categories, String startDate, String endDate, String icons) {
		String sql = "select DATE_FORMAT(date,'%Y-%m-%d')date, sum(paid_num)paid_num, sum(fd_paid_num)fd_paid_num from payment_detail where date between ? and ? and os in (" + icons + ") group by date";
		List<PaymentDetail> paymentDetail = PaymentDetail.dao.find(sql, startDate, endDate);
		
		Map<String, Long> paid = new LinkedHashMap<String, Long>();
		Map<String, Long> fdPaid = new LinkedHashMap<String, Long>();
		Map<String, Object> sum = new LinkedHashMap<String, Object>();
		Map<String, Object> series = new LinkedHashMap<String, Object>();
		
		for(String c : categories){
			paid.put(c, 0L);
			fdPaid.put(c, 0L);
		}
		
		long pNSum = 0L;
		long fdNSum = 0L;
		for(PaymentDetail pd : paymentDetail){
			long pN = pd.getBigDecimal("paid_num").longValue();
			long fdN = pd.getBigDecimal("fd_paid_num").longValue();
			paid.put(pd.getStr("date"), pN);
			fdPaid.put(pd.getStr("date"), fdN);
			pNSum += pN;
			fdNSum += fdN;
		}
		sum.put("活跃", pNSum);
		sum.put("首日付费", fdNSum);
		
		List<Long> paidList = new ArrayList<Long>();
		List<Long> fdPaidList = new ArrayList<Long>();
		paidList.addAll(paid.values());
		fdPaidList.addAll(fdPaid.values());
		
		Map<String, Map<String,Object>> data = new HashMap<String, Map<String,Object>>();
		series.put("活跃玩家", paidList);
		series.put("首日付费玩家", fdPaidList);
		data.put("sum", sum);
		data.put("series", series);
		return data;
	}
}
