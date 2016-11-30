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
import common.mysql.DbSelector;
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
	private String db = DbSelector.getDbName();
	/**
	 * 每日流失接口
	 * @param categories 日期列表 
	 * @param icons  当前的icon   ---apple/android/windows
	 * @param startDate  所选起始时间
	 * @param endDate  所选结束时间
	 * @param type  玩家类型 --活跃/付费/非付费 
	 */
	public Map<String, Object> queryDayLoss(List<String> categories, String icons, String startDate, String endDate, String type) {
		logger.info("params:{"+"type:"+type+"}");
		String[] typeArray = {type};
		type = StringUtils.arrayToQueryString(typeArray);
		String sql = "select DATE_FORMAT(date,'%Y-%m-%d')date,sum(num)num,sum(first_day)first_day,sum(second_day)second_day,sum(third_day)third_day,sum(forth_day)forth_day,sum(fifth_day)fifth_day,sum(sixth_day)sixth_day,sum(seven_day)seven_day,sum(eighth_day)eighth_day,sum(ninth_day)ninth_day,sum(tenth_day)tenth_day,sum(eleventh_day)eleventh_day,sum(twelfth_day)twelfth_day,sum(thirteenth_day)thirteenth_day,sum(fourteenth_day)fourteenth_day,sum(thirty_day)thirty_day from loss_user where date between ? and ? and os in (" + icons + ") and type = " + type + " group by date;";
		List<LossUser> lossUser = LossUser.dao.use(db).find(sql, startDate, endDate);
		
		//init
		Map<String, DayUser> sort = new LinkedHashMap<String, DayUser>();
		for(String d : categories){
			DayUser du = new DayUser();
			sort.put(d, du);
		}
		
		for(LossUser lu : lossUser){
			String date = lu.getStr("date");
			int num = lu.getBigDecimal("num").intValue();
			int firstDay = lu.getBigDecimal("first_day").intValue();
			int secondDay = lu.getBigDecimal("second_day").intValue();
			int thirdDay = lu.getBigDecimal("third_day").intValue();
			int forthDay = lu.getBigDecimal("forth_day").intValue();
			int fifthDay = lu.getBigDecimal("fifth_day").intValue();
			int sixthDay = lu.getBigDecimal("sixth_day").intValue();
			int seventhDay = lu.getBigDecimal("seven_day").intValue();
			int eighthDay = lu.getBigDecimal("eighth_day").intValue();
			int ninthDay = lu.getBigDecimal("ninth_day").intValue();
			int tenthDay = lu.getBigDecimal("tenth_day").intValue();
			int eleventhDay = lu.getBigDecimal("eleventh_day").intValue();
			int twelfthDay = lu.getBigDecimal("twelfth_day").intValue();
			int thirteenthDay = lu.getBigDecimal("thirteenth_day").intValue();
			int fourteenthDay = lu.getBigDecimal("fourteenth_day").intValue();
			int thirtyDay = lu.getBigDecimal("thirty_day").intValue();
			
			DayUser du = sort.get(date);
			du.setNum(num);
			du.setFirstDay(firstDay);
			du.setSecondDay(secondDay);
			du.setThirdDay(thirdDay);
			du.setForthDay(forthDay);
			du.setFifthDay(fifthDay);
			du.setSixthDay(sixthDay);
			du.setSeventhDay(seventhDay);
			du.setEighthDay(eighthDay);
			du.setNinthDay(ninthDay);
			du.setTenthDay(tenthDay);
			du.setEleventhDay(eleventhDay);
			du.setTwelfthDay(twelfthDay);
			du.setThirteenthDay(thirteenthDay);
			du.setFourteenthDay(fourteenthDay);
			du.setThirdDay(thirtyDay);
			sort.put(date, du);
		}
		
		List<Double> firstDayLR = new ArrayList<Double>();
		List<Double> secondDayLR = new ArrayList<Double>();
		List<Double> thirdDayLR = new ArrayList<Double>();
		List<Double> forthDayLR = new ArrayList<Double>();
		List<Double> fifthDayLR = new ArrayList<Double>();
		List<Double> sixthDayLR = new ArrayList<Double>();
		List<Double> sevenDayLR = new ArrayList<Double>();
		List<Double> eighthDayLR = new ArrayList<Double>();
		List<Double> ninthDayLR = new ArrayList<Double>();
		List<Double> tenthDayLR = new ArrayList<Double>();
		List<Double> eleventhDayLR = new ArrayList<Double>();
		List<Double> twelfthDayLR = new ArrayList<Double>();
		List<Double> thirteenthDayLR = new ArrayList<Double>();
		List<Double> fourteenthDayLR = new ArrayList<Double>();
		List<Double> thirtyDayLR = new ArrayList<Double>();
		
		List<List<String>> tableData = new ArrayList<List<String>>();
		
		for(Map.Entry<String, DayUser> entry : sort.entrySet()){
			DayUser du = entry.getValue();
			int num = du.getNum();
			int firstDay = du.getFirstDay();
			int secondDay = du.getSecondDay();
			int thirdDay = du.getThirdDay();
			int forthDay = du.getForthDay();
			int fifthDay = du.getFifthDay();
			int sixthDay = du.getSixthDay();
			int seventhDay = du.getSeventhDay();		
			int eighthDay = du.getEighthDay();
			int ninthDay = du.getNinthDay();
			int tenthDay = du.getTenthDay();
			int eleventhDay = du.getEleventhDay();
			int twelfthDay = du.getTwelfthDay();
			int thirteenthDay = du.getThirteenthDay();
			int fourteenthDay = du.getFourteenthDay();
			int thirtyDay = du.getThirtyDay();
			
			double firstDayDr = 0.00;
			double secondDayDr = 0.00;
			double thirdDayDr = 0.00;
			double forthDayDr = 0.00;
			double fifthDayDr = 0.00;
			double sixthDayDr = 0.00;
			double sevenDayDr = 0.00;
			double eighthDayDr = 0.00;
			double ninthDayDr = 0.00;
			double tenthDayDr = 0.00;
			double eleventhDayDr = 0.00;
			double twelfthDayDr = 0.00;
			double thirteenthDayDr = 0.00;
			double fourteenthDayDr = 0.00;
			double thirtyDayDr = 0.00;
			
			if(num!=0){
				firstDayDr = firstDay*100.0/num;
				secondDayDr = secondDay*100.0/num;
				thirdDayDr = thirdDay*100.0/num;
				forthDayDr = forthDay*100.0/num;
				fifthDayDr = fifthDay*100.0/num;
				sixthDayDr = sixthDay*100.0/num;
				sevenDayDr = seventhDay*100.0/num;
				eighthDayDr = eighthDay*100.0/num;
				ninthDayDr = ninthDay*100.0/num;
				tenthDayDr = tenthDay*100.0/num;
				eleventhDayDr = eleventhDay*100.0/num;
				twelfthDayDr = twelfthDay*100.0/num;
				thirteenthDayDr = thirteenthDay*100.0/num;
				fourteenthDayDr = fourteenthDay*100.0/num;
				thirtyDayDr = thirtyDay*100.0/num;
				
				BigDecimal firstBg = new BigDecimal(firstDayDr);
				BigDecimal secondBg = new BigDecimal(secondDayDr);
				BigDecimal thirdBg = new BigDecimal(thirdDayDr);
				BigDecimal forthBg = new BigDecimal(forthDayDr);
				BigDecimal fifthBg = new BigDecimal(fifthDayDr);
				BigDecimal sixthBg = new BigDecimal(sixthDayDr);
				BigDecimal seventhBg = new BigDecimal(sevenDayDr);
				BigDecimal eighthBg = new BigDecimal(eighthDayDr);
				BigDecimal ninthBg = new BigDecimal(ninthDayDr);
				BigDecimal tenthBg = new BigDecimal(tenthDayDr);
				BigDecimal eleventhBg = new BigDecimal(eleventhDayDr);
				BigDecimal twelfthBg = new BigDecimal(twelfthDayDr);
				BigDecimal thirteenthBg = new BigDecimal(thirteenthDayDr);
				BigDecimal fourteenthBg = new BigDecimal(fourteenthDayDr);
				BigDecimal thirtyBg = new BigDecimal(thirtyDayDr);
				
				firstDayDr = firstBg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				secondDayDr = secondBg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				thirdDayDr = thirdBg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				forthDayDr = forthBg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				fifthDayDr = fifthBg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				sixthDayDr = sixthBg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				sevenDayDr = seventhBg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				eighthDayDr = eighthBg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				ninthDayDr = ninthBg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				tenthDayDr = tenthBg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				eleventhDayDr = eleventhBg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				twelfthDayDr = twelfthBg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				thirteenthDayDr = thirteenthBg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				fourteenthDayDr = fourteenthBg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				thirtyDayDr = thirtyBg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			}
			firstDayLR.add(firstDayDr);
			secondDayLR.add(secondDayDr);
			thirdDayLR.add(thirdDayDr);
			forthDayLR.add(forthDayDr);
			fifthDayLR.add(fifthDayDr);
			sixthDayLR.add(sixthDayDr);
			sevenDayLR.add(sevenDayDr);
			eighthDayLR.add(eighthDayDr);
			ninthDayLR.add(ninthDayDr);
			tenthDayLR.add(tenthDayDr);
			eleventhDayLR.add(eleventhDayDr);
			twelfthDayLR.add(twelfthDayDr);
			thirteenthDayLR.add(thirteenthDayDr);
			fourteenthDayLR.add(fourteenthDayDr);
			thirtyDayLR.add(thirtyDayDr);

			List<String> subList = new ArrayList<String>(Arrays.asList(entry.getKey(), String.valueOf(num), firstDay+"("+firstDayDr+"%)", secondDay+"("+secondDayDr+"%)", thirdDay+"("+thirdDayDr+"%)", forthDay+"("+forthDayDr+"%)", fifthDay+"("+fifthDayDr+"%)", sixthDay+"("+sixthDayDr+"%)",seventhDay+"("+sevenDayDr+"%)",eighthDay+"("+eighthDayDr+"%)",ninthDay+"("+ninthDayDr+"%)",tenthDay+"("+tenthDayDr+"%)",eleventhDay+"("+eleventhDayDr+"%)",twelfthDay+"("+twelfthDayDr+"%)",thirteenthDay+"("+thirteenthDayDr+"%)",fourteenthDay+"("+fourteenthDayDr+"%)",thirtyDay+"("+thirtyDayDr+"%)"));
			tableData.add(subList);
		}
		
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("firstDayLR", firstDayLR);
		data.put("secondDayLR", secondDayLR);
		data.put("thirdDayLR", thirdDayLR);
		data.put("forthDayLR", forthDayLR);
		data.put("fifthDayLR", fifthDayLR);
		data.put("sixthDayLR", sixthDayLR);
		data.put("sevenDayLR", sevenDayLR);
		data.put("eighthDayLR", eighthDayLR);
		data.put("ninthDayLR", ninthDayLR);
		data.put("tenthDayLR", tenthDayLR);
		data.put("eleventhDayLR", eleventhDayLR);
		data.put("twelfthDayLR", twelfthDayLR);
		data.put("thirteenthDayLR", thirteenthDayLR);
		data.put("fourteenthDayLR", fourteenthDayLR);
		data.put("thirtyDayLR", thirtyDayLR);
		
		data.put("tableData", tableData);
		logger.info("data:" + data);
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
		logger.info("params:{"+"type:"+type+"}");
		String[] typeArray = {type};
		type = StringUtils.arrayToQueryString(typeArray);
		String sql = "select DATE_FORMAT(date,'%Y-%m-%d')date,sum(num)num,sum(first_day)first_day,sum(second_day)second_day,sum(third_day)third_day,sum(forth_day)forth_day,sum(fifth_day)fifth_day,sum(sixth_day)sixth_day,sum(seven_day)seven_day,sum(eighth_day)eighth_day,sum(ninth_day)ninth_day,sum(tenth_day)tenth_day,sum(eleventh_day)eleventh_day,sum(twelfth_day)twelfth_day,sum(thirteenth_day)thirteenth_day,sum(fourteenth_day)fourteenth_day,sum(thirty_day)thirty_day from return_user where date between ? and ? and os in (" + icons + ") and type = " + type + " group by date;";
		List<ReturnUser> returnUser = ReturnUser.dao.use(db).find(sql, startDate, endDate);
		//init
		Map<String, DayUser> sort = new LinkedHashMap<String, DayUser>(); 
		for(String d : categories){
			DayUser du = new DayUser();
			sort.put(d, du);
		}
		
		for(ReturnUser ru : returnUser){
			String date = ru.getStr("date");
			int num = ru.getBigDecimal("num").intValue();
			int firstDay = ru.getBigDecimal("first_day").intValue();
			int secondDay = ru.getBigDecimal("second_day").intValue();
			int thirdDay = ru.getBigDecimal("third_day").intValue();
			int forthDay = ru.getBigDecimal("forth_day").intValue();
			int fifthDay = ru.getBigDecimal("fifth_day").intValue();
			int sixthDay = ru.getBigDecimal("sixth_day").intValue();
			int seventhDay = ru.getBigDecimal("seven_day").intValue();
			int eighthDay = ru.getBigDecimal("eighth_day").intValue();
			int ninthDay = ru.getBigDecimal("ninth_day").intValue();
			int tenthDay = ru.getBigDecimal("tenth_day").intValue();
			int eleventhDay = ru.getBigDecimal("eleventh_day").intValue();
			int twelfthDay = ru.getBigDecimal("twelfth_day").intValue();
			int thirteenthDay = ru.getBigDecimal("thirteenth_day").intValue();
			int fourteenthDay = ru.getBigDecimal("fourteenth_day").intValue();
			int thirtyDay = ru.getBigDecimal("thirty_day").intValue();
			
			DayUser du = sort.get(date);
			du.setNum(num);
			du.setFirstDay(firstDay);
			du.setSecondDay(secondDay);
			du.setThirdDay(thirdDay);
			du.setForthDay(forthDay);
			du.setFifthDay(fifthDay);
			du.setSixthDay(sixthDay);
			du.setSeventhDay(seventhDay);
			du.setEighthDay(eighthDay);
			du.setNinthDay(ninthDay);
			du.setTenthDay(tenthDay);
			du.setEleventhDay(eleventhDay);
			du.setTwelfthDay(twelfthDay);
			du.setThirteenthDay(thirteenthDay);
			du.setFourteenthDay(fourteenthDay);
			du.setThirdDay(thirtyDay);
			sort.put(date, du);
		}
		
		List<Integer> firstDL = new ArrayList<Integer>();
		List<Integer> secondDL = new ArrayList<Integer>();
		List<Integer> thirdDL = new ArrayList<Integer>();
		List<Integer> forthDL = new ArrayList<Integer>();
		List<Integer> fifthDL = new ArrayList<Integer>();
		List<Integer> sixthDL = new ArrayList<Integer>();
		List<Integer> sevenDL = new ArrayList<Integer>();
		List<Integer> eighthDL = new ArrayList<Integer>();
		List<Integer> ninthDL = new ArrayList<Integer>();
		List<Integer> tenthDL = new ArrayList<Integer>();
		List<Integer> eleventhDL = new ArrayList<Integer>();
		List<Integer> twelfthDL = new ArrayList<Integer>();
		List<Integer> thirteenthDL = new ArrayList<Integer>();
		List<Integer> fourteenthDL = new ArrayList<Integer>();
		List<Integer> thirtyDL = new ArrayList<Integer>();
		List<List<String>> tableData = new ArrayList<List<String>>();
		
		for(Map.Entry<String, DayUser> entry : sort.entrySet()){
			DayUser du = entry.getValue();
			int num = du.getNum();
			int firstDay = du.getFirstDay();
			int secondDay = du.getSecondDay();
			int thirdDay = du.getThirdDay();
			int forthDay = du.getForthDay();
			int fifthDay = du.getFifthDay();
			int sixthDay = du.getSixthDay();
			int seventhDay = du.getSeventhDay();		
			int eighthDay = du.getEighthDay();
			int ninthDay = du.getNinthDay();
			int tenthDay = du.getTenthDay();
			int eleventhDay = du.getEleventhDay();
			int twelfthDay = du.getTwelfthDay();
			int thirteenthDay = du.getThirteenthDay();
			int fourteenthDay = du.getFourteenthDay();
			int thirtyDay = du.getThirtyDay();
			
			firstDL.add(firstDay);
			secondDL.add(secondDay);
			thirdDL.add(thirdDay);
			forthDL.add(forthDay);
			fifthDL.add(fifthDay);
			sixthDL.add(sixthDay);
			sevenDL.add(seventhDay);
			eighthDL.add(eighthDay);
			ninthDL.add(ninthDay);
			tenthDL.add(tenthDay);
			eleventhDL.add(eleventhDay);
			twelfthDL.add(twelfthDay);
			thirteenthDL.add(thirteenthDay);
			fourteenthDL.add(fourteenthDay);
			thirtyDL.add(thirtyDay);
			
			List<String> subList = new ArrayList<String>(Arrays.asList(entry.getKey(), String.valueOf(num), String.valueOf(firstDay), String.valueOf(secondDay), String.valueOf(thirdDay), String.valueOf(forthDay), String.valueOf(fifthDay), String.valueOf(sixthDay), String.valueOf(seventhDay), String.valueOf(eighthDay), String.valueOf(ninthDay), String.valueOf(tenthDay), String.valueOf(eleventhDay), String.valueOf(twelfthDay), String.valueOf(thirteenthDay), String.valueOf(fourteenthDay), String.valueOf(thirtyDay)));
			tableData.add(subList);
		}
		
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("firstDL", firstDL);
		data.put("secondDL",secondDL);
		data.put("thirdDL", thirdDL);
		data.put("forthDL", forthDL);
		data.put("fifthDL",fifthDL);
		data.put("sixthDL", sixthDL);
		data.put("sevenDL", sevenDL);
		data.put("eighthDL",eighthDL);
		data.put("ninthDL", ninthDL);
		data.put("tenthDL", tenthDL);
		data.put("eleventhDL",eleventhDL);
		data.put("twelfthDL", twelfthDL);
		data.put("thirteenthDL", thirteenthDL);
		data.put("fourteenthDL",fourteenthDL);
		data.put("thirtyDL", thirtyDL);
		
		data.put("tableData", tableData);
		logger.info("data:" + data);
		return data;
	}
}
