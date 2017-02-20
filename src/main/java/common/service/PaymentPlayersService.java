package common.service;

import java.util.List;
/**
 * 付费玩家接口
 * @author chris
 */
public interface PaymentPlayersService {
	public List<List<String>> queryPLayersList(String startDate, String endDate, String icons, String db);

	public List<List<String>> queryPlayerByAccount(String account, String db);
}
