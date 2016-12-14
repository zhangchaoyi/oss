var startDate = "";
var endDate = "";
//get today date for dateselector
//获取某一天的日期,格式 yyyy-mm-dd,参数用于当前的日期相减的数
function getFormatDate(day){
    var date = new Date();
    date.setDate(date.getDate()-day);
    var seperator1 = "-";
    var year = date.getFullYear();
    var month = date.getMonth() + 1;
    var strDate = date.getDate();
    if (month >= 1 && month <= 9) {
        month = "0" + month;
    }
    if (strDate >= 0 && strDate <= 9) {
        strDate = "0" + strDate;
    }
    var currentdate = year + seperator1 + month + seperator1 + strDate;
    return currentdate;
}

//指标说明开关
$("#btn-explain-switch").click(function() {
    $("#explain-panel").toggleClass("toggle-switch");
    var txt = $(this).text();
    if (txt == "打开") {
        $(this).text("关闭");
        $("#btn-explain-up").show();
        $("#btn-explain-down").show();
    } else {
        $(this).text("打开");
        $("#btn-explain-up").hide();
        $("#btn-explain-down").hide();
    }
});
//第一个数据表格开关
$("#btn-first-data-panel-switch").click(function(){
    $("div.table-zoom-first").toggleClass("toggle-switch");
    var txt = $(this).text();
    if (txt == "打开") {
        $(this).text("关闭");
    } else {
        $(this).text("打开");
    }
});
//第二个数据表格开关
$('#btn-gamedetail-switch').click(function(){
    $("div.table-zoom-second").toggleClass("toggle-switch");
    var txt = $(this).text();
    if (txt == "打开") {
        $(this).text("关闭");
    } else {
        $(this).text("打开");
    }
});
//第三个数据表格开关
$('#btn-third-data-panel-switch').click(function(){
    $("div.table-zoom-third").toggleClass("toggle-switch");
    var txt = $(this).text();
    if (txt == "打开") {
        $(this).text("关闭");
    } else {
        $(this).text("打开");
    }
});

//change the css when mouse cover date
function dateSelected(obj) {
    var nodes = $(obj).siblings("a");
    for(var i=0;i<nodes.length;i++) {
        $(nodes[i]).removeClass("selected");
    }
    $(obj).addClass("selected");
}

//control the filter button
$("#btn-selall").click(function(){
    $("table.tab-pane.fade.active.in").find("div").iCheck("check");
    $("table[class='tab-pane fade']").find("div").iCheck("uncheck");
});
//筛选反选
$("#btn-selreverse").click(function(){
    $("table.tab-pane.fade.active.in").find("div").iCheck("toggle");
});

//save the filter chioce
$("#btn-filtersave").click(function(){
    var filterCheckBoxs = $("table.tab-pane.fade.active.in").find("div");
    for(var i=0;i<filterCheckBoxs.length;i++) { 
        if($(filterCheckBoxs[i]).hasClass("checked")){
            //extend
            console.log($(filterCheckBoxs[i]).siblings("span").text());
        }
    }
});

//部分页面不区分终端 ul显示全选 文字显示不区分
function withoutIcon(){
    $("#ios").find("input").iCheck('check');
    $("#wp").find("input").iCheck('check');
    $("#and").find("input").iCheck('check');
    $("#btn-dropdownIcon").html("<span class='withoutIcon'>不区分终端</span>");
}

//onload initial 跳转页面获取url处理icon,起始结束时间
$(document).ready(function(){
    var icon = getCookie("icons");
    startDate = getCookie("startDate");
    endDate = getCookie("endDate");
    icon=(icon==null)?"apple":icon;
    var iconArray = String(icon).split(",");
    var htmlStr="";
    for(var i=0;i<iconArray.length;i++){
        if(iconArray[i]=="iOS"){
            iconArray[i]="apple";
        }
        htmlStr += "<span class='fa fa-" + iconArray[i] + " icons icons-view' data-info=" + iconArray[i] + " aria-hidden='true'></span>";
        switch(iconArray[i]){
            case "apple":
            $("#ios").find("input").iCheck('check');
            break;
            case "windows":
            $("#wp").find("input").iCheck('check');
            break;
            case "android":
            $("#and").find("input").iCheck('check');
            break;
        }
    }

    $("#btn-dropdownIcon").prepend(htmlStr);
});

