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

//onload initial 跳转页面获取url处理icon,起始结束时间
$(document).ready(function(){
    var icon = GetQueryString("icon");
    startDate = GetQueryString("startDate");
    endDate = GetQueryString("endDate");
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
    //去除url后参数不跳转 
    window.history.pushState({},0,window.location.pathname );
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
            if(sD!=getFormatDate(6) || eD!=getFormatDate(0)){
               $("div.time-title.active > a.selected").removeClass("selected");
            }
        break;
        case "aRecent30Days":
            if(sD!=getFormatDate(29) || eD!=getFormatDate(0)){
               $("div.time-title.active > a.selected").removeClass("selected");
            }
        break;
        case "aRecent90Days":
            if(sD!=getFormatDate(89) || eD!=getFormatDate(0)){
               $("div.time-title.active > a.selected").removeClass("selected");
            }
        break;
    }
}

function getWeekdayFromDate(date){
    if(date.split("-").length!=3){
        return undefined;
    }
    var weekArray = new Array("星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六");
    var weekday = weekArray[new Date(date).getDay()];
    return weekday;
}