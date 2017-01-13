package common.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import common.config.PropertiesConfigs;
import common.model.DeviceInfo;
import common.service.ApiService;

public class ApiServiceImpl implements ApiService {
	private static Logger logger = Logger.getLogger(ApiServiceImpl.class);
	private Map<String, String> configChannels = PropertiesConfigs.getChannels();
	
	public Map<String, Object> queryChannelsVersions(String db){
		String sql = "select distinct ch_id,script_version from device_info";
		List<DeviceInfo> chver = DeviceInfo.dao.use(db).find(sql);
		
		Map<String, Object> data = new HashMap<String, Object>();
		Map<String, String> channels = new HashMap<String, String>();
		Set<String> versions = new HashSet<String>();
		//获取查询的渠道和配置渠道的交集 版本号
		for(DeviceInfo di : chver){
			String v = di.getStr("script_version");
			String chId = di.getStr("ch_id");
			versions.add(v);
			if(channels.containsKey(chId)){
				continue;
			}
			if(!configChannels.containsKey(chId)){
				logger.info("------------------------------------------配置表不存在该渠道-----------------------------");
				continue;
			}
			channels.put(chId, configChannels.get(chId));
		}
		List<String> verList = new ArrayList<String>(versions);
		data.put("channels", channels);
		data.put("versions", verList);
		return data;
	}
}
