var roleCurrentInfoTable = "#table-player-info";
var roleCurrentHeroTable = "#table-player-info-hero";
var roleCurrentObjTable = "#table-player-info-object";

$(function(){
	withoutIcon();
	loadData();
})

function loadData(){
	loadPlayerInfo();
}

function loadPlayerInfo(){
	// var accountName = $("#input-account-name").val();
	// if(accountName==""||accountName==null){
	// 	return;
	// }
	// $.post("", {
 //        account:accountName
 //    },
 //    function(data, status) {
 //        configTable(data, dataTable);
 //    });
 	var test = {
		"account":20090101,
		"roleName":"jack",
		"uid":"abcdefghijklmn",
		"level":10,
		"lastLogin":"2016-12-13 20:00:01",
		"lastPaid":"2016-12-12 10:20:11",
		"gold":1000,
		"rmb":199,
		"heroList":[{"name":"guanyu","level":2},{"name":"huangzhong","level":3},{"name":"linchong","level":4}],
		"objectList":[{"obj_id":"obj_1","num":2},{"obj_id":"obj_2","num":3}],
		"arena":{"score":1234,"rank":4},
		"ladder":{"score":1234,"rank":4},
		"history":{"score":1234,"rank":3,"title":"蛋元人尊"},
		"PVP":{"wins":9},
		"friends":{"focus":100,"fans":101}	
	}
	var dealData = dealJsonDataToTable(test);
	var tableData = [];
	tableData.push(dealData);
	configTable(tableData, roleCurrentInfoTable);
	configTable(getHeroInfo(test.heroList), roleCurrentHeroTable);
	configTable(getObjInfo(test.objectList), roleCurrentObjTable);
}

function configTable(data,dataTable) {
    $(dataTable).dataTable().fnClearTable();  
    $(dataTable).dataTable({
        "destroy": true,
        // retrive:true,
        "data": data,
        "order": [[ 1, 'asc' ]],
        "dom": '<"top"f>rt<"left"lip>',
        'language': {
            'emptyTable': '没有数据',
            'loadingRecords': '加载中...',
            'processing': '查询中...',
            'search': '查询:',
            'lengthMenu': '每页显示 _MENU_ 条记录',
            'zeroRecords': '没有数据',
            "sInfo": "(共 _TOTAL_ 条记录)",
            'infoEmpty': '没有数据',
            'infoFiltered': '(过滤总件数 _MAX_ 条)'
        }
    });
}

//帐号-角色名-uid-等级-最后一次登录时间-最后一次充值时间-金币-钻石-仓库-PVP-历史排名
function dealJsonDataToTable(data){
	var tableData = [];
	tableData.push(data.account);
	tableData.push(data.roleName);
	tableData.push(data.uid);
	tableData.push(data.level);
	tableData.push(data.lastLogin);
	tableData.push(data.lastPaid);
	tableData.push(data.gold);
	tableData.push(data.rmb);
	// tableData.push(getHeroInfo(data.heroList));
	// tableData.push(getObjInfo(data.objectList));
	tableData.push("积分:"+data.ladder.score+" 排名:"+data.ladder.rank);
	tableData.push("积分:"+data.arena.score+" 排名:"+data.arena.rank);
	tableData.push("积分:"+data.history.score+" 排名:"+data.history.rank+" 称号:"+data.history.title);
	tableData.push("约战胜场:"+data.PVP.wins);
	tableData.push("关注:"+data.friends.focus+" 粉丝:"+data.friends.fans);
	return tableData;
}

//"heroList":[{"name":"guanyu","level":2},{"name":"huangzhong","level":3}]
function getHeroInfo(heroList){
	var heroesArray = [];
	for(var i in heroList){
		var name = heroList[i].name;
		var level = heroList[i].level;
		var heroname = contants["hero"][name];
		var heroArray = [];
		heroArray.push(heroname==undefined?name:heroname);
		heroArray.push(level);
		heroesArray.push(heroArray);
	}
	return heroesArray;
}
//"objectList":[{"obj_id":"obj_1","num":2},{"obj_id":"obj_2","num":3}]
function getObjInfo(objList){
	var objsArray = [];
	for(var i in objList){
		var objId = objList[i].obj_id;
		var num = objList[i].num;
		var objName = contants["properties"][objId];
		var objArray = [];
		objArray.push(objName==undefined?objId:objName);
		objArray.push(num);
		objsArray.push(objArray);
	}
	return objsArray;
}

$("#menu-account-name > li").click(function(){
	var txt = $(this).children("a").text();
	$("#btn-account-name").html(txt+"<span class='caret'><span>");
});

$("#btn-player-info").click(function(){
	loadPlayerInfo();
});