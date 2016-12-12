$(function(){
	loadData();
})

function loadData(){
	var account = $("#input-account-name").val();
	if(account==""){
		configTable(null);
		return;
	}
	loadSingleObject(account);
}

function loadSingleObject(account){
	$.post("/oss/api/operation/object/player", {
        startDate:$("input#startDate").attr("value"),
        endDate:$("input#endDate").attr("value"),
        account:account
    },
    function(data, status) {
        configTable(data);
    });
}

function configTable(data) {
    $("#table-object").dataTable().fnClearTable();  
    $("#table-object").dataTable({
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

$("#btn-single-object").click(function(){
	var account = $("#input-account-name").val();
	if(account==""){
		return;
	}
	loadSingleObject(account);
});


//锁死图标选择下拉菜单 清除按钮
$("button.btn.btn-default.btn-circle").attr('disabled',"true");
$("ul.dropdown-menu.iconBar > li").addClass("disabled");
$("li.btn-icons").unbind("click");
$("li.disabled > button.btn.btn-primary").attr('disabled',"true");
$("#btn-dropdownIcon").one("click", function(){
    $('.btn-icons > a > div').iCheck('disable');
});