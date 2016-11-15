var currentAccount = "";
var currentServer = "";
$(function(){
    loadData();
})

function loadData() {
    var first = $("#data-first").css("display");
    if(first=="block"){
        loadFeedbackData();    
    }else{
        loadUserData(currentAccount, currentServer);
    }
    
}
//访问用户列表接口
function loadFeedbackData(){
    $.post("/oss/api/operation/feedback/list", {
        startDate:$("input#startDate").attr("value"),
        endDate:$("input#endDate").attr("value")
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
        "dom": '<"top"f>rt<"left"lip>',
        "lengthMenu": [[7,15,30,-1 ],[7,15,30,'全部']],
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
            return '<button type="button" class="btn btn-danger" data-toggle="modal" data-target="#feedback" account-info='+ full[0] +' id-info='+ data +'>回复</button>';
           }
         },
         {
           "targets": -2,
           "render": function ( data, type, full, meta ) {
            if(data=='0'){
                return '<span class="label label-success">未回复</span>';
            }else{
                return '<span class="label label-default">已回复</span>';
            }
           }
         }
     ],

    });
}

//点击某个用户的修改权限按钮 进入个人角色管理页
$(document).on("click","#data-table-feedback tbody tr td a",function() {
    currentAccount = $(this).attr("user-info");
    currentServer = $(this).attr("server-info");
    loadUserData(currentAccount, currentServer);
    $("#data-first").slideToggle();
    $("#data-second").slideToggle();
});
//访问用户信息接口
function loadUserData(account, server) {
    $.post("/oss/api/operation/feedback/user", {
        account:account,
        server:server,
        startDate:$("input#startDate").attr("value"),
        endDate:$("input#endDate").attr("value")
    },
    function(data, status) {
        configDetailTable(data);
    });
}

//返回用户列表
$("#whaleDetailback").click(function(){
    loadFeedbackData();
    $("#data-first").slideToggle();
    $("#data-second").slideToggle();
});


//点击回复反馈按钮
$(document).on("click","#table-feedback-detail tbody tr td button",function() {
    var account = $(this).attr("account-info");
    $("#reply-account").attr("value", account);
    var id = $(this).attr("id-info");
    $("#reply-account").attr("id-info", id);
});
//点击发送按钮
$("#btn-send").click(function(){
    var account = $("#reply-account").val();
    var id = $("#reply-account").attr("id-info");
    var title = $("#reply-title").val();
    var area = $("#area").val();
    

    var text = [];
    text.push(account);
    text.push(title);
    text.push(area);

    var data = {
    "cmd":"send_custom_mail",
    "parms":text,
    "account":"cheyingda",
    "password":"7c4a8d09ca3762af61e59520943dc26494f8941b"
    };

    $.ajax({
        type: "POST",
        url: "http://120.25.209.140:8002/gm",
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify(data),
        crossDomain: true,
        dataType: "json",
        success: function (data) {
            if (data.result == '1') {
                alert("已发送");
                $.post("/oss/api/operation/feedback/user/reply", {
                    id:id
                },
                function(data, status) {
                    loadUserData(currentAccount, currentServer);
                });
            }
        },
    });

    $("#reply-title").val("");
    $("#area").val("");
});
//点击关闭按钮
$("#btn-reply-close").click(function(){
    $("#reply-title").val("");
    $("#area").val("");
});

//锁死图标选择下拉菜单 清除按钮
$("button.btn.btn-default.btn-circle").attr('disabled',"true");
$("ul.dropdown-menu.iconBar > li").addClass("disabled");
$("li.btn-icons").unbind("click");
$('.btn-icons > a > div > input').iCheck('disable');
$("li.disabled > button.btn.btn-primary").attr('disabled',"true");