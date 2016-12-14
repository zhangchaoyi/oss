var malaiAddress = "http://47.89.47.176:8002/gm";
var ucAddress = "http://118.178.17.105:8002/gm";
var iosAddress = "http://118.178.19.95:8002/gm";
var testAddress = "http://120.25.209.140:8002/gm";
var malaiServer = "egghk.koogame.cn";
var ucServer = "egguccn2.koogame.cn";
var iosServer = "egguccn.koogame.cn";
var testServer = "eggactest.koogame.cn";

$(function(){
    loadData();
    initSelectAll();
})

function loadData() {
    loadFeedbackData(getServerFromIcon($("#btn-db").attr("data-info")));
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

//点击任意回复反馈按钮  允许选择全服邮件
$("#btn-atwill-reply").click(function(){
    $("#account-row").children("span").attr("data-toggle","dropdown");
    var info = $("#reply-account").attr("data-info");
    if(info!="mail-server"){
        $("#reply-account").removeAttr("readonly");
    }
});

//点击个人回复反馈按钮
$(document).on("click","#table-feedback-detail tbody tr td button.btn.btn-danger",function() {
    $("#account-row").children("span").removeAttr("data-toggle");
    $("#reply-account").attr("data-info","mail-account");
    $("#account-row").children("span").text("帐号");
    var account = $(this).attr("account-info");
    $("#reply-account").val(account);
    var id = $(this).attr("id-info");
    $("#reply-account").attr("id-info", id);
    $("#reply-account").attr("readonly","readonly");
});
//点击发送按钮 需要获取回复反馈的所有内容,包括附件信息,需要验证是否为空或者参数不符合要求
$("#btn-send").click(function(){
    var account = $("#reply-account").val();
    var accountInfo = $("#reply-account").attr("data-info");
    var id = $("#reply-account").attr("id-info");
    var title = $("#reply-title").val();
    var area = $("#area").val();

    if(accountInfo=="mail-account"&&(account==null||account==""||account.length!=8)){
        alert("账户名不合法");
        return;
    }
    if(title==null||title==""){
        alert("标题不能为空");
        return;
    }
    if(area==null||area==""){
        alert("正文内容不能为空");
        return;
    }
    if(account=="全服邮件"&&accountInfo=="mail-server"){
        account = '*';
    }

    //[account,title,content,[{"obj_id":"obj_1","num":1024},{"obj_id":"obj_2","num":21},{"obj_id":"obj_11","num":1,"param_list":{"guanyu",3}}]]
    var text = [];
    text.push(account);
    text.push(title);
    text.push(area);

    var objList = [];
    //获取所勾选的物品列表
    var objs = $("#attach-result > span > input");
    for(var i=0;i<objs.length;i++){
        var objId = $(objs[i]).attr("obj-id");
        var heroId = $(objs[i]).attr("hero-info");
        //非英雄
        if(heroId==undefined){
            var num = $(objs[i]).val();
            if(!checkNum(num)){
                alert("附件物品输入框内需要是正整数");
                return;
            }
            var item = "";
            //宝箱类型 num是类型编号(1~100) 数量恒定为1个
            if(objId=='obj_19'||objId=='obj_21'||objId=='obj_22'||objId=='obj_23'||objId=='obj_31'||objId=='obj_37'){
                item = {"obj_id":objId,"num":1,"param_list":[parseInt(num),1]};
            }else{
                item = {"obj_id":objId,"num":parseInt(num)};
            }
            objList.push(item);
        }else{
            //英雄 数量恒定为一个 输入框为阶数
            var level = $(objs[i]).val();
            if(!checkNum(level)){
                alert("附件英雄阶数需要是正整数");
                return;
            }
            if(parseInt(level)>10){
                alert("英雄阶数最大是10阶");
                return;
            }
            var item = {"obj_id":"obj_11","num":1,"param_list":[heroId,parseInt(level)]};
            objList.push(item);
        }
    }
    text.push(objList);
    var payloadData = {
    "cmd":"send_custom_mail",
    "parms":text,
    "account":"admin",
    "password":"af03f87cca0a5e8838c3c8454f58605de41f77f5"
    };

    $.ajax({
        type: "POST",
        url: getAddressFromIcon($("#btn-db").attr("data-info")),
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify(payloadData),
        crossDomain: true,
        dataType: "json",
        success: function (data) {
            if (data.result == '1') {
                alert("已发送");
                $.post("/oss/api/operation/record", {
                    account:$("#userAccount").text(),
                    operation:JSON.stringify(payloadData),
                    emailAddress:getAddressFromIcon($("#btn-db").attr("data-info")),
                    type:"mail"
                },
                function(data, status) {
                });

                if(id==undefined){
                    return;
                }

                $.post("/oss/api/operation/feedback/user/reply", {
                    id:id
                },
                function(data, status) {
                    loadFeedbackData(getServerFromIcon($("#btn-db").attr("data-info")));
                });
            }
        },
    });

    //邮件发送完需要清空/还原
    $("#reply-account").val("");
    $("#reply-account").attr("data-info","mail-account");
    $("#account-row").children("span").removeAttr("data-toggle");
    $("#account-row").children("span").text("帐号");
    $("#reply-title").val("");
    $("#area").val("");
    $("#btn-attachment").html("<i class='fa fa-plus nest' aria-hidden='true'></i>附件");
    $("div.attachment").empty();
    $("#attach-result").empty();
});
//点击关闭按钮 还原初始设置
$("#btn-reply-close,#btn-fb-close").click(function(){
    //关闭邮件需要清空/还原
    $("#reply-account").val("");
    $("#reply-account").attr("data-info","mail-account");
    $("#account-row").children("span").removeAttr("data-toggle");
    $("#account-row").children("span").text("帐号");
    $("#reply-title").val("");
    $("#area").val("");
    $("#btn-attachment").html("<i class='fa fa-plus nest' aria-hidden='true'></i>附件");
    $("div.attachment").empty();
    $("#attach-result").empty();
});


//点击详情后的回复按钮
$("#btn-reply").click(function(){
    $("#account-row").children("span").removeAttr("data-toggle");
    $("#reply-account").attr("data-info","mail-account");
    $("#account-row").children("span").text("帐号");
    var account = $("#feedback-account").val();
    var id = $("#btn-reply").attr("id-info");
    $("#reply-account").val(account);
    $("#reply-account").attr("id-info", id);
    $("#reply-account").attr("readonly","readonly");
    $("#reply-title").val("");
    $("#area").val("");
    //调整样式变形
    $("body").css("padding-right","0px");
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
        loadFeedbackData(getServerFromIcon($("#btn-db").attr("data-info")));
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

//附件按钮 切换选项则清除覆盖前面选项
$(".btn-group.btn-attachment > ul > li").click(function(){
    var info = $(this).children("a").attr("data-info");
    var html = $(this).children("a").html();

    var placeHolder = "";
    var attaText = "";
    switch(info){
        case "money":
        placeHolder = "请输入金币数量";
        attaText = "金币数量";
        $(".attachment").html("<div class='input-group'><span class='input-group-addon'><i class='fa fa-plus-circle nest' aria-hidden='true'></i>" + attaText + "</span><input type='text' class='form-control' id='attachment-num' placeholder=" + placeHolder + "></div><button type='button' class='btn btn-danger' id='btn-attach-save'>保存</button>");
        initAttachBtn("金币","obj_1");
        break;

        case "diamond":
        placeHolder = "请输入钻石数量";
        attaText = "钻石数量";
        $(".attachment").html("<div class='input-group'><span class='input-group-addon'><i class='fa fa-plus-circle nest' aria-hidden='true'></i>" + attaText +  "</span><input type='text' class='form-control' id='attachment-num' placeholder=" + placeHolder + "></div><button type='button' class='btn btn-danger' id='btn-attach-save'>保存</button>");
        initAttachBtn("钻石","obj_2");
        break;

        case "exp":
        placeHolder = "请输入经验值";
        attaText = "经验值";
        $(".attachment").html("<div class='input-group'><span class='input-group-addon'><i class='fa fa-plus-circle nest' aria-hidden='true'></i>" + attaText +  "</span><input type='text' class='form-control' id='attachment-num' placeholder=" + placeHolder + "></div><button type='button' class='btn btn-danger' id='btn-attach-save'>保存</button>");
        initAttachBtn("经验","obj_3");
        break;

        case "hero":
        $(".attachment").html("<table id='attach-table'><tbody><tr><td><li id='linchong'>林冲</li></td><td><li id='zhangfei'>张飞</li></td><td><li id='zhaoyun'>赵云</li></td><td><li id='yuantiangang'>袁天罡</li></td><td><li id='huangzhong'>黄忠</li></td></tr><tr><td><li id='zhentianxingcun'>真田幸村</li></td><td><li id='diaochan'>貂蝉</li></td><td><li id='fububanzang'>服部半藏</li></td><td><li id='daji'>妲己</li></td><td><li id='baiqi'>白起</li></td></tr><tr><td><li id='fahai'>法海</li></td><td><li id='fengchenxiuji'>丰臣秀吉</li></td><td><li id='yuanfeizuozhu'>猿飞佐助</li></td><td><li id='changzongwobuyuanqin'>斯巴达</li></td><td><li id='lvbu'>吕布</li></td></tr><tr><td><li id='likui'>李逵</li></td><td><li id='jingke'>荆轲</li></td><td><li id='mozi'>墨子</li></td><td><li id='guidie'>归蝶</li></td><td><li id='zhitianxinchang'>织田信长</li></td></tr><tr><td><li id='chengyaojin'>程咬金</li></td><td><li id='anbeiqingming'>安倍晴明</li></td><td><li id='zhugeliang'>诸葛亮</li></td><td><li id='liguang'>李广</li></td></tr></tbody></table>");
        //获取result中已选的英雄,对英雄菜单进行背景色预处理
        var selectedHero = $("[hero-info]");
        for(var i=0;i<selectedHero.length;i++){
            var selectedId = $(selectedHero[i]).attr("hero-info");
            $("#"+selectedId).addClass("selected");
            $("#"+selectedId).parent().css("background-color","#B9DEA0");
        }

        //选择英雄列表 支持动态操作dom
        $("#attach-table td > li").click(function(){
            var id = $(this).attr("id");
            var hero = $(this).text();
            $(this).toggleClass("selected");
            if($(this).hasClass("selected")){
                $(this).parent().css("background-color","#B9DEA0");
                $("#attach-result").append("<span class='input-group selected-result'><span class='input-group-addon'>" + hero + "</span><input type='text' class='form-control attach-selected' value='1' placeholder='阶数' obj-id='obj_11' hero-info=" + id + "></span>");    
                //允许选择后的结果可以点击删除 将英雄列表中对应的英雄修改为[未选择]状态 需要防止其余的元素并没有 hero-info
                $("#attach-result > span > span").click(function(){
                    var heroId = $(this).siblings("input").attr("hero-info");
                    $(this).parent().remove();
                    if(heroId==undefined){
                        return;
                    }
                    $("#"+heroId).toggleClass("selected");
                    $("#"+heroId).parent().css("background-color","#E6CAC4");
                });
            }else{
                $(this).parent().css("background-color","#E6CAC4");
                $("[hero-info="+ id +"]").parent().remove();
            }
            
        });
        break;

        case "property":
        $(".attachment").html("<table id='attach-table' style='font-size:5px'><tbody><tr><td><li id='obj_19'>绿宝箱</li></td><td><li id='obj_21'>蓝宝箱</li></td><td><li id='obj_22'>紫宝箱</li></td><td><li id='obj_23'>金宝箱</li></td><td><li id='obj_27'>蓝色钥匙</li></td></tr><tr><td><li id='obj_28'>紫色钥匙</li></td><td><li id='obj_29'>金色钥匙</li></td><td><li id='obj_31'>大碎片宝箱</li></td><td><li id='obj_36'>技能石</li></td><td><li id='obj_37'>碎片宝箱</li></td></tr><tr><td><li id='obj_3002'>盗贼笔记</li></td><td><li id='obj_3003'>百步穿杨</li></td><td><li id='obj_3004'>大道无形</li></td><td><li id='obj_3005'>护甲片</li></td><td><li id='obj_3006'>枪头</li></td></tr><tr><td><li id='obj_3007'>毒药</li></td><td><li id='obj_3008'>箭羽</li></td><td><li id='obj_3009'>太极图</li></td><td><li id='obj_6000'>万能碎片</li></td><td><li id='obj_6001'>关羽碎片</li></td></tr><tr><td><li id='obj_3000'>钢铁之躯</li></td><td><li id='obj_3001'>热血战魂</li></td><td><li id='obj_6029'>诸葛亮碎片</li></td><td><li id='obj_6030'>安倍晴明碎片</li></td><td><li id='obj_6031'>法海碎片</li></td></tr><tr><td><li id='obj_6002'>秦琼碎片</li></td><td><li id='obj_6003'>李逵碎片</li></td><td><li id='obj_6004'>程咬金碎片</li></td><td><li id='obj_6005'>项羽碎片</li></td><td><li id='obj_6006'>丰臣秀吉碎片</li></td></tr><tr><td><li id='obj_6007'>张飞碎片</li></td><td><li id='obj_6008'>林冲碎片</li></td><td><li id='obj_6009'>赵云碎片</li></td><td><li id='obj_6010'>吕布碎片</li></td><td><li id='obj_6011'>白起碎片</li></td></tr><tr><td><li id='obj_6012'>斯巴达碎片</li></td><td><li id='obj_6013'>真田幸村碎片</li></td><td><li id='obj_6014'>黄忠碎片</li></td><td><li id='obj_6015'>花木兰碎片</li></td><td><li id='obj_6016'>李广碎片</li></td></tr><tr><td><li id='obj_6017'>养由基碎片</li></td><td><li id='obj_6018'>织田信长碎片</li></td><td><li id='obj_6019'>归蝶碎片</li></td><td><li id='obj_6020'>荆轲碎片</li></td><td><li id='obj_6021'>貂蝉碎片</li></td></tr><tr><td><li id='obj_6022'>李白碎片</li></td><td><li id='obj_6023'>墨子碎片</li></td><td><li id='obj_6024'>猿飞佐助碎片</li></td><td><li id='obj_6025'>服部半藏碎片</li></td><td><li id='obj_6026'>姜子牙碎片</li></td></tr><tr><td><li id='obj_6027'>妲己碎片</li></td><td><li id='obj_6028'>袁天罡碎片</li></td><td><li id='obj_38'>刷新券</li></td></tr></tbody></table>");
        var selectedProp = $("[prop-info]");
        for(var i=0;i<selectedProp.length;i++){
            var selectedId = $(selectedProp[i]).attr("obj-id");
            $("#"+selectedId).addClass("selected");
            $("#"+selectedId).parent().css("background-color","#B9DEA0");
        }
        //物品选择列表 
        $("#attach-table td > li").click(function(){
            var id = $(this).attr("id");
            var property = $(this).text();
            $(this).toggleClass("selected");
            if($(this).hasClass("selected")){
                $(this).parent().css("background-color","#B9DEA0");
                if(id=='obj_19'||id=='obj_21'||id=='obj_22'||id=='obj_23'||id=='obj_31'||id=='obj_37'){
                    $("#attach-result").append("<span class='input-group selected-result'><span class='input-group-addon'>" + property + "</span><input type='text' class='form-control attach-selected' value='1' placeholder='类型' prop-info='true' obj-id=" + id + "></span>");    
                }else{
                    $("#attach-result").append("<span class='input-group selected-result'><span class='input-group-addon'>" + property + "</span><input type='text' class='form-control attach-selected' value='1' placeholder='数量' prop-info='true' obj-id=" + id + "></span>");    
                }
                //允许选择后的结果可以点击删除 将物品列表中对应的物品修改为[未选择]状态
                $("#attach-result > span > span").click(function(){
                    var propId = $(this).siblings("input").attr("obj-id");
                    $(this).parent().remove();
                    if(propId==undefined || propId=="obj_11" || propId=="obj_1" || propId=="obj_2" || propId=="obj_3"){
                        return;
                    }
                    $("#"+propId).toggleClass("selected");
                    $("#"+propId).parent().css("background-color","#E6CAC4");
                });
            }else{
                $(this).parent().css("background-color","#E6CAC4");
                $("[obj-id="+ id +"]").parent().remove();
            }
        });
        break;
    }
    //修改附件按钮的状态
    $("#btn-attachment").html(html);
    $("#attachment-num").attr("data-info", info);
});

//验证附件物品数量为正整数
function checkNum(num){
    var re = /^[1-9]+[0-9]*]*$/;
    return re.test(num);
}
//初始化附件保存按钮
function initAttachBtn(txt, objId){
    $("#btn-attach-save").click(function(){
        if($("[obj-id="+objId+"]").length > 0){
            return;
        }
        var val = $('#attachment-num').val();
        if(!checkNum(val)){
            val = 1;
        }
        $("#attach-result").append("<span class='input-group selected-result'><span class='input-group-addon'>"+ txt +"</span><input type='text' class='form-control attach-selected' value="+ val +" readonly='readonly' obj-id="+objId+"></span>");
        //点击结果集删除
        $("#attach-result > span").click(function(){
            $(this).remove();
        });
    });
    
}


$("ul#select-account > li").click(function(){
    var mailAccountInfo = $(this).children("a").attr("data-info");
    var mailAccountTxt = $(this).children("a").text();
    var spanAccount =  $("#select-account").siblings("span");
    $(spanAccount).text(mailAccountTxt);
    $("#reply-account").attr("data-info",mailAccountInfo);
    if(mailAccountInfo=="mail-server"){
        $("#reply-account").val(mailAccountTxt);
        $("#reply-account").attr("readonly","readonly");
    }else{
        $("#reply-account").val("");
        $("#reply-account").removeAttr("readonly");
    }
});

//锁死图标选择下拉菜单 清除按钮
$("button.btn.btn-default.btn-circle").attr('disabled',"true");
$("ul.dropdown-menu.iconBar > li").addClass("disabled");
$("li.btn-icons").unbind("click");
$("li.disabled > button.btn.btn-primary").attr('disabled',"true");
$("#btn-dropdownIcon").one("click", function(){
    $('.btn-icons > a > div').iCheck('disable');
});

function getAddressFromIcon(icon){
    var emailAddress = "";
    switch(icon){
        case "malai":
        emailAddress = malaiAddress;
        break;
        case "uc":
        emailAddress = ucAddress;
        break;
        case "ios":
        emailAddress = iosAddress;
        break;
        case "test":
        emailAddress = testAddress;
        break;
    }
    return emailAddress;
}

function getServerFromIcon(icon){
    var server = "";
    switch(icon){
        case "malai":
        server = malaiServer;
        break;
        case "uc":
        server = ucServer;
        break;
        case "ios":
        server = iosServer;
        break;
        case "test":
        server = testServer;
        break;
    }
    return server;
}