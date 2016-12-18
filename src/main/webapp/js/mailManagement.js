var mailManagementTable = "#table-mail-management";
$(function(){
	withoutIcon();
	loadData();
})

function loadData(){
	loadMailInfo();	
}

function loadMailInfo(){
	var accountName = $("#input-account-name").val();
	if(accountName==""||accountName==null){
		configTable(null, mailManagementTable);
		return;
	}

    var text = [];
	text.push(accountName);
	text.push($("input#startDate").attr("value"));
	text.push($("input#endDate").attr("value"));

	var payloadData = {
    "cmd":"get_player_mail_list",
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
                configTable(dealMailList(data.ret_data), mailManagementTable);
        	}else{
        		alert("帐号不存在");
				configTable(null, mailManagementTable);
        	}
        },
    });
}

//mail_list:[{"state":1,"extracted":true,"mail_content":"个人邮件测试","mail_title":"个人邮件测试","mail_date":1481701820,"account_id":"系统","obj_list":[{"obj_id":"obj_11","num":1,"param_list":["liguang",3]},{"obj_id":"obj_11","num":1,"param_list":["mozi",4]},{"num":1,"obj_id":"obj_3001"}]}]
//account-name-title-content-attachment-time-sender
function dealMailList(retData){
	var tableData = [];
    var account = retData.account;
    var roleName = retData.team_name;
    var mailList = retData.mail_list;
	for(var i in mailList){
		var mailArray = [];
        var title = mailList[i].mail_title;
        var content = mailList[i].mail_content;
        var attachment = dealAttachment(mailList[i].obj_list);
        var time = getDateFromTM(mailList[i].mail_date);
        var sender = mailList[i].account_id;
        mailArray.push(account);
        mailArray.push(roleName);
        mailArray.push(title);
        mailArray.push(content);
        mailArray.push(attachment);
        mailArray.push(time);
        mailArray.push(sender);
		tableData.push(mailArray);
	}
	return tableData;
}

//[{"obj_id":"obj_1","num":"2"},{"obj_id":"obj_11","num":"1","param_list":["guanyu",5]}]
function dealAttachment(attachment){
	var attachmentArray = []
	for(var i in attachment){
		var objId = attachment[i].obj_id;
		var num = attachment[i].num;
		if(objId=="obj_11"&&attachment.hasOwnProperty("param_list")){
			var heroList = attachment[i].param_list;
			var heroName = contants["hero"][heroList[0]];
			var level = heroList[1];
			heroName=(heroName==undefined)?heroList[0]:heroName;
			attachmentArray.push(heroName+"*"+level+"阶");
		}else{
			var objName = contants["properties"][objId];
			attachmentArray.push(objName+"*"+num);
		}
	}
	return attachmentArray;
}

function configTable(data,dataTable) {
    $(dataTable).dataTable().fnClearTable();  
    $(dataTable).dataTable({
        "destroy": true,
        // retrive:true,
        "data": data,
        "order": [[ 5, 'asc' ]],
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


$("#menu-account-name > li").click(function(){
	var txt = $(this).children("a").text();
	$("#btn-account-name").html(txt+"<span class='caret'><span>");
});

$("#btn-account-name-query").click(function(){
	loadMailInfo();
});

function getDateFromTM(tm){
    var d = new Date(tm*1000);
    var Y = d.getFullYear();
    var M = d.getMonth()+1;
    if (M >= 1 && M <= 9) {
        M = "0" + M;
    }
    var D = d.getDate();
    var h = d.getHours();
    var m = d.getMinutes();
    var s = d.getSeconds();

    return Y+"-"+M+"-"+D+" "+h+":"+m+":"+s;
}