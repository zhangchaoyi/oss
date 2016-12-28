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
	var accountName = $("#input-account-name").val();
	if(accountName==""||accountName==null){
		configTable(null, roleCurrentInfoTable);
		configTable(null, roleCurrentHeroTable);
		configTable(null, roleCurrentObjTable);
		return;
	}
	var text = [];
	text.push(accountName);
	var payloadData = {
    "cmd":"get_player_info",
    "parms":text,
    "account":"admin",
    "password":"af03f87cca0a5e8838c3c8454f58605de41f77f5"
    };

    //请求游戏服务器
    $.ajax({
        type: "POST",
        url: getAddressFromIcon($("#btn-db").attr("data-info")),
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify(payloadData),
        crossDomain: true,
        dataType: "json",
        success: function (data) {
        	if(data.result=='1'){
        		configTable(dealJsonDataToTable(data.ret_data), roleCurrentInfoTable);
				configTable(getHeroInfo(data.ret_data.hero_list), roleCurrentHeroTable);
				configTable(getObjInfo(data.ret_data.knapsack), roleCurrentObjTable);
        	}else{
        		alert("帐号不存在");
        		configTable(null, roleCurrentInfoTable);
				configTable(null, roleCurrentHeroTable);
				configTable(null, roleCurrentObjTable);
        	}
        },
    });
}

function configTable(data,dataTable) {
    $(dataTable).dataTable().fnClearTable();
    if(dataTable=="#table-player-info"){
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
	        },
	        "columnDefs": [ {
	           "targets": 2,
	           "render": function ( data, type, full, meta ) {
	            return '<input class="form-control" readonly="readonly" value='+data+' title='+data+'>';
	           }
	         },
	         {
	           "targets": 3,
	           "render": function ( data, type, full, meta ) {
	            return '<input class="form-control" readonly="readonly" value='+data+' title='+data+'>';
	           }
	         }],
	        scrollX:true
	    });
    }else{
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
	        },
	        scrollX:true
	    });
    }
}

//帐号-角色名-uid-openudid-等级-最后一次登录时间-最后一次充值时间-金币-钻石-仓库-PVP-历史排名
function dealJsonDataToTable(data){
	var tableData = [];
	var row = [];
	row.push(data.account);
	row.push(data.role_name);
	row.push(data.udid==undefined?"-":data.udid);
	row.push(data.openudid==undefined?"-":data.openudid);
	row.push(data.level);
	row.push(data.last_login=="null"?"-":data.last_login);
	row.push(data.last_paid=="null"?"-":data.last_paid);
	row.push(data.gold);
	row.push(data.rmb);
	row.push("积分:"+data.arena.score+" 排名:"+data.arena.rank);
	row.push("积分:"+data.ladder.score+" 排名:"+data.ladder.rank);
	row.push("积分:"+data.world_rank.score+" 排名:"+data.world_rank.rank+" 称号:"+(data.world_rank.title==undefined?"-":data.world_rank.title));
	row.push("约战胜场:"+data.invite.win);
	row.push("关注:"+data.friend.follow+" 粉丝:"+data.friend.fans);
	tableData.push(row);
	return tableData;
}

//"hero_list":[{"skill_level":4,"level":6,"feature_data":[{"feature_type":1,"level":7},{"feature_type":2,"level":3}],"name":"guanyu"}]
//name-level-skillLevel-feature
function getHeroInfo(heroList){
	var heroesArray = [];
	for(var i in heroList){
		var name = heroList[i].name;
		var level = heroList[i].level;
		var heroname = contants["hero"][name];
		var skillLevel = heroList[i].skill_level;
		skillLevel = (skillLevel==undefined)?"-":skillLevel;
		var feature = heroList[i].feature_data;
		var featureStr = "";
		for(var j in feature){
			var featureType = contants["heroFeatureType"][feature[j].feature_type];
			featureStr += featureType+"*"+feature[j].level+"级 ";
		}

		var heroArray = [];
		heroArray.push(heroname==undefined?name:heroname);
		heroArray.push(level);
		heroArray.push(skillLevel);
		heroArray.push(featureStr==""?"-":featureStr);
		heroesArray.push(heroArray);
	}
	return heroesArray;
}
//"knapsack":[{"obj_id":"obj_1","num":2},{"obj_id":"obj_2","num":3}]
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