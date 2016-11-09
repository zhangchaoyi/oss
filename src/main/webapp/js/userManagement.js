$(function(){
    $("#data-second").hide();
    loadData();
    initSelectAll();
})

function loadData() {
    loadUserRoleData();
}

function loadUserRoleData() {
    $.post("/oss/api/admin/manageUsers", {
    },
    function(data, status) {
        configTable(data);
    });
}

function configTable(data) {
    $('#data-table-user-management').dataTable().fnClearTable();
    var _table = $('#data-table-user-management').dataTable({
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
        },
        "columnDefs": [ {
           "targets": -1,
           "render": function ( data, type, full, meta ) {
            return '<a class="manage-role" data-info='+data+' data-role='+ full[2] +'>修改权限</a>';
           }
         },
     {
           "targets": 0,
           "render": function ( data, type, full, meta ) {
            return '<input type="checkbox" value='+data+'></input>';
           }
         }  
     ],
    });
}

function initSelectAll(){
    $("#data-table-user-management thead tr th div ins").click(function(){
        var checked = $(this).parent().hasClass("checked");
        if(checked==true) {
            $("#data-table-user-management tbody tr td input").prop("checked","checked");
        }else{
            $("#data-table-user-management tbody tr td input").prop("checked","");
        }
    });
}

//选择删除按钮
$("#data-table-user-management thead tr th a").click(function(){
    var checkboxs = $("#data-table-user-management tbody tr td input");
    var list = [];
    for(var i=0;i<checkboxs.length;i++){
        if($(checkboxs[i]).prop("checked")){
            list.push($(checkboxs[i]).attr("value"));
        }
    }
    $.post("/oss/api/admin/deleteUsers", {
        users:list
    },
    function(data, status) {
        if(data.message=="0"){
            alert("删除失败");
        }else{
            alert("删除成功");
        }
        loadData();   
    });
});

$(document).on("click","#data-table-user-management tbody tr td a",function() {
    var accountParam = $(this).attr("data-info");
    var role = $(this).attr("data-role");
    $("#username").text(accountParam);
    $("#role-manage").attr("value",role);
    $("#data-first").slideToggle();
    $("#data-second").slideToggle();
});

$("#whaleDetailback").click(function(){
    $("#data-first").slideToggle();
    $("#data-second").slideToggle();
});




//锁死图标选择下拉菜单 清除按钮
$("button.btn.btn-default.btn-circle").attr('disabled',"true");
$("ul.dropdown-menu.iconBar > li").addClass("disabled");
$("li.btn-icons").unbind("click");
$('.btn-icons > a > div > input').iCheck('disable');
$("li.disabled > button.btn.btn-primary").attr('disabled',"true");