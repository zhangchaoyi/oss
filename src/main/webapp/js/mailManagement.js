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
	// text.push($("input#startDate").attr("value"));
	// text.push($("input#endDate").attr("value"));
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
        		console.log(data.ret_data);
        		// dealMailList(data.ret_data);
        	}else{
        		alert("帐号不存在");
				configTable(null, mailManagementTable);
        	}
        },
    });


}

//["{["state"]=1,["extracted"]=true,["obj_list"]={[1]={["param_list"]={[2]=3,[1]="liguang", },["obj_id"]="obj_11", ["num"]=1,},[2]={["param_list"]={[2]=4,[1]="mozi", },["obj_id"]="obj_11", ["num"]=1,},[3]={["num"]=1,["obj_id"]="obj_3001", },},["mail_title"]="个人邮件测试", ["mail_content"]="个人邮件测试", ["mail_date"]=1481701820,["account_id"]="系统", }"]
//account-name-title-content-attachment-time
function dealMailList(mailList){
	var tableData = [];
	for(var i in mailList){
		var mailArray = [];
		console.log("----"+mailList[i]);
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