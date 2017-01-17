var aptChart = echarts.init(document.getElementById('avg-period-times-chart'));
var fDChart = echarts.init(document.getElementById('fp-details-chart'));

var aptTable = '#data-table-avg-period-times';
var fDTable = '#data-table-fp-details';

$(function(){
    loadData();
})

function loadData() {
    loadAvgGamePeriodData($("div.nav-tab.habits > ul > li.active > a").attr("data-info"), $("ul.nav.nav-tabs.avg-period-times-tab > li.active > a").attr("data-info"));
    loadGameDetailData($("div.nav-tab.habits > ul > li.active > a").attr("data-info"), function(){
            var info = $("div.nav-tab.paid-detail-subtab > ul > li.active > a > span").attr("data-info");
            return info===undefined?"period":info;
        });
}

function loadAvgGamePeriodData(playerTag,tag) {
    aptChart.showLoading();
    $.post("/oss/api/online/habits/avgGP", {
        playerTag:playerTag,
        tag:tag,
        icon:getIcons(),
        startDate:$("input#startDate").attr("value"),
        endDate:$("input#endDate").attr("value")
    },
    function(data, status) {
        aptChart.hideLoading();
        configChart(data, aptChart, "aptChart");
        configTable(data, aptTable, false);
        dealAvgNote(tag, data);
    });
}

function loadGameDetailData(playerTag,tag) {
    if(playerTag=='paid-players'){
        fDChart.clear();
        $(fDTable).dataTable().fnClearTable(); 
        return;
    }
    fDChart.showLoading();
    $.post("/oss/api/online/habits/detail", {
        playerTag:playerTag,
        tag:tag,
        icon:getIcons(),
        startDate:$("input#startDate").attr("value"),
        endDate:$("input#endDate").attr("value")
    },
    function(data, status) {
        fDChart.hideLoading();
        configChart(data, fDChart, "fDChart");
        configTable(data, fDTable, true);
    });
}

function configChart(data, chart, chartName) {
    var recData = data.data;
    var categoryName = "";
    for(var key in data.category){
        categoryName = key;
    }
    chart.clear();
    chart.setOption({
        tooltip: {
            trigger: 'axis',
        },
        legend: {
            data: data.type
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
        dataZoom: [{
            type: 'slider',
            start: 0,
            end: 100
        },
        {
            type: 'inside',
            start: 0,
            end: 50
        }],
        yAxis: function() {
            if(chartName=="fDChart" && categoryName!="游戏时段"){
                var item = {
                    type: 'category',
                    data: function() {
                        for (var key in data.category) {
                            return data.category[key];
                        }
                    } ()
                };
                return item;
            }
            var item = {
                type:'value',
                axisLabel: {
                    formatter: '{value} '
                }
            };
            return item;
        } (),

        xAxis: function() {
            if(chartName=="fDChart" && categoryName!="游戏时段") {
                var item = {
                    type:'value',
                    axisLabel: {
                        formatter: '{value} '
                    }
                };
                return item;
            }
            var item = {
                type: 'category',
                data: function() {
                    for (var key in data.category) {
                        return data.category[key];
                    }
                } ()
            };
            return item;
        } (), 

        series: function() {
            var serie = [];
            for (var key in recData) {
                var item = {
                    name: key,
                    type: function(){
                        if(key=='每玩家游戏时长(分钟)' || categoryName=="游戏时段"){
                            return "line";
                        }
                        return "bar";
                    }(),
                    smooth:true,
                    data: recData[key]
                }
                serie.push(item);
            };
            return serie;
        } ()

    });
}

//自定义dataTable列排序   s/min/h
jQuery.extend(jQuery.fn.dataTableExt.oSort, {
            "num-html-pre": function(a) {
                var time = String(a).split(" ")[1];
                var num = String(a).split(" ")[0].split("~")[0];
                if(num=='<span'){
                   var date = String(a).substring(16,26);
                   var nums = String(date).split("-");
                   var num = "";
                    for(var i=0;i<nums.length;i++){
                        num += nums[i];
                    }          
                }
                if(num=='<10'){
                    num = 9;
                }
                if(num==">4"){
                    num=5;
                }
                if(num==">20"){
                    num = 21;
                }
                if(num==">60"){
                    num = 59;
                }
                if(time=='min'){
                    num *= 60;
                }
                if(time=='h'){
                    num *= 60*60;
                }
                   
                return parseFloat(num);
            },

            "num-html-asc": function(a, b) {
                return ((a < b) ? -1 : ((a > b) ? 1 : 0));
            },

            "num-html-desc": function(a, b) {
                return ((a < b) ? 1 : ((a > b) ? -1 : 0));
            }
});

function configTable(data, dataTable, percent) {
    appendTableHeader(data,dataTable);
    var tableData = dealTableData(data, percent);
    $(dataTable+" > tbody > tr > td > span[title]").tooltip({"delay":0,"track":true,"fade":250});
    $(dataTable).dataTable().fnClearTable();  
    $(dataTable).dataTable({
        "destroy": true,
        // retrive:true,
        "data": tableData,
        columnDefs: [{
                type: 'num-html',
                targets: 0,
                "render": function ( data, type, full, meta ) {
                    var weekday = getWeekdayFromDate(data);
                    if(weekday==undefined){
                       return data;     
                    }
                    return '<span title='+weekday+'>'+data+'</span>';
                }
        }],
        "dom": '<"top"f>rt<"left"lip>',
        "order": [[ 0, 'desc' ]],
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
        }
    });
}

