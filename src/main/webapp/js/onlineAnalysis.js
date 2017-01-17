var staChart = echarts.init(document.getElementById('start-time-analysis-chart'));
var spChart = echarts.init(document.getElementById('start-period-chart'));

var spTable = '#data-table-start-period';

$(function(){
    loadData();
})

function loadData() {
    loadStartTimesData($("ul.nav.nav-tabs.start-times-analysis-tab > li.active > a").attr("data-info"));
    loadNeightborPeriodData();
}

function loadStartTimesData(tag) {
    staChart.showLoading();
    $.post("/oss/api/online/analysis/startTimes", {
        tag:tag,
        icon:getIcons(),
        startDate:$("input#startDate").attr("value"),
        endDate:$("input#endDate").attr("value")
    },
    function(data, status) {
        staChart.hideLoading();
        configChart(data, staChart, "staChart");
    });
}

function loadNeightborPeriodData() {
    spChart.showLoading();
    $.post("/oss/api/online/analysis/neightbor", {
        icon:getIcons(),
        startDate:$("input#startDate").attr("value"),
        endDate:$("input#endDate").attr("value")
    },
    function(data, status) {
        spChart.hideLoading();
        configChart(data, spChart, "spChart");
        configTable(data, spTable);
    });
}

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
        yAxis: {
            type: 'value',
            axisLabel: {
                formatter: '{value} '
            }
        },
        xAxis: {
            type: 'category',
            data: function() {
                for (var key in data.category) {
                    return data.category[key];
                }
            } ()
        },
        series: function() {
            var serie = [];
            for (var key in recData) {
                var item = {
                    name: key,
                    type: function(){
                        if(chartName=='spChart' && key=='人数'){
                            return "bar";
                        }
                        return "line";
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

//自定义dataTable列排序
jQuery.extend(jQuery.fn.dataTableExt.oSort, {
            "num-html-pre": function(a) {
                var time = String(a).split(" ")[1];
                var num = String(a).split(" ")[0].split("~")[0];
                if(time=='D'){
                    num *= 24;
                }
                if(time=='min'){
                    num = num/60;
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

function configTable(data,dataTable) {
    appendTableHeader(data,dataTable);
    var tableData = dealTableData(data);

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

function dealTableData(data) {
    var type = data.type;
    var categories;

    for (var key in data.category) {
        categories = data.category[key];
    }

    var serie = data.data;
    var dataArray = [];

    for (var i = 0; i < categories.length; i++) {
        var item = [];
        item.push(categories[i]);
        for (var j = 0; j < type.length; j++) {   
            item.push(serie[type[j]][i]);     
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

$("ul.nav.nav-tabs.start-times-analysis-tab > li").click(function(){
    var info = $(this).children("a").attr("data-info");
    loadStartTimesData(info);
});