var contants = {};

$(function(){
	contants["hero"] = {
		"guanyu":"关羽",
		"linchong":"林冲",
		"zhangfei":"张飞",
		"zhaoyu":"赵云",
		"yuantiangang":"袁天罡",
		"huangzhong":"黄忠",
		"zhentianxingcun":"真田幸村",
		"diaochan":"貂蝉",
		"fububanzang":"服部半藏",
		"daji":"妲己",
		"baiqi":"白起",
		"fahai":"法海",
		"fengchenxiuji":"丰臣秀吉",
		"yuanfeizuozhu":"猿飞佐助",
		"changzongwobuyuanqin":"斯巴达",
		"lvbu":"吕布",
		"likui":"李逵",
		"jingke":"荆轲",
		"mozi":"墨子",
		"guidie":"归蝶",
		"zhitianxinchang":"织田信长",
		"chengyaojin":"程咬金",
		"anbeiqingming":"安倍晴明",
		"zhugeliang":"诸葛亮",
		"liguang":"李广",
		"qinqiong":"秦琼",
		"xiangyu":"项羽",
		"libai":"李白",
		"huamulan":"花木兰",
		"yangyouji":"养由基",
		"jiangziya":"姜子牙"
	};
	contants["properties"]= {
		"obj_1":"金币",	
		"obj_2":"钻石",	
		"obj_3":"经验",	
		"obj_5":"任务",
		"obj_7":"队伍等级	",	
		"obj_8":"属性物品	",	
		"obj_9":"升阶道具	",	
		"obj_11":"英雄",
		"obj_18":"英雄属性",
		"obj_19":"绿宝箱",
		"obj_20":"竞技场积分",	
		"obj_21":"蓝宝箱",	
		"obj_22":"紫宝箱",		
		"obj_23":"金宝箱",		
		"obj_24":"战斗位置",	
		"obj_26":"角斗场积分",	
		"obj_27":"蓝色钥匙",	
		"obj_28":"紫色钥匙",		
		"obj_29":"金色钥匙",		
		"obj_30":"称号",
		"obj_31":"大碎片宝箱",	
		"obj_32":"英雄皮肤",		
		"obj_33":"紫色瞬开宝箱",
		"obj_34":"金色瞬开宝箱",
		"obj_35":"英雄技能",	
		"obj_36":"技能石",
		"obj_37":"碎片宝箱",		
		"obj_38":"刷新券",
		"obj_3000":"钢铁之躯",
		"obj_3001":"热血战魂",
		"obj_3002":"盗贼笔记",
		"obj_3003":"百步穿杨",
		"obj_3004":"大道无形",
		"obj_3005":"护甲片",
		"obj_3006":"枪头",
		"obj_3007":"毒药",
		"obj_3008":"箭羽",
		"obj_3009":"太极图",
		"obj_4001":"普通生命石",
		"obj_4002":"稀有生命石",
		"obj_4003":"史诗生命石",
		"obj_4004":"普通物攻石",
		"obj_4005":"稀有物攻石",
		"obj_4006":"史诗物攻石",
		"obj_4007":"普通法攻石",
		"obj_4008":"稀有法攻石",
		"obj_4009":"史诗法攻石",
		"obj_4010":"普通物防石",
		"obj_4011":"稀有物防石",
		"obj_4012":"史诗物防石",
		"obj_4013":"普通法防石",
		"obj_4014":"稀有法防石",
		"obj_4015":"史诗法防石",
		"obj_4016":"普通暴击石",
		"obj_4017":"稀有暴击石",
		"obj_4018":"史诗暴击石",
		"obj_4019":"普通防暴石",
		"obj_4020":"稀有防暴石",
		"obj_4021":"史诗防暴石",
		"obj_4022":"普通命中石",
		"obj_4023":"稀有命中石",
		"obj_4024":"史诗命中石",
		"obj_4025":"普通闪避石",
		"obj_4026":"稀有闪避石",
		"obj_4027":"史诗闪避石",
		"obj_4028":"普通击退石",
		"obj_4029":"稀有击退石",
		"obj_4030":"史诗击退石",
		"obj_4031":"普通抗击退石",
		"obj_4032":"稀有抗击退石",
		"obj_4033":"史诗抗击退石",
		"obj_4034":"普通攻速石",
		"obj_4035":"稀有攻速石",
		"obj_4036":"史诗攻速石",
		"obj_4037":"普通破甲石",
		"obj_4038":"稀有破甲石",
		"obj_4039":"史诗破甲石",
		"obj_4040":"普通法穿石",
		"obj_4041":"稀有法穿石",
		"obj_4042":"史诗法穿石",
		"obj_4043":"普通速度石",
		"obj_4044":"稀有速度石",
		"obj_4045":"史诗速度石",		
		"obj_6000":"万能碎片",
		"obj_6001":"关羽碎片",
		"obj_6002":"秦琼碎片",
		"obj_6003":"李逵碎片",
		"obj_6004":"程咬金碎片",
		"obj_6005":"项羽碎片",
		"obj_6006":"丰臣秀吉碎片",
		"obj_6007":"张飞碎片",
		"obj_6008":"林冲碎片",
		"obj_6009":"赵云碎片",
		"obj_6010":"吕布碎片",
		"obj_6011":"白起碎片",
		"obj_6012":"斯巴达碎片",
		"obj_6013":"真田幸村碎片",
		"obj_6014":"黄忠碎片",
		"obj_6015":"花木兰碎片",
		"obj_6016":"李广碎片",
		"obj_6017":"养由基碎片",
		"obj_6018":"织田信长碎片",
		"obj_6019":"归蝶碎片",
		"obj_6020":"荆轲碎片",
		"obj_6021":"貂蝉碎片",
		"obj_6022":"李白碎片",
		"obj_6023":"墨子碎片",
		"obj_6024":"猿飞佐助碎片",
		"obj_6025":"服部半藏碎片",
		"obj_6026":"姜子牙碎片",
		"obj_6027":"妲己碎片",
		"obj_6028":"袁天罡碎片",
		"obj_6029":"诸葛亮碎片",
		"obj_6030":"安倍晴明碎片",
		"obj_6031":"法海碎片",
	};

	contants["urls"] = {
		"malaiAddress":"http://47.89.47.176:8002/gm",
		"ucAddress":"http://118.178.17.105:8002/gm",
		"iosAddress":"http://118.178.19.95:8002/gm",
		"testAddress":"http://120.25.209.140:8002/gm"
	};
	contants["servers"] = {
		"malaiServer":"egghk.koogame.cn",
		"ucServer":"egguccn2.koogame.cn",
		"iosServer":"egguccn.koogame.cn",
		"testServer":"eggactest.koogame.cn"
	};
	contants["heroFeatureType"] = {
		"1":"高血",
		"2":"物攻",
		"3":"法攻",
		"4":"物防",
		"5":"法防",
		"6":"暴击",
		"7":"防暴击",
		"8":"命中",
		"9":"闪避",
		"10":"击退",
		"11":"抗击退",
		"12":"攻速",
		"13":"破甲",
		"14":"法穿",
		"15":"速度"
	};

})

function getAddressFromIcon(icon){
    var emailAddress = "";
    switch(icon){
        case "malai":
        emailAddress = contants["urls"]["malaiAddress"];
        break;
        case "uc":
        emailAddress = contants["urls"]["ucAddress"];
        break;
        case "ios":
        emailAddress = contants["urls"]["iosAddress"];
        break;
        case "test":
        emailAddress = contants["urls"]["testAddress"];
        break;
    }
    return emailAddress;
}

function getServerFromIcon(icon){
    var server = "";
    switch(icon){
        case "malai":
        server = contants["servers"]["malaiServer"];
        break;
        case "uc":
        server = contants["servers"]["ucServer"];
        break;
        case "ios":
        server = contants["servers"]["iosServer"];
        break;
        case "test":
        server = contants["servers"]["testServer"];
        break;
    }
    return server;
}