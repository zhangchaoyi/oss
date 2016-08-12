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

$(".collapsed.explain-title").click(function(){
    // var right = ".fa.fa-chevron-circle-right";
    // var down = ".fa.fa-chevron-circle-down"
    var rightCss = "fa fa-chevron-circle-right";
    var downCss = "fa fa-chevron-circle-down";

    $(this).siblings("i").toggleClass(downCss);
    $(this).siblings("i").toggleClass(rightCss);


    // if($(right).length != 0) {
    //     var temp = $(right);
    //     temp.removeClass(rightCss);
    //     temp.addClass(downCss);
    // }else{
    //     var temp = $(down);
    //     temp.removeClass(downCss);
    //     temp.addClass(rightCss);
    // }

});

$(".btn.btn-2.btn-2c").click(function(){
    $("#explain-panel").toggleClass("explain-switch");
    var txt = $(this).text();
    console.log(txt);
    if(txt == "打开" ){
        $(this).text("关闭");
    }else{
        $(this).text("打开");
    }
});