//percent参数控制 表格是否需要百分比列  其中该页'单次游戏时长'需要五列
function dealTableData(data, percent) {
    var type = data.type;
    var categories;
    var sum = 0;
    var times;
    var singleSum = 0; 
    var categoryName = "";

    for (var key in data.category) {
        categoryName = key;
        categories = data.category[key];
    }
    var serie = data.data;
    var dataArray = [];

    if(percent===true){
        for(var t in serie){
            for(var k in serie[t]){
                sum = sum + serie[t][k];
            }
        }
    }

    if(categoryName == "单次游戏时长"){
        times = data.times;
        for(var t in times){
            singleSum += times[t];
        }
    }
    for (var i = 0; i < categories.length; i++) {
        var item = [];
        item.push(categories[i]);
        for (var j = 0; j < type.length; j++) {   
            item.push(serie[type[j]][i]);
            if(percent===true){
                if(sum==0){
                    item.push('0.00%');
                    continue;
                }else{
                    item.push(((serie[type[j]][i]/sum*100)).toFixed(2) + '%');
                }
            }
        }
        if(categoryName=="单次游戏时长"){
            item.push(times[i]);
            if(singleSum==0){
                item.push('0.00%');
            }else{
                item.push(((times[i]/singleSum*100)).toFixed(2) + '%');
            }
        }
        
        dataArray.push(item);
    }
    return dataArray;
}

function appendTableHeader(data,dataTable) {
    var header = data.header;
    var txt = "";

    var tableId = dataTable;

    for (var i = 0; i < header.length; i++) {
        txt = txt + "<th><span>" + header[i] + "</span></th>";
    }

    if ($(tableId).children("thead").length != 0) {
        $(tableId).children("thead").empty();
        $(tableId).prepend("<thead><tr>" + txt + "</tr></thead>");
        return;
    }
    $(tableId).append("<thead><tr>" + txt + "</tr></thead>");
}

//控制平均 的标签
function dealAvgNote(tag,data) {
    var type = data.type;
    var categories;
    var timesSum = 0.0;
    var timeSum = 0.0;
    var timesAvg = 0.0;
    var timeAvg = 0.0;

    for (var key in data.category) {
        categories = data.category[key];
    }
    var length = categories.length;
    var serie = data.data;
    for (var i = 0; i < length; i++) {
        for (var j = 0; j < type.length; j++) {   
            if(type[j]=="每玩家游戏次数"){
                timesSum += parseFloat(serie[type[j]][i]);
            }else{
                timeSum += parseFloat(serie[type[j]][i]);
            }
        }
    }
    if(length!=0){
        timesAvg = (timesSum/length).toFixed(2);
        timeAvg = (timeSum/length).toFixed(2);
    }
    $("#avg-times").text(timesAvg);
    $("#avg-time").text(timeAvg);

    var dateTxt = "";
    switch(tag){
        case "day":
        dateTxt = "日";
        break;
        case "week":
        dateTxt = "周";
        break
        case "month":
        dateTxt = "月";
        break;
    }
    $("div#avg-note > span > span.per-date").text(dateTxt);
}

