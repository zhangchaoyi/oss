package common.utils;

import java.util.HashMap;
import java.util.Map;

public class Contants {
	public static final String HEROID = "obj_11";
	public static final String GREENBOXID = "obj_19";
	public static final String BLUEBOXID = "obj_21";
	public static final String PURPLEBOXID = "obj_22";
	public static final String GOLDENBOXID = "obj_23";
	public static final String BIGFRAGMENTBOXID = "obj_31";
	public static final String FRAGMENTBOXID = "obj_37";
	private static final Map<String, String> propsMap = new HashMap<String, String>();

	public static Map<String, String> getPropMap() {
		return propsMap;
	}

	public static void initPropMap() {
		propsMap.put("linchong", "林冲");
		propsMap.put("zhangfei", "张飞");
		propsMap.put("zhaoyun", "赵云");
		propsMap.put("yuantiangang", "袁天罡");
		propsMap.put("huangzhong", "黄忠");
		propsMap.put("zhentianxingcun", "真田幸村");
		propsMap.put("diaochan", "貂蝉");
		propsMap.put("fububanzang", "服部半藏");
		propsMap.put("daji", "妲己");
		propsMap.put("baiqi", "白起");
		propsMap.put("fahai", "法海");
		propsMap.put("fengchenxiuji", "丰臣秀吉");
		propsMap.put("yuanfeizuozhu", "猿飞佐助");
		propsMap.put("changzongwobuyuanqin", "斯巴达");
		propsMap.put("lvbu", "吕布");
		propsMap.put("likui", "李逵");
		propsMap.put("jingke", "荆轲");
		propsMap.put("mozi", "墨子");
		propsMap.put("guidie", "归蝶");
		propsMap.put("zhitianxinchang", "织田信长");
		propsMap.put("chengyaojin", "程咬金");
		propsMap.put("anbeiqingming", "安倍晴明");
		propsMap.put("zhugeliang", "诸葛亮");
		propsMap.put("liguang", "李广");
		propsMap.put("obj_1", "金币");
		propsMap.put("obj_2", "钻石");
		propsMap.put("obj_3", "经验");
		propsMap.put(Contants.GREENBOXID, "绿宝箱");
		propsMap.put(Contants.BLUEBOXID, "蓝宝箱");
		propsMap.put(Contants.PURPLEBOXID, "紫宝箱");
		propsMap.put(Contants.GOLDENBOXID, "金宝箱");
		propsMap.put(Contants.FRAGMENTBOXID, "碎片宝箱");
		propsMap.put(Contants.BIGFRAGMENTBOXID, "大碎片宝箱");
		propsMap.put("obj_27", "蓝色钥匙");
		propsMap.put("obj_28", "紫色钥匙");
		propsMap.put("obj_29", "金色钥匙");
		propsMap.put("obj_36", "技能石");
		propsMap.put("obj_3002", "盗贼笔记");
		propsMap.put("obj_3003", "百步穿杨");
		propsMap.put("obj_3004", "大道无形");
		propsMap.put("obj_3005", "护甲片");
		propsMap.put("obj_3006", "枪头");
		propsMap.put("obj_3007", "毒药");
		propsMap.put("obj_3008", "箭羽");
		propsMap.put("obj_3009", "太极图");
		propsMap.put("obj_6000", "万能碎片");
		propsMap.put("obj_6001", "关羽碎片");
		propsMap.put("obj_3000", "钢铁之躯");
		propsMap.put("obj_3001", "热血战魂");
		propsMap.put("obj_6029", "诸葛亮碎片");
		propsMap.put("obj_6030", "安倍晴明碎片");
		propsMap.put("obj_6031", "法海碎片");
		propsMap.put("obj_6002", "秦琼碎片");
		propsMap.put("obj_6003", "李逵碎片");
		propsMap.put("obj_6004", "程咬金碎片");
		propsMap.put("obj_6005", "项羽碎片");
		propsMap.put("obj_6006", "丰臣秀吉碎片");
		propsMap.put("obj_6007", "张飞碎片");
		propsMap.put("obj_6008", "林冲碎片");
		propsMap.put("obj_6009", "赵云碎片");
		propsMap.put("obj_6010", "吕布碎片");
		propsMap.put("obj_6011", "白起碎片");
		propsMap.put("obj_6012", "斯巴达碎片");
		propsMap.put("obj_6013", "真田幸村碎片");
		propsMap.put("obj_6014", "黄忠碎片");
		propsMap.put("obj_6015", "花木兰碎片");
		propsMap.put("obj_6016", "李广碎片");
		propsMap.put("obj_6017", "养由基碎片");
		propsMap.put("obj_6018", "织田信长碎片");
		propsMap.put("obj_6019", "归蝶碎片");
		propsMap.put("obj_6020", "荆轲碎片");
		propsMap.put("obj_6021", "貂蝉碎片");
		propsMap.put("obj_6022", "李白碎片");
		propsMap.put("obj_6023", "墨子碎片");
		propsMap.put("obj_6024", "猿飞佐助碎片");
		propsMap.put("obj_6025", "服部半藏碎片");
		propsMap.put("obj_6026", "姜子牙碎片");
		propsMap.put("obj_6027", "妲己碎片");
		propsMap.put("obj_6028", "袁天罡碎片");
	}

	public static String getPropName(String objId) {
		objId = objId.replace("\"", "");
		return propsMap.get(objId);
	}

	public static String getAddressFromIcon(String icon) {
		String emailAddress = "";
		switch (icon) {
		case "malai":
			emailAddress = "http://47.89.47.176:8002/gm";
			break;
		case "uc":
			emailAddress = "http://118.178.17.105:8002/gm";
			break;
		case "ios":
			emailAddress = "http://118.178.19.95:8002/gm";
			break;
		case "test":
			emailAddress = "http://120.25.209.140:8002/gm";
			break;
		}
		return emailAddress;
	}
}
