var rankChart = echarts.init(document.getElementById('rank-paymentBehavior-chart'));
var periodChart = echarts.init(document.getElementById('period-paymentBehavior-chart'));
var fpDetailsChart = echarts.init(document.getElementById('fp-details-chart'));

var rankTable = '#data-table-rank-paymentBehavior';
var periodTable = '#data-table-period-paymentBehavior';
var fpDetailTable = '#data-table-fp-details';

$(function(){
    loadData();
})

function loadData() {
    loadRankData($("ul.nav.nav-tabs.rank-payment-tab > li.active > a").attr("data-info"));
    loadPeriodData($("ul.nav.nav-tabs.period-payment-tab > li.actice > a").attr("data-info"));
    loadFdData($("ul.nav.nav-tabs.paid-details > li.active > a").attr("data-info"),$("div.nav-tab.paid-detail-subtab > ul > li.active > a > span").attr("data-info"));
}

function loadRankData(tag) {
    $.post("/oss/api/payment/behavior/rank", {
        tag:tag,
        icon:getIcons(),
        startDate:$("input#startDate").attr("value"),
        endDate:$("input#endDate").attr("value")
    },
    function(data, status) {
        configChart(data, rankChart, "rankChart");
        configTable(data, rankTable);
    });
}

function loadPeriodData(tag) {
    $.post("/oss/api/payment/behavior/period", {
        tag:tag,
        icon:getIcons(),
        startDate:$("input#startDate").attr("value"),
        endDate:$("input#endDate").attr("value")
    },
    function(data, status) {
        configChart(data, periodChart, "periodChart");
        configTable(data, periodTable);
    });
}

function loadFdData(tag, subTag) {
    $.post("/oss/api/payment/behavior/fp/detail", {
        tag:tag,
        subTag:subTag,
        icon:getIcons(),
        startDate:$("input#startDate").attr("value"),
        endDate:$("input#endDate").attr("value")
    },
    function(data, status) {
        configChart(data, fpDetailsChart, "fpDetailsChart");
        configTable(data, fpDetailTable);
    });
}

function appendTableHeader(data,dataTable){
    var header = data.header;
    var tableType = data.table;
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

function dealTableData(data, percent) {
    var type = data.type;
    var categories;
    var sum = 0;

    for (var key in data.category) {
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

    for (var i = 0; i < categories.length; i++) {
        var item = [];
        item.push(categories[i]);
        for (var j = 0; j < type.length; j++) {   
            item.push(serie[type[j]][i]);     
            if(percent===true){
                if(sum==0){
                    item.push('0.00%');
                    continue;
                }
                item.push(((serie[type[j]][i]/sum*100)).toFixed(2) + '%');
            }
        }
        dataArray.push(item);
    }
    return dataArray;
}

//自定义dataTable列排序
jQuery.extend(jQuery.fn.dataTableExt.oSort, {
            "num-html-pre": function(a) {
                var time = String(a).split(" ")[1];
                var num = String(a).split(" ")[0].split("~")[0];
                //分钟--小时
                if(num == ">100") {
                    num = 100;
                }
                if(num == "<10") {
                    num = 9;
                }
                if(time == "小时") {
                    num *= 60;
                }
                //天--周
                if(num == "首日") {
                    num =1 ;
                }
                if(num == ">12") {
                    num =12 ;
                }
                if(time == "周"){
                    num *= 7;
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

function configChart(data, chart, chartName) {
    var recData = data.data;
    chart.clear();
    chart.setOption({
        tooltip: {
            trigger: 'axis',
        },
        legend: {
            data: data.type
        },
        grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
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
            if(chartName=="rankChart"){
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
        xAxis: function() {
            if(chartName=="rankChart") {
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
        series: function() {
            var serie = [];
            for (var key in recData) {
                var item = {
                    name: key,
                    type: "bar",
                    smooth:true,
                    data: recData[key]
                }
                serie.push(item);
            };
            return serie;
        } ()

    });
}

function configTable(data,dataTable) {
    appendTableHeader(data,dataTable);
    var tableData;
    if(dataTable==rankTable){
        tableData = dealTableData(data,false);
    } 
    tableData = dealTableData(data,true);

    $(dataTable).dataTable().fnClearTable();  
    $(dataTable).dataTable({
        "destroy": true,
        // retrive:true,
        "data": tableData,
        columnDefs: [{
                type: 'num-html',
                targets: 0
        }],
        "dom": '<"top"f>rt<"left"lip>',
        "lengthMenu": [[14,30,-1 ],[14,30,'全部']],
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

//explain up and down button 
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

//付费等级选择栏
$("ul.nav.nav-tabs.rank-payment-tab > li").click(function(){
    loadRankData($(this).children("a").attr("data-info"));
});

//付费间隔选择栏
$("ul.nav.nav-tabs.period-payment-tab > li").click(function(){
    loadPeriodData($(this).children("a").attr("data-info"));
});

//首付选择区
$("ul.nav.nav-tabs.paid-details > li").click(function(){
    var info = $(this).children("a").attr("data-info");
    switch(info){
        case "fp-cycle":
        $("div.nav-tab.paid-detail-subtab").show();
        break;
        case "fp-rank":
        case "fp-money":
        $("div.nav-tab.paid-detail-subtab").hide();
        break;
    }
    loadFdData(info, $("div.nav-tab.paid-detail-subtab > ul > li.active > a > span").attr("data-info"));
});

//首付选择区域的子选择栏
$("div.nav-tab.paid-detail-subtab > ul > li").click(function(){
    $(this).siblings("li.active").toggleClass("active");
    $(this).addClass("active");
    loadFdData($("ul.nav.nav-tabs.paid-details > li.active > a").attr("data-info"), $(this).children("a").children("span").attr("data-info"));
});
