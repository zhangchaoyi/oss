var currentRole;
$(function(){
    $("#data-second").hide();
    loadData();
    initSelectAll();
})
//可以供 header.js 的图标按钮调用
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

//初始化表格,配置项 columnDefs[0,-1]用于修改第一列和最后一列
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
           "orderable":false,
           "render": function ( data, type, full, meta ) {
            return '<a class="manage-role" data-info='+data+' data-role='+ full[2] +'>修改权限</a>';
           }
         },
     {
           "targets": 0,
           "orderable":false,
           "render": function ( data, type, full, meta ) {
            return function(){
                if(data=="systemroot"){
                    return  ""; 
                }else{
                    return '<input type="checkbox" value='+data+'></input>';
                }
            }()

           }
         }  
     ],
    });
}

//初始化 全选 点击事件
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

//删除按钮
$("#delete-feedback").click(function(){
    var checkboxs = $("#data-table-user-management tbody tr td input");
    var list = [];
    for(var i=0;i<checkboxs.length;i++){
        if($(checkboxs[i]).prop("checked")){
            list.push($(checkboxs[i]).attr("value"));
        }
    }
    if(list.length==0){
        alert("请选择用户");
        return;
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

//点击某个用户的修改权限按钮 进入个人角色管理页
$(document).on("click","#data-table-user-management tbody tr td a",function() {
    var accountParam = $(this).attr("data-info");
    if(accountParam=="systemroot"){
        alert("无法修改最高用户权限");
        return;
    }
    var role = $(this).attr("data-role");
    //保存当前用户角色 全局变量 用于判断是否发生修改
    currentRole = role;
    $("#username").text(accountParam);
    $("#role-manage").attr("value",role);
    $("#data-first").slideToggle();
    $("#data-second").slideToggle();
});

//返回用户列表
$("#whaleDetailback").click(function(){
    $("#data-first").slideToggle();
    $("#data-second").slideToggle();
});

//角色添加,判断角色是否存在
$("#role-add-menu > li").click(function(){
    var inputTxt = $("#role-manage").val();
    var selectedRole = $(this).children("a").attr("data-info");
    if(inputTxt.indexOf(selectedRole)>-1){
        alert("角色已存在");
        return;
    }
    if(inputTxt==""){
        inputTxt += selectedRole;    
    }else{
        inputTxt += ','+ selectedRole;    
    }
    
    $("#role-manage").attr("value",inputTxt);
});

//角色删除,判断角色是否空
$("#role-delete-menu > li").click(function(){
    var inputTxt = $("#role-manage").val();
    var selectedRole = $(this).children("a").attr("data-info");
    if(inputTxt.indexOf(selectedRole)==-1){
        alert("该用户不拥有所选角色");
        return;
    }
    //替换后可能出现 (,XXXX | XXX,,XXX | XXXX, )三种情况
    inputTxt = inputTxt.replace(selectedRole,"");
    inputTxt = inputTxt.replace(",,",",");
    if(inputTxt.indexOf(",")==0){
        inputTxt = inputTxt.substring(1,inputTxt.length);
    }
    if(inputTxt.lastIndexOf(",")==inputTxt.length-1){
        inputTxt = inputTxt.substring(0,inputTxt.length-1);
    }
    $("#role-manage").attr("value",inputTxt);
});

//修改角色按钮
$("#change-role").click(function(){
    var inputTxt = $("#role-manage").val();
    if(!isRoleChange(currentRole, inputTxt)){
        alert("无角色修改变动");
        return;
    }
    var list = [];
    var array = inputTxt.split(",");
    for(var i=0;i<array.length;i++){
        list.push(array[i]);
    }
    if(list.length==0 || inputTxt==""){
        alert("角色列表不能为空");
        return;
    }
    var username = $("#username").text();
    if(username=="systemroot"){
        alert("无法修改最高用户权限");
    }
    $.post("/oss/api/admin/changeRole", {
        username:username,
        roles:list
    },
    function(data, status) {
        if(data.message=="successfully"){
            alert("修改成功");
            loadUserRoleData();
            currentRole = inputTxt;
        }
    });
});

//判断是否发生角色修改
//包括 角色顺序修改但是 角色不变认为未修改
function isRoleChange(cr,inputTxt){
    var changed = false;
    var crArrays = cr.split(",");
    var itArrays = inputTxt.split(",");
    if(crArrays.length != itArrays.length){
    changed = true;        
    return changed;
    }
    var map = {};
    //初始化
    for(var i=0;i<crArrays.length;i++){
        map[crArrays[i]]=0;
    }
    for(var i=0;i<itArrays.length;i++){
        if(map.hasOwnProperty(itArrays[i])){
            map[itArrays[i]]=1;
        }else{
            map[itArrays[i]]=0;
        }
    }
    for(var prop in map) {
        if(map[prop]==0) {
            changed=true;
        break;
        }
    }
    return changed;

}

//锁死图标选择下拉菜单 清除按钮
$("button.btn.btn-default.btn-circle").attr('disabled',"true");
$("ul.dropdown-menu.iconBar > li").addClass("disabled");
$("li.btn-icons").unbind("click");
$('.btn-icons > a > div > input').iCheck('disable');
$("li.disabled > button.btn.btn-primary").attr('disabled',"true");