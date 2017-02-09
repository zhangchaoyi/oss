package common.service;

import java.util.List;
import java.util.Map;

public interface OperationService {
	public boolean addFeedback(String account, String title, String content, String server, String port);

	public List<List<String>> queryFeedback(String startDate, String endDate, String server);

	public int completeReply(int id);
	
	public Map<String, String> queryFeedbackById(String id);
	
	public int insertGmRecord(String account, String operation, String emailAddress, String type);
	
	public List<List<String>> queryGmRecord(String startDate, String endDate, String type, String address);
	
	public int setGmRecordMailToSucceed(int rowId);
}
