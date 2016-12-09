package common.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import common.model.LogCharge;
import common.model.Login;
import common.model.PaymentDetail;
import common.mysql.DbSelector;
import common.pojo.AreaARU;
import common.service.PaymentDataService;

/**
 * 查询处理付费数据页
 * @author chris
 *
 */
public class PaymentDataServiceImpl implements PaymentDataService {
	private static Logger logger = Logger.getLogger(PaymentDataServiceImpl.class);
	private String db = DbSelector.getDbName();
	/**
	 * 计算付费金额
	 * @param categories 日期列表
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间
	 */
	public Map<String, Map<String,Object>> queryMoneyPayment(List<String> categories, String startDate, String endDate, String icons){
		String sql = "select DATE_FORMAT(date,'%Y-%m-%d')date,sum(paid_money)paid_money, sum(ft_paid_money)ft_paid_money, sum(fd_paid_money)fd_paid_money from payment_detail where date between ? and ? and os in (" + icons + ") group by date";
		List<PaymentDetail> paymentDetail = PaymentDetail.dao.use(db).find(sql, startDate, endDate);
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
		
		sum.put("活跃", "$" + String.valueOf(pDSum));
		sum.put("首次付费", "$" + String.valueOf(ftPDSum));
		sum.put("首日付费", "$" + String.valueOf(fdPDSum));
		List<Double> paidList = new ArrayList<Double>();
		List<Double> ftPaidList = new ArrayList<Double>();
		List<Double> fdPaidList = new ArrayList<Double>();
		paidList.addAll(paid.values());
		ftPaidList.addAll(ftPaid.values());
		fdPaidList.addAll(fdPaid.values());
		
		Map<String, Map<String,Object>> data = new HashMap<String, Map<String, Object>>();
		series.put("活跃玩家($)", paidList);
		series.put("首次付费玩家($)", ftPaidList);
		series.put("首日付费玩家($)", fdPaidList);
		data.put("sum", sum);
		data.put("series", series);
		logger.info("data:" + data);
		return data;
	}
	/**
	 * 计算付费人数
	 * @param categories 日期列表
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间
	 */
	public Map<String, Map<String,Object>> queryPeoplePayment(List<String> categories, String startDate, String endDate, String icons) {
		String sql = "select DATE_FORMAT(date,'%Y-%m-%d')date,sum(paid_people)paid_people, sum(ft_paid_people)ft_paid_people, sum(fd_paid_people)fd_paid_people from payment_detail where date between ? and ? and os in (" + icons + ") group by date";
		List<PaymentDetail> paymentDetail = PaymentDetail.dao.use(db).find(sql, startDate, endDate);
		
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
		logger.info("data:" + data);
		return data;
	}
	/**
	 * 计算付费次数
	 * @param categories 日期列表
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间
	 */
	public Map<String, Map<String,Object>> queryNumPayment(List<String> categories, String startDate, String endDate, String icons) {
		String sql = "select DATE_FORMAT(date,'%Y-%m-%d')date, sum(paid_num)paid_num, sum(fd_paid_num)fd_paid_num from payment_detail where date between ? and ? and os in (" + icons + ") group by date";
		List<PaymentDetail> paymentDetail = PaymentDetail.dao.use(db).find(sql, startDate, endDate);
		
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
		logger.info("data:" + data);
		return data;
	}

