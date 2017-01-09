package common.service.impl;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import common.model.LogGold;
import common.model.LogRmb;
import common.pojo.CurrencyRmb;
import common.service.OperationCurrencyService;

public class OperationCurrencyServiceImpl implements OperationCurrencyService {
	/**
	 * 全服货币币获取和消耗
	 * @param startDate 
	 * @param endDate
	 * @param currency 货币类型 金币或钻石 
	 * @param start  mysql limit 起始
	 * @param length  mysql limit 长度
	 * @format 帐号-时间-途径-物品类型-数量-操作
	 * 需要对数据进行整理排序
	 * @author chris
	 */
	public Map<String, Object> queryAllCurrency(String startDate, String endDate, String currency, int start, int length, String db) {
		String goldSql = "select account,count,get_or_consume,reason,timestamp from log_gold where date between ? and ? limit ?,?";
		String RMBSql = "select account,count,get_or_consume,reason,timestamp from log_RMB where date between ? and ? limit ?,?";
		String goldCountSql = "select count(*)count from log_gold where date between ? and ?";
		String RMBCountSql = "select count(*)count from log_RMB where date between ? and ?";
		
		Map<String, Object> data = new HashMap<String, Object>();
		List<List<String>> tableData = new ArrayList<List<String>>();
		if(currency.equals("gold")){
			List<LogGold> logGold = LogGold.dao.use(db).find(goldSql, startDate, endDate, start, length);
			LogGold goldCount = LogGold.dao.use(db).findFirst(goldCountSql, startDate, endDate);
			for(LogGold lg : logGold){
				String account = lg.getStr("account");
				String count = lg.getInt("count").toString();
				String getConsume = lg.getInt("get_or_consume")==1?"获取":"消耗";
				String reason =  changeGoldReasonToChinese(lg.getStr("reason"));
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date queryTimestamp = lg.getDate("timestamp");
				String timestamp = sdf.format(queryTimestamp);
				List<String> subList = new ArrayList<String>();
				subList.add(account);
				subList.add(timestamp);
				subList.add(reason);
				subList.add("金币");
				subList.add(count);
				subList.add(getConsume);
				tableData.add(subList);
			}
			data.put("count", goldCount.getLong("count").intValue());
		}else{
			List<LogRmb> logRmb = LogRmb.dao.use(db).find(RMBSql, startDate, endDate, start, length);
			LogRmb RMBCount = LogRmb.dao.use(db).findFirst(RMBCountSql, startDate, endDate);
			for(LogRmb lr : logRmb){
				String account = lr.getStr("account");
				String count = lr.getInt("count").toString();
				String getConsume = lr.getInt("get_or_consume")==1?"获取":"消耗";
				String reason =  lr.getStr("reason");
				if("OBJ".equals(reason)){
					continue;
				}
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date queryTimestamp = lr.getDate("timestamp");
				String timestamp = sdf.format(queryTimestamp);
				List<String> subList = new ArrayList<String>();
				subList.add(account);
				subList.add(timestamp);
				subList.add(changeRMBReasonToChinese(reason));
				subList.add("钻石");
				subList.add(count);
				subList.add(getConsume);
				tableData.add(subList);
			}
			data.put("count", RMBCount.getLong("count").intValue());
		}
		data.put("tableData", tableData);
		return data;
	}
	
	
	
