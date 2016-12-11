package common.service;

import java.util.List;
import java.util.Map;

import common.model.SecUser;

public interface AdminService {
	public List<String> queryRoleByUsername(String username);

	public boolean validateRolePermission(List<String> roles, String queryRole);
	
	public SecUser getUser(String username);

	public boolean signupUser(String username, String password, String role, Map<String, String> map);
	
	public boolean existUser(String username);
	
	public List<List<String>> queryUsers(String...queryUsername);
	
	public int deleteByUserName(String users);
	
	public void changeRoles(String username, String[] queryRole, Map<String, String> map);
}