	/**
	 * 查询所有付费数据的table数据
	 * @param categories 日期列表
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间
	 */
	public List<List<Object>> queryDataPayment(List<String> categories, String startDate, String endDate, String icons){
		String sql = "select DATE_FORMAT(date,'%Y-%m-%d')date,sum(paid_money)paid_money,sum(paid_people)paid_people,sum(paid_num)paid_num,sum(ft_paid_money)ft_paid_money,sum(ft_paid_people)ft_paid_people,sum(fd_paid_money)fd_paid_money,sum(fd_paid_people)fd_paid_people,sum(fd_paid_num)fd_paid_num from payment_detail where date between ? and ? and os in (" + icons + ") group by date";
		List<PaymentDetail> paymentDetail = PaymentDetail.dao.use(db).find(sql, startDate, endDate);
		Map<String, Map<String, Object>> sort = new LinkedHashMap<String, Map<String, Object>>();
		//初始化
		for(String date : categories){
			Map<String, Object> emptySubMap = new LinkedHashMap<String, Object>();
			emptySubMap.put("pM", 0.00);
			emptySubMap.put("pP", 0);
			emptySubMap.put("pN", 0);
			emptySubMap.put("ftPM", 0.00);
			emptySubMap.put("ftPP", 0);
			emptySubMap.put("fdPM", 0.00);
			emptySubMap.put("fdPP", 0);
			emptySubMap.put("fdPN", 0);
			sort.put(date, emptySubMap);
		}
		//load query data
		for(PaymentDetail pd : paymentDetail){
			String date = pd.getStr("date");
			Map<String, Object> subMap = sort.get(date);
			subMap.put("pM", pd.getDouble("paid_money"));
			subMap.put("pP", pd.getBigDecimal("paid_people").longValue());
			subMap.put("pN", pd.getBigDecimal("paid_num").longValue());
			subMap.put("ftPM", pd.getDouble("ft_paid_money"));
			subMap.put("ftPP", pd.getBigDecimal("ft_paid_people").longValue());
			subMap.put("fdPM", pd.getDouble("fd_paid_money"));
			subMap.put("fdPP", pd.getBigDecimal("fd_paid_people").longValue());
			subMap.put("fdPN", pd.getBigDecimal("fd_paid_num").longValue());
			sort.put(date, subMap);
		}
		
		List<List<Object>> data = new ArrayList<List<Object>>();
		for(Map.Entry<String, Map<String, Object>> entry : sort.entrySet()){
				List<Object> per = new ArrayList<Object>();
				per.add(entry.getKey());
				per.addAll(entry.getValue().values());
				data.add(per);
		}
		return data;
	}
	/**
	 * 计算日 付费金额
	 * @param categories 日期列表
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间
	 */
	public List<Integer> queryDayPaymentMoney(List<String> categories, String icons, String startDate, String endDate){
		String sql = "select A.count from log_charge A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where A.is_product = 1 and C.os in (" + icons + ") and DATE_FORMAT(A.timestamp,'%Y-%m-%d') between ? and ?";
		List<LogCharge> logCharge = LogCharge.dao.use(db).find(sql, startDate, endDate);
		//init
		Map<String,Integer> paymentPeriod = new LinkedHashMap<String, Integer>();
		for(String category:categories){
			paymentPeriod.put(category, 0);
		}
		//load data
		for(LogCharge lc : logCharge){
			double r = lc.getDouble("count");
			BigDecimal bg = new BigDecimal(r);
			r = bg.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();  	
			if(r>0.00&&r<=2.00){
				increaseValue("1",paymentPeriod);
			}else if(r>2.00&&r<=3.00){
				increaseValue("2",paymentPeriod);
			}else if(r>3.00&&r<=4.00){
				increaseValue("3",paymentPeriod);
			}else if(r>4.00&&r<=5.00){
				increaseValue("4",paymentPeriod);
			}else if(r>5.00&&r<=6.00){
				increaseValue("5",paymentPeriod);
			}else if(r>6.00&&r<=10.00){
				increaseValue("6~10",paymentPeriod);
			}else if(r>10.00&&r<=50.00){
				increaseValue("11~50",paymentPeriod);
			}else if(r>50.00&&r<=100.00){
				increaseValue("51~100",paymentPeriod);
			}else if(r>100.00&&r<=500.00){
				increaseValue("101~500",paymentPeriod);
			}else if(r>500.00&&r<=1000.00){
				increaseValue("501~1000",paymentPeriod);
			}else if(r>1000.00&&r<=2000.00){
				increaseValue("1001~2000",paymentPeriod);
			}else if(r>2000.00){
				increaseValue(">2000",paymentPeriod);
			}
		}
		List<Integer> data = new ArrayList<Integer>();
		data.addAll(paymentPeriod.values());
		return data;
	}
	
