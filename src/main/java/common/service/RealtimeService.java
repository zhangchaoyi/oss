package common.service;

import java.util.Map;

public interface RealtimeService {
	public Map<String, String> queryBeforeData(String icons, String db);
	
	public Map<String, String> queryRealtimeData(String icons, String db);
	
	public Map<String, Object> queryRealtimeDevice(String icons, String[] date, String db);
	
	public Map<String, Object> queryRealtimeAddPlayers(String icons, String[] date, String db);
	
	public Map<String, Object> queryRealtimeRevenue(String icons, String[] date, String db);
	
	public Map<String, Object> queryRealtimePlayerCount(String[] date, String db);
}
