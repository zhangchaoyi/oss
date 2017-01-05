$(function(){
	loadData();
	withoutIcon();
});

function loadData(){
	var account = $("#input-query-account").val();
	if(account==null||account==""){
		configTable(null);
		return;
	}
	queryOrderInfo(account);
}

$("#btn-execute").click(function(){
	var account = $("#input-recover-account").val();
	var serialNumber = $("#serial-number").val().trim();
	var isPro = $("#is-pro").val();
	if(account==null||account==""){
		alert("帐号不能为空");
		return;
	}
	if(serialNumber==null||serialNumber==""){
		alert("订单号不能为空");
		return;
	}
	if(isPro==null||isPro==""){
		alert("是否正式订单不能为空");
		return;
	}
	var param = [];
	param.push(account);
	param.push(serialNumber);
	param.push(isPro);
	var payloadData = {
	"cmd":"recover_charge",
	"parms":param,
    "account":"admin",
    "password":"af03f87cca0a5e8838c3c8454f58605de41f77f5"
 	};
 	$.ajax({
        type: "POST",
        url: getAddressFromIcon(getCookie("server")),
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify(payloadData),
        crossDomain: true,
        dataType: "json",
        success: function (data) {
        	if(data.result=='1'){
        		alert("处理成功");
        		loadData();
        	}else{
        		alert("处理失败,原因:"+data.ret_data);
        	}
        }
    });
});

$("#btn-query").click(function(){
	var account = $("#input-query-account").val().trim();
	if(account==null||account==""){
		alert("帐号不能为空");
		return;
	}
	queryOrderInfo(account);
});

function queryOrderInfo(account){
	$.post("/oss/api/operation/order", {
		account:account
    },
    function(data, status) {
    	if(data.result=='0'){
    		alert("记录不存在");
    		configTable(null);
    		return;
    	}
    	configTable(data.tableData);
    });
}

function configTable(tableData){
    $('#table-order-query').dataTable().fnClearTable();  
    $('#table-order-query').dataTable({
        "destroy": true,
        // retrive:true,
        "data": tableData,
        "dom": '<"top"f>rt<"left"lip>',
        "order": [[ 5, 'desc' ]],
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

//锁死图标选择下拉菜单 清除按钮
$("button.btn.btn-default.btn-circle").attr('disabled',"true");
$("ul.dropdown-menu.iconBar > li").addClass("disabled");
$("li.btn-icons").unbind("click");
$("li.disabled > button.btn.btn-primary").attr('disabled',"true");
$("#btn-dropdownIcon").one("click", function(){
    $('.btn-icons > a > div').iCheck('disable');
});

