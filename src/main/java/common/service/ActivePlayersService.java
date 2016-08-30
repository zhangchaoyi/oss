package common.service;

import java.util.List;

public interface ActivePlayersService {

	public List<Long> queryDau(List<String> categories, String startDate, String endDate);
}
