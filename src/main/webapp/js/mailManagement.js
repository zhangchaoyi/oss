var mailManagementTable = "#table-mail-management";
$(function(){
	withoutIcon();
	loadData();
})

function loadData(){
	loadMailInfo();	
}

function loadMailInfo(){
	// var accountName = $("#input-account-name").val();
	// if(accountName==""||accountName==null){
	// 	return;
	// }
	// $.post("", {
			// startDate:$("input#startDate").attr("value"),
   //      	endDate:$("input#endDate").attr("value"),
 //        account:accountName
 //    },
 //    function(data, status) {
 //        configTable(data, dataTable);
 //    });
 		var test = [
			{"account":"20090101","roleName":"jack","time":"2016-12-12 20:02:00","from":"系统","title":"标题1","content":"正文1","attachment":[{"obj_id":"obj_38","num":"1"}]},
			{"account":"20090101","roleName":"jack","time":"2016-12-15 21:12:00","from":"系统","title":"标题2","content":"正文2","attachment":[{"obj_id":"obj_1","num":"2"}]},
			{"account":"20090101","roleName":"jack","time":"2016-12-10 00:02:00","from":"系统","title":"标题3","content":"正文3","attachment":[{"obj_id":"obj_11","num":"1","param_list":["guanyu",5]}]},
			{"account":"20090101","roleName":"jack","time":"2016-12-02 22:02:00","from":"系统","title":"标题4","content":"正文4"}
		];
		var tableData = dealMailList(test);
		configTable(tableData,mailManagementTable);
}

function dealMailList(mailList){
	var tableData = [];
	for(var i in mailList){
		var mailArray = [];
		mailArray.push(mailList[i].account);
		mailArray.push(mailList[i].roleName);
		mailArray.push(mailList[i].title);
		mailArray.push(mailList[i].content);
		if(mailList[i].hasOwnProperty("attachment")){
			mailArray.push(dealAttachment(mailList[i].attachment));
		}else{
			mailArray.push("-");
		}
		mailArray.push(mailList[i].time);
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

$("#btn-account-name").click(function(){
	loadMailInfo();
});