//onload initial
//dateSelector
$(function() {
    var dateRange = new pickerDateRange('date_seletor', {
        isTodayValid: true,
        startDate: (startDate==null||startDate==""||startDate==getFormatDate(0))?getFormatDate(6):startDate,
        endDate: (endDate==null||endDate=="")?getFormatDate(0):endDate,
        //needCompare : true,
        //isSingleDay : true,
        //shortOpr : true,
        defaultText: ' 至 ',
        inputTrigger: 'input_trigger',
        theme: 'ta',
        success : function(obj) { 
            setCookie("startDate", $("input#startDate").attr("value"));
            setCookie("endDate", $("input#endDate").attr("value"));
            //设置回调句柄 
            loadData();
            validateSelectedDate();
        } 
    });
    validateSelectedDate();
    //filter
    jQuery(document).ready(function(){
            jQuery("#filter").jcOnPageFilter({
                animateHideNShow: true,
                focusOnLoad:true,
                highlightColor:'#FFFF00',
                textColorForHighlights:'#000000',
                caseSensitive:false,
                hideNegatives:true,
                parentLookupClass:'jcorgFilterTextParent',
                childBlockClass:'jcorgFilterTextChild'
            });
    });

    //icheck  checkbox
    $('input').iCheck({
        checkboxClass: 'icheckbox_polaris'
        // increaseArea: '-10%' // optional
    });

    var menuCookie = getCookie("menu");
    var menu = "";
    if(location.hostname=="localhost"){
        menu = JSON.parse(String(menuCookie).substring(1,menuCookie.length-1));     
    }else{
        menu = JSON.parse(menuCookie);     
    }
    initMenu(menu);

    //左侧导航栏展开 显示当前的页面
    var hrefs = $("#main-menu").find("a");
    var localPath = window.location.pathname.split("-")[0];
    for(var i=0;i<hrefs.length;i++){
        if($(hrefs[i]).attr("href")==localPath){
            var subParentTag = $(hrefs[i]).parent().parent();
            var rootParentTag = $(subParentTag).parent();
            //二级或三级菜单
            if(rootParentTag.get(0).tagName == "LI"){
                $(rootParentTag).addClass("active");
                $(subParentTag).addClass("in");
                $(subParentTag).attr("aria-expanded","true");
                //设置合适高度
                var length = $(subParentTag).children().length;
                length = length * 39;
                $(subParentTag).css("height",length + "px");

                if(subParentTag.hasClass("nav-third-level")){
                    var secondUl = $(rootParentTag).parent();
                    var secondLi = $(secondUl).parent();
                    $(secondLi).addClass("active");
                    $(secondUl).addClass("in");
                    $(secondUl).attr("aria-expanded","true");
                }

            }
            $(hrefs[i]).css("background","#d6d5d5");
        }
    }
});

//判断日期选择器和右侧 时间栏不相符时去除高亮效果  区分留存页
function validateSelectedDate(){
    var id = $("div.time-title.active > a.selected").attr("id");
    var sD = $("input#startDate").attr("value");
    var eD = $("input#endDate").attr("value");
    switch(id){
        case "aToday":
            if(sD!=getFormatDate(0) || eD!=getFormatDate(0)){
                $("div.time-title.active > a.selected").removeClass("selected");
            }
        break;
        case "aYesterday":
            if(sD!=getFormatDate(1) || eD!=getFormatDate(1)){
               $("div.time-title.active > a.selected").removeClass("selected");
            }
        break;
        case "aRecent7Days":
            if(location.pathname.indexOf("retain")!=-1){
                if(sD!=getFormatDate(6) || eD!=getFormatDate(2)){
                   $("div.time-title.active > a.selected").removeClass("selected");
                }
            }else{
                if(sD!=getFormatDate(6) || eD!=getFormatDate(0)){
                   $("div.time-title.active > a.selected").removeClass("selected");
                }
            }
        break;
        case "aRecent14Days":
            if(location.pathname.indexOf("retain")!=-1){
                if(sD!=getFormatDate(13) || eD!=getFormatDate(2)){
                   $("div.time-title.active > a.selected").removeClass("selected");
                }
            }else{
                if(sD!=getFormatDate(13) || eD!=getFormatDate(0)){
                   $("div.time-title.active > a.selected").removeClass("selected");
                }
            }
        break;
        case "aRecent30Days":
            if(location.pathname.indexOf("retain")!=-1){
                if(sD!=getFormatDate(29) || eD!=getFormatDate(2)){
                   $("div.time-title.active > a.selected").removeClass("selected");
                }
            }else{
                if(sD!=getFormatDate(29) || eD!=getFormatDate(0)){
                   $("div.time-title.active > a.selected").removeClass("selected");
                }
            }
        break;
        case "aRecent90Days":
            if(location.pathname.indexOf("retain")!=-1){
                if(sD!=getFormatDate(89) || eD!=getFormatDate(2)){
                   $("div.time-title.active > a.selected").removeClass("selected");
                }
            }else{
                if(sD!=getFormatDate(89) || eD!=getFormatDate(0)){
                   $("div.time-title.active > a.selected").removeClass("selected");
                }
            }
        break;
    }
}

