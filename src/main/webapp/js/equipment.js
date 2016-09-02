var equipmentChart = echarts.init(document.getElementById('equipment-chart'));
var equipmentDetailChart = echarts.init(document.getElementById('equipment-details-chart'));

$(function(){
    loadData();
});

function loadData(){
    loadEquipmentData($("div.nav-tab.equipment > ul > li.active > a").attr("data-info"));
    loadEquipmentDetailsData($("div.nav-tab.equipment > ul > li.active > a").attr("data-info"), $("ul.nav.nav-tabs.equipment-details > li.active > a").attr("data-info"));
}

function loadEquipmentData(playerTagInfo) {

    $.post("/api/players/equipment", {
        playerTagInfo:playerTagInfo,
        icon:getIcons(),
        startDate:$("input#startDate").attr("value"),
        endDate:$("input#endDate").attr("value")
    },
    function(data, status) {
        configEquipmentChart(data);
        configEquipmentTable(data);   
    });
}

function loadEquipmentDetailsData(playerTagInfo, detailTagInfo) {
    $.post("/api/players/equipment/details", {
        playerTagInfo:playerTagInfo,
        detailTagInfo:detailTagInfo,
        icon:getIcons(),
        startDate:$("input#startDate").attr("value"),
        endDate:$("input#endDate").attr("value")
    },
    function(data, status) {
        configDetailChart(data);
        configDetailTable(data);   
    });
}

function configEquipmentChart(data) {
    var recData = data.data;
    equipmentChart.clear();
    equipmentChart.setOption({
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
                formatter: '{value} '
            }
        },
        series: function() {
            var serie = [];
            for (var key in recData) {
                var item = {
                    name: key,
                    type: "bar",
                    itemStyle: {
                        normal: {
                            color: 'rgb(87, 139, 187)'
                        }
                    },
                    data: recData[key]
                }
                serie.push(item);
            };
            return serie;
        } ()

    });
}

function configEquipmentTable(data) {
    appendEquipmentTableHeader(data);
    $('#data-table-equipment').dataTable().fnClearTable();  
    var tableData = dealTableData(data,true);

    $('#data-table-equipment').dataTable({
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

function dealTableData(data,percent) {
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
                item.push(((serie[type[j]][i]/sum*100)).toFixed(2) + '%');
            }
        }
        dataArray.push(item);
    }
    return dataArray;
}


function appendEquipmentTableHeader(data) {
    var type = data.type;
    var category = data.category;
    var txt = "";
    for (var key in category) {
        txt = "<th><span>" + key + "</span></th>";
    }

    for (var i = 0; i < type.length; i++) {
        txt = txt + "<th><span>" + type[i] + "</span></th>"
    }
    txt = txt + "<th><span>百分比</span></th>";

    if ($("table#data-table-equipment > thead").length != 0) {
        $("table#data-table-equipment > thead").empty();
        $("#data-table-equipment").prepend("<thead><tr>" + txt + "</tr></thead>");
        return;
    }
    $("#data-table-equipment").append("<thead><tr>" + txt + "</tr></thead>");
}


function configDetailChart(data) {
    var recData = data.data;

    equipmentDetailChart.clear();
    equipmentDetailChart.setOption({
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
        yAxis: {
            type: 'category',
            data: function() {
                for (var key in data.category) {
                    return data.category[key];
                }
            } ()
        },
        xAxis: {
            type: 'value',
            boundaryGap: [0, 0.01],
        },
        series: function() {
            var serie = [];
            for (var key in recData) {
                var item = {
                    name: key,
                    type: "bar",
                    smooth:true,
                    itemStyle: {
                        normal: {
                            color: 'rgb(87, 139, 187)'
                        }
                    },
                    data: recData[key]
                }
                serie.push(item);
            };
            return serie;
        } ()

    });
}

function configDetailTable(data) {
    appendDetailTableHeader(data);
    var tableData = dealTableData(data, true);
    $('#data-table-equipment-details').dataTable().fnClearTable(); 
    $('#data-table-equipment-details').dataTable({
        destroy: true,
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


function appendDetailTableHeader(data) {
    var type = data.type;
    var category = data.category;
    var txt = "";
    for (var key in category) {
        txt = "<th><span>" + key + "</span></th>";
    }

    for (var i = 0; i < type.length; i++) {
        txt = txt + "<th><span>" + type[i] + "</span></th>"
    }
    txt = txt + "<th><span>百分比</span></th>";

    if ($("table#data-table-equipment-details > thead").length != 0) {
        $("table#data-table-equipment-details > thead").empty();
        $("#data-table-equipment-details").prepend("<thead><tr>" + txt + "</tr></thead>");
        return;
    }
    $("#data-table-equipment-details").append("<thead><tr>" + txt + "</tr></thead>");
}

//player tag event
$("div.nav-tab.equipment > ul > li").click(function(){
    $("div.nav-tab.equipment > ul > li.active").toggleClass("active");
    $(this).toggleClass("active");

    var playerTagInfo = $(this).children("a").attr("data-info");
    var detailTagInfo = $("ul.nav.nav-tabs.equipment-details > li.active > a").attr("data-info");

    loadEquipmentData(playerTagInfo);
    loadEquipmentDetailsData(playerTagInfo, detailTagInfo);
});

//detail tag event
$("ul.nav.nav-tabs.equipment-details > li").click(function(){
    var detailTagInfo = $(this).children("a").attr("data-info");
    var playerTagInfo = $("div.nav-tab.equipment > ul > li.active").children("a").attr("data-info");

    loadEquipmentDetailsData(playerTagInfo, detailTagInfo);
})


//explain button event
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
        if(value <= -155){
            $("#btn-explain-down").addClass("disabled");
        }
        if($("#btn-explain-up").hasClass("disabled")){ 
            $("#btn-explain-up").removeClass("disabled");
        }
        return value;
    });
});