$("#btn-explain-up").click(function(){
    $("div.explain-content-box").css("margin-top", function(index,value){
        value = parseFloat(value) + 144;
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
        value = parseFloat(value) - 144;
        if(value <= -288){
            $("#btn-explain-down").addClass("disabled");
        }
        if($("#btn-explain-up").hasClass("disabled")){ 
            $("#btn-explain-up").removeClass("disabled");
        }
        return value;
    });
});

//player-tag
// $("div.nav-tab.habits > ul.nav.nav-pills > li").click(function(){
//  $(this).siblings("li.active").toggleClass("active");
//     $(this).addClass("active");
//     loadAvgGamePeriodData($(this).children("a").attr("data-info"),$("ul.nav.nav-tabs.avg-period-times-tab > li.active > a").attr("data-info"));
// });

//time select
$("ul.nav.nav-tabs.avg-period-times-tab > li").click(function(){
    loadAvgGamePeriodData($("div.nav-tab.habits > ul > li.active > a").attr("data-info"),$(this).children("a").attr("data-info"));
});

//detail-tag
$(document).on("click","div.nav-tab.paid-detail-subtab > ul.nav.nav-pills > li",function(){
    $(this).siblings("li.active").toggleClass("active");
    $(this).addClass("active");
    loadGameDetailData($("div.nav-tab.habits > ul > li.active > a").attr("data-info"), $(this).children("a").children("span").attr("data-info"));
});

//玩家Tag  detailTag 的选择  改变dom树
$("ul.nav.nav-tabs.game-details > li, div.nav-tab.habits > ul > li").click(function(){
    var playerTag = "";
    var detailTag = "";
    var str = "";
    tagType = $(this).children("a").attr("tagType");
    switch(tagType){
        case "player":
        playerTag = $(this).children("a").attr("data-info");
        detailTag = $("ul.nav.nav-tabs.game-details > li.active > a").attr("data-info");
        $(this).siblings("li.active").toggleClass("active");
        $(this).addClass("active");
        break;
        case "detail":
        detailTag = $(this).children("a").attr("data-info");
        playerTag = $("div.nav-tab.habits > ul > li.active > a").attr("data-info");
        break;
    }
    
    $("div.nav-tab.paid-detail-subtab > ul.nav.nav-pills").text("");
    switch(detailTag){
        case "frequency":
        switch(playerTag){
            case "add-players":
            str = "<li class='active'><a><span data-info='day-times'>日游戏次数</span></a></li>";
            break;
            case "active-players":
            str = "<li class='active'><a><span data-info='day-times'>日游戏次数</span></a></li>";
            str += "<li><a><span data-info='week-times'>周游戏次数</span></a></li>";
            str += "<li><a><span data-info='week-days'>周游戏天数</span></a></li>";
            str += "<li><a><span data-info='month-days'>月游戏天数</span></a></li>";
            break;
        }
        $("div.nav-tab.paid-detail-subtab > ul.nav.nav-pills").append(str);
        $("div.nav-tab.paid-detail-subtab > ul.nav.nav-pills").show();
        break;
        
        case "time":
        switch(playerTag){
            case "add-players":
            str = "<li class='active'><a><span data-info='day-time'>日游戏时长</span></a></li>";
            str += "<li><a><span data-info='single-time'>单次游戏时长</span></a></li>";
            break;
            case "active-players":
            str = "<li class='active'><a><span data-info='day-time'>日游戏时长</span></a></li>";
            str += "<li><a><span data-info='week-time'>周游戏时长</span></a></li>";
            str += "<li><a><span data-info='single-time'>单次游戏时长</span></a></li>";
            break;
        }
        $("div.nav-tab.paid-detail-subtab > ul.nav.nav-pills").append(str);
        $("div.nav-tab.paid-detail-subtab > ul.nav.nav-pills").css("width","123px");
        $("div.nav-tab.paid-detail-subtab > ul.nav.nav-pills").show();
        break;
        
        case "period":
        $("div.nav-tab.paid-detail-subtab > ul.nav.nav-pills").hide();
        break;
    }

    switch(tagType){
        case "player":
        loadAvgGamePeriodData(playerTag,$("ul.nav.nav-tabs.avg-period-times-tab > li.active > a").attr("data-info"));
        break;
        case "detail":
        break;
    }
    if(detailTag=='period'){
        loadGameDetailData(playerTag, "period");
        return;
    }
    loadGameDetailData(playerTag, $("div.nav-tab.paid-detail-subtab > ul.nav.nav-pills > li.active > a > span").attr("data-info"));
});