//用于进行表格的日期 判断星期几
function getWeekdayFromDate(date){
    if(date.split("-").length!=3){
        return undefined;
    }
    var weekArray = new Array("星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六");
    var weekday = weekArray[new Date(date).getDay()];
    return weekday;
}

//获取cookie 前提是可读
function getCookie(name){
    var arr,reg=new RegExp("(^| )"+name+"=([^;]*)(;|$)");
    if(arr=document.cookie.match(reg))
        return unescape(arr[2]);
    else
        return null;
}
//设置cookie 作用域是/
function setCookie(c_name,value) {
    document.cookie=c_name+ "=" +escape(value)+";path=/";
}

//根据cookie 动态生成menu 带顺序
function initMenu(menu){
    var menuHtml = "";
    if(menu.realtime!=undefined){
        menuHtml += '<li> <a href="/oss/realtime/info"> <i class="fa fa-clock-o "></i>实时概况</a> </li>';
    }
    if(menu.form!=undefined){
        menuHtml += '<li> <a href="#"> <i class="fa fa-line-chart "></i>报表</a> </li>';
    }
    if(menu.playerAnalyse!=undefined){
        menuHtml += '<li> <a href="#"> <i class="fa fa-user"></i>玩家分析 <span class="fa arrow"></span></a><ul class="nav nav-second-level collapse" aria-expanded="false">';
        var player = menu.playerAnalyse;
        if(player.charAt(0)=='1'){
            menuHtml += '<li> <a href="/oss/players/add"> <i class="fa fa-user-plus" aria-hidden="true"></i>新增玩家</a> </li>';
        }
        if(player.charAt(1)=='1'){
            menuHtml += '<li> <a href="/oss/players/active"> <i class="fa fa-users "></i>活跃玩家</a> </li>';
        }
        if(player.charAt(2)=='1'){
            menuHtml += '<li> <a href="/oss/players/retain"> <i class="fa fa-bars "></i>留存统计</a> </li>';
        }
        if(player.charAt(3)=='1'){
            menuHtml += '<li> <a href="/oss/players/effective"> <i class="fa fa-user-secret "></i>有效玩家</a> </li>';
        }
        if(player.charAt(4)=='1'){
            menuHtml += '<li> <a href="/oss/players/equipment"> <i class="fa fa-tablet "></i>设备分析</a> </li>';
        }
        if(player.charAt(5)=='1'){
            menuHtml += '<li> <a href="/oss/players/accdetail"> <i class="fa fa-circle-o-notch "></i>生命轨迹</a> </li>';
        }
        menuHtml += '</ul></li>';
    }
    if(menu.paidAnalyse!=undefined){
        menuHtml += '<li> <a href="#"> <i class="fa fa-money "></i>付费分析 <span class="fa arrow"></span></a><ul class="nav nav-second-level collapse" aria-expanded="false">';
        var paidAnalyse = menu.paidAnalyse;
        if(paidAnalyse.charAt(0)=='1'){
            menuHtml += '<li> <a href="/oss/payment/data"> <i class="fa fa-bar-chart"></i>付费数据</a> </li>';
        }
        if(paidAnalyse.charAt(1)=='1'){
            menuHtml += '<li> <a href="/oss/payment/behavior"> <i class="fa fa-neuter "></i>付费行为</a> </li>';
        }
        if(paidAnalyse.charAt(2)=='1'){
            menuHtml += '<li> <a href="/oss/payment/transform"> <i class="fa fa-key "></i>付费转化</a> </li>';
        }
        if(paidAnalyse.charAt(3)=='1'){
            menuHtml += '<li> <a href="/oss/payment/rank"> <i class="fa fa-arrows-v "></i>付费排行</a> </li>';
        }
        if(paidAnalyse.charAt(4)=='1'){
            menuHtml += '<li> <a href="/oss/payment/players"> <i class="fa fa-credit-card "></i>付费玩家</a> </li>';
        }
        menuHtml += '</ul> </li>';
    }
    if(menu.loss!=undefined){
        menuHtml += '<li> <a href="/oss/loss"> <i class="fa fa-spinner "></i>流失分析</a> </li>';
    }
    if(menu.onlineAnalyse!=undefined){
        menuHtml += '<li> <a href="#"> <i class="fa fa-check-square-o "></i>在线分析 <span class="fa arrow"></span></a><ul class="nav nav-second-level collapse" aria-expanded="false">';
        var onlineAnalyse = menu.onlineAnalyse;
        if(onlineAnalyse.charAt(0)=='1'){
            menuHtml += '<li> <a href="/oss/online/analysis"> <i class="fa fa-check-square-o"></i>在线分析</a> </li>';
        }
        if(onlineAnalyse.charAt(1)=='1'){
            menuHtml += '<li> <a href="/oss/online/habits"> <i class="fa fa-arrows "></i>在线习惯</a> </li>';
        }
        if(onlineAnalyse.charAt(2)=='1'){
            menuHtml += '<li> <a href="/oss/online/count"> <i class="fa fa-street-view"></i>在线人数</a> </li>';
        }
        menuHtml += '</ul> </li>';
    }
    if(menu.channelAnalyse!=undefined){
        menuHtml += '<li> <a href="#"> <i class="fa fa-shopping-cart "></i>渠道分析 <span class="fa arrow"></span></a><ul class="nav nav-second-level collapse" aria-expanded="false">';
        var channelAnalyse = menu.channelAnalyse;
        if(channelAnalyse.charAt(0)=='1'){
            menuHtml += '<li> <a href="#"> <i class="fa fa-shopping-cart"></i>渠道分析</a> </li>';
        }
        if(channelAnalyse.charAt(1)=='1'){
            menuHtml += '<li> <a href="#"> <i class="fa fa-link "></i>渠道短链追踪</a> </li>';
        }
        menuHtml += '</ul> </li>';
    }
    if(menu.systemAnalyse!=undefined){
        menuHtml += '<li> <a href="#"> <i class="fa fa-gamepad "></i>系统分析 <span class="fa arrow"></span></a><ul class="nav nav-second-level collapse" aria-expanded="false">';
        var systemAnalyse = menu.systemAnalyse;
        if(systemAnalyse.charAt(0)=='1'){
            menuHtml += '<li> <a href="#"> <i class="fa fa-gavel"></i>道具分析</a> </li>';
        }
        if(systemAnalyse.charAt(1)=='1'){
            menuHtml += '<li> <a href="#"> <i class="fa fa-tasks "></i>任务分析</a> </li>';
        }
        if(systemAnalyse.charAt(2)=='1'){
            menuHtml += '<li> <a href="#"> <i class="fa fa-rocket "></i>关卡分析</a> </li>';
        }
        if(systemAnalyse.charAt(3)=='1'){
            menuHtml += '<li> <a href="#"> <i class="fa fa-flag "></i>等级分析</a> </li>';
        }
        if(systemAnalyse.charAt(4)=='1'){
            menuHtml += '<li> <a href="#"> <i class="fa fa-bitcoin "></i>虚拟币统计</a> </li>';
        }
        menuHtml += '</ul> </li>';
    }
    if(menu.versionAnalyse!=undefined){
        menuHtml += '<li> <a href="#"> <i class="fa fa-file "></i>版本分析</a> </li>';
    }
    if(menu.customEvent!=undefined){
        menuHtml += '<li> <a href="#"> <i class="fa fa-cog "></i>自定义事件 <span class="fa arrow"></span></a> <ul class="nav nav-second-level collapse" aria-expanded="false">';
        var customEvent = menu.customEvent;
        if(customEvent.charAt(0)=='1'){
            menuHtml += '<li> <a href="#"> <i class="fa fa-qrcode"></i>事件列表</a> </li>';
        }
        if(customEvent.charAt(1)=='1'){
            menuHtml += '<li> <a href="#"> <i class="fa fa-filter "></i>漏斗管理</a> </li>';
        }
        menuHtml += '</ul> </li>';
    }
    if(menu.opSupport!=undefined){
        menuHtml += '<li> <a href="#"> <i class="fa fa-send "></i>运营支持 <span class="fa arrow"></span></a> <ul class="nav nav-second-level collapse" aria-expanded="false">';
        var opSupport = menu.opSupport;
        if(opSupport.charAt(0)=='1'){
            menuHtml += '<li> <a href="/oss/operation/feedback"> <i class="fa fa-commenting-o"></i>用户反馈</a> </li>';
        }
        if(opSupport.charAt(1)=='1'){
            menuHtml += '<li> <a href="/oss/operation/record"> <i class="fa fa-history"></i>操作记录</a> </li>';
        }
        if(opSupport.charAt(2)=='1'){
            menuHtml += '<li> <a href="/oss/operation/currency"> <i class="fa fa-bitcoin"></i>货币消耗获取</a> </li>';
        }
        if(opSupport.charAt(3)=='1'){
            menuHtml += '<li> <a href="/oss/operation/object"> <i class="fa fa-gavel"></i>物品消耗获取</a> </li>';
        }
        if(opSupport.charAt(4)=='1'){
            menuHtml += '<li> <a href="/oss/operation/playerInfo"> <i class="fa fa-info-circle"></i>角色当前信息</a> </li>';
        }
        if(opSupport.charAt(5)=='1'){
            menuHtml += '<li> <a href="/oss/operation/mailManagement"> <i class="fa fa-archive"></i>邮件管理</a> </li>';
        }
        if(opSupport.charAt(6)=='1'){
            menuHtml += '<li> <a href="#"> <i class="fa fa-bell"></i>数据报警</a> </li>';
        }
        menuHtml += '</ul> </li>';
    }
    if(menu.dataDig!=undefined){
        menuHtml += '<li> <a href="#"> <i class="fa fa-anchor "></i>数据挖掘 <span class="fa arrow"></span></a> <ul class="nav nav-second-level collapse" aria-expanded="false">';
        var dataDig = menu.dataDig;
        if(dataDig.charAt(0)=='1'){
            menuHtml += '<li> <a href="#"> <i class="fa fa-crop"></i>聚类分析</a> </li>';
        }
        if(dataDig.charAt(1)=='1'){
            menuHtml += '<li> <a href="#"> <i class="fa fa-shopping-cart"></i>新玩家价值</a> </li>';
        }
        menuHtml += '</ul> </li>';
    }
    if(menu.marketAnalyse!=undefined){
        menuHtml += '<li> <a href="#"> <i class="fa fa-bar-chart "></i>市场分析</a> </li>';
    }
    if(menu.techSupport!=undefined){
        menuHtml += '<li> <a href="#"> <i class="fa fa-wrench "></i>技术支持 <span class="fa arrow"></span></a> <ul class="nav nav-second-level collapse" aria-expanded="false">';
        var techSupport = menu.techSupport;
        if(techSupport.charAt(0)=='1'){
            menuHtml += '<li> <a href="#"> <i class="fa fa-vine"></i>在线参数</a> </li>';
        }
        if(techSupport.charAt(1)=='1'){
            menuHtml += '<li> <a href="#"> <i class="fa fa-bookmark "></i>实时日志</a> </li>';
        }
        if(techSupport.charAt(2)=='1'){
            menuHtml += '<li> <a href="#"> <i class="fa fa-ban "></i>崩溃分析</a> </li>';
        }
        if(techSupport.charAt(3)=='1'){
            menuHtml += '<li> <a href="#"> <i class="fa fa-exclamation-triangle "></i>用户错误</a> </li>';
        }
        menuHtml += '</ul> </li>';
    }
    if(menu.managementCenter!=undefined){
        menuHtml += '<li> <a href="#"> <i class="fa fa-suitcase "></i>管理中心 <span class="fa arrow"></span></a> <ul class="nav nav-second-level collapse" aria-expanded="false">';
        var managementCenter = menu.managementCenter;
        if(managementCenter.charAt(0)=='1'){
            menuHtml += '<li> <a href="/oss/admin/createUser"><i class="fa fa-plus "></i>新增用户角色</a></li>';
        }
        if(managementCenter.charAt(1)=='1'){
            menuHtml += '<li> <a href="/oss/admin/manageUsers"><i class="fa fa-comments-o "></i>用户角色管理</a></li>';
        }
        menuHtml += '</ul> </li>';
    }
    $("#main-menu").html(menuHtml);
    $("#main-menu ul.nav").parent("li").click(function(){
        $(this).toggleClass("active");
        var ul = $(this).children("ul");
        var expanded = $(ul).attr("aria-expanded");
        if(expanded=="true"){
            $(ul).removeClass("in");
            $(ul).attr("aria-expanded","false");
        }else{
            $(ul).addClass("in");
            $(ul).attr("aria-expanded","true");
        }
    });
}