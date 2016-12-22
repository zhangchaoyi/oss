$(function(){
	loadData();
	withoutIcon();
})

function loadData(){
	loadAnnouncementList($("ul.nav.nav-tabs.announcement > li.active > a").attr("data-info"));
}
//查询公告和跑马灯列表
function loadAnnouncementList(tagInfo){
 	var payloadData = {
    "parms":[],
    "account":"admin",
    "password":"af03f87cca0a5e8838c3c8454f58605de41f77f5"
 	};
 	var header = "";
 	switch(tagInfo){
 		case "announcement":
 		payloadData["cmd"] = "list_announcement";
 		header += "<th><span>标题</span></th><th><span>内容</span></th><th><span>创建时间</span></th><th><span><a class='btn btn-warning' data-toggle='modal' data-target='#delete-confirm'>删除全部</a></span></th>";
 		break;
 		case "rolling-content":
 		payloadData["cmd"] = "list_broadcast";
 		header += "<th><span>间隔</span></th><th><span>剩余次数</span></th><th><span>内容</span></th><th><span><a class='btn btn-warning' data-toggle='modal' data-target='#delete-confirm'>删除全部</a></span></th>";
 		break;
 	} 

 	//请求游戏服务器
    $.ajax({
        type: "POST",
        url: "http://192.168.0.213:8002/gm",
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify(payloadData),
        crossDomain: true,
        dataType: "json",
        success: function (data) {
        	if(data.result=='1'){
 				configTable(data, header, tagInfo);
        	}else{
 				alert("查询公告和跑马灯列表信息失败");
        	}
        },
    });
}

//公告和跑马灯切换 修改样式
$("ul.nav.nav-tabs.announcement > li").click(function(){
	var info = $(this).children("a").attr("data-info");
	switch(info){
		case "rolling-content":
		initRollingContentHtml();
		$("#running-announcement-header").text("正在显示的跑马灯");
		break;
		case "announcement":
		$(".form-group.announcement-tab").html("<label>标题</label><input class='form-control' type='text' id='input-title'>");
		$("#running-announcement-header").text("正在显示的公告");
		break;
	}
	loadAnnouncementList(info);
});

//根据tagInfo 进行处理数据
function dealTableData(retData, tagInfo){
	var tableData = [];
	switch(tagInfo){
		case "announcement":
		for(var i in retData){
			var perData = [];
			var announce = retData[i].announce;
			perData.push(announce.title);
			perData.push(announce.content);
			perData.push(getDateFromTM(announce.timestamp));
			perData.push(retData[i].id);
			tableData.push(perData);
		}
		break;
		case "rolling-content":
		for(var i in retData){
			var perData = [];
			perData.push(retData[i].interval);
			perData.push(retData[i].max_count==undefined?"不限":retData[i].max_count);
			perData.push(retData[i].msg);
			perData.push(retData[i].id);
			tableData.push(perData);
		}
		break;
	}
	return tableData;
}

function configTable(data, header, tagInfo){
	appendHeader(header);
	var tableData = dealTableData(data.ret_data, tagInfo);
	$("#table-announcement-list").dataTable().fnClearTable();  
    $("#table-announcement-list").dataTable({
        "destroy": true,
        // retrive:true,
        "data": tableData,
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
            return '<button type="button" class="btn btn-warning" id-info='+ data +'>删除</button>';
           }
         }]
    });
}

function appendHeader(headerHtml) {
    if ($("#table-announcement-list > thead").length != 0) {
        $("#table-announcement-list > thead").empty();
        $("#table-announcement-list").prepend("<thead><tr>" + headerHtml + "</tr></thead>");
        return;
    }
    $("#table-announcement-list").append("<thead><tr>" + headerHtml + "</tr></thead>");
}

