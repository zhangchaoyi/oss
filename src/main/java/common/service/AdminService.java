package common.service;

import java.util.List;
import java.util.Map;

import common.model.SecUser;
/**
 * 后台帐号管理接口
 * @author chris
 */
public interface AdminService {
	public List<String> queryRoleByUsername(String username);

	public boolean validateRolePermission(List<String> roles, String queryRole);
	
	public SecUser getUser(String username);

	public boolean signupUser(String username, String password, String role, Map<String, String> map, String db);
	
	public boolean existUser(String username);
	
	public List<List<String>> queryUsers(String...queryUsername);
	
	public int deleteByUserName(String users, String db);
	
	public void changeRoles(String username, String[] queryRole, Map<String, String> map, String db);
	
	public int changeUserPw(String username, String newPassword, String db);
}
