var dateCollection=[];
dateCollection.push(getFormatDate(0));
dateCollection.push(getFormatDate(1));
var realtimeDetailsChart = echarts.init(document.getElementById('realtime-details-chart'));

//将时间个位数的补齐两位
function checkTime(i){
	if (i<10){
	  i="0" + i
	}
  	return i
}
setInterval(function() {
    var date = new Date();
    $('#current-time').text(checkTime(date.getHours()) +":"+ checkTime(date.getMinutes()) +":"+ checkTime(date.getSeconds()));
}, 1000);

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

$(function(){
	var dateRange = new pickerDateRange('date-contrast-seletor', { 
		isTodayValid : true, 
		startDate : getFormatDate(0), 
		theme : 'ta', 
		isSingleDay : true,
		inputTrigger: 'input-contrast-trigger',
		shortOpr : true,
		autoSubmit : true,
		success : function(obj) { 
		    //设置回调句柄
            var time = $("#startDate").attr("value");
            if(isExist(dateCollection,time))return; 
			dateCollection.push(time);
			loadInfoData();
		} 
	}); 
})

$("#btn-contrast-clear").click(function(){
	dateCollection=[];
	dateCollection.push(getFormatDate(0));
	dateCollection.push(getFormatDate(1));
	loadInfoData();
});


function isExist(arr,time) {
    for(var i=0;i<arr.length;i++){
        if(arr[i]==time){
            return true;
        }
    }
    return false;
}

$(function(){
	$("div.realtime-content").hide();
	loadData();
});

function loadData(){
    loadInfoData($("#data-info-details > ul > li.active > a").attr("data-info"));
    loadBeforeTableData();
    loadRealtimeTableData();
}

function loadInfoData(detailTag) {
	$.post("/oss/api/realtime/info", {
    	detailTag:detailTag,
        icon:getIcons(),
        startDate:dateCollection
    },
    function(data, status) {
    	configChart(data);
    });
}

function loadBeforeTableData(){
    $.post("/oss/api/realtime/beforedata", {
    },
    function(data, status) {
        configBeforeTable(data);
    });
}

function loadRealtimeTableData(){
    $.post("/oss/api/realtime/realtimedata", {
    },
    function(data, status) {
        configRealtimeTable(data);
    });
}

function configRealtimeTable(data){
    $("#equipment").text(data["e"]);
    $("#totalActive").text(data["aP"]);
    $("#payPlayers").text(data["pP"]);
    $("#incomeToday").text(data["rT"]);
    $("#gameNums").text(data["gT"]);
    $("#players").text(data["nP"]);
    $("#oldPlayers").text(data["oP"]);
    $("#payNums").text(data["pT"]);
    $("#incomeAccumulative").text(data["rSum"]);
    $("#gameTimes").text(data["aGPT"]);
}

function configBeforeTable(data){
    $("#equipmentDays1").text(data["e1"]);
    $("#equipmentDays7").text(data["e7"]);
    $("#equipmentEmDays30").text(data["e30"]);
    $("#totalActiveDays1").text(data["aP1"]);
    $("#totalActiveDays7").text(data["aP7"]);
    $("#totalActiveDays30").text(data["aP30"]);
    $("#payPlayersDays1").text(data["pP1"]);
    $("#payPlayersDays7").text(data["pP7"]);
    $("#payPlayersDays30").text(data["pP30"]);
    $("#incomeTodayDays1").text(data["r1"]);
    $("#incomeTodayDays7").text(data["r7"]);
    $("#incomeTodayDays30").text(data["r30"]);
    $("#gameNumsDays1").text(data["gT1"]);
    $("#gameNumsDays7").text(data["gT7"]);
    $("#gameNumsDays30").text(data["gT30"]);
    $("#playersDays1").text(data["nP1"]);
    $("#playersDays7").text(data["nP7"]);
    $("#playersDays30").text(data["nP30"]);
    $("#oldPlayersDays1").text(data["oP1"]);
    $("#oldPlayersDays7").text(data["oP7"]);
    $("#oldPlayersDays30").text(data["oP30"]);
    $("#payNumsDays1").text(data["pT1"]);
    $("#payNumsDays7").text(data["pT7"]);
    $("#payNumsDays30").text(data["pT30"]);
    $("#incomeAccumulativeDays1").text(data["rSum1"]);
    $("#incomeAccumulativeDays7").text(data["rSum7"]);
    $("#incomeAccumulativeDays30").text(data["rSum30"]);
    $("#gameTimesDays1").text(data["aGP1"]);
    $("#gameTimesDays7").text(data["aGP7"]);
    $("#gameTimesDays30").text(data["aGP30"]);
}

function configChart(data) {
    var recData = data.data;
    realtimeDetailsChart.clear();
    realtimeDetailsChart.setOption({
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

//点击tag时需要清空全局时间变量
$("#data-info-details > ul > li > a").click(function(){
    dateCollection=[];
    dateCollection.push(getFormatDate(0));
    dateCollection.push(getFormatDate(1));
	loadInfoData();
});

//右侧隐藏条
$("a#switch-up").click(function(){
	$(this).toggleClass("btn-current");
	$(this).find("i").toggleClass("fa-chevron-down");
	$(this).find("i").toggleClass("fa-chevron-up");
	var info = $(this).attr("data-info");
	if(info=="close"){
		$(this).attr("data-info","open");
		$("div#data-info-details").css("margin-top",function(){
			var margin = $("div#data-info-details").css("margin-top");
			margin = parseInt(margin) + 70;
			return $("div#data-info-details").css("margin-top",margin+'px');
		}());
		$("div.realtime-content.up").show();
	}else{
		$(this).attr("data-info","close");
		$("div#data-info-details").css("margin-top",function(){
			var margin = $("div#data-info-details").css("margin-top");
			margin = parseInt(margin) - 70;
			return $("div#data-info-details").css("margin-top",margin+'px');
		}());
		$("div.realtime-content.up").hide();
	}
});
$("a#switch-down").click(function(){
	$(this).toggleClass("btn-current");
	$(this).find("i").toggleClass("fa-chevron-down");
	$(this).find("i").toggleClass("fa-chevron-up");
	var info = $(this).attr("data-info");
	if(info=="close"){
		$(this).attr("data-info","open");
		$("div#data-info-details").css("margin-top",function(){
			var margin = $("div#data-info-details").css("margin-top");
			margin = parseInt(margin) + 70;
			return $("div#data-info-details").css("margin-top",margin+'px');
		}());
		$("div.realtime-content.down").show();
	}else{
		$(this).attr("data-info","close");
		$("div#data-info-details").css("margin-top",function(){
			var margin = $("div#data-info-details").css("margin-top");
			margin = parseInt(margin) - 70;
			return $("div#data-info-details").css("margin-top",margin+'px');
		}());
		$("div.realtime-content.down").hide();
	}
});
