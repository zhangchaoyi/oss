package common.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import common.model.LogGold;
import common.model.LogRmb;
import common.mysql.DbSelector;
import common.service.OperationCurrencyService;

public class OperationCurrencyServiceImpl implements OperationCurrencyService {
	private String db = DbSelector.getDbName();
	/**
	 * 全服货币获取和消耗
	 * @param startDate 
	 * @param endDate
	 * @format 帐号-时间-途径-物品类型-数量-操作
	 * @author chris
	 */
	public List<List<String>> queryAllCurrency(String startDate, String endDate) {
		String goldSql = "select account,count,get_or_consume,reason,timestamp from log_gold where date between ? and ?";
		String RMBSql = "select account,count,get_or_consume,reason,timestamp from log_RMB where date between ? and ?";
		List<LogGold> logGold = LogGold.dao.use(db).find(goldSql, startDate, endDate);
		List<LogRmb> logRmb = LogRmb.dao.use(db).find(RMBSql, startDate, endDate);
		List<List<String>> data = new ArrayList<List<String>>();
		for(LogGold lg : logGold){
			String account = lg.getStr("account");
			String count = lg.getInt("count").toString();
			String getConsume = lg.getInt("get_or_consume")==1?"获取":"消耗";
			String reason =  changeGoldReasonToChinese(lg.getStr("reason"));
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String timestamp = sdf.format(lg.getDate("timestamp"));
			List<String> subList = new ArrayList<String>();
			subList.add(account);
			subList.add(timestamp);
			subList.add(reason);
			subList.add("金币");
			subList.add(count);
			subList.add(getConsume);
			data.add(subList);
		}
		for(LogRmb lr : logRmb){
			String account = lr.getStr("account");
			String count = lr.getInt("count").toString();
			String getConsume = lr.getInt("get_or_consume")==1?"获取":"消耗";
			String reason =  changeRMBReasonToChinese(lr.getStr("reason"));
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String timestamp = sdf.format(lr.getDate("timestamp"));
			List<String> subList = new ArrayList<String>();
			subList.add(account);
			subList.add(timestamp);
			subList.add(reason);
			subList.add("钻石");
			subList.add(count);
			subList.add(getConsume);
			data.add(subList);
		}
		
		return data;
	}
	
	/**
	 * 个人货币获取和消耗
	 * @param startDate 
	 * @param endDate
	 * @format 帐号-时间-途径-物品类型-数量-操作
	 * @author chris
	 * @return tableData
	 */
	public List<List<String>> querySingleCurrency(String startDate, String endDate, String account) {
		String goldSql = "select account,count,get_or_consume,reason,timestamp from log_gold where date between ? and ? and account = ?";
		String RMBSql = "select account,count,get_or_consume,reason,timestamp from log_RMB where date between ? and ? and account = ?";
		List<LogGold> logGold = LogGold.dao.use(db).find(goldSql, startDate, endDate, account);
		List<LogRmb> logRmb = LogRmb.dao.use(db).find(RMBSql, startDate, endDate, account);
		List<List<String>> data = new ArrayList<List<String>>();

		for(LogGold lg : logGold){
			String count = lg.getInt("count").toString();
			String getConsume = lg.getInt("get_or_consume")==1?"获取":"消耗";
			String reason =  changeGoldReasonToChinese(lg.getStr("reason"));
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String timestamp = sdf.format(lg.getDate("timestamp"));
			List<String> subList = new ArrayList<String>();
			subList.add(account);
			subList.add(timestamp);
			subList.add(reason);
			subList.add("金币");
			subList.add(count);
			subList.add(getConsume);
			data.add(subList);
		}
		for(LogRmb lr : logRmb){
			String count = lr.getInt("count").toString();
			String getConsume = lr.getInt("get_or_consume")==1?"获取":"消耗";
			String reason =  changeRMBReasonToChinese(lr.getStr("reason"));
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String timestamp = sdf.format(lr.getDate("timestamp"));
			List<String> subList = new ArrayList<String>();
			subList.add(account);
			subList.add(timestamp);
			subList.add(reason);
			subList.add("钻石");
			subList.add(count);
			subList.add(getConsume);
			data.add(subList);
		}
		return data;
	} 
	
	private String changeGoldReasonToChinese(String reason){
		switch(reason){
		case "ArenaGetGuideScoreReward":
			reason = "天梯引导奖励";
			break;
		case "LevelUp":
			reason = "升级";
			break;
		case "ArenaBattleReward":
			reason = "天梯战斗奖励";
			break;
		case "TaleGetReward":
			reason = "剧情任务奖励";
			break;
		case "nil":
			reason = "未知";
			break;
		case "SecretAreaReward":
			reason = "秘境奖励";
			break;
		case "OpenFreeChest":
			reason = "开免费宝箱";
			break;
		case "friend steal":
			reason = "偷东西";
			break;
		case "LadderGetReward":
			reason = "角斗场奖励";
			break;
		case "TaskGetReward":
			reason = "任务奖励";
			break;
		case "TaskGetDailyPersentReward":
			reason = "日常任务奖励";
			break;
		case "StoreContentGrant":
			reason = "购买物品失败回退";
			break;
		case "OpenRandomRewardChest":
			reason = "开随机奖励宝箱";
			break;
		case "DirectOpenChest":
			reason = "直接开启宝箱";
			break;
		case "GetRewardByRewardTimes":
			reason = "角斗场宝箱";
			break;
		case "STORE_SPEND":
			reason = "商店花费";
			break;
		case "ManualRefresh":
			reason = "手动刷新商店";
			break;
		case "LadderGetWeeklyRankReward":
			reason = "角斗场周排行奖励";
			break;
		case "TaleGetExtraReward":
			reason = "发话额外奖励";
			break;
		case "ArenaGetRankReward":
			reason = "天梯排名奖励";
			break;
		case "LadderGetDanGradingReward":
			reason = "角斗场段位奖励";
			break;
		}
		return reason;
	}
	
	private String changeRMBReasonToChinese(String reason){
		switch(reason){
		case "TaleGetReward":
			reason = "剧情任务奖励";
			break;
		case "chat":
			reason = "聊天花费";
			break;
		case "OBJ":
			reason = "购买物品";
			break;
		case "TaskGetDailyPersentReward":
			reason = "日常任务奖励";
			break;
		case "ArenaBattleReward":
			reason = "天梯战斗奖励";
			break;
		case "TaskGetReward":
			reason = "任务奖励";
			break;
		case "STORE_SPEND":
			reason = "商店花费";
			break;
		case "CancelChestCold":
			reason = "取消宝箱剩余冷却时间";
			break;
		case "OpenFreeChest":
			reason = "开免费宝箱";
			break;
		case "DirectOpenChest":
			reason = "直接开启宝箱";
			break;
		case "OpenRandomRewardChest":
			reason = "开随机奖励宝箱";
			break;
		case "ArenaGuideBattleReward":
			reason = "天梯引导战斗奖励";
			break;
		case "LadderGetWeeklyRankReward":
			reason = "角斗场周排行奖励";
			break;
		case "ArenaRefreshExpGetTimes":
			reason = "刷新天梯经验上限";
			break;
		case "RefreshDailyLoseTimes":
			reason = "刷新日常失败记录";
			break;
		case "ArenaGetRankReward":
			reason = "天梯排行奖励";
			break;
		case "become guard":
			reason = "成为擂主";
			break;
		case "LadderGetDanGradingReward":
			reason = "角斗场段位奖励";
			break;
		}
		return reason;
	}
}
