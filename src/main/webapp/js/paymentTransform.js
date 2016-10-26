var apaChart = echarts.init(document.getElementById('data-paymentTransform-chart'));
var rateChart = echarts.init(document.getElementById('rate-paymentTransform-chart'));
var detailChart = echarts.init(document.getElementById('paymentTransform-details-chart'));

var apaTable = "#data-table-data-paymentTransform";
var rateTable = "#data-table-rate-paymentTransform";
var detailTable = "#data-table-paymentTransform-details";


$(function(){
    loadData();
})

function loadData(){
    loadApaData();
    loadRateData($("ul.nav.nav-tabs.rate-paymentTransform-tab > li.active > a").attr("data-info"));
    loadDetailData($("ul.nav.nav-tabs.paid-details > li.active > a").attr("data-info"));
}

function loadApaData() {
    $.post("/oss/api/payment/transform/paidAnalyze", {
        icon:getIcons(),
        startDate:$("input#startDate").attr("value"),
        endDate:$("input#endDate").attr("value")
    },
    function(data, status) {
        configChart(data, apaChart, "apaChart");
        configTable(data, apaTable);
    });
}

function loadRateData(tag) {
    $.post("/oss/api/payment/transform/rate", {
        tag:tag,
        icon:getIcons(),
        startDate:$("input#startDate").attr("value"),
        endDate:$("input#endDate").attr("value")
    },
    function(data, status) {
        configChart(data, rateChart, "rateChart");
        configTable(data, rateTable);
        $("#avg-pt-rate > span > font").text(dealAvg(data));
    });   
}

function loadDetailData(tag) {
    $.post("/oss/api/payment/transform/detail", {
        tag:tag,
        icon:getIcons(),
        startDate:$("input#startDate").attr("value"),
        endDate:$("input#endDate").attr("value")
    },
    function(data, status) {
        configChart(data, detailChart, "detailChart");
        configTable(data, detailTable);
        $("#avg-pt-detail > span > font").text(dealAvg(data));
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


function configChart(data, chart, chartName){
    var recData = data.data;
    var chartType = data.chartType;
    chart.clear();
    chart.setOption({
        tooltip: {
            trigger: 'axis',
            formatter:function(params) {  
               var relVal = params[0].name;
               for (var i = 0, l = params.length; i < l; i++) {
                    var value = params[i].value;
                    if(value==undefined){
                        value=0.0;
                    }  
                    relVal += '<br/>' + params[i].seriesName + ' : ' + value+'%' ;  
                }  
               return relVal;  
            } 
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
            start: 10,
            end: 50
        }],
        yAxis: function() {
            if(chartName=="detailChart"){
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
            if(chartName=="detailChart") {
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
                    type: chartType,
                    smooth:true,
                    data: recData[key]
                }
                serie.push(item);
            };
            return serie;
        } ()
    });
}

function configTable(data,dataTable){
    appendTableHeader(data,dataTable);
    $(dataTable).dataTable().fnClearTable();  
    $(dataTable).dataTable({
        "destroy": true,
        // retrive:true,
        "data": data.tableData,
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
        }
    });
}

function dealAvg(data){
    var type = data.type;
    var serie = data.data[type];
    var sum = 0.0;
    var avg = "0.0%";
    var length = serie.length;
    for(var i=0;i<length;i++){
        sum += parseFloat(serie[i]);
    }
    if(length!=0){
        avg = (sum/length).toFixed(2) + '%';
    }
    return avg;
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

//付费率选择区
$("ul.nav.nav-tabs.rate-paymentTransform-tab > li").click(function(){
    var info = $(this).children("a").attr("data-info");
    loadRateData(info);
});
//地区选择栏
$("ul.nav.nav-tabs.paid-details > li").click(function(){
    var info = $(this).children("a").attr("data-info");
    loadDetailData(info);
});