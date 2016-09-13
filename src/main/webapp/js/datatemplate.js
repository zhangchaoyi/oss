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

$(".collapsed.explain-title").click(function() {
    var rightCss = "fa fa-chevron-circle-right";
    var downCss = "fa fa-chevron-circle-down";

    $(this).siblings("i").toggleClass(downCss);
    $(this).siblings("i").toggleClass(rightCss);
});

$(".btn.btn-2.btn-2c").click(function() {
    $("#explain-panel").toggleClass("explain-switch");
    var txt = $(this).text();
    if (txt == "打开") {
        $(this).text("关闭");
    } else {
        $(this).text("打开");
    }
});

function dateSelected(obj) {
    var nodes = $(obj).siblings("a");
    for(var i=0;i<nodes.length;i++) {
        $(nodes[i]).removeClass("selected");
    }
    $(obj).addClass("selected");
}

//onload initial
$(function() {
    var dateRange = new pickerDateRange('date_seletor', {
        isTodayValid: true,
        startDate: getNowFormatDate(),
        endDate: getNowFormatDate(),
        //needCompare : true,
        //isSingleDay : true,
        //shortOpr : true,
        defaultText: ' 至 ',
        inputTrigger: 'input_trigger',
        theme: 'ta',

    });
});



