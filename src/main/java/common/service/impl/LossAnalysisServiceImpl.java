package common.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import common.model.LossUser;
import common.model.ReturnUser;
import common.pojo.DayUser;
import common.service.LossAnalysisService;
import common.utils.StringUtils;

/**
 * 流失分析页
 * @author chris
 *
 */
public class LossAnalysisServiceImpl implements LossAnalysisService{
	private Logger logger = Logger.getLogger(LossAnalysisServiceImpl.class);
	
	/**
	 * 每日流失接口
	 * @param categories 日期列表 
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间
	 * @param type  玩家类型 --活跃/付费/非付费 
	 */
	public Map<String, Object> queryDayLoss(List<String> categories, String icons, String startDate, String endDate, String type) {
		String[] typeArray = {type};
		type = StringUtils.arrayToQueryString(typeArray);
		String sql = "select DATE_FORMAT(date,'%Y-%m-%d')date,sum(num)num,sum(seven_day)sd,sum(forteen_day)fd,sum(thirty_day)td from loss_user where date between ? and ? and os in (" + icons + ") and type = " + type + " group by date;";
		List<LossUser> lossUser = LossUser.dao.find(sql, startDate, endDate);
		
		//init
		Map<String, DayUser> sort = new LinkedHashMap<String, DayUser>();
		for(String d : categories){
			DayUser du = new DayUser();
			sort.put(d, du);
		}
		
		for(LossUser lu : lossUser){
			String date = lu.getStr("date");
			int num = lu.getBigDecimal("num").intValue();
			int sd = lu.getBigDecimal("sd").intValue();
			int fd = lu.getBigDecimal("fd").intValue();
			int td = lu.getBigDecimal("td").intValue();
			
			DayUser du = sort.get(date);
			du.setNum(num);
			du.setSd(sd);
			du.setFd(fd);
			du.setTd(td);
			sort.put(date, du);
		}
		
		List<Double> sdLR = new ArrayList<Double>();
		List<Double> fdLR = new ArrayList<Double>();
		List<Double> tdLR = new ArrayList<Double>();
		List<List<String>> tableData = new ArrayList<List<String>>();
		
		for(Map.Entry<String, DayUser> entry : sort.entrySet()){
			DayUser du = entry.getValue();
			int num = du.getNum();
			int sd = du.getSd();
			int fd = du.getFd();
			int td = du.getTd();
			
			double sdr = 0.00;
			double fdr = 0.00;
			double tdr = 0.00;
			
			if(num!=0){
				sdr = sd*100.0/num;
				fdr = fd*100.0/num;
				tdr = td*100.0/num;
				BigDecimal sdrBg = new BigDecimal(sdr);
				BigDecimal fdrBg = new BigDecimal(fdr);
				BigDecimal tdrBg = new BigDecimal(tdr);
				sdr = sdrBg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				fdr = fdrBg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				tdr = tdrBg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			}
			sdLR.add(sdr);
			fdLR.add(fdr);
			tdLR.add(tdr);
			List<String> subList = new ArrayList<String>(Arrays.asList(entry.getKey(), String.valueOf(num), sd+"("+sdr+"%)", fd+"("+fdr+"%)", td+"("+tdr+"%)"));
			tableData.add(subList);
		}
		
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("sdLR", sdLR);
		data.put("fdLR", fdLR);
		data.put("tdLR", tdLR);
		data.put("tableData", tableData);
		logger.debug("queryDayLoss:" + data);
		return data;
	}
	/**
	 * 每日回流接口
	 * @param categories 日期列表 
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间
	 * @param type  玩家类型 --活跃/付费/非付费 
	 */
	public Map<String, Object> queryDayReturn(List<String> categories, String icons, String startDate, String endDate, String type){
		String[] typeArray = {type};
		type = StringUtils.arrayToQueryString(typeArray);
		String sql = "select DATE_FORMAT(date,'%Y-%m-%d')date,sum(num)num,sum(seven_day)sd,sum(forteen_day)fd,sum(thirty_day)td from return_user where date between ? and ? and os in (" + icons + ") and type = " + type + " group by date";
		List<ReturnUser> returnUser = ReturnUser.dao.find(sql, startDate, endDate);
		//init
		Map<String, DayUser> sort = new LinkedHashMap<String, DayUser>(); 
		for(String d : categories){
			DayUser du = new DayUser();
			sort.put(d, du);
		}
		
		for(ReturnUser ru : returnUser){
			String date = ru.getStr("date");
			int num = ru.getBigDecimal("num").intValue();
			int sd = ru.getBigDecimal("sd").intValue();
			int fd = ru.getBigDecimal("fd").intValue();
			int td = ru.getBigDecimal("td").intValue();
			
			DayUser du = sort.get(date);
			du.setNum(num);
			du.setSd(sd);
			du.setFd(fd);
			du.setTd(td);
			sort.put(date, du);
		}
		
		List<Integer> sdL = new ArrayList<Integer>();
		List<Integer> fdL = new ArrayList<Integer>();
		List<Integer> tdL = new ArrayList<Integer>();
		List<List<String>> tableData = new ArrayList<List<String>>();
		
		for(Map.Entry<String, DayUser> entry : sort.entrySet()){
			DayUser du = entry.getValue();
			int num = du.getNum();
			int sd = du.getSd();
			int fd = du.getFd();
			int td = du.getTd();
			
			sdL.add(sd);
			fdL.add(fd);
			tdL.add(td);
			List<String> subList = new ArrayList<String>(Arrays.asList(entry.getKey(), String.valueOf(num), String.valueOf(sd), String.valueOf(fd), String.valueOf(td)));
			tableData.add(subList);
		}
		
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("sdL", sdL);
		data.put("fdL",fdL);
		data.put("tdL", tdL);
		data.put("tableData", tableData);
		logger.debug("queryDayReturn:" + data);
		return data;
	}
}
