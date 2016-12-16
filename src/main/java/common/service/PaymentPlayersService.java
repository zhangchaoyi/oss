package common.service;

import java.util.List;

public interface PaymentPlayersService {
	public List<List<String>> queryPLayersList(String startDate, String endDate, String icons, String db);

	public List<List<String>> queryPlayerByAccount(String account, String db);
}
