$(function(){
	initTimeSelector();
	var periodInfo = $(".nav.nav-pills.customize-period-selected > li.active").children("a").attr("data-info");
	var addPlayerInfo = $("#customize-retain-players").attr("data-info");
	var startGameInfo = $("#customize-retain-game-type").attr("data-info");
	var timesInfo = $("#customize-retain-times").attr("data-info");
	var retentionInfo = $("#customize-retain-type").attr("data-info");
	loadData(periodInfo, addPlayerInfo, startGameInfo, timesInfo, retentionInfo);
})

function loadData(period, addPlayer, startGame, times, retentionType) {

    $.post("/oss/api/players/retain-customize", {
        period:period,
        addPlayer:addPlayer,
        startGame:startGame,
        times:times,
        retentionType:retentionType
    },
    function(data, status) {
        configTable(data);
    });
}

function configTable(data) {
    appendTableHeader(data);
    var tableData = dealTableData(data);
    table = $('#data-table-customize-retain').dataTable({
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
    if ($("table#data-table-customize-retain > thead").length != 0) {
        $("table#data-table-customize-retain > thead").empty();
        $("#data-table-customize-retain").prepend("<thead><tr>" + txt + "</tr></thead>");
        return;
    }
    $("#data-table-customize-retain").append("<thead><tr>" + txt + "</tr></thead>");
}

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

$('.dropdown-menu > li').click(function(){
	$(this).parent().siblings("button").html($(this).text()+ " " + "<span class='caret'></span>");
	$(this).parent().siblings("button").attr("data-info", $(this).children("a").attr("data-info"));
});

$(".nav.nav-pills.customize-period-selected > li").click(function(){
	var periodInfo = $(this).children("a").attr("data-info");
	var addPlayerInfo = $("#customize-retain-players").attr("data-info");
	var startGameInfo = $("#customize-retain-game-type").attr("data-info");
	var timesInfo = $("#customize-retain-times").attr("data-info");
	var retentionInfo = $("#customize-retain-type").attr("data-info");

	loadData(periodInfo, addPlayerInfo, startGameInfo, timesInfo, retentionInfo);
});

$("ul.dropdown-menu > li").click(function(){
	var ulInfo = $(this).parent("ul").attr("aria-labelledby");
	var periodInfo = $(".nav.nav-pills.customize-period-selected > li.active").children("a").attr("data-info");
	var addPlayerInfo;
	var startGameInfo;
	var timesInfo;
	var retentionInfo;

	switch(ulInfo){
		case "customize-retain-players":{
			addPlayerInfo = $(this).children("a").attr("data-info");
			startGameInfo = $("#customize-retain-game-type").attr("data-info");
			timesInfo = $("#customize-retain-times").attr("data-info");
			retentionInfo = $("#customize-retain-type").attr("data-info");
			break;
		}
		case "customize-retain-game-type":{
			addPlayerInfo = $("#customize-retain-players").attr("data-info");
			startGameInfo = $(this).children("a").attr("data-info");
			timesInfo = $("#customize-retain-times").attr("data-info");
			retentionInfo = $("#customize-retain-type").attr("data-info");
			break;	
		}
		case "customize-retain-times":{
			addPlayerInfo = $("#customize-retain-players").attr("data-info");
			startGameInfo = $("#customize-retain-game-type").attr("data-info");
			timesInfo = $(this).children("a").attr("data-info");
			retentionInfo = $("#customize-retain-type").attr("data-info");
			break;
		}
		case "customize-retain-type":{
			addPlayerInfo = $("#customize-retain-players").attr("data-info");
			startGameInfo = $("#customize-retain-game-type").attr("data-info");
			timesInfo = $("#customize-retain-times").attr("data-info");
			retentionInfo = $(this).children("a").attr("data-info");
			break;
		}
	}

	loadData(periodInfo, addPlayerInfo, startGameInfo, timesInfo, retentionInfo);
});


$("div.nav-tab.retain-tab > ul > li > a").click(function(){
    var href = $(this).attr("href");
    $(this).attr("href",href + "?icon=" + getIcons());
});