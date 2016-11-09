package common.service.impl;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import common.model.SecRole;
import common.model.SecUser;
import common.model.SecUserRole;
import common.service.AdminService;
import common.utils.EncryptUtils;
import common.utils.RandomUtil;
import org.apache.log4j.Logger;

public class AdminServiceImpl implements AdminService {
	private static Logger logger = Logger.getLogger(AdminServiceImpl.class);
	//查看user是否存在
	public SecUser getUser(String username) {
		String sql = "select password, salt from sec_user where user_name = ?";
		SecUser secUser = SecUser.dao.findFirst(sql, username);
		return secUser;
	}
	
	//根据username查询 用户角色
	public List<String> queryRoleByUsername(String username) {
		String sql = "select role_name from sec_role A join sec_user_role B on A.role_id = B.role_id join sec_user C on B.user_id = C.user_id where C.user_name = ?";
		List<SecRole> secRole = SecRole.dao.find(sql, username);
		
		List<String> roles = new ArrayList<String>();
		for(SecRole sr : secRole){
			String role = sr.getStr("role_name");
			roles.add(role);
		}
		return roles;
	}
	//判断角色是否具有该角色权限,如果该用户的role列表中存在一个role比当前Interceptor权重大则为true
	//例如Interceptor为DataGuest,而用户的具有admin的role权限,则为true
	public boolean validateRolePermission(List<String> roles, String queryRole) {
		boolean permission = false;
		int queryRoleWeight = getRoleWeight(queryRole);
		for(String r : roles){
			if(getRoleWeight(r) >= queryRoleWeight){
				permission = true;
				break;
			}
		}
		return permission;
	}
	//注册新账户
	public boolean signupUser(String username, String password, String role) {
		boolean succeed = false;
		String salt = RandomUtil.getRandomString(6);
		//密码原文 + salt
		password += salt;
		//md5处理密码后存数据库 password 字段
		try {
			password = EncryptUtils.EncoderByMd5(password);
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			e.printStackTrace();
			logger.debug("<AdminServiceImpl> Exception:", e);
		}
		SecUser secUser = new SecUser().set("user_name", username).set("password", password).set("salt", salt).set("created_time", new Date());
		succeed = secUser.save();
		int roleId = getRoleIdByRoleName(role);
		succeed = new SecUserRole().set("user_id", secUser.get("user_id")).set("role_id", roleId).save();
		return succeed;
	}
	
	//用户名是否已经存在
	public boolean existUser(String username) {
		boolean exist = false;
		String sql = "select * from sec_user where user_name = ?";
		SecUser secUser = SecUser.dao.findFirst(sql, username);
		if(secUser!=null){
			exist=true;
		}
		return exist;
	}
	
	//查询所有用户的权限
	public List<List<String>> queryAllUsers() {
		String sql = "select A.user_name,A.created_time,C.role_name from sec_user A join sec_user_role B on A.user_id = B.user_id join sec_role C on B.role_id = C.role_id;";
		List<SecUser> secUsers = SecUser.dao.find(sql);
		//Map<username,Map<role/createTime,String>>
		Map<String, Map<String, String>> sort = new HashMap<String, Map<String, String>>();
		for(SecUser su : secUsers){
			String username = su.getStr("user_name");
			String rolename = su.getStr("role_name");
			Date createTime = su.getDate("created_time");
			String roleStr = "";
			Map<String, String> subMap = null;
			if(sort.containsKey(username)){
				subMap = sort.get(username);
				roleStr = subMap.get("roleName");
				roleStr += ","+rolename;
				subMap.put("roleName", roleStr);
			}else{
				subMap = new LinkedHashMap<String, String>();
				subMap.put("roleName", rolename);
				subMap.put("createTime", createTime.toString());
			}
			sort.put(username, subMap);
		}
		System.out.println(sort);
		List<List<String>> data = new ArrayList<List<String>>();
		for(Map.Entry<String, Map<String, String>> entry : sort.entrySet()){
			List<String> subList = new ArrayList<String>();
			subList.add(entry.getKey());
			for(Map.Entry<String, String> subEntry : entry.getValue().entrySet()){
				switch(subEntry.getKey()){
				case "roleName":
					subList.add(1, subEntry.getValue());
					break;
				case "createTime":
					subList.add(2, subEntry.getValue());
					break;
				}
			}
			data.add(subList);
		}
		System.out.println(data);
		logger.debug("<AdminServiceImpl> queryAllUsers:" + data);
		return data;
	}
	
	private int getRoleWeight(String role){
		int weight = 0;
		switch(role){
		case "root":
			weight = 4;
			break;
		case "admin":
			weight = 3;
			break;
		case "vip":
			weight = 2;
			break;
		case "data_guest":
			weight = 1;
			break;
		case "gm":
			weight = 1;
			break;
		}
		return weight;
	}
	
	private int getRoleIdByRoleName(String role){
		int roleId = 0;
		switch(role){
		case "root":
			roleId = 1;
			break;
		case "admin":
			roleId = 2;
			break;
		case "vip":
			roleId = 3;
			break;
		case "data_guest":
			roleId = 4;
			break;
		case "gm":
			roleId = 5;
			break;
		}
		return roleId;
	}
}
