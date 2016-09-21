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

//实时时间
setInterval(function() {
    var date = new Date();
    $('#current-time').text(checkTime(date.getHours()) +":"+ checkTime(date.getMinutes()) +":"+ checkTime(date.getSeconds()));
}, 1000);

//定时发送查询请求
setInterval(function(){
    loadRealtimeTableData(false);
    $("div.contrast-data").find("em").show();
}, 10*1000);

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
    $("div.realtime-data > em").hide();
	loadData();
    $("div.contrast-data").find("em").hide();
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
    },
    function(data, status) {
        configBeforeTable(data);
        showBeforeArrowData();

    });
}

function loadRealtimeTableData(initial){
    $.post("/oss/api/realtime/realtimedata", {
    },
    function(data, status) {
        if(initial==false){
            dealRealtimeData(data);
        }
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

function dealRealtimeData(data){
    //calculate the diffent
    var e = $("#equipment").text();
    var aP = $("#totalActive").text();
    var pP = $("#payPlayers").text();
    var rT = $("#incomeToday").text();
    var gT = $("#gameNums").text();
    var nP = $("#players").text();
    var oP = $("#oldPlayers").text();
    var pT = $("#payNums").text();
    var rSum = $("#incomeAccumulative").text();
    var aGPT = $("#gameTimes").text();

    var eDif = parseInt(data["e"])-parseInt(e);
    var aPDif = parseInt(data["aP"])-parseInt(aP);
    var pPDif = parseInt(data["pP"])-parseInt(pP);
    var rTDif = parseInt(data["rT"])-parseInt(rT);
    var gTDif = parseInt(data["gT"])-parseInt(gT);
    var nPDif = parseInt(data["nP"])-parseInt(nP);
    var oPDif = parseInt(data["oP"])-parseInt(oP);
    var pTDif = parseInt(data["pT"])-parseInt(pT);
    var rSum = parseInt(data["rSum"])-parseInt(rSum);
    var aGPT = parseInt(data["aGPT"])-parseInt(aGPT);

    $("#equipmentEm").text("+" + eDif);
    $("#totalActiveEm").text("+" + aPDif);
    $("#payPlayersEm").text("+" + pPDif);
    $("#incomeTodayEm").text("+" + rTDif);
    $("#gameNumsEm").text("+" + gTDif);
    $("#playersEm").text("+" + nPDif);
    $("#oldPlayersEm").text("+" + oPDif);
    $("#payNumsEm").text("+" + pTDif);
    $("#incomeAccumulativeEm").text("+" + rSum);
    $("#gameTimesEm").text(parseInt(aGPT)>=0?"+" + aGPT:aGPT);

    $("div.realtime-data > em").fadeIn();
    setTimeout('$("div.realtime-data > em").fadeOut()', 4000);



 }

function showBeforeArrowData(){
    var e1 = $("#equipmentDays1").text();
    var e7 = $("#equipmentDays7").text();
    var e30 = $("#equipmentDays30").text();
    var aP1 = $("#totalActiveDays1").text();
    var aP7 = $("#totalActiveDays7").text();
    var aP30 = $("#totalActiveDays30").text();
    var pP1 = $("#payPlayersEmDays1").text();
    var pP7 = $("#payPlayersEmDays7").text();
    var pP30 = $("#payPlayersEmDays30").text();
    var rT1 = $("#incomeTodayDays1").text();
    var rT7 = $("#incomeTodayDays7").text();
    var rT30 = $("#incomeTodayDays30").text();
    var gT1 = $("#gameNumsDays1").text();
    var gT7 = $("#gameNumsDays7").text();
    var gT30 = $("#gameNumsDays30").text();
    var nP1 = $("#playersDays1").text();
    var nP7 = $("#playersDays7").text();
    var nP30 = $("#playersDays30").text();
    var oP1 = $("#oldPlayersDays1").text();
    var oP7 = $("#oldPlayersDays7").text();
    var oP30 = $("#oldPlayersDays30").text();
    var pT1 = $("#payNumsDays1").text();
    var pT7 = $("#payNumsDays7").text();
    var pT30 = $("#payNumsDays30").text();
    var rSum1 = $("#incomeAccumulativeDays1").text();
    var rSum7 = $("#incomeAccumulativeDays7").text();
    var rSum30 = $("#incomeAccumulativeDays30").text();
    var aGPT1 = $("#gameTimesDays1").text();
    var aGPT7 = $("#gameTimesDays7").text();
    var aGPT30 = $("#gameTimesDays30").text();

    var pE30 = calArrowData(e7,e30);
    var pE7 = calArrowData(e1,e7);
    var pAP30 = calArrowData(aP7,aP30);
    var pAP7 = calArrowData(aP1,aP7);
    var pPP30 = calArrowData(pP7,pP30);
    var pPP7 = calArrowData(pP1,pP7);
    var pRT30 = calArrowData(rT7,rT30);
    var pRT7 = calArrowData(rT1,rT7);
    var pGT30 = calArrowData(gT7,gT30);
    var pGT7 = calArrowData(gT1,gT7);
    var pNP30 = calArrowData(nP7,nP30);
    var pNP7 = calArrowData(nP1,nP7);
    var pOP30 = calArrowData(oP7,oP30);
    var pOP7 = calArrowData(oP1,oP7); 
    var pPT30 = calArrowData(pT7,pT30);
    var pPT7 = calArrowData(pT1,pT7); 
    var pRS30 = calArrowData(rSum7,rSum30);
    var pRS7 = calArrowData(rSum1,rSum7);
    var pAGPT30 = calArrowData(aGPT7,aGPT30);
    var pAGPT7 = calArrowData(aGPT1,aGPT7);


    showArrow("#equipmentEmDays30",pE30);
    showArrow("#equipmentEmDays7",pE7);
    showArrow("#totalActiveEmDays30",pAP30);
    showArrow("#totalActiveEmDays7",pAP7);
    showArrow("#payPlayersEmDays30",pPP30);
    showArrow("#payPlayersEmDays7",pPP7);
    showArrow("#incomeTodayEmDays30",pRT30);
    showArrow("#incomeTodayEmDays7",pRT7);
    showArrow("#gameNumsDaysEm30",pGT30);
    showArrow("#gameNumsDaysEm7",pGT7);
    showArrow("#playersEmDays30",pNP30);
    showArrow("#playersEmDays7",pNP7);
    showArrow("#oldPlayersEmDays30",pOP30);
    showArrow("#oldPlayersEmDays7",pOP7);
    showArrow("#payNumsEmDays30",pPT30);
    showArrow("#payNumsEmDays7",pPT7);
    showArrow("#incomeAccumulativeEmDays30",pRS30);
    showArrow("#incomeAccumulativeEmDays7",pRS7);
    showArrow("#gameTimesEmDays30",pAGPT30);
    showArrow("#gameTimesEmDays7",pAGPT7);
}

function showArrow(id,value){
    var direct = "down";
    if(String(value).charAt(0)=='+'){
        direct = "up";
    }
    str = "<i class='fa fa-arrow-" + direct + "' aria-hidden='true'>" + String(value).substr(1);
    $(id).text("");
    $(id).prepend(str);
    $(id).removeClass($(id).attr("class"));
    $(id).addClass(function(){
        var classNAme ="green";
        switch(direct){
            case "up":
            className="red";
            break;
            case "down":
            className="green"
            break;
        }
        return className;
    });
}

function calArrowData(f,b){
    if(b==0){
        return parseInt(f)>0?"+100%":"+0%";
    }
    var percent = "";
    if(parseInt(f)-parseInt(b)>=0){
        percent = ((parseInt(f)-parseInt(b))/parseInt(b)*100).toFixed(1);
        return '+' + percent + '%';
    }else{
        percent = ((parseInt(b)-parseInt(f))/parseInt(b)*100).toFixed(1);
        return '-' + percent + '%'; 
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
