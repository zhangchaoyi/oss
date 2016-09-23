package common.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import common.model.PaymentDetail;
import common.service.PaymentDataService;

public class PaymentDataServiceImpl implements PaymentDataService {
	public Map<String, Object> queryMoneyPayment(List<String> categories, String startDate, String endDate, String icons){
		String sql = "select DATE_FORMAT(date,'%Y-%m-%d')date,sum(paid_money)paid_money, sum(ft_paid_money)ft_paid_money, sum(fd_paid_money)fd_paid_money from payment_detail where date between ? and ? and os in (" + icons + ") group by date";
		List<PaymentDetail> paymentDetail = PaymentDetail.dao.find(sql, startDate, endDate);
		//每个map分别处理 金额/人数/次数
		Map<String, Double> paid = new LinkedHashMap<String, Double>();
		Map<String, Double> ftPaid = new LinkedHashMap<String, Double>();
		Map<String, Double> fdPaid = new LinkedHashMap<String, Double>();
		//初始化防止日期空值
		for(String c : categories){
			paid.put(c, 0.0);
			ftPaid.put(c, 0.0);
			fdPaid.put(c, 0.0);
		}
		//分别载入数据
		for(PaymentDetail pd : paymentDetail){
			paid.put(pd.getStr("date"), pd.getDouble("paid_money"));
			ftPaid.put(pd.getStr("date"), pd.getDouble("ft_paid_money"));
			fdPaid.put(pd.getStr("date"), pd.getDouble("fd_paid_money"));
		}
		List<Double> paidList = new ArrayList<Double>();
		List<Double> ftPaidList = new ArrayList<Double>();
		List<Double> fdPaidList = new ArrayList<Double>();
		paidList.addAll(paid.values());
		ftPaidList.addAll(ftPaid.values());
		fdPaidList.addAll(fdPaid.values());
		
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("活跃玩家", paidList);
		data.put("首次付费玩家", ftPaidList);
		data.put("首日付费玩家", fdPaidList);
		return data;
	}
	
	public Map<String, Object> queryPeoplePayment(List<String> categories, String startDate, String endDate, String icons) {
		String sql = "select DATE_FORMAT(date,'%Y-%m-%d')date,sum(paid_people)paid_people, sum(ft_paid_people)ft_paid_people, sum(fd_paid_people)fd_paid_people from payment_detail where date between ? and ? and os in (" + icons + ") group by date";
		List<PaymentDetail> paymentDetail = PaymentDetail.dao.find(sql, startDate, endDate);
		
		Map<String, Long> paid = new LinkedHashMap<String, Long>();
		Map<String, Long> ftPaid = new LinkedHashMap<String, Long>();
		Map<String, Long> fdPaid = new LinkedHashMap<String, Long>();
		
		for(String c : categories){
			paid.put(c, 0L);
			ftPaid.put(c, 0L);
			fdPaid.put(c, 0L);
		}
		
		for(PaymentDetail pd : paymentDetail){
			paid.put(pd.getStr("date"), pd.getBigDecimal("paid_people").longValue());
			ftPaid.put(pd.getStr("date"), pd.getBigDecimal("ft_paid_people").longValue());
			fdPaid.put(pd.getStr("date"), pd.getBigDecimal("fd_paid_people").longValue());
		}
		
		List<Long> paidList = new ArrayList<Long>();
		List<Long> ftPaidList = new ArrayList<Long>();
		List<Long> fdPaidList = new ArrayList<Long>();
		paidList.addAll(paid.values());
		ftPaidList.addAll(ftPaid.values());
		fdPaidList.addAll(fdPaid.values());
		
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("活跃玩家", paidList);
		data.put("首次付费玩家", ftPaidList);
		data.put("首日付费玩家", fdPaidList);
		return data;
	}
	
	public Map<String, Object> queryNumPayment(List<String> categories, String startDate, String endDate, String icons) {
		String sql = "select DATE_FORMAT(date,'%Y-%m-%d')date, sum(paid_num)paid_num, sum(fd_paid_num)fd_paid_num from payment_detail where date between ? and ? and os in (" + icons + ") group by date";
		List<PaymentDetail> paymentDetail = PaymentDetail.dao.find(sql, startDate, endDate);
		
		Map<String, Long> paid = new LinkedHashMap<String, Long>();
		Map<String, Long> fdPaid = new LinkedHashMap<String, Long>();
		
		for(String c : categories){
			paid.put(c, 0L);
			fdPaid.put(c, 0L);
		}
		
		for(PaymentDetail pd : paymentDetail){
			paid.put(pd.getStr("date"), pd.getBigDecimal("paid_num").longValue());
			fdPaid.put(pd.getStr("date"), pd.getBigDecimal("fd_paid_num").longValue());
		}
		
		List<Long> paidList = new ArrayList<Long>();
		List<Long> fdPaidList = new ArrayList<Long>();
		paidList.addAll(paid.values());
		fdPaidList.addAll(fdPaid.values());
		
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("活跃玩家", paidList);
		data.put("首日付费玩家", fdPaidList);
		return data;
	}
}
