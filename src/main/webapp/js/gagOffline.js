$('#datetimepicker').datetimepicker({lang:'ch'});
$(function(){
	loadData();
	withoutIcon();
});

function loadData(){
	loadGagList();
};

function loadGagList(){
	var payloadData = {
	"cmd":"list_gag",
	"parms":[],
    "account":"admin",
    "password":"af03f87cca0a5e8838c3c8454f58605de41f77f5"
 	};
    sendGmReq(payloadData, "gagList");
}
//请求游戏服务器
function sendGmReq(payloadData, type){
	$.ajax({
        type: "POST",
        url: getAddressFromIcon(getCookie("server")),
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify(payloadData),
        crossDomain: true,
        dataType: "json",
        success: function (data) {
        	switch(type){
        		case "gagList":
        		if(data.result=='1'){
	        		var tableData = dealGagList(data.ret_data);
	        		configTable(tableData);
	        	}else{
	        		configTable(null);
	        	}
        		break;
        		case "removeGag":
        		if(data.result=='1'){
	        		alert("解除成功");
	        		loadGagList();
	        	}else{
	        		alert("解除失败");
	        	}
        		break;
        		case "gagPlayer":
        		if(data.result=='1'){
	        		alert("禁言成功");
	        		loadGagList();
	        	}else{
	        		alert("禁言失败");
	        	}
        		break;
        		case "kickPlayer":
        		if(data.result=='1'){
	        		alert("下线成功");
	        	}else{
	        		alert("下线失败");
	        	}
        		break;
        	}
        	
        }
    });
}
//处理禁言列表数据
function dealGagList(retData){
	var tableData = [];
	for(var i in retData){
		var perData = [];
		perData.push(retData[i].account);
		perData.push(retData[i].team_name);
		perData.push(getDateFromTM(retData[i].gag_time));
		perData.push(retData[i].account);
		tableData.push(perData);
	}
	return tableData;
}
//解除禁言
function removeGag(account){
	var param = [];
	param.push(account);
	var payloadData = {
	"cmd":"remove_gag",
	"parms":param,
    "account":"admin",
    "password":"af03f87cca0a5e8838c3c8454f58605de41f77f5"
 	};
	sendGmReq(payloadData, "removeGag");
}
//禁言
function gagPlayer(account, time){
	var param = [];
	param.push(account);
	param.push(time);
	var payloadData = {
	"cmd":"gag_player",
	"parms":param,
    "account":"admin",
    "password":"af03f87cca0a5e8838c3c8454f58605de41f77f5"
 	};
 	sendGmReq(payloadData, "gagPlayer");
}
//强制下线
function kickPlayer(account){
	var param = [];
	param.push(account);
	var payloadData = {
	"cmd":"kick_player",
	"parms":param,
    "account":"admin",
    "password":"af03f87cca0a5e8838c3c8454f58605de41f77f5"
 	};
 	sendGmReq(payloadData, "kickPlayer");
}
function configTable(data){
	$("#table-gag").dataTable().fnClearTable();  
    $("#table-gag").dataTable({
        "destroy": true,
        // retrive:true,
        "data": data,
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
           "targets": -1,
           "orderable":false,
           "render": function ( data, type, full, meta ) {
            return '<button type="button" class="btn btn-warning" account-info='+ data +'>解除</button>';
           }
         }]
    });
}

$(document).on("click","#table-gag tbody tr td button.btn.btn-warning",function() {
	var account = $(this).attr("account-info");
	removeGag(account);
});

$("#btn-account-execute").click(function(){
	var type = $("#input-account").attr("data-info");
	var account = $("#input-account").val();
	if(account==null||account==""){
		alert("帐号格式错误");
		return;
	}
	switch(type){
		case "gag":
		var time = $("#datetimepicker").val();
		if(time==null||time==""){
			alert("时间格式错误");
			return;
		}		
		var inputTimestamp = Date.parse(new Date(time))/1000;
		gagPlayer(account, inputTimestamp);
		break;
		case "offline":
		kickPlayer(account);
		break;
	}
	$("#input-account").val("");
	$("#datetimepicker").val("");
})

$("#menu-account > li").click(function(){
	var txt = $(this).children("a").text();
	$("#btn-account").html(txt+"<span class='caret'><span>");
	var dataInfo = $(this).attr("data-info");
	switch(dataInfo){
		case "gag":
		$(".gag-time").show();
		break;
		case "offline":
		$(".gag-time").hide();
		break;
	}
	$("#input-account").attr("data-info", dataInfo);

});

function getDateFromTM(tm){
    var d = new Date(tm*1000);
    var Y = d.getFullYear();
    var M = d.getMonth()+1;
    if (M >= 1 && M <= 9) {
        M = "0" + M;
    }
    var D = d.getDate() < 10?"0" + d.getDate():d.getDate();
    var h = d.getHours() < 10?"0" + d.getHours():d.getHours();
    var m = d.getMinutes() < 10?"0" + d.getMinutes():d.getMinutes();
    var s = d.getSeconds() < 10?"0" + d.getSeconds():d.getSeconds();

    return Y+"-"+M+"-"+D+" "+h+":"+m+":"+s;
}