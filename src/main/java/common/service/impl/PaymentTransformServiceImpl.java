package common.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import common.model.LogCharge;
import common.model.Login;
import common.model.PaymentDetail;
import common.pojo.AddPaymentAnalyze;
import common.pojo.PaidRate;
import common.service.PaymentTransformService;
import common.utils.DateUtils;

/**
 * 付费转化页
 * @author chris
 *
 */
public class PaymentTransformServiceImpl implements PaymentTransformService{
	//新增付费分析
	public Map<String, Object> queryAddPaymentAnalyze(List<String> categories, String icons, String startDate, String endDate) {
		String sql = "select DATE_FORMAT(date,'%Y-%m-%d')date,sum(add_players)add_players,sum(fd_paid_people)fd_paid_people,sum(fw_paid_people)fw_paid_people,sum(fm_paid_people)fm_paid_people from payment_detail where date between ? and ? and os in (" + icons + ") group by date";
		List<PaymentDetail> apAnalyze = PaymentDetail.dao.find(sql,startDate,endDate);
		
		//initial
		Map<String, AddPaymentAnalyze> sort = new LinkedHashMap<String, AddPaymentAnalyze>();
		for(String date : categories){
			AddPaymentAnalyze apa = new AddPaymentAnalyze();
			sort.put(date, apa);
		}
		
		//load data
		for(PaymentDetail pd : apAnalyze){
			String date = pd.getStr("date");
			int addPlayers = pd.getBigDecimal("add_players").intValue();
			int fdPaidPeople = pd.getBigDecimal("fd_paid_people").intValue();
			int fwPaidPeople = pd.getBigDecimal("fw_paid_people").intValue();
			int fmPaidPeople = pd.getBigDecimal("fm_paid_people").intValue();
			AddPaymentAnalyze apa = sort.get(date);
			apa.setAddPlayers(addPlayers);
			apa.setFdPaidPeople(fdPaidPeople);
			apa.setFwPaidPeople(fwPaidPeople);
			apa.setFmPaidPeople(fmPaidPeople);
			sort.put(date,apa);
		}
		List<Integer> aP = new ArrayList<Integer>();
		List<Double> fdPP = new ArrayList<Double>();
		List<Double> fwPP = new ArrayList<Double>();
		List<Double> fmPP = new ArrayList<Double>();
		List<List<String>> tableData = new ArrayList<List<String>>();
		for(Map.Entry<String,AddPaymentAnalyze> entry : sort.entrySet()){
			AddPaymentAnalyze apa = entry.getValue();
			int ap = apa.getAddPlayers();
			int fd = apa.getFdPaidPeople();
			int fw = apa.getFwPaidPeople();
			int fm = apa.getFmPaidPeople();
			double fdr = 0.00;
			double fwr = 0.00;
			double fmr = 0.00;
			
			List<String> subList = new ArrayList<String>();
			if(ap==0){
				subList.addAll(Arrays.asList(entry.getKey(),"0","0("+ fdr +"%)","0("+ fwr +"%)","0("+ fmr +"%)"));
			}else{
				fdr = fd*100.0/ap;
				fwr = fw*100.0/ap;
				fmr = fm*100.0/ap;
				BigDecimal bgFd = new BigDecimal(fdr);
				BigDecimal bgFw = new BigDecimal(fwr);
				BigDecimal bgFm = new BigDecimal(fmr);
				fdr = bgFd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				fwr = bgFw.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				fmr = bgFm.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				
				subList.addAll(Arrays.asList(entry.getKey(),String.valueOf(ap),fd+"("+fdr+"%)",fw+"("+fwr+"%)",fm+"("+fmr+"%)"));
			}
			fdPP.add(fdr);
			fwPP.add(fwr);
			fmPP.add(fmr);
			tableData.add(subList);
			aP.add(ap);
		}
		
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("tableData", tableData);
		data.put("aP", aP);
		data.put("fdPP", fdPP);
		data.put("fwPP", fwPP);
		data.put("fmPP", fmPP);
		return data;
	}
	
