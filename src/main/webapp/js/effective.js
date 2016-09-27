var effectiveChart = echarts.init(document.getElementById('effective-chart'));

$(function() {
    loadData();
})

function loadData(){
    configEffectData($("ul.nav.nav-tabs.effective > li.active").children("a").attr("data-info"));
}

function configEffectData(tagDataInfo) {

    $.post("/oss/api/players/effective", {
        tagDataInfo: tagDataInfo
    },
    function(data, status) {
        configChart(data);
        configTable(data);
    });
}

function configChart(data) {
    var recData = data.serie;
    effectiveChart.clear();
    effectiveChart.setOption({
        tooltip: {
            trigger: 'axis'
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
            start: 10,
            end: 80
        },
        {
            type: 'inside',
            start: 10,
            end: 50
        }],
        xAxis: {
            type: 'category',
            data: function() {
                for (var key in data.category) {
                    return data.category[key];
                }
            } ()
        },
        yAxis: {
            type: 'value',
            axisLabel: {
                formatter: '{value} %'
            }
        },
        series: function() {
            var serie = [];
            for (var key in recData) {
                var item = {
                    name: key,
                    type: "line",
                    data: recData[key]
                }
                serie.push(item);
            };
            return serie;
        } ()

    });
}


function configTable(data) {
    appendTableHeader(data);
    var tableData = dealTableData(data);
    table = $('#effective-table').dataTable({
        destroy: true,
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

function dealTableData(data) {
    var type = data.type;
    var category = data.category;
    var categories;
    for (var key in category) {
        categories = category[key];
    }
    var serie = data.serie;
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

function appendTableHeader(data) {
    var type = data.type;
    var category = data.category;
    var txt = "";
    for (var key in category) {
        txt = "<th><span>" + key + "</span></th>";
    }

    for (var i = 0; i < type.length; i++) {
        txt = txt + "<th><span>" + type[i] + "</span></th>"
    }
    if ($("table#effective-table > thead").length != 0) {
        $("table#effective-table > thead").remove();
        $("#effective-table").prepend("<thead><tr>" + txt + "</tr></thead>");
        // var spans = $("table#effective-table > thead").find("span");
        // for(var i=1;i<spans.length;i++){
        //     $(spans[i]).text(type[i]);
        // }
        return;

    }
    $("#effective-table").append("<thead><tr>" + txt + "</tr></thead>");
}

$("ul[class='nav nav-tabs effective'] > li").click(function() {
    var tagDataInfo = $(this).children("a").attr("data-info");
    configEffectData(tagDataInfo);
});

$("div.nav-tab.effective > ul > li > a").click(function(){
    var href = $(this).attr("href");
    $(this).attr("href",href + "?icon=" + getIcons());
});