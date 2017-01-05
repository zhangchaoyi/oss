package common.service;

import java.util.List;

public interface PaidRecoverService {
	public List<List<String>> queryOrderByAccount(String account, String db);
}