	/**
	 * 计算日付费次数
	 * @param categories 日期列表
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间
	 */
	public List<Integer> queryDayPaymentTimes(List<String> categories, String icons, String startDate, String endDate){
		String sql = "select count(A.account)count from log_charge A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where A.is_product = 1 and C.os in (" + icons + ") and DATE_FORMAT(A.timestamp,'%Y-%m-%d') between ? and ? group by A.account";
		List<LogCharge> logCharge = LogCharge.dao.use(db).find(sql, startDate, endDate);
		//init
		Map<String,Integer> paymentPeriod = new LinkedHashMap<String, Integer>();
		for(String category:categories){
			paymentPeriod.put(category, 0);
		}
		//load data
		for(LogCharge lc : logCharge){
			int c = lc.getLong("count").intValue();
			switch(c){
			case 1:
				increaseValue("1",paymentPeriod);
				break;
			case 2:
				increaseValue("2",paymentPeriod);
				break;
			case 3:
				increaseValue("3",paymentPeriod);
				break;
			case 4:
				increaseValue("4",paymentPeriod);
				break;
			case 5:
				increaseValue("5",paymentPeriod);
				break;
			case 6:
				increaseValue("6",paymentPeriod);
				break;
			case 7:
				increaseValue("7",paymentPeriod);
				break;
			case 8:
				increaseValue("8",paymentPeriod);
				break;
			case 9:
				increaseValue("9",paymentPeriod);
				break;
			case 10:
				increaseValue("10",paymentPeriod);
				break;
			default:{
				if(c>10&&c<=20){
					increaseValue("11~20",paymentPeriod);
				}else if(c>20&&c<=30){
					increaseValue("21~30",paymentPeriod);
				}else if(c>30&&c<=40){
					increaseValue("31~40",paymentPeriod);
				}else if(c>40&&c<=50){
					increaseValue("41~50",paymentPeriod);
				}else if(c>50&&c<=100){
					increaseValue("51~100",paymentPeriod);
				}else if(c>100){
					increaseValue(">100",paymentPeriod);
				}
			  }	
			}	
		}
		List<Integer> data = new ArrayList<Integer>();
		data.addAll(paymentPeriod.values());
		return data;
	}
	
	/**
	 * 计算日ARPU   每天的收入/每天活跃玩家
	 * @param categories 日期列表
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间
	 */
	public List<Double> queryDayARPU(List<String> categories, String icons, String startDate, String endDate){
		String arpuSql = "select DATE_FORMAT(A.timestamp,'%Y-%m-%d')date,sum(count)revenue from log_charge A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where A.is_product = 1 and C.os in (" + icons + ") and DATE_FORMAT(A.timestamp,'%Y-%m-%d') between ? and ? group by date;";
		String aPSql = "select DATE_FORMAT(A.date,'%Y-%m-%d')date,count(distinct A.account)count from login A join device_info B on A.openudid = B.openudid where date between ? and ? and B.os in (" + icons + ") group by date;";
		
		List<LogCharge> logCharge = LogCharge.dao.use(db).find(arpuSql, startDate, endDate);
		List<Login> aP = Login.dao.use(db).find(aPSql, startDate, endDate);
		//init
		Map<String, Double> lcMap = new LinkedHashMap<String, Double>();
		Map<String, Long> aPMap = new LinkedHashMap<String, Long>();
		for(String date : categories){
			lcMap.put(date, 0.0);
			aPMap.put(date, 0L);
		}
		//load
		for(LogCharge lc : logCharge){
			lcMap.put(lc.getStr("date"),lc.getDouble("revenue"));
		}		
		for(Login l : aP){
			aPMap.put(l.getStr("date"),l.getLong("count"));
		}
		List<Double> arpu = new ArrayList<Double>();
		for(String date : categories){
			double revenue = lcMap.get(date);
			long apNum = aPMap.get(date);
			if(apNum==0){
				revenue = 0.0;
			}else{
				revenue = revenue/(double)apNum;
			}
			BigDecimal bg = new BigDecimal(revenue);
			revenue = bg.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue(); 
			arpu.add(revenue);
		}
		return arpu;
	}
	
