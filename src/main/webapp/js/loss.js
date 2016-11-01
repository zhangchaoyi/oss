var lcChart = echarts.init(document.getElementById('loss-count-chart'));

var lcTable = '#data-table-loss-count';

$(function(){
    loadData();
})

function loadData(){
	loadLossData($("div.nav-tab.loss > ul > li.active > a").attr("data-info"), $("ul.nav.nav-tabs.loss-count-tab > li.active > a").attr("data-info"));
}

function loadLossData(playerTag,tag){
	$.post("/oss/api/loss", {
        playerTag:playerTag,
        tag:tag,
        icon:getIcons(),
        startDate:$("input#startDate").attr("value"),
        endDate:$("input#endDate").attr("value")
    },
    function(data, status) {
        configChart(data, lcChart, "lcChart");
        configTable(data, lcTable);
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

        yAxis: {
            type:'value',
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
                    type: "line",
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
    var tableData = data.tableData;

    $(dataTable).dataTable().fnClearTable();  
    $(dataTable).dataTable({
        "destroy": true,
        // retrive:true,
        "data": tableData,
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

$("div.nav-tab.loss > ul.nav.nav-pills > li").click(function(){
	$(this).siblings("li.active").toggleClass("active");
    $(this).addClass("active");
    loadLossData($(this).children("a").attr("data-info"), $("ul.nav.nav-tabs.loss-count-tab > li.active > a").attr("data-info"));
});

$("ul.nav.nav-tabs.loss-count-tab > li").click(function(){
    loadLossData($("div.nav-tab.loss > ul > li.active > a").attr("data-info"), $(this).children("a").attr("data-info"));
});