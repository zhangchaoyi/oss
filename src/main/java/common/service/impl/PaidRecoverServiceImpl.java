package common.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import common.model.LogOrder;
import common.service.PaidRecoverService;

public class PaidRecoverServiceImpl implements PaidRecoverService {
	private static Logger logger = Logger.getLogger(PaidRecoverServiceImpl.class);
	/**
	 * 根据 account/角色名 查询订单详情
	 * @param account 
	 * @param db
	 * @return
	 */
	public List<List<String>> queryOrderByAccount(String account, String db){
		logger.info("params:{"+"account:"+account+"}");
		String sql = "";
		List<LogOrder> logOrder = new ArrayList<LogOrder>();
		if(account.length()==8 && isNumeric(account)){
			sql = "select * from log_order where account = ?";
			logOrder = LogOrder.dao.use(db).find(sql, account);
		}else{
			sql = "select * from log_order where team_name = ?";
			logOrder = LogOrder.dao.use(db).find(sql, account);
		}
		List<List<String>> data = new ArrayList<List<String>>();
		for(LogOrder lo : logOrder){
			String queryAccount = lo.getStr("account");
			String roleName = lo.getStr("team_name");
			String orderNum = lo.getStr("serial_num");
			String price = lo.getInt("price").toString();
			Date createTime = lo.getDate("create_time");
			String os = lo.getStr("os");
			String payPoint = "";
			switch(os){
			case "iOS":
				payPoint = getIOSPayPointDesc(lo.getStr("ios_pay_point"));
				break;
			case "android":
				payPoint = getAndroidPayPointDesc(lo.getStr("android_pay_point"));
				break;
			}
			String status = "charged".equals(lo.getStr("status"))?"已完成":"未完成";
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm"); 
			List<String> subList = new ArrayList<String>();
			subList.add(queryAccount);
			subList.add(roleName);
			subList.add(orderNum);
			subList.add(price);
			subList.add(payPoint);
			subList.add(sdf.format(createTime));
			subList.add(status);
			data.add(subList);
		}
		return data;
	}
	
	private String getAndroidPayPointDesc(String payPoint){
		String pp = "";
		switch(payPoint){
		case "gem60":
			pp = "钻石*60";
			break;
		case "gem350":
			pp = "钻石*350";
			break;
		case "gem800":
			pp = "钻石*800";
			break;
		case "gem1550":
			pp = "钻石*1650";
			break;
		case "gem4000":
			pp = "钻石*4250";
			break;
		case "gem8000s":
			pp = "钻石*8500";
			break;
		case "com.koogame.eggworld01.monthly_card":
			pp = "月卡";
			break;
		case "com.koogame.eggworld01.quarterly_card":
			pp = "季度卡";
			break;
		case "com.koogame.eggworld01.yearly_card":
			pp = "年卡";
			break;
		}
		return pp;
	}
	
	private String getIOSPayPointDesc(String payPoint){
		String pp = "";
		switch(payPoint){
		case "com.koogame.eggworld01.gem60":
			pp = "钻石*60";
			break;
		case "com.koogame.eggworld01.gem350":
			pp = "钻石*350";
			break;
		case "com.koogame.eggworld01.gem800":
			pp = "钻石*800";
			break;
		case "com.koogame.eggworld01.gem1550":
			pp = "钻石*1650";
			break;
		case "com.koogame.eggworld01.gem4000":
			pp = "钻石*4250";
			break;
		case "com.koogame.eggworld01.gem8000s":
			pp = "钻石*8500";
			break;
		case "com.koogame.eggworld01.monthly_card":
			pp = "月卡";
			break;
		case "com.koogame.eggworld01.quarterly_card":
			pp = "季度卡";
			break;
		case "com.koogame.eggworld01.yearly_card":
			pp = "年卡";
			break;
		}
		return pp;
	}
	private boolean isNumeric(String str){ 
		Pattern pattern = Pattern.compile("[0-9]*"); 
		Matcher isNum = pattern.matcher(str);
		if( !isNum.matches() ){
		   return false; 
		} 
		return true; 
	}
}
