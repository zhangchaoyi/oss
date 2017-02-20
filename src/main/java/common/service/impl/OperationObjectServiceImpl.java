package common.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import common.model.LogObj;
import common.service.OperationObjectService;
import common.util.Contants;

public class OperationObjectServiceImpl implements OperationObjectService {
	/**
	 * 个人物品获取和消耗
	 * @param startDate 
	 * @param endDate
	 * @format 帐号-时间-途径-物品类型-数量-操作
	 * @author chris
	 * @return tableData
	 */
	public List<List<String>> querySingleObject(String startDate, String endDate, String account, String db) {
		String sql = "select account,obj_id,count,get_or_consume,reason,timestamp from log_obj where date between ? and ? and account = ?";
		List<LogObj> logObj = LogObj.dao.use(db).find(sql, startDate, endDate, account);
		List<List<String>> data = new ArrayList<List<String>>();
		for(LogObj lo : logObj){
			String objId = lo.getStr("obj_id");
			String count = lo.getInt("count").toString();
			String getConsume = lo.getInt("get_or_consume")==1?"获取":"消耗";
			String reason = changeReasonToChinese(lo.getStr("reason"));
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String timestamp = sdf.format(lo.getDate("timestamp"));
			String objName = Contants.getPropName(objId);
			List<String> subList = new ArrayList<String>();
			subList.add(account);
			subList.add(timestamp);
			subList.add(reason);
			subList.add(objName);
			subList.add(count);
			subList.add(getConsume);
			data.add(subList);
		}
		return data;
	}
	
	private String changeReasonToChinese(String reason){
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
}