	/**
	 * 个人货币获取和消耗
	 * @param startDate 
	 * @param endDate
	 * @param currency  货币类型 金币或钻石
	 * @format 帐号-时间-途径-物品类型-数量-操作
	 * @author chris
	 * @return tableData
	 * @DESC 对于个人的钻石查询，由于reason=OBJ和reason=CHONGZHI 存在重复，因此需要去除OBJ中的CHONGZHI
	 */
	public List<List<String>> querySingleCurrency(String startDate, String endDate, String currency, String account, String db) {
		String goldSql = "select account,count,get_or_consume,reason,timestamp from log_gold where date between ? and ? and account = ?";
		String RMBSql = "select account,count,get_or_consume,reason,timestamp from log_RMB where date between ? and ? and account = ?";
		List<List<String>> data = new ArrayList<List<String>>();
		if(currency.equals("gold")){
			List<LogGold> logGold = LogGold.dao.use(db).find(goldSql, startDate, endDate, account);
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
		}else{
			List<LogRmb> logRmb = LogRmb.dao.use(db).find(RMBSql, startDate, endDate, account);
			//Map<count,Map<timestamp,CurrencyRmb>>
			Map<Integer,Map<Long,CurrencyRmb>> objMap = new HashMap<Integer,Map<Long,CurrencyRmb>>();
			//获取objMap
			for(LogRmb lr : logRmb){
				String reason =  lr.getStr("reason");
				if(!"OBJ".equals(reason)){
					continue;
				}
				int count = lr.getInt("count");
				String getConsume = lr.getInt("get_or_consume")==1?"获取":"消耗";
				Date queryTimestamp = lr.getDate("timestamp");
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String timestamp = sdf.format(queryTimestamp);
				Timestamp ts = Timestamp.valueOf(timestamp);
				
				CurrencyRmb cr = new CurrencyRmb();
				cr.setAccount(account);
				cr.setCount(count);
				cr.setGetOrConsume(getConsume);
				cr.setReason(reason);
				cr.setTimestamp(timestamp);
				if(objMap.containsKey(count)){
					Map<Long, CurrencyRmb> subMap = objMap.get(count);
					subMap.put(ts.getTime()/1000, cr);
					objMap.put(count, subMap);
				}else{
					Map<Long, CurrencyRmb> subMap = new TreeMap<Long, CurrencyRmb>();
					subMap.put(ts.getTime()/1000, cr);
					objMap.put(count, subMap);
				}
			}
			for(LogRmb lr : logRmb){
				int count = lr.getInt("count");
				String getConsume = lr.getInt("get_or_consume")==1?"获取":"消耗";
				String reason =  lr.getStr("reason");
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String timestamp = sdf.format(lr.getDate("timestamp"));
				Timestamp ts = Timestamp.valueOf(timestamp);
				long t = ts.getTime()/1000;
				if("OBJ".equals(reason)){
					continue;
				}
				if("CHONGZHI".equals(reason)){
					Map<Long, CurrencyRmb> subMap = objMap.get(count);
					try{
						Set<Long> keySet = subMap.keySet();
						for(long l:keySet ){
							if((t>l-15)&&(t<l+15)){
								subMap.remove(l);
								break;
							}
						}
					}catch(Exception e){
						e.printStackTrace();
					}
				}
				
				List<String> subList = new ArrayList<String>();
				subList.add(account);
				subList.add(timestamp);
				subList.add(changeRMBReasonToChinese(reason));
				subList.add("钻石");
				subList.add(String.valueOf(count));
				subList.add(getConsume);
				data.add(subList);
			}
			for(Map.Entry<Integer, Map<Long, CurrencyRmb>> entry:objMap.entrySet()){
				for(Map.Entry<Long, CurrencyRmb>subEntry:entry.getValue().entrySet()){
					CurrencyRmb cr = subEntry.getValue();
					List<String> subList = new ArrayList<String>();
					subList.add(account);
					subList.add(cr.getTimestamp());
					subList.add(changeRMBReasonToChinese(cr.getReason()));
					subList.add("钻石");
					subList.add(String.valueOf(cr.getCount()));
					subList.add(cr.getGetOrConsume());
					data.add(subList);
				}
			}
		}
		return data;
	} 
	
	private String changeGoldReasonToChinese(String reason){
		switch(reason){
		case "ArenaGetGuideScoreReward":
			reason = "天梯引导奖励";
			break;
		case "ArenaGuideBattleReward":
			reason = "天梯引导战斗奖励";
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
		case "OpenFixedRewardChest":
			reason = "开固定宝箱奖励";
			break;
		case "HandlerExReward":
			reason = "额外奖励";
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
			reason = "邮件附件/充值返利";
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
		case "CHONGZHI":
			reason = "充值";
			break;
		case "NEW_RONDOM_NAME":
			reason = "重新命名";
			break;
		case "HandlerExReward":
			reason = "额外奖励";
			break;
		case "OpenFixedRewardChest":
			reason = "开固定宝箱奖励";
			break;
		}
		return reason;
	}
	
}
