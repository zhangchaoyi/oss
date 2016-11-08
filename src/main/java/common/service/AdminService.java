package common.service;

import java.util.List;

import common.model.SecUser;

public interface AdminService {
	public List<String> queryRoleByUsername(String username);

	public boolean validateRolePermission(List<String> roles, String queryRole);
	
	public SecUser getUser(String username);

	public boolean signupUser(String username, String password, String role);
	
	public boolean existUser(String username);
}
