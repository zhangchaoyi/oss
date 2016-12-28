$(function(){
	loadData();
    withoutIcon();
});

function loadData(){
	loadAccountInfo();
}

function loadAccountInfo(){
	$.post("/oss/api/admin/account", {
    },
    function(data, status) {
        configTable(data);
    });
}

function configTable(data) {
    $('#data-table-account-permission').dataTable().fnClearTable();
    var _table = $('#data-table-account-permission').dataTable({
        "destroy": true,
        "data": data==null?null:data,
        "dom": '',
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
$("#btn-db").one("click",function(){
    $("#db-menu > li").addClass("disabled");
    $("#db-menu > li").unbind("click");   
});
$("#btn-dropdownIcon").one("click", function(){
    $('.btn-icons > a > div').iCheck('disable');
});