	/**
	 * 查询日 ARPPU
	 * @param categories 日期列表
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间
	 */
	public List<Double> queryDayARPPU(List<String> categories, String icons, String startDate, String endDate){
		String sql = "select DATE_FORMAT(A.timestamp,'%Y-%m-%d')date,sum(count)revenue,count(distinct A.account)count from log_charge A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where A.is_product = 1 and C.os in ("+ icons + ") and DATE_FORMAT(A.timestamp,'%Y-%m-%d') between ? and ? group by date";
		List<LogCharge> logCharge = LogCharge.dao.use(db).find(sql, startDate, endDate);
		Map<String, Double> sort = new LinkedHashMap<String, Double>();
		//init
		for(String date : categories){
			sort.put(date,0.0);
		}
		//load
		for(LogCharge lc : logCharge){
			double r = lc.getDouble("revenue");
			long num = lc.getLong("count");
			if(num==0){
				r=0.0;
			}else{
				r = r/(double)num;
			}
			BigDecimal bg = new BigDecimal(r);
			r = bg.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
			sort.put(lc.getStr("date"), r);
		}
		List<Double> arppu = new ArrayList<Double>();
		arppu.addAll(sort.values());
		
		return arppu;
	}
	
	/**
	 * 查询所有付费金额 --表格
	 * @param categories 日期列表
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间
	 */
	public List<List<Object>> queryAllPaymentMoney(List<String> categories, String icons, String startDate, String endDate){
		List<Integer> day = queryDayPaymentMoney(categories,icons,startDate,endDate);
		List<Integer> week = Arrays.asList(0,0,0,0,0,0,0,0,0,0,0,0);
		List<Integer> month = Arrays.asList(0,0,0,0,0,0,0,0,0,0,0,0);
		List<List<Object>> data = new ArrayList<List<Object>>();
		for(int i=0;i<categories.size();i++){
			List<Object> per = new ArrayList<Object>();
			per.add(categories.get(i) + "($)");
			per.add(day.get(i));
			per.add(week.get(i));
			per.add(month.get(i));
			data.add(per);
		}
		return data;
	}
	
	/**
	 * 查询所有付费次数 --表格
	 * @param categories 日期列表
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间
	 */
	public List<List<Object>> queryAllPaymentTimes(List<String> categories, String icons, String startDate, String endDate){
		List<Integer> day = queryDayPaymentTimes(categories,icons,startDate,endDate);
		List<Integer> week = Arrays.asList(0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0);
		List<Integer> month = Arrays.asList(0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0);
		List<List<Object>> data = new ArrayList<List<Object>>();
		for(int i=0;i<categories.size();i++){
			List<Object> per = new ArrayList<Object>();
			per.add(categories.get(i));
			per.add(day.get(i));
			per.add(week.get(i));
			per.add(month.get(i));
			data.add(per);
		}
		return data;
	}
	/**
	 * 查询ARPU --表格
	 * @param categories 日期列表
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间
	 */
	public List<List<Object>> queryArpu(List<String> categories, String icons, String startDate, String endDate){
		List<Double> queryData = queryDayARPU(categories,icons,startDate,endDate);
		List<List<Object>> data = new ArrayList<List<Object>>();
		for(int i=0;i<categories.size();i++){
			List<Object> per = new ArrayList<Object>();
			per.add(categories.get(i));
			per.add(queryData.get(i));
			data.add(per);
		}
		return data;
	}
	/**
	 * 查询ARPPU --表格
	 * @param categories 日期列表
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间
	 */
	public List<List<Object>> queryArppu(List<String> categories, String icons, String startDate, String endDate){
		List<Double> queryData = queryDayARPU(categories,icons,startDate,endDate);
		List<List<Object>> data = new ArrayList<List<Object>>();
		for(int i=0;i<categories.size();i++){
			List<Object> per = new ArrayList<Object>();
			per.add(categories.get(i));
			per.add(queryData.get(i));
			data.add(per);
		}
		return data;
	}
	
