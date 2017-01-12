package common.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import common.config.PropertiesConfigs;
import common.model.DeviceInfo;
import common.service.ApiService;

public class ApiServiceImpl implements ApiService {
	private static Logger logger = Logger.getLogger(ApiServiceImpl.class);
	private Map<String, String> configChannels = PropertiesConfigs.getChannels();
	
	public Map<String, String> queryChannels(String db){
		String sql = "select distinct ch_id from device_info";
		List<DeviceInfo> deviceInfo = DeviceInfo.dao.use(db).find(sql);
		Map<String, String> channels = new HashMap<String, String>();
		//获取查询的渠道和配置渠道的交集
		for(DeviceInfo di : deviceInfo){
			String chId = di.getStr("ch_id");
			if(!configChannels.containsKey(chId)){
				logger.info("------------------------------------------配置表不存在该渠道-----------------------------");
				continue;
			}
			channels.put(chId, configChannels.get(chId));
		}
		return channels;
	}
}