//跑马灯样式
function initRollingContentHtml(){
	var htmlStr = "<div class='input-group'><div class='input-group-btn'><button type='button' class='btn btn-info dropdown-toggle' data-toggle='dropdown'>次数<span class='caret'></span></button><ul class='dropdown-menu' role='menu' id='times-menu'><li><a data-info='limit'>次数</a></li><li><a data-info='unlimit'>不限</a></li></ul></div><input type='text' class='form-control' style='width:10%;margin-right:20px' id='input-times' data-info='limit'> <input type='text' class='form-control' placeholder='请输入间隔' data-info='none' style='width:10%' id='input-period'><div class='input-group-btn' style='float:left'><button type='button' class='btn btn-info dropdown-toggle' data-toggle='dropdown'>间隔<span class='caret'></span></button><ul class='dropdown-menu dropdown-menu' role='menu' id='period-menu'><li><a data-info='hour'>时</a></li><li><a data-info='minute'>分</a></li><li><a data-info='second'>秒</a></li></ul></div></div>";
	$(".form-group.announcement-tab").html(htmlStr);
	$("#period-menu > li").click(function(){
		var period = $(this).children("a").attr("data-info");
		var txt = $(this).text();
		$("#input-period").attr("data-info",period);
		$("#period-menu").siblings("button").html(txt + "<span class='caret'></span>");
	});
	$("#times-menu > li").click(function(){
		var timeType = $(this).children("a").attr("data-info");
		var txt = $(this).text();
		$("#input-times").attr("data-info",timeType);
		if(timeType=="limit"){
			$("#input-times").val("");
			$("#input-times").removeAttr("readonly");
		}else{
			$("#input-times").val(txt);
			$("#input-times").attr("readonly","readonly");
		}
		$("#times-menu").siblings("button").html(txt + "<span class='caret'></span>");

	});
}
//发送按钮
$("#btn-send-announcement").click(function(){
	var info = $("ul.nav.nav-tabs.announcement > li.active > a").attr("data-info");
	var content = $("#input-content").val();
	switch(info){
		case "announcement":
			var title = $("#input-title").val();
			if(title==""||content==""){
				alert("标题或内容不能为空");
				return;
			}
			sendAnnouncement(title, content);
			break;
		case "rolling-content":
			var times = $("#input-times").val();
			var period = $("#input-period").val();
			var measure = $("#input-period").attr("data-info");
			if(times==""){
				alert("请输入次数");
				return;
			}
			if(times!="不限"&&!checkNum(times)){
				alert("次数需要为正整数");
				return;
			}
			if(period==""){
				alert("请输入间隔");
				return;
			}
			if(!checkNum(period)){
				alert("间隔需要为正整数");
				return;
			}
			switch(measure){
				case "hour":
					period = 3600*period;
					break;
				case "minute":
					period = 60*period
					break;
				case "none":
					alert("请选择间隔");
					return;
					break;
			}
			sendRollingContent(parseInt(times), parseInt(period), content);
			break;
	}
});
//发送公告
function sendAnnouncement(title, content){
	var params = [];
	params.push(title);
	params.push(content);
	var payloadData = {
	"cmd":"add_custom_announcement",
	"parms":params,
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
 				alert("发送成功");
 				clearAnnouncementInput();
 				loadAnnouncementList($("ul.nav.nav-tabs.announcement > li.active > a").attr("data-info"));
 				//record operation
 				$.post("/oss/api/operation/record", {
                    account:$("#userAccount").text(),
                    operation:JSON.stringify(payloadData),
                    emailAddress:getAddressFromIcon($("#btn-db").attr("data-info")),
                    type:"announcement"
                },
                function(data, status) {
                });
        	}else{
 				alert("发送失败");
        	}
        },
    });
}
//发送跑马灯
function sendRollingContent(times, period, content){
	var params = [];
	params.push(content);
	params.push(period);
	if(times!='不限'){
		params.push(times);
	}
	
	var payloadData = {
	"cmd":"custom_broadcast",
	"parms":params,
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
 				alert("发送成功");
 				clearAnnouncementInput();
 				loadAnnouncementList($("ul.nav.nav-tabs.announcement > li.active > a").attr("data-info"));
 				//record operation
 				$.post("/oss/api/operation/record", {
                    account:$("#userAccount").text(),
                    operation:JSON.stringify(payloadData),
                    emailAddress:getAddressFromIcon($("#btn-db").attr("data-info")),
                    type:"announcement"
                },
                function(data, status) {
                });
        	}else{
 				alert("发送失败");
        	}
        },
    });
}

//点击各项删除按钮
$(document).on("click","#table-announcement-list tbody tr td button.btn.btn-warning",function() {
	deleteAnnouncement($("ul.nav.nav-tabs.announcement > li.active > a").attr("data-info"), $(this).attr("id-info"));
});
//遮罩确定删除按钮
$("#delete-feedback").click(function(){
	deleteAllAnnouncement($("ul.nav.nav-tabs.announcement > li.active > a").attr("data-info"));
});

function deleteAllAnnouncement(tagInfo){
	var payloadData = {
	"parms":[],
    "account":"admin",
    "password":"af03f87cca0a5e8838c3c8454f58605de41f77f5"
 	};
 	switch(tagInfo){
 		case "announcement":
 		payloadData["cmd"] = "remove_all_announcement";
 		break;
 		case "rolling-content":
 		payloadData["cmd"] = "remove_all_broadcast";
 		break;
 	}
 	$.ajax({
        type: "POST",
        url: getAddressFromIcon($("#btn-db").attr("data-info")),
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify(payloadData),
        crossDomain: true,
        dataType: "json",
        success: function (data) {
        	if(data.result=='1'){
 				alert("删除成功");
 				loadAnnouncementList($("ul.nav.nav-tabs.announcement > li.active > a").attr("data-info"));
        	}else{
 				alert("删除失败");
        	}
        },
    });
}

//发送删除公告和跑马灯请求
function deleteAnnouncement(tagInfo, id){
	var params = [];
	params.push(id);
	var payloadData = {
	"parms":params,
    "account":"admin",
    "password":"af03f87cca0a5e8838c3c8454f58605de41f77f5"
 	};
 	switch(tagInfo){
 		case "announcement":
 		payloadData["cmd"] = "remove_announcement";
 		break;
 		case "rolling-content":
 		payloadData["cmd"] = "remove_broadcast";
 		break;
 	} 

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
 				alert("删除成功");
 				loadAnnouncementList($("ul.nav.nav-tabs.announcement > li.active > a").attr("data-info"));
        	}else{
 				alert("删除失败");
        	}
        },
    });
}

function clearAnnouncementInput(){
	$("#input-title").val("");
	$("#input-content").val("");
	if($("#input-times").val()!="不限"){
		$("#input-times").val("");	
	}
	$("#input-period").val("");
}

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

//验证正整数
function checkNum(num){
    var re = /^[1-9]+[0-9]*]*$/;
    return re.test(num);
}