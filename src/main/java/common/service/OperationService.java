package common.service;

import java.util.List;

public interface OperationService {
	public boolean addFeedback(String account, String title, String content, String server, String port);

	public List<List<String>> queryFeedback(String startDate, String endDate);
	
	public List<List<String>> queryFeedbackDetail(String queryAccount, String queryServer, String startDate, String endDate);

	public int completeReply(int id);
}