	/**
	 * 查询地区收入
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间
	 */
	public List<LogCharge> queryAreaRevenue(String icons, String startDate, String endDate) {
		String sql = "select C.province,sum(A.count)revenue from log_charge A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where A.is_product = 1 and DATE_FORMAT(A.timestamp,'%Y-%m-%d') between ? and ? and C.os in (" + icons + ") group by C.province";
		List<LogCharge> logCharge = LogCharge.dao.use(db).find(sql,startDate,endDate);
		return logCharge;
	}
	/**
	 * 查询地区日均ARPU  --先load付费情况,再load 活跃人数   日ARPU = 当日充值总额度/当日活跃玩家数量
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间
	 */
	public Map<String, Object> queryAreaARPU(String icons, String startDate, String endDate) {
		String rSql = "select C.province,DATE_FORMAT(A.timestamp,'%Y-%m-%d')date,sum(A.count)revenue from log_charge A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where A.is_product = 1 and DATE_FORMAT(A.timestamp,'%Y-%m-%d') between ? and ? and C.os in (" + icons + ") group by date,C.province";
		String aSql = "select B.province,DATE_FORMAT(A.login_time,'%Y-%m-%d')date,count(distinct A.account)count from login A join device_info B on A.openudid = B.openudid where DATE_FORMAT(A.login_time,'%Y-%m-%d') between ? and ? and B.os in (" + icons +") group by B.province,date;";
		List<LogCharge> logCharge = LogCharge.dao.use(db).find(rSql, startDate, endDate);
		List<Login> login = Login.dao.use(db).find(aSql, startDate, endDate);
		//Map<Area,Map<date,AreaARPU>>
		Map<String, Map<String,AreaARU>> sort = new HashMap<String, Map<String, AreaARU>>();
		//load charge
		for(LogCharge lc : logCharge){
			String area = lc.getStr("province");
			String date = lc.getStr("date");
			double revenue = lc.getDouble("revenue");
			Map<String, AreaARU> subMap;
			if(sort.containsKey(area)){
				subMap = sort.get(area);
			}else{
				subMap = new HashMap<String, AreaARU>();
			}
			AreaARU aA = new AreaARU(revenue);
			subMap.put(date, aA);
			sort.put(area, subMap);
		}
		//load active player
		for(Login l : login) {
			String area = l.getStr("province");
			String date = l.getStr("date");
			long count = l.getLong("count");
			if(!sort.containsKey(area)){
				continue;
			}
			Map<String,AreaARU> subMap = sort.get(area);
			if(!subMap.containsKey(date)){
				continue;
			}
			AreaARU aA = subMap.get(date);
			aA.setCount(count);
		}
		Map<String, Double> avgArpu = dealAreaARU(sort);
		Map<String, Object> data = new HashMap<String, Object>();
		List<String> area = new ArrayList<String>();
		List<Double> series = new ArrayList<Double>();
		area.addAll(avgArpu.keySet());
		series.addAll(avgArpu.values());
		data.put("area", area);
		data.put("data", series);
		logger.info("data:" + data);
		return data;
	}
	
