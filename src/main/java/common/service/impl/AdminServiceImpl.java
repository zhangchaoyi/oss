package common.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import common.model.SecRole;
import common.model.SecUser;
import common.model.SecUserRole;
import common.service.AdminService;
import common.utils.RandomUtil;

public class AdminServiceImpl implements AdminService {
	//查看user是否存在
	public SecUser getUser(String username) {
		String sql = "select password from sec_user where user_name = ?";
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
	//判断角色是否具有该角色权限
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
		String salt = RandomUtil.getRandomString(8);
		SecUser secUser = new SecUser().set("user_name", username).set("password", password).set("salt", salt).set("created_time", new Date());
		succeed = secUser.save();
		int roleId = getRoleIdByRoleName(role);
		succeed = new SecUserRole().set("user_id", secUser.get("user_id")).set("role_id", roleId).save();
		return succeed;
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
