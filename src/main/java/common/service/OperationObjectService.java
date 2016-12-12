package common.service;

import java.util.List;

public interface OperationObjectService {
	public List<List<String>> querySingleObject(String startDate, String endDate, String account);
}
