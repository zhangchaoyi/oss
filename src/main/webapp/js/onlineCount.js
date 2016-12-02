var ccuChart = echarts.init(document.getElementById('online-count-ccu-chart'));
var pcuChart = echarts.init(document.getElementById('online-count-pcu-chart'));

$(function(){
	loadData();
});

function loadData(){
	loadCcuData();
	loadPcuData();
}

function loadCcuData(){
	$.post("/oss/api/online/count/ccu", {
        startDate:$("input#startDate").attr("value"),
        endDate:$("input#endDate").attr("value")
    },
    function(data, status) {
        configChart(data, ccuChart);
        $("#period-pcu").text(data.periodPcu);
		$("#history-pcu").text(data.historyPcu);
    });
}

function loadPcuData(){
	$.post("/oss/api/online/count/pcu", {
        startDate:$("input#startDate").attr("value"),
        endDate:$("input#endDate").attr("value")
    },
    function(data, status) {
        configChart(data, pcuChart);
        configTable(data);
    });
}

function configChart(data, chart) {
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
    var tableData = dealTableData(data);

    $("#data-table-online-count-pcu").dataTable().fnClearTable();  
    $("#data-table-online-count-pcu").dataTable({
        "destroy": true,
        // retrive:true,
        "data": tableData,
        "order": [[ 0, 'desc' ]],
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
        },
        "columnDefs": [ {
           "targets": 0,
           "render": function ( data, type, full, meta ) {
                var weekday = getWeekdayFromDate(data);
                return '<span title='+weekday+'>'+data+'</span>';
            }
         }]
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

//锁死图标选择下拉菜单 清除按钮
$("ul.dropdown-menu.iconBar > li").addClass("disabled");
$("li.btn-icons").unbind("click");
$("li.disabled > button.btn.btn-primary").attr('disabled',"true");
$("#btn-dropdownIcon").one("click", function(){
    $('.btn-icons > a > div').iCheck('disable');
});