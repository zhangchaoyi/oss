var retainEquipmentChart = echarts.init(document.getElementById('equipment-retain-chart'));

$(function(){
    initTimeSelector();
    loadData();
});

function loadData() {

    $.post("/oss/api/players/retain-equipment/rate", {
        icon:getIcons(),
        startDate:$("input#startDate").attr("value"),
        endDate:validateDate($("input#endDate").attr("value"))
    },
    function(data, status) {
        configChart(data);
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
            start: 0,
            end: 100
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

function dealTableData(data) {    
    var categories;
    var dataArray = [];
    var table = data.tableData;

    for (var key in data.category) {
        categories = data.category[key];
    }
      
    for(var i = 0; i < categories.length; i++){
        var item = [];
        item.push(categories[i]);
        for(var key in table){
            if(key=='addEquipment' || key=='activeDevice'){
                item.push(table[key][i]);
                continue;
            }
            item.push(table[key][i] + '%');
        }        

        dataArray.push(item);
    }
    return dataArray;
}

function appendTableHeader(data) {
    var header = data.header;
    var txt="";
    for(var i=0;i<header.length;i++){
        if(i<3){
            txt = txt + "<th rowspan=2><span>" + header[i] + "</span></th>"
            continue;
        }
        if(i==3){
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
//默认选择前七天,在留存统计中 选择前14天
function initTimeSelector(){
       var startDate = $("input#startDate").attr("value");
       var endDate = $("input#endDate").attr("value"); 
       if(startDate==getFormatDate(6)&&endDate==getFormatDate(0)){
         startDate = getFormatDate(13);
         endDate = getFormatDate(2);
       }
       
       $("#startDate").attr('value',startDate);
       $('#date_seletor').text(startDate + ' 至 ' + endDate);
}

//留存统计只对前两天有效,因此需要判断日期去除当前日期和昨天日期
function validateDate(date) {
    var today = getFormatDate(0);
    var yesterday = getFormatDate(1);
    if(date==today || yesterday==date) {
        date=getFormatDate(2);
        var startDate = $("#startDate").attr("value");
        $("#endDate").attr("value",date);
        $('#date_seletor').text(startDate + ' 至 ' + date);
    }
    return date;
}

$("div.nav-tab.retain-tab > ul > li > a").click(function(){
    var href = $(this).attr("href");
    $(this).attr("href",href + "?icon=" + getIcons());
});