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
        },
        "columnDefs": [ {
           "targets": -1,
           "render": function ( data, type, full, meta ) {
            return '<a data-toggle="modal" data-target="#change-pwd" data-info='+data+'>修改密码</a>';
           }
         }]
    });
}
//修改密码
$("#btn-confirm").click(function(){
    var oldPwd = $("#old-pwd").val().trim();
    var newPwd = $("#new-pwd").val().trim();
    var reNewPwd = $("#re-new-pwd").val().trim();
    var username = $("[data-target='#change-pwd']").attr("data-info");
    $("#old-pwd").val("");
    $("#new-pwd").val("");
    $("#re-new-pwd").val("");
    if(newPwd!=reNewPwd){
        alert("两次输入新密码不一致");
        return;
    }
    var key = randomWord(false,16,16);

    $.post("/oss/api/admin/changePassword", {
        username:Encrypt(username, key),
        oldPwd:Encrypt(oldPwd, key),
        newPwd:Encrypt(newPwd, key),
        key:key
    },
    function(data, status) {
        if(data.message=="success"){
            alert("修改密码成功");
            location.href = location.protocol + "//" + location.host + "/oss/login";
        }else{
            alert(data.message);
        }
    });
    
});

function Encrypt(word, key){
    var key = CryptoJS.enc.Utf8.parse(key);
    var srcs = CryptoJS.enc.Utf8.parse(word);
    var encrypted = CryptoJS.AES.encrypt(srcs, key, {mode:CryptoJS.mode.ECB,padding: CryptoJS.pad.Pkcs7});
    return encrypted.toString();
}

/*
** randomWord 产生任意长度随机字母数字组合
** randomFlag-是否任意长度 min-任意长度最小位[固定位数] max-任意长度最大位
** xuanfeng 2014-08-28
*/
 
function randomWord(randomFlag, min, max){
    var str = "",
        range = min,
        arr = ['0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'];
 
    // 随机产生
    if(randomFlag){
        range = Math.round(Math.random() * (max-min)) + min;
    }
    for(var i=0; i<range; i++){
        pos = Math.round(Math.random() * (arr.length-1));
        str += arr[pos];
    }
    return str;
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