package common.service;

import java.util.Map;
/**
 * api接口
 * @author chris
 */
public interface ApiService {
	public Map<String, Object> queryChannelsVersions(String db);
}
