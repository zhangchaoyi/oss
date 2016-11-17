var emailAddress = "http://47.89.47.176:8002/gm";

$(function(){
    loadData();
    initSelectAll();
})

function loadData() {
    loadFeedbackData($(".nav-tab.feedback > ul > li.active > a").attr("data-info"));      
}
//访问用户列表接口
function loadFeedbackData(server){
    $.post("/oss/api/operation/feedback/list", {
        startDate:$("input#startDate").attr("value"),
        endDate:$("input#endDate").attr("value"),
        server:server
    },
    function(data, status) {
        configTable(data);
    });
} 



function configTable(data) {
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
            return '<button type="button" class="btn btn-danger" data-toggle="modal" data-target="#feedback" account-info='+ full[1] +' id-info='+ data +'>回复</button>';
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
         },
         {
           "targets": -3,
           "render": function ( data, type, full, meta ) {
            return '<button type="button" class="btn btn-info" data-toggle="modal" data-target="#fb-detail" id-info='+ data +'>详情</button>';
           }
         },
         {
           "targets": 0,
           "render": function ( data, type, full, meta ) {
            return function(){
                return '<input type="checkbox" value='+data+'></input>';
            }()

           }
         },
         {
           "targets": 2,
           "render": function ( data, type, full, meta ) {
                return data.substr(0,15) + '......';
           }
         } 
     ],

    });
}

//选择区服
$(".nav-tab.feedback > ul > li").click(function(){
    $(this).siblings("li.active").toggleClass("active");
    $(this).addClass("active");
    
    var server = $(this).children("a").attr("data-info");
    if(server=="eggactest.koogame.cn"){
        emailAddress = "http://120.25.209.140:8002/gm";    
    }
    if(server=="egghk.koogame.cn"){
        emailAddress = "http://47.89.47.176:8002/gm";    
    }
    if(server=="egguccn2.koogame.cn"){
        emailAddress = "http://118.178.17.105:8002/gm";
    }
    loadFeedbackData(server);
});

//点击回复反馈按钮
$(document).on("click","#table-feedback-detail tbody tr td button.btn.btn-danger",function() {
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
    "account":"admin",
    "password":"af03f87cca0a5e8838c3c8454f58605de41f77f5"
    };

    $.ajax({
        type: "POST",
        url: emailAddress,
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
                    loadFeedbackData($(".nav-tab.feedback > ul > li.active > a").attr("data-info"));
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

//点击详情后的回复按钮
$("#btn-reply").click(function(){
    var account = $("#feedback-account").val();
    var id = $("#btn-reply").attr("id-info");
    $("#reply-account").attr("value", account);
    $("#reply-account").attr("id-info", id);
});

//点击详情按钮
$(document).on("click","#table-feedback-detail tbody tr td button.btn.btn-info",function() {
    var id = $(this).attr("id-info");
    $.post("/oss/api/operation/feedback/user/detail", {
        id:id
    },
    function(data, status) {
        $("#feedback-account").attr("value", data.account);
        $("#feedback-content").val(data.content);
        $("#btn-reply").attr("id-info",id);
    });
});

//删除按钮
$("#delete-feedback").click(function(){
    var checkboxs = $("#table-feedback-detail tbody tr td input");
    var list = [];
    for(var i=0;i<checkboxs.length;i++){
        if($(checkboxs[i]).prop("checked")){
            list.push($(checkboxs[i]).attr("value"));
        }
    }
    if(list.length==0){
        alert("请选择删除用户");
        return;
    }
    $.post("/oss/api/operation/feedback/user/delete", {
        ids:list
    },
    function(data, status) {
        if(data=="0"){
            alert("删除失败");
        }else{
            alert("删除成功");
        }
        loadFeedbackData($(".nav-tab.feedback > ul > li.active > a").attr("data-info"));
    });
});

//初始化 全选 点击事件
function initSelectAll(){
    $("#table-feedback-detail thead tr th div ins").click(function(){
        var checked = $(this).parent().hasClass("checked");
        if(checked==true) {
            $("#table-feedback-detail tbody tr td input").prop("checked","checked");
        }else{
            $("#table-feedback-detail tbody tr td input").prop("checked","");
        }
    });
}


//锁死图标选择下拉菜单 清除按钮
$("button.btn.btn-default.btn-circle").attr('disabled',"true");
$("ul.dropdown-menu.iconBar > li").addClass("disabled");
$("li.btn-icons").unbind("click");
$('.btn-icons > a > div').iCheck('disable');
$("li.disabled > button.btn.btn-primary").attr('disabled',"true");
$("#db-menu > li").addClass("disabled");
$("#db-menu > li").unbind("click");