//get today date for dateselector
function getNowFormatDate() {
    var date = new Date();
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

function getSevenEarlyFormatDate(){
    var date = new Date();
    date.setDate(date.getDate()-6);
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

$("#btn-first-data-panel-switch").click(function(){
    $("div.table-zoom-first").toggleClass("toggle-switch");
    var txt = $(this).text();
    if (txt == "打开") {
        $(this).text("关闭");
    } else {
        $(this).text("打开");
    }
});

$('#btn-gamedetail-switch').click(function(){
    $("div.table-zoom-second").toggleClass("toggle-switch");
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

//onload initial
//dateSelector
$(function() {
    var dateRange = new pickerDateRange('date_seletor', {
        isTodayValid: true,
        startDate: getSevenEarlyFormatDate(),
        endDate: getNowFormatDate(),
        //needCompare : true,
        //isSingleDay : true,
        //shortOpr : true,
        defaultText: ' 至 ',
        inputTrigger: 'input_trigger',
        theme: 'ta',
        success : function(obj) { 
        //设置回调句柄 
            loadData();
        } 
    });
});

//filter
$(function(){
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
})

//icheck  checkbox
$(function(){
    
    $('input').iCheck({
        checkboxClass: 'icheckbox_polaris'
        // increaseArea: '-10%' // optional
    });

});

