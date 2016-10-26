var aptChart = echarts.init(document.getElementById('avg-period-times-chart'));

var aptTable = '#data-table-avg-period-times';

$(function(){
    loadData();
})

function loadData() {
	loadAvgGamePeriodData($("div.nav-tab.habits > ul > li.active > a").attr("data-info"),$("ul.nav.nav-tabs.avg-period-times-tab > li.active > a").attr("data-info"));
}

function loadAvgGamePeriodData(playerTag,tag) {
    $.post("/oss/api/online/habits/avgGP", {
    	playerTag:playerTag,
        tag:tag,
        icon:getIcons(),
        startDate:$("input#startDate").attr("value"),
        endDate:$("input#endDate").attr("value")
    },
    function(data, status) {
        configChart(data, aptChart, "aptChart");
        configTable(data, aptTable);
        dealAvgNote(tag, data);
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
                        if(chartName=='aptChart' && key=='每玩家游戏次数'){
                            return "bar";
                        }
                        return "line";
                    }(),
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

function dealAvgNote(tag,data) {
	var type = data.type;
	var categories;
	var timesSum = 0.0;
	var timeSum = 0.0;
	var timesAvg = 0.0;
	var timeAvg = 0.0;

    for (var key in data.category) {
        categories = data.category[key];
    }
    var length = categories.length;
    var serie = data.data;
    for (var i = 0; i < length; i++) {
        for (var j = 0; j < type.length; j++) {   
            if(type[j]=="每玩家游戏次数"){
            	timesSum += parseFloat(serie[type[j]][i]);
            }else{
            	timeSum += parseFloat(serie[type[j]][i]);
            }
        }
    }
    if(length!=0){
    	timesAvg = (timesSum/length).toFixed(2);
    	timeAvg = (timeSum/length).toFixed(2);
    }
    $("#avg-times").text(timesAvg);
    $("#avg-time").text(timeAvg);

	var dateTxt = "";
	switch(tag){
		case "day":
		dateTxt = "日";
		break;
		case "week":
		dateTxt = "周";
		break
		case "month":
		dateTxt = "月";
		break;
	}
	$("div#avg-note > span > span.per-date").text(dateTxt);
}

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

//player-tag
$("div.nav-tab.habits > ul.nav.nav-pills > li").click(function(){
	$(this).siblings("li.active").toggleClass("active");
    $(this).addClass("active");
    loadAvgGamePeriodData($(this).children("a").attr("data-info"),$("ul.nav.nav-tabs.avg-period-times-tab > li.active > a").attr("data-info"));
});

//time select
$("ul.nav.nav-tabs.avg-period-times-tab > li").click(function(){
	loadAvgGamePeriodData($("div.nav-tab.habits > ul > li.active > a").attr("data-info"),$(this).children("a").attr("data-info"));
});

//detail-tag
$("div.nav-tab.paid-detail-subtab > ul.nav.nav-pills > li").click(function(){
	$(this).siblings("li.active").toggleClass("active");
    $(this).addClass("active");
});
$("ul.nav.nav-tabs.game-details > li").click(function(){
	var info = $(this).children("a").attr("data-info");
	switch(info){
		case "frequency":
		$("div.nav-tab.paid-detail-subtab > ul.nav.nav-pills").text("");
		var str = "<li class='active'><a><span data-info='day-times'>日游戏次数</span></a></li>";
		str += "<li><a><span data-info='week-times'>周游戏次数</span></a></li>";
		str += "<li><a><span data-info='week-days'>周游戏天数</span></a></li>";
		str += "<li><a><span data-info='month-days'>月游戏天数</span></a></li>";
		$("div.nav-tab.paid-detail-subtab > ul.nav.nav-pills").append(str);
		$("div.nav-tab.paid-detail-subtab > ul.nav.nav-pills").show();
		break;
		case "time":
		$("div.nav-tab.paid-detail-subtab > ul.nav.nav-pills").text("");
		var str = "<li class='active'><a><span data-info='day-time'>日游戏时长</span></a></li>";
		str += "<li><a><span data-info='week-time'>周游戏时长</span></a></li>";
		str += "<li><a><span data-info='single-time'>单次游戏时长</span></a></li>";
		$("div.nav-tab.paid-detail-subtab > ul.nav.nav-pills").append(str);
		$("div.nav-tab.paid-detail-subtab > ul.nav.nav-pills").css("width","123px");
		$("div.nav-tab.paid-detail-subtab > ul.nav.nav-pills").show();
		break;
		case "period":
		$("div.nav-tab.paid-detail-subtab > ul.nav.nav-pills").hide();
		break;
	}
});