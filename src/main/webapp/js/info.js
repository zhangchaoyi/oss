var dateCollection=[];
var realtimeDetailsChart = echarts.init(document.getElementById('realtime-details-chart'));

function initialDateArray(){
    dateCollection=[];
    dateCollection.push(getFormatDate(0));
    dateCollection.push(getFormatDate(1));
    dateCollection.push(getFormatDate(7));
    dateCollection.push(getFormatDate(30));
}

//将时间个位数的补齐两位
function checkTime(i){
	if (i<10){
	  i="0" + i
	}
  	return i
}

//实时时间
setInterval(function() {
    var date = new Date();
    $('#current-time').text(checkTime(date.getHours()) +":"+ checkTime(date.getMinutes()) +":"+ checkTime(date.getSeconds()));
}, 1000);

//定时发送查询请求
setInterval(function(){
    loadRealtimeTableData(false);
}, 60*1000);

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
			loadInfoData($("#data-info-details > ul > li.active > a").attr("data-info"));
		} 
	}); 
})

//清除
$("#btn-contrast-clear").click(function(){
	initialDateArray();
	$("#date-contrast-seletor").text(getFormatDate(0));
    loadInfoData($("#data-info-details > ul > li.active > a").attr("data-info"));
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
    initialDateArray();
    $("div.realtime-content").hide();
	loadData();
});

function loadData(){
    loadInfoData($("#data-info-details > ul > li.active > a").attr("data-info"));
    loadBeforeTableData();
    loadRealtimeTableData(true);
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
        icon:getIcons()
    },
    function(data, status) {
        configBeforeTable(data);
    });
}

function loadRealtimeTableData(initial){
    $.post("/oss/api/realtime/realtimedata", {
        icon:getIcons()
    },
    function(data, status) {
        configRealtimeTable(data);
    });
}

//显示实时的数据
function configRealtimeTable(data){
    $("#dActivateDev-dNewAccount").text(data["dActivateDev"]+" / "+data["dNewAccount"]);
    $("#dau-dauOld").text(data["dau"]+" / "+data["dauOld"]);
    $("#dPaidRate").text(data["dnewPaidRate"]+" / "+data["dPaidRate"]+" / "+data["allPaidRate"]);
    $("#dr-allr").text(data["dRevenue"]+" / "+data["allRevenue"]);
    $("#dfirstPp-dPp").text(data["dFirstPaid"]+" / "+data["dPaidDev"]);
    $("#allActivateDev-allNewAccount").text(data["allActivateDev"]+" / "+data["allNewAccount"]);
    $("#DAU-ARPU").text(data["dDAUARPU"]+" / ");
    $("#ARPU-ARPPU").text(data["ARPU"]+" / "+data["ARPPU"]);
    $("#avgPaidTimes").text(data["dAvgPaidTimes"]+" / "+data["allAvgPaidTimes"]);
    $("#dAvgLoginTimes").text(data["dAvgSinglePeriod"]+" / "+data["dAvgLoginTimes"]);
}
//昨日 7日前 30日前的数据
function configBeforeTable(data){
    var arr = [1,7,30];
    for(var i in arr){
        $("#dActivateDev-dNewAccount-Days"+arr[i]).text(data["dActivateDev"+arr[i]]+" / "+data["dNewAccount"+arr[i]]);
        $("#dau-dauOld-Days"+arr[i]).text(data["dau"+arr[i]]+" / "+data["dauOld"+arr[i]]);
        $("#dPaidRate-Days"+arr[i]).text(data["dnewPaidRate"+arr[i]]+" / "+data["dPaidRate"+arr[i]]+data["allPaidRate"+arr[i]]);
        $("#dr-allr-Days"+arr[i]).text(data["dRevenue"+arr[i]]+" / "+data["allRevenue"+arr[i]]);
        $("#dfirstPp-dPp-Days"+arr[i]).text(data["dFirstPaid"+arr[i]]+" / "+data["dPaidDev"+arr[i]]);
        $("#allActivateDev-allNewAccount-Days"+arr[i]).text(data["allActivateDev"+arr[i]]+" / "+data["allNewAccount"+arr[i]]);
        $("#DAU-ARPU-Days"+arr[i]).text(data["dDAUARPU"+arr[i]]+" / ");
        $("#ARPU-ARPPU-Days"+arr[i]).text(data["ARPU"+arr[i]]+" / "+data["ARPPU"+arr[i]]);
        $("#avgPaidTimes-Days"+arr[i]).text(data["dAvgPaidTimes"+arr[i]]+" / "+data["allAvgPaidTimes"+arr[i]]);
        $("#dAvgLoginTimes-Days"+arr[i]).text(data["dAvgSinglePeriod"+arr[i]]+" / "+data["dAvgLoginTimes"+arr[i]]);
    }
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
    initialDateArray();
    $("#date-contrast-seletor").text(getFormatDate(0));
    loadInfoData($(this).attr("data-info"));
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
