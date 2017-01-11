package common.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import com.jfinal.kit.PropKit;

public class PropertiesConfigs {
	private static Map<String,String> channels = null;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map<String, String> getChannels(){
		if(channels==null){
			synchronized(PropertiesConfigs.class){
				Properties prop = PropKit.use("channel.txt").getProperties();
				channels = new HashMap<String, String>((Map)prop);
			}
		}
		return channels;
	}
}
