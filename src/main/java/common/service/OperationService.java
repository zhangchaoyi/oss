package common.service;

import java.util.List;

public interface OperationService {
	public boolean addFeedback(String account, String title, String content, String server, String port);

	public List<List<String>> queryFeedback();
	
	public List<List<String>> queryFeedbackDetail(String queryAccount, String queryServer);
}
