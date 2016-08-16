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

// $(".collapsed.explain-title").click(function() {
//     var rightCss = "fa fa-chevron-circle-right";
//     var downCss = "fa fa-chevron-circle-down";

//     $(this).siblings("i").toggleClass(downCss);
//     $(this).siblings("i").toggleClass(rightCss);
// });

//toggle the explain-row css
$(".btn.btn-2.btn-2c").click(function() {
    $("#explain-panel").toggleClass("explain-switch");
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
            // console.log($(filterCheckBoxs[i]).siblings("span").text());
        }
    }
});

//explain up and down button 
$("#btn-explain-up").click(function(){
    $("div.explain-content-box").css("margin-top", function(index,value){
        value = parseFloat(value) + 155;
        if(value>=0){
            $("#btn-explain-up").addClass("disabled");
        }
        if($("#btn-explain-down").hasClass("disabled")){ 
            $("#btn-explain-down ").removeClass("disabled");
        }
        return value;
    });       
});
$("#btn-explain-down").click(function(){
    $("div.explain-content-box").css("margin-top", function(index,value){
        value = parseFloat(value) - 155;
        if(value <= -310){
            $("#btn-explain-down").addClass("disabled");
        }
        if($("#btn-explain-up").hasClass("disabled")){ 
            $("#btn-explain-up").removeClass("disabled");
        }
        return value;
    });
});

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

$(function() {
    // 基于准备好的dom，初始化echarts实例
    var myChart1 = echarts.init(document.getElementById('main'));
    var myChart2 = echarts.init(document.getElementById('side'));
    var myChart3 = echarts.init(document.getElementById('third'));

    // 指定图表的配置项和数据
    var option = {
        tooltip: {
            trigger: 'axis'

        },
        legend: {
            // show:false,
            zlevel:-1,
            left:20,
            // padding:30,
            // left:"left",
            // align:"left",
            // selectedMode:false,
            // inactiveColor:"black"
            // orient:"vertical",
            data: ['设备激活', '新增用户', '新增设备'],
            shadowColor:'rgba(1, 0, 1, 1.1)',
            shadowBlur: 10,
            shadowOffsetX:10,
            shadowOffsetX:10,
        },
        
        toolbox: {
            show: true,
            feature: {
                dataZoom: {
                    yAxisIndex: 'none'
                },
                dataView: {
                    readOnly: false
                },
                magicType: {
                    type: ['line', 'bar']
                },
                restore: {},
                saveAsImage: {}
            }
        },
        xAxis: {
            type: 'category',
            boundaryGap: true,
            name:"test",
            data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
        },
        yAxis: {
            type: 'value',
            axisLabel: {
                formatter: '{value} cm'
            }
        },
        series: [{
            name: '设备激活',
            type: 'line',
            data: [11, 11, 15, 13, 12, 13, 10]
        },
        {
            name: '新增用户',
            type: 'line',
            data: [1, -2, 2, 5, 3, 2, 0]
        
        },
        {
            name: '新增设备',
            type: 'line',
            data: [14, 2, 14, 5, 4, 12, 10]
        
        }]
    };
    // 使用刚指定的配置项和数据显示图表。
    myChart1.setOption(option);
    myChart2.setOption(option);
    myChart3.setOption(option);
});

$(function(){
    
    $('input').iCheck({
        checkboxClass: 'icheckbox_polaris'
        // increaseArea: '-10%' // optional
    });

});