	//日付费率
	public Map<String, Object> queryDayPaidRate(List<String>categories, String icons, String startDate, String endDate) {
		String pSql = "select DATE_FORMAT(A.timestamp,'%Y-%m-%d') date, count(distinct A.account)count from log_charge A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where DATE_FORMAT(A.timestamp,'%Y-%m-%d') between ? and ? and C.os in (" + icons + ") group by date";
		String aSql = "select DATE_FORMAT(A.date,'%Y-%m-%d') date, count(distinct A.account)count from login A join device_info B on A.openudid = B.openudid where DATE_FORMAT(login_time,'%Y-%m-%d') between ? and ? and B.os in (" + icons + ") group by date";
		
		List<LogCharge> paid = LogCharge.dao.find(pSql, startDate, endDate);
		List<Login> active = Login.dao.find(aSql, startDate, endDate);
		
		//init 
		Map<String, PaidRate> sort = new HashMap<String, PaidRate>();
		for(String date : categories){
			PaidRate pr = new PaidRate();
			sort.put(date, pr);
		}
		//load data
		for(LogCharge lc : paid){
			String date = lc.getStr("date");
			PaidRate pr = sort.get(date);
			pr.setPaidPlayers(lc.getLong("count"));
			sort.put(date, pr);
		}
		for(Login l : active){
			String date = l.getStr("date");
			PaidRate pr = sort.get(date);
			pr.setActivePlayers(l.getLong("count"));
			sort.put(date, pr);
		}
		
		List<Double> rate = new ArrayList<Double>();
		List<List<String>> tableData = new ArrayList<List<String>>();
		
		for(Map.Entry<String, PaidRate> entry : sort.entrySet()){
			PaidRate pr = entry.getValue();
			long paidCount = pr.getPaidPlayers();
			long activeCount = pr.getActivePlayers();
			double paidRate = 0.0;
			
			if(activeCount!=0){
				paidRate = (double)paidCount*100.0/(double)activeCount;
				BigDecimal bgPr = new BigDecimal(paidRate);
				paidRate = bgPr.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			}
			rate.add(paidRate);
			
			List<String> subList = new ArrayList<String>();
			subList.addAll(Arrays.asList(entry.getKey(), String.valueOf(paidCount), String.valueOf(activeCount), paidRate+"%"));
			tableData.add(subList);
		}
		
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("dpr", rate);
		data.put("tableData", tableData);
		return data;
	}
	//周付费率
	public Map<String, Object> queryWeekPaidRate(List<String>categories, String icons, String startDate, String endDate) {
		Map<String, String> week = DateUtils.divideDateToWeek(startDate, endDate);
		Map<String, PaidRate> sort = new LinkedHashMap<String, PaidRate>();
		
		String pSql = "select count(distinct A.account)count from log_charge A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where DATE_FORMAT(A.timestamp,'%Y-%m-%d') between ? and ? and C.os in (" + icons + ")";
		String aSql = "select count(distinct A.account)count from login A join device_info B on A.openudid = B.openudid where DATE_FORMAT(login_time,'%Y-%m-%d') between ? and ? and B.os in (" + icons + ")";
		//循环执行 各周时间段 语句
		for(Map.Entry<String, String> entry : week.entrySet()){
			String start = entry.getKey();
			String end = entry.getValue();
			String period = start + "~" + end;
			
			List<LogCharge> paid = LogCharge.dao.find(pSql, start, end);
			List<Login> active = Login.dao.find(aSql, start, end);
			
			Long paidCount = 0L;
			Long activeCount = 0L;
			for(LogCharge lc : paid){
				paidCount = lc.getLong("count");
			}
			for(Login l : active){
				activeCount = l.getLong("count");
			}
			//initial
			PaidRate pr = new PaidRate();
			pr.setPaidPlayers(paidCount);
			pr.setActivePlayers(activeCount);
			sort.put(period, pr);
		}
		
		List<Double> rate = new ArrayList<Double>();
		List<List<String>> tableData = new ArrayList<List<String>>();
		//load data
		for(Map.Entry<String, PaidRate> entry : sort.entrySet()){
			PaidRate pr = entry.getValue();
			long paidCount = pr.getPaidPlayers();
			long activeCount = pr.getActivePlayers();
			double paidRate = 0.0;
			
			if(activeCount!=0){
				paidRate = (double)paidCount*100.0/(double)activeCount;
				BigDecimal bgPr = new BigDecimal(paidRate);
				paidRate = bgPr.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			}
			rate.add(paidRate);
			
			List<String> subList = new ArrayList<String>();
			subList.addAll(Arrays.asList(entry.getKey(), String.valueOf(paidCount), String.valueOf(activeCount), paidRate+"%"));
			tableData.add(subList);
		}
		categories.clear();
		categories.addAll(sort.keySet());
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("wpr", rate);
		data.put("tableData", tableData);
		data.put("categories", categories);
		return data;
	}
	
	//月付费率
	public Map<String, Object> queryMonthPaidRate(List<String>categories, String icons, String startDate, String endDate) {
		String start = DateUtils.monthToStr(DateUtils.strToDate(startDate));
		String end = DateUtils.monthToStr(DateUtils.strToDate(endDate));
		
		String pSql = "select DATE_FORMAT(A.timestamp,'%Y-%m') month, count(distinct A.account)count from log_charge A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where DATE_FORMAT(A.timestamp,'%Y-%m') between ? and ? and C.os in (" + icons + ") group by month";
		String aSql = "select DATE_FORMAT(A.date,'%Y-%m') month, count(distinct A.account)count from login A join device_info B on A.openudid = B.openudid where DATE_FORMAT(login_time,'%Y-%m') between ? and ? and B.os in (" + icons + ") group by month";
		
		List<String> month = DateUtils.getMonthList(start, end);
		
		Map<String, Object> data = new HashMap<String, Object>();
		return data;
	} 
	
}
