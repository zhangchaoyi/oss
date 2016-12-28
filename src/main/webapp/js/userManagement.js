var currentRole;
var isMenuChanged = 0;
$(function(){
    $("#data-second").hide();
    loadData();
    initSelectAll();
    initSelectMenu();
    withoutIcon();
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
    //清空checkbox历史记录
    $(".cu-select-options input").iCheck("uncheck");
    $("#cu-select-all").iCheck("uncheck");

    var accountParam = $(this).attr("data-info");
    if(accountParam=="systemroot"){
        alert("无法修改最高用户权限");
        return;
    }
    var role = $(this).attr("data-role");
    //保存当前用户角色 全局变量 用于判断是否发生修改
    currentRole = role;
    
    getOwnedPermission(accountParam);

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
    if(!isRoleChange(currentRole, inputTxt)&&isMenuChanged==0){
        alert("无角色修改变动或者页面变动");
        return;
    }
    var username = $("#username").text();
    if(username=="systemroot"){
        alert("无法修改最高用户权限");
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
    
    //页面菜单列表
    var selectList = {};
    var checkbox = $(".cu-select-data");
    for(var i=0;i<checkbox.length;i++){
        if($(checkbox[i]).prop("checked")){
            var dataInfo = $(checkbox[i]).parent().siblings("a").attr("data-info");
            var groupInfo = $(checkbox[i]).parent().siblings("a").attr("group-info");
            if(selectList.hasOwnProperty(groupInfo)){
                var value = selectList[groupInfo];
                value.push(dataInfo);
                selectList[groupInfo] = value;
            }else{
                var value = [];
                value.push(dataInfo);
                selectList[groupInfo] = value;
            }
        }
    }

    if(isEmptyObject(selectList)){
        alert("请选择服务器");
        return;
    }

    if(selectList.server==undefined){
        alert("请选择服务器");
        return;
    }

    $.post("/oss/api/admin/changeRole", {
        username:username,
        roles:list,
        selectList:JSON.stringify(selectList)
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


function getOwnedPermission(userName){
    $.post("/oss/api/admin/manageUsers/user", {
        userName:userName
    },
    function(data, status) {
        setPermission(data);
    });

}

function setPermission(data){
    if(data.realtime!=undefined){
        $("[data-info='realtime']").siblings("div").children("input").iCheck('check');
    }
    if(data.form!=undefined){
        $("[data-info='form']").siblings("div").children("input").iCheck('check');   
    }
    if(data.playerAnalyse!=undefined){
        var playerAnalyse = data.playerAnalyse;
        if(playerAnalyse.charAt(0)=='1'){
            $("[data-info='new-players']").siblings("div").children("input").iCheck('check');
        }
        if(playerAnalyse.charAt(1)=='1'){
            $("[data-info='active-players']").siblings("div").children("input").iCheck('check');
        }
        if(playerAnalyse.charAt(2)=='1'){
            $("[data-info='retain']").siblings("div").children("input").iCheck('check');
        }
        if(playerAnalyse.charAt(3)=='1'){
            $("[data-info='effective']").siblings("div").children("input").iCheck('check');
        }
        if(playerAnalyse.charAt(4)=='1'){
            $("[data-info='equipment']").siblings("div").children("input").iCheck('check');
        }
        if(playerAnalyse.charAt(5)=='1'){
            $("[data-info='circle']").siblings("div").children("input").iCheck('check');
        }
    }
    if(data.paidAnalyse!=undefined){
        var paidAnalyse = data.paidAnalyse;
        if(paidAnalyse.charAt(0)=='1'){
            $("[data-info='paid-data']").siblings("div").children("input").iCheck('check');
        }
        if(paidAnalyse.charAt(1)=='1'){
            $("[data-info='paid-deed']").siblings("div").children("input").iCheck('check');
        }
        if(paidAnalyse.charAt(2)=='1'){
            $("[data-info='paid-transform']").siblings("div").children("input").iCheck('check');
        }
        if(paidAnalyse.charAt(3)=='1'){
            $("[data-info='paid-rank']").siblings("div").children("input").iCheck('check');
        }
        if(paidAnalyse.charAt(4)=='1'){
            $("[data-info='paid-players']").siblings("div").children("input").iCheck('check');
        }
    }
    if(data.loss!=undefined){
        $("[data-info='loss']").siblings("div").children("input").iCheck('check');
    }
    if(data.onlineAnalyse!=undefined){
        var onlineAnalyse = data.onlineAnalyse;
        if(onlineAnalyse.charAt(0)=='1'){
            $("[data-info='online-analyse']").siblings("div").children("input").iCheck('check');
        }
        if(onlineAnalyse.charAt(1)=='1'){
            $("[data-info='online-habits']").siblings("div").children("input").iCheck('check');
        }
        if(onlineAnalyse.charAt(2)=='1'){
            $("[data-info='online-count']").siblings("div").children("input").iCheck('check');
        }
    }
    if(data.channelAnalyse!=undefined){
        var channelAnalyse = data.channelAnalyse;
        if(channelAnalyse.charAt(0)=='1'){
            $("[data-info='channel-analyse']").siblings("div").children("input").iCheck('check');
        }
        if(channelAnalyse.charAt(1)=='1'){
            $("[data-info='channel-trace']").siblings("div").children("input").iCheck('check');
        }
    }
    if(data.systemAnalyse!=undefined){
        var systemAnalyse = data.systemAnalyse;
        if(systemAnalyse.charAt(0)=='1'){
            $("[data-info='prop-analyse']").siblings("div").children("input").iCheck('check');
        }
        if(systemAnalyse.charAt(1)=='1'){
            $("[data-info='task-analyse']").siblings("div").children("input").iCheck('check');
        }
        if(systemAnalyse.charAt(2)=='1'){
            $("[data-info='pass-analyse']").siblings("div").children("input").iCheck('check');
        }
        if(systemAnalyse.charAt(3)=='1'){
            $("[data-info='rank-analyse']").siblings("div").children("input").iCheck('check');
        }
        if(systemAnalyse.charAt(4)=='1'){
            $("[data-info='money-count']").siblings("div").children("input").iCheck('check');
        }
    }
    if(data.versionAnalyse!=undefined){
        $("[data-info='version-analyse']").siblings("div").children("input").iCheck('check');
    }
    if(data.customEvent!=undefined){
        var customEvent = data.customEvent;
        if(customEvent.charAt(0)=='1'){
            $("[data-info='event-list']").siblings("div").children("input").iCheck('check');
        }
        if(customEvent.charAt(1)=='1'){
            $("[data-info='filter-management']").siblings("div").children("input").iCheck('check');
        }
    }   
    if(data.opSupport!=undefined){
        var opSupport = data.opSupport;
        if(opSupport.charAt(0)=='1'){
            $("[data-info='user-feedback']").siblings("div").children("input").iCheck('check');
        }
        if(opSupport.charAt(1)=='1'){
            $("[data-info='op-record']").siblings("div").children("input").iCheck('check');
        }
        if(opSupport.charAt(2)=='1'){
            $("[data-info='currency-obtain-consume']").siblings("div").children("input").iCheck('check');
        }
        if(opSupport.charAt(3)=='1'){
            $("[data-info='object-obtain-consume']").siblings("div").children("input").iCheck('check');
        }
        if(opSupport.charAt(4)=='1'){
            $("[data-info='role-current-info']").siblings("div").children("input").iCheck('check');
        }
        if(opSupport.charAt(5)=='1'){
            $("[data-info='mail-management']").siblings("div").children("input").iCheck('check');
        }
        if(opSupport.charAt(6)=='1'){
            $("[data-info='announcement']").siblings("div").children("input").iCheck('check');
        }
        if(opSupport.charAt(7)=='1'){
            $("[data-info='lock-account']").siblings("div").children("input").iCheck('check');
        }
        if(opSupport.charAt(8)=='1'){
            $("[data-info='gag-offline']").siblings("div").children("input").iCheck('check');
        }
        if(opSupport.charAt(9)=='1'){
            $("[data-info='paid-recover']").siblings("div").children("input").iCheck('check');
        }
    }
    if(data.dataDig!=undefined){
        var dataDig = data.dataDig;
        if(dataDig.charAt(0)=='1'){
            $("[data-info='cluster-analyse']").siblings("div").children("input").iCheck('check');
        }
        if(dataDig.charAt(0)=='1'){
            $("[data-info='newplayers-value']").siblings("div").children("input").iCheck('check');
        }
    }
    if(data.marketAnalyse!=undefined){
        $("[data-info='market-analyse']").siblings("div").children("input").iCheck('check');
    }
    if(data.techSupport!=undefined){
        var techSupport = data.techSupport;
        if(techSupport.charAt(0)=='1'){
            $("[data-info='online-param']").siblings("div").children("input").iCheck('check');
        }
        if(techSupport.charAt(1)=='1'){
            $("[data-info='realtime-log']").siblings("div").children("input").iCheck('check');
        }
        if(techSupport.charAt(2)=='1'){
            $("[data-info='crash-analyse']").siblings("div").children("input").iCheck('check');
        }
        if(techSupport.charAt(3)=='1'){
            $("[data-info='user-mistake']").siblings("div").children("input").iCheck('check');
        }
    }
    if(data.managementCenter!=undefined){
        var management = data.managementCenter;
        if(management.charAt(0)=='1'){
            $("[data-info='create-role']").siblings("div").children("input").iCheck('check');   
        }
        if(management.charAt(1)=='1'){
            $("[data-info='manage-role']").siblings("div").children("input").iCheck('check');
        }
    }
    if(data.server!=undefined){
        var server = data.server;
        if(server.charAt(0)=='1'){
            $("[data-info='malai']").siblings("div").children("input").iCheck('check');
        }
        if(server.charAt(1)=='1'){
            $("[data-info='iOS']").siblings("div").children("input").iCheck('check');
        }
        if(server.charAt(2)=='1'){
            $("[data-info='uc']").siblings("div").children("input").iCheck('check');
        }
        if(server.charAt(3)=='1'){
            $("[data-info='test']").siblings("div").children("input").iCheck('check');
        }
    }

}

function initSelectMenu(){
    $("#cu-select-all").siblings("ins").click(function(){
        var checked = $(this).parent().hasClass("checked");
        if(checked==true) {
            $(".cu-select-options input").iCheck("check");
        }else{
            $(".cu-select-options input").iCheck("uncheck");
        }
        isMenuChanged = 1;
    });

    $(".cu-type").siblings("ins").click(function(){
        var checked = $(this).parent().hasClass("checked");
        if(checked==true) {
            $(this).parent().siblings("div").find("input").iCheck("check");
        }else{
            $(this).parent().siblings("div").find("input").iCheck("uncheck");
        }
        isMenuChanged = 1;
    });

    $(".cu-select-data").siblings("ins").click(function(){
        isMenuChanged = 1;
    });
}

function isEmptyObject(obj) {
  for (var key in obj) {
    return false;
  }
  return true;
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