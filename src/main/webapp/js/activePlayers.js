var activeChart = echarts.init(document.getElementById('active-players-chart'));
var detailChart = echarts.init(document.getElementById('active-details-chart'));

$(function(){
    loadData();
});

function loadData(){
    loadActivePlayerData($("ul.nav.nav-tabs.activeplayer > li.active").children("a").attr("data-info"));
    loadActiveDetailData($("ul.nav.nav-tabs.active-details > li.active > a").attr("data-info"));
};

function loadActivePlayerData(playerTag) {

    $.post("/oss/api/players/active", {
    	playerTag:playerTag,
        icon:getIcons(),
        startDate:$("input#startDate").attr("value"),
        endDate:$("input#endDate").attr("value")
    },
    function(data, status) {
        configPlayerChart(data);
        configPlayerTable(data);   
    });
}

function loadActiveDetailData(detailTagInfo) {
	$.post("/oss/api/players/active/details", {
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

function configPlayerChart(data) {
    var recData = data.data;
    activeChart.clear();
    activeChart.setOption({
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
	$('#data-table-active-players').dataTable().fnClearTable();  
    var tableData = dealTableData(data,false);

    $('#data-table-active-players').dataTable({
        "destroy": true,
        // retrive:true,
        "data": tableData,
        "dom": '<"top"f>rt<"left"lip>',
        "orderFixed": [ 0, 'desc' ],
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
    if ($("table#data-table-active-players > thead").length != 0) {
        $("table#data-table-active-players > thead").empty();
        $("#data-table-active-players").prepend("<thead><tr>" + txt + "</tr></thead>");
        return;
    }
    $("#data-table-active-players").append("<thead><tr>" + txt + "</tr></thead>");
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

//自定义dataTable的列排序
jQuery.extend(jQuery.fn.dataTableExt.oSort, {
            "num-html-pre": function(a) {
                var time = String(a).split(" ")[1];
                var num = String(a).split(" ")[0].split("-")[0];
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
    $('#data-table-active-details').dataTable().fnClearTable(); 
    table = $('#data-table-active-details').dataTable({
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
    if ($("table#data-table-active-details > thead").length != 0) {
        $("table#data-table-active-details > thead").empty();
        $("#data-table-active-details").prepend("<thead><tr>" + txt + "</tr></thead>");
        return;
    }
    $("#data-table-active-details").append("<thead><tr>" + txt + "</tr></thead>");
}



$("ul.nav.nav-tabs.activeplayer > li").click(function(){
	var playerTagInfo = $(this).children("a").attr("data-info");
	loadActivePlayerData(playerTagInfo);
});


$("ul.nav.nav-tabs.active-details > li").click(function(){
    var detailTagInfo = $(this).children("a").attr("data-info");
    loadActiveDetailData(detailTagInfo);
});

//explain up and down button 
$("#btn-explain-up").click(function(){
    $("div.explain-content-box").css("margin-top", function(index,value){
        value = parseFloat(value) + 145;
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
        value = parseFloat(value) - 145;
        if(value <= -290){
            $("#btn-explain-down").addClass("disabled");
        }
        if($("#btn-explain-up").hasClass("disabled")){ 
            $("#btn-explain-up").removeClass("disabled");
        }
        return value;
    });
});