package common.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import common.model.CreateRole;
import common.model.DeviceInfo;
import common.model.LogCharge;
import common.model.Login;
import common.model.Logout;
import common.service.AccdetailService;

/**
 * 查询生命轨迹页
 * @author chris
 */
public class AccdetailServiceImpl implements AccdetailService {
	private static Logger logger = Logger.getLogger(AccdetailServiceImpl.class);
	/**
	 * 根据帐号id获得 帐号的所有信息
	 * @param accountId 帐号id或者角色名
	 * 表头字段顺序在 html 已定义好
	 * 分多次sql 查询
	 * ------20161227-----------------------
	 * 支持account和角色名相互转换查询,
	 * @return Map<String, Object>
	 */
	public Map<String, Object> queryAccdetail(String accountId, String db) {
		logger.info("params:{"+"accountId:"+accountId+"}");
		String account = "";
		String roleName = "";
		String sql = "";
		Map<String, Object> data = new HashMap<String, Object>();
		
		//进行帐号和非帐号的处理
		//帐号id
		if(accountId.length()==8 && isNumeric(accountId)){
			account = accountId;
			sql = "select team_name from create_role where account = ?";
			CreateRole cr = CreateRole.dao.use(db).findFirst(sql, account);
			roleName = (cr==null)?"-":cr.getStr("team_name");
		}else{
			roleName = accountId;
			sql = "select account from create_role where team_name = ?";
			CreateRole cr = CreateRole.dao.use(db).findFirst(sql, roleName);
			if(cr==null){
				data.put("code", 1);
				data.put("error", "后台角色名无对应帐号");
				return data;
			}
			account = cr.getStr("account");
		}
		
		String deviceSql = "select B.model,B.resolution,B.os,B.os_version,B.country,B.province,B.carrier,B.net from (select distinct account,openudid  from login where account = ?) A join device_info B on A.openudid = B.openudid";
		String loginSql = "select DATE_FORMAT(min(login_time),'%Y-%m-%d')firstLogin,DATE_FORMAT(max(login_time),'%Y-%m-%d')lastLogin,count(distinct date)loginDay,count(*)loginTimes  from login where account = ?";
		String onlineSql = "select A.onlineSum,B.max_level from (select account,sum(online_time)onlineSum from logout where account = ?) A join (select account,max(level)max_level from level_up where account = ?) B on A.account = B.account";
		String paidSql = "select DATE_FORMAT(min(timestamp),'%Y-%m-%d')firstPaid,DATE_FORMAT(max(timestamp),'%Y-%m-%d')lastPaid,sum(count)paidSum from log_charge where is_product = 1 and account = ?";
		//根据account进行查询
		List<DeviceInfo> device = DeviceInfo.dao.use(db).find(deviceSql, account);
		List<Login> login = Login.dao.use(db).find(loginSql, account);
		List<Logout> logout = Logout.dao.use(db).find(onlineSql, account, account);
		List<LogCharge> logCharge = LogCharge.dao.use(db).find(paidSql, account);
		if(device.size()==0){
			data.put("code", 1);
			return data;
		}
		
		List<List<String>> deviceTable = new ArrayList<List<String>>();
		List<List<String>> detailTable = new ArrayList<List<String>>();
		List<String> detailList = new ArrayList<String>();
		//查询帐号的设备信息,一个帐号可能存在多个设备
		for(DeviceInfo di : device){
			List<String> list = new ArrayList<String>();
			list.add(di.getStr("model")==null? "-" : di.getStr("model"));
			list.add(di.getStr("resolution")==null? "-" : di.getStr("resolution"));
			list.add(di.getStr("os")==null? "-" : di.getStr("os"));
			list.add(di.getStr("os_version")==null? "-" : di.getStr("os_version"));
			list.add(di.getStr("country")==null? "-" : di.getStr("country"));
			list.add(di.getStr("province")==null? "-" : di.getStr("province"));
			list.add(di.getStr("carrier")==null? "-" : di.getStr("carrier"));
			list.add(di.getStr("net")==null? "-" : di.getStr("net"));
			deviceTable.add(list);
		}
		detailList.add(account);
		detailList.add(roleName);
		//登录信息
		for(Login l : login){
			detailList.add(l.getStr("firstLogin")==null? "-" : l.getStr("firstLogin"));
			detailList.add(l.getStr("lastLogin")==null? "-" : l.getStr("lastLogin"));
			detailList.add(l.getLong("loginDay")==0? "-" : l.getLong("loginDay").toString() + "天");
			detailList.add(l.getLong("loginTimes")==0? "-" : l.getLong("loginTimes").toString() + "次");	
		}
		//总在线时长
		if(logout.isEmpty()){
			detailList.add("-");
			detailList.add("-");
		}
		for(Logout l : logout){
			detailList.add(l.getBigDecimal("onlineSum")==null? "-" : l.getBigDecimal("onlineSum").toString());
			detailList.add(l.getInt("max_level")==null? "-" : l.getInt("max_level").toString());
		}
		
		
		//付费信息
		for(LogCharge lc : logCharge){
			detailList.add(lc.getStr("firstPaid")==null? "-" : lc.getStr("firstPaid"));
			detailList.add(lc.getStr("lastPaid")==null? "-" : lc.getStr("lastPaid"));
			detailList.add(lc.getDouble("paidSum")==null? "-" : lc.getDouble("paidSum").toString());
		}
		detailTable.add(detailList);
		data.put("code", 0);
		data.put("device", deviceTable);
		data.put("detail", detailTable);
		logger.info("data:" + data);
		return data;
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
