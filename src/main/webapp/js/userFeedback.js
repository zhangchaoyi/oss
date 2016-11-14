$(function(){
    loadData();
})

function loadData() {
    loadFeedbackData();
}

function loadFeedbackData(){
    $.post("/oss/api/operation/feedback/list", {
    },
    function(data, status) {
        configTable(data);
    });
} 

function configTable(data) {
    $('#data-table-feedback').dataTable().fnClearTable();
    var _table = $('#data-table-feedback').dataTable({
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
            return '<a user-info='+ full[0] +' server-info='+ full[1] +'>查看用户</a>';
           }
         },
     ],
    });
}

function configDetailTable(data) {
    $('#table-feedback-detail').dataTable().fnClearTable();
    var _table = $('#table-feedback-detail').dataTable({
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
            return '<button type="button" class="btn btn-danger" account-info='+ full[0] +'>回复</button>';
           }
         },
     ],
    });
}

//点击某个用户的修改权限按钮 进入个人角色管理页
$(document).on("click","#data-table-feedback tbody tr td a",function() {
    var account = $(this).attr("user-info");
    var server = $(this).attr("server-info");

    $.post("/oss/api/operation/feedback/user", {
        account:account,
        server:server
    },
    function(data, status) {
        configDetailTable(data);
    });

    $("#data-first").slideToggle();
    $("#data-second").slideToggle();
});

//返回用户列表
$("#whaleDetailback").click(function(){
    $("#data-first").slideToggle();
    $("#data-second").slideToggle();
});


//点击回复反馈按钮
$(document).on("click","#data-table-feedback tbody tr td a",function() {
    var account = $(this).attr("account-info");
}
