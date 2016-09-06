var retainEquipmentChart = echarts.init(document.getElementById('equipment-retain-chart'));

$(function(){
    loadData();
});

function loadData() {

    $.post("/api/players/retain-equipment/rate", {
        icon:getIcons(),
        startDate:$("input#startDate").attr("value"),
        endDate:$("input#endDate").attr("value")
    },
    function(data, status) {
        configChart(data);
    });

    $.post("/api/players/retain-equipment/detail", {
    },
    function(data, status) {
        configTable(data);
    });
}

function configChart(data) {
    var recData = data.data;
    retainEquipmentChart.clear();
    retainEquipmentChart.setOption({
        tooltip: {
            trigger: 'axis',
            formatter:function(params) {  
               var relVal = params[0].name;  
               for (var i = 0, l = params.length; i < l; i++) {  
                    relVal += '<br/>' + params[i].seriesName + ' : ' + params[i].value+"%";  
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
                    smooth:true,
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
    table = $('#data-table-equipment-retain').dataTable({
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
    var dataArray = data.data;   
    return dataArray;
}

function appendTableHeader(data) {
    var header = data.header;
    var txt="";
    for(var i=0;i<header.length;i++){
        if(i<2){
            txt = txt + "<th rowspan=2><span>" + header[i] + "</span></th>"
            continue;
        }
        if(i==2){
            txt = txt + "<th colspan=9 class='center thead'><span>" + header[i] + "</span></th></tr><tr>"
            continue;
        }

        txt = txt + "<th class='bhead'><span>" + header[i] + "</span></th>"
    }
    if ($("table#data-table-equipment-retain > thead").length != 0) {
        $("table#data-table-equipment-retain > thead").empty();
        $("#data-table-equipment-retain").prepend("<thead><tr>" + txt + "</tr></thead>");
        return;
    }
    $("#data-table-equipment-retain").append("<thead><tr>" + txt + "</tr></thead>");
}