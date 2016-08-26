var addChart = echarts.init(document.getElementById('add-players-chart'));
var detailChart = echarts.init(document.getElementById('add-players-details-chart'));

$(function(){
    loadData();
})

function loadData() {
    loadAddPlayerData($("ul.nav.nav-tabs.add-players > li.active > a").attr("data-info"));
    loadAddDetailData($("ul.nav.nav-tabs.add-details > li.active > a").attr("data-info"));
}

function loadAddPlayerData(addTagInfo) {
    $.post("/api/players/add", {
        addTagInfo:addTagInfo,
        startDate:$("input#startDate").attr("value"),
        endDate:$("input#endDate").attr("value")
    },
    function(data, status) {
        configPlayerChart(data);
        configPlayerTable(data)
    });
}

function loadAddDetailData(addDetailTagInfo) {
    $.post("/api/players/add/detail", {
        addDetailTagInfo:addDetailTagInfo,
        startDate:$("input#startDate").attr("value"),
        endDate:$("input#endDate").attr("value")
    },
    function(data, status) {
        configDetailChart(data);
        configDetailTable(data);   
    });
}


function configPlayerChart(data) {
    var recData = data.data;
    addChart.clear();
    addChart.setOption({
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
                formatter: '{value}'
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

function configPlayerTable(data) {
    appendPlayerTableHeader(data);
    $('#data-table-add-players').dataTable().fnClearTable();  
    var tableData = dealTableData(data,false);

    $('#data-table-add-players').dataTable({
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


function appendPlayerTableHeader(data) {
    var type = data.type;
    var category = data.category;
    var txt = "";
    for (var key in category) {
        txt = "<th><span>" + key + "</span></th>";
    }

    for (var i = 0; i < type.length; i++) {
        txt = txt + "<th><span>" + type[i] + "</span></th>"
    }
    if ($("table#data-table-add-players > thead").length != 0) {
        $("table#data-table-add-players > thead").empty();
        $("#data-table-add-players").prepend("<thead><tr>" + txt + "</tr></thead>");
        return;
    }
    $("#data-table-add-players").append("<thead><tr>" + txt + "</tr></thead>");
}

function configDetailChart(data) {
    var recData = data.data;
    var categoryName;
    var categories;
    for(var i in data.category) {
        categoryName = i;
        categories = data.category[i];
    }
    detailChart.clear();

    if(categoryName=="性别"){
        detailChart.setOption({
            tooltip: {
                trigger: 'item',
                formatter: "{a} <br/>{b} : {c} ({d}%)"
            },
            legend: {
                orient: 'vertical',
                // left: 'left',
                data: data.type
            },
            backgroundColor: "white",
            series: [{
                name: data.type,
                type: 'pie',
                radius: '65%',
                center: ['50%', '50%'],
                data: function() {
                    var serie = [];
                    for (var i = 0; i < categories.length; i++) {
                        var item = {
                            name: categories[i],
                            value: recData[data.type][i],
                        };
                        serie.push(item);
                    }
                    return serie;
                } (),

                itemStyle: {
                    emphasis: {
                        shadowBlur: 10,
                        shadowOffsetX: 0,
                        shadowColor: 'rgba(0, 0, 0, 0.5)'
                    }
                }
            }]
        });
        return;
    }

    detailChart.setOption({
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

jQuery.extend(jQuery.fn.dataTableExt.oSort, {
            "num-html-pre": function(a) {
                var time = String(a).split(" ")[1];
                var num = String(a).split(" ")[0].split("~")[0];
                if (num == ">60") {
                    num = 60;
                }
                if (time == "min") {
                    num *= 60;
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

function configDetailTable(data) {
    appendDetailTableHeader(data);
    var tableData = dealTableData(data, true);
    $('#data-table-add-players-details').dataTable().fnClearTable(); 
    table = $('#data-table-add-players-details').dataTable({
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
    if ($("table#data-table-add-players-details > thead").length != 0) {
        $("table#data-table-add-players-details > thead").empty();
        $("#data-table-add-players-details").prepend("<thead><tr>" + txt + "</tr></thead>");
        return;
    }
    $("#data-table-add-players-details").append("<thead><tr>" + txt + "</tr></thead>");
}


$("ul.nav.nav-tabs.add-players > li").click(function(){
    var addTagInfo = $(this).children("a").attr("data-info");
    if(addTagInfo=="players-change-rate"){
        $("#newPlayer-note").hide();
    }
    loadAddPlayerData(addTagInfo);
});

$("ul.nav.nav-tabs.add-details > li").click(function(){
    var addDetailTagInfo = $(this).children("a").attr("data-info");
    loadAddDetailData(addDetailTagInfo);
});


//explain up and down button 
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
        if(value <= -310){
            $("#btn-explain-down").addClass("disabled");
        }
        if($("#btn-explain-up").hasClass("disabled")){ 
            $("#btn-explain-up").removeClass("disabled");
        }
        return value;
    });
});