	/**
	 * 查询地区日均ARPPU   日ARPPU = 当日充值总额度/当日付费玩家数量
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间
	 */
	public Map<String,Object> queryAreaARPPU(String icons, String startDate, String endDate) {
		String sql = "select C.province,DATE_FORMAT(A.timestamp,'%Y-%m-%d')date,sum(A.count)revenue,count(distinct A.account)count from log_charge A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where A.is_product = 1 and DATE_FORMAT(A.timestamp,'%Y-%m-%d') between ? and ? and C.os in (" + icons + ") group by C.province,date";
		List<LogCharge> logCharge = LogCharge.dao.use(db).find(sql, startDate, endDate);
		Map<String, Map<String,AreaARU>> sort = new HashMap<String, Map<String, AreaARU>>();
		for(LogCharge lc : logCharge){
			String area = lc.getStr("province");
			String date = lc.getStr("date");
			double revenue = lc.getDouble("revenue");
			long count = lc.getLong("count");
			Map<String,AreaARU> subMap;
			if(sort.containsKey(area)){
				subMap = sort.get(area);
			}else{
				subMap = new HashMap<String,AreaARU>();
			}
			AreaARU aA = new AreaARU(revenue,count);
			subMap.put(date, aA);
			sort.put(area,subMap);
		}
		Map<String, Double> avgArppu = dealAreaARU(sort);
		Map<String, Object> data = new HashMap<String, Object>();
		List<String> area = new ArrayList<String>();
		List<Double> series = new ArrayList<Double>();
		area.addAll(avgArppu.keySet());
		series.addAll(avgArppu.values());
		data.put("area", area);
		data.put("data", series);
		logger.info("data:" + data);
		return data;
	}
	//处理中间数据
	private Map<String, Double> dealAreaARU(Map<String, Map<String, AreaARU>> sort){
		Map<String,Double> data = new HashMap<String,Double>();
		for(Map.Entry<String,Map<String,AreaARU>> entry : sort.entrySet()){
			int num = 0;
			double avg = 0.0;
			double calSum = 0.0;
			for(Map.Entry<String,AreaARU> subEntry : entry.getValue().entrySet()){
  				 AreaARU aA = subEntry.getValue();
				 double cal = 0.0;
  				 long c = aA.getCount();
				 double r = aA.getRevenue();
				 cal = r/(double)c;
				 BigDecimal bg = new BigDecimal(cal);
				 cal = bg.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
				 calSum += cal;
				 num++;
			}
			avg = calSum/(double)num;
			BigDecimal bg = new BigDecimal(avg);
			avg = bg.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
			data.put(entry.getKey(), avg);
		}
		return data;
	}
	
