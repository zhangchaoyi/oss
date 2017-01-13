package common.service;

import java.util.Map;

public interface RealtimeService {	
	public Map<String, Object> queryRealtimeDevice(String icons, String[] date, String db, String versions, String chId);
	
	public Map<String, Object> queryRealtimeAddPlayers(String icons, String[] date, String db, String versions, String chId);
	
	public Map<String, Object> queryRealtimeRevenue(String icons, String[] date, String db, String versions, String chId);
	
	public Map<String, Object> queryRealtimePlayerCount(String[] date, String db);
	
	public Map<String, String> realtimeData(String icons, String db, String versions, String chId);
	
	public Map<String, String> beforeData(String icons, String db, String versions, String chId);
}
