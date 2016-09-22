package common.service;

import java.util.Map;

public interface RealtimeService {
	public Map<String, String> queryBeforeData();
	
	public Map<String, String> queryRealtimeData();
	
	public Map<String, Object> queryRealtimeDevice(String icons, String[] date);
	
	public Map<String, Object> queryRealtimeAddPlayers(String icons, String[] date);
	
	public Map<String, Object> queryRealtimeRevenue(String icons, String[] date);
}