	/**
	 * 查询国家收入
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间
	 */
	public List<LogCharge> queryCountryRevenue(String icons, String startDate, String endDate) {
		String sql = "select country,sum(A.count)revenue from log_charge A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where A.is_product = 1 and DATE_FORMAT(A.timestamp,'%Y-%m-%d') between ? and ? and C.os in (" + icons + ") group by country;";
		List<LogCharge> logCharge = LogCharge.dao.use(db).find(sql,startDate,endDate);
		return logCharge;
	}
	/**
	 * 查询国家 ARPU
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间
	 */
	public Map<String, Object> queryCountryARPU(String icons, String startDate, String endDate) {
		String rSql = "select C.country,DATE_FORMAT(A.timestamp,'%Y-%m-%d')date,sum(A.count)revenue from log_charge A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where A.is_product = 1 and DATE_FORMAT(A.timestamp,'%Y-%m-%d') between ? and ? and C.os in (" + icons + ") group by date,C.country";
		String aSql = "select B.country,DATE_FORMAT(A.login_time,'%Y-%m-%d')date,count(distinct A.account)count from login A join device_info B on A.openudid = B.openudid where DATE_FORMAT(A.login_time,'%Y-%m-%d') between ? and ? and B.os in (" + icons + ") group by B.country,date;";
		
		List<LogCharge> logCharge = LogCharge.dao.use(db).find(rSql, startDate, endDate);
		List<Login> login = Login.dao.use(db).find(aSql, startDate, endDate);
		
		//Map<Area,Map<date,AreaARPU>>
		Map<String, Map<String,AreaARU>> sort = new HashMap<String, Map<String, AreaARU>>();
		//load charge
		for(LogCharge lc : logCharge){
			String country = lc.getStr("country");
			String date = lc.getStr("date");
			double revenue = lc.getDouble("revenue");
			Map<String, AreaARU> subMap;
			if(sort.containsKey(country)){
				subMap = sort.get(country);
			}else{
				subMap = new HashMap<String, AreaARU>();
			}
			AreaARU aA = new AreaARU(revenue);
			subMap.put(date, aA);
			sort.put(country, subMap);
		}
		//load active player
		for(Login l : login) {
			String country = l.getStr("country");
			String date = l.getStr("date");
			long count = l.getLong("count");
			if(!sort.containsKey(country)){
				continue;
			}
			Map<String,AreaARU> subMap = sort.get(country);
			if(!subMap.containsKey(date)){
				continue;
			}
			AreaARU aA = subMap.get(date);
			aA.setCount(count);
		}
		Map<String, Double> avgArpu = dealAreaARU(sort);
		Map<String, Object> data = new HashMap<String, Object>();
		List<String> country = new ArrayList<String>();
		List<Double> series = new ArrayList<Double>();
		country.addAll(avgArpu.keySet());
		series.addAll(avgArpu.values());
		data.put("country", country);
		data.put("data", series);
		logger.info("data:" + data);
		return data;
	}
	
	/**
	 * 查询国家日均ARPPU   日ARPPU = 当日充值总额度/当日付费玩家数量
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间
	 */
	public Map<String,Object> queryCountryARPPU(String icons, String startDate, String endDate) {
		String sql = "select C.country,DATE_FORMAT(A.timestamp,'%Y-%m-%d')date,sum(A.count)revenue,count(distinct A.account)count from log_charge A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where A.is_product = 1 and DATE_FORMAT(A.timestamp,'%Y-%m-%d') between ? and ? and C.os in (" + icons + ") group by C.country,date";
		List<LogCharge> logCharge = LogCharge.dao.use(db).find(sql, startDate, endDate);
		Map<String, Map<String,AreaARU>> sort = new HashMap<String, Map<String, AreaARU>>();
		for(LogCharge lc : logCharge){
			String country = lc.getStr("country");
			String date = lc.getStr("date");
			double revenue = lc.getDouble("revenue");
			long count = lc.getLong("count");
			Map<String,AreaARU> subMap;
			if(sort.containsKey(country)){
				subMap = sort.get(country);
			}else{
				subMap = new HashMap<String,AreaARU>();
			}
			AreaARU aA = new AreaARU(revenue,count);
			subMap.put(date, aA);
			sort.put(country,subMap);
		}
		Map<String, Double> avgArppu = dealAreaARU(sort);
		Map<String, Object> data = new HashMap<String, Object>();
		List<String> country = new ArrayList<String>();
		List<Double> series = new ArrayList<Double>();
		country.addAll(avgArppu.keySet());
		series.addAll(avgArppu.values());
		data.put("country", country);
		data.put("data", series);
		logger.info("data:" + data);
		return data;
	}
	
	/**
	 * 查询移动运营商
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间
	 */
	public List<LogCharge> queryMobile(String icons, String startDate, String endDate) {
		String sql = "select C.carrier,sum(A.count)revenue from log_charge A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where A.is_product = 1 and DATE_FORMAT(timestamp,'%Y-%m-%d') between ? and ? and C.os in (" + icons + ") group by C.carrier";
		List<LogCharge> logCharge = LogCharge.dao.use(db).find(sql, startDate, endDate);
		return logCharge;
	}
	
	private void increaseValue(String key, Map<String, Integer> map){
		int value = map.get(key);
		value++;
		map.put(key, value);
	}
}
