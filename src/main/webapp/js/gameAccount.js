$('#datetimepicker').datetimepicker({lang:'ch'});
$(function(){
	loadData();
	withoutIcon();
});

function loadData(){
	loadLockAccountList();
}
//查询锁定帐号列表
function loadLockAccountList(){
	var payloadData = {
	"cmd":"list_forbid",
	"parms":[],
    "account":"admin",
    "password":"af03f87cca0a5e8838c3c8454f58605de41f77f5"
 	};
 	sendGmReq(payloadData, "lockList");
}
//锁定帐号
function lockPlayer(account, time, reason){
	var param = [];
	param.push(account);
	param.push(time);
	param.push(reason);
	var payloadData = {
	"cmd":"forbid_player",
	"parms":param,
    "account":"admin",
    "password":"af03f87cca0a5e8838c3c8454f58605de41f77f5"
 	};
 	sendGmReq(payloadData, "lockPlayer");
}
//解除锁定
function removeLock(account){
	var param = [];
	param.push(account);
	var payloadData = {
	"cmd":"unforbid_player",
	"parms":param,
    "account":"admin",
    "password":"af03f87cca0a5e8838c3c8454f58605de41f77f5"
 	};
 	sendGmReq(payloadData, "removeLock");
}
//解析锁定列表---帐号-角色名-时间-原因-解除
function dealLockList(retData){
	var tableData = [];
	for(var i in retData){
		var perData = [];
		perData.push(retData[i].account);
		perData.push(retData[i].team_name);
		perData.push(getDateFromTM(retData[i].forbid_time));
		perData.push(retData[i].reason);
		perData.push(retData[i].account);
		tableData.push(perData);
	}
	return tableData;
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
        		case "lockList":
        		if(data.result=='1'){
        			var tableData = dealLockList(data.ret_data);
        			configTable(tableData);
	        	}else{
	        		configTable(null);
	        	}
        		break;
        		case "removeLock":
        		if(data.result=='1'){
        			alert("解除锁定成功");
        			loadLockAccountList();
	        	}else{
	        		alert("解除锁定失败,原因:"+data.ret_data);
	        	}
        		break;
        		case "lockPlayer":
        		if(data.result=='1'){
        			alert("锁定帐号成功");
        			loadLockAccountList();
	        	}else{
	        		alert("锁定帐号失败,原因:"+data.ret_data);
	        	}
        		break;
        	}
        	
        }
    });
}

//时间戳
$("#btn-account-lock").click(function(){
	var account = $("#input-lock-account").val();
	var time = $("#datetimepicker").val();
	var reason = $("#lock-reason").val();
	if(account==null||account==""){
		alert("帐号格式错误");
		return;
	}
	if(time==null||time==""){
		alert("时间格式错误");
		return;
	}
	var inputTimestamp = Date.parse(new Date(time))/1000;
	if(reason==null||reason==""){
		alert("原因不能为空");
		return;
	}
	lockPlayer(account, inputTimestamp, reason);
	$("#input-lock-account").val("");
	$("#datetimepicker").val("");
	$("#lock-reason").val("");
});

function configTable(data){
	$("#table-lock-account-management").dataTable().fnClearTable();  
    $("#table-lock-account-management").dataTable({
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

$(document).on("click","#table-lock-account-management tbody tr td button.btn.btn-warning",function() {
	var account = $(this).attr("account-info");
	removeLock(account);
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

//锁死图标选择下拉菜单 清除按钮
$("button.btn.btn-default.btn-circle").attr('disabled',"true");
$("ul.dropdown-menu.iconBar > li").addClass("disabled");
$("li.btn-icons").unbind("click");
$("li.disabled > button.btn.btn-primary").attr('disabled',"true");
$("#btn-dropdownIcon").one("click", function(){
    $('.btn-icons > a > div').iCheck('disable');
});