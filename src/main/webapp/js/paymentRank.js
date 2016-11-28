var rDPChart = echarts.init(document.getElementById('rank-detail-payment-chart'));
var queryData;
var accountParam = "";
var detail = false;
var tagType = new Array("在线时长","登录次数","付费金额");

//调整指标说明的高度
$(function(){
    $("#data-second").hide();
	loadData();
})

function loadData() {
    if(detail==false){
        loadRankData();
    }else if(detail==true && accountParam!=""){
        loadRankAccountDetail(accountParam);
    }
}

function loadRankData() {
    $.post("/oss/api/payment/rank/players", {
        icon:getIcons(),
        startDate:$("input#startDate").attr("value"),
        endDate:$("input#endDate").attr("value")
    },
    function(data, status) {
        configTable(data);
    });
}

function loadRankAccountDetail(account){
    $.post("/oss/api/payment/rank/account/detail", {
        account:account,
        icon:getIcons(),
        startDate:$("input#startDate").attr("value"),
        endDate:$("input#endDate").attr("value")
    },
    function(data, status) {
        queryData = data;
        configDetailChart(data,$("ul.nav.nav-tabs.rank-detail-payment-tab > li.active > a").attr("data-info"));
        configDetailTable(data);
    });
}

//充值玩家选择栏
$("ul.nav.nav-tabs.rank-payment-tab > li").click(function(){
	var info = $(this).children("a").attr("data-info");
	var txt = '';	
	switch(info){
        case "whaleRegion":
    	txt += '<li><a href="#">全部</a></li><li><a href="#">区服1</a></li><li><a href="#">区服2</a></li><li><a href="#">区服3</a></li>';
    	$("#rank-menu").text("");
       	$("#rank-menu").append(txt);
        break;
        case "whaleVersion":
        txt += '<li><a href="#">全部</a></li><li><a href="#">版本1</a></li><li><a href="#">版本2</a></li><li><a href="#">版本3</a></li>';
    	$("#rank-menu").text("");
       	$("#rank-menu").append(txt);
        break;
        case "whaleChannel":
        txt += '<li><a href="#">全部</a></li><li><a href="#">渠道1</a></li><li><a href="#">渠道2</a></li><li><a href="#">渠道3</a></li>';
    	$("#rank-menu").text("");
       	$("#rank-menu").append(txt);
        break;
    }
});

function configTable(data) {
    $('#data-table-rank-paymentBehavior').dataTable().fnClearTable();
    $("#data-table-rank-paymentBehavior > tbody > tr > td > span[title]").tooltip({"delay":0,"track":true,"fade":250});
    var _table = $('#data-table-rank-paymentBehavior').dataTable({
        "destroy": true,
        "data": data==null?null:data.data,
        "dom": '',
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
        },
        "columnDefs": [ {
           "targets": -1,
           "render": function ( data, type, full, meta ) {
            return '<a class="detail-rank" data-info='+data+'>查看</a>';
           }
         },
         {
           "targets": 2,
           "render": function ( data, type, full, meta ) {
                var weekday = getWeekdayFromDate(data);
                return '<span title='+weekday+'>'+data+'</span>';
           }
         },
         {
           "targets": 3,
           "render": function ( data, type, full, meta ) {
                var weekday = getWeekdayFromDate(data);
                return '<span title='+weekday+'>'+data+'</span>';
           }
         } ]
    });
}

function configDetailChart(data, info) {
    var recData = data.data;
    var type = "";
    switch(info){
        case "time":
        type = tagType[0];
        break;
        case "times":
        type = tagType[1];
        break;
        case "payment":
        type = tagType[2];
        break;
    }
    console.log(type);
    rDPChart.clear();
    rDPChart.setOption({
        tooltip: {
            trigger: 'axis'
        },
        
        legend: {
            data: type
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
            type: 'value'
        },
        series: function() {
            var serie = [];
            var item = {
                name: type,
                type: "line",
                smooth:true,
                data: recData[type]
            }
            serie.push(item);
            console.log(serie);
            return serie;
        } ()

    });
}


function configDetailTable(data) {
    $('#data-table-rank-detail-payment').dataTable().fnClearTable();
    var _table = $('#data-table-rank-detail-payment').dataTable({
        "destroy": true,
        "data": data==null?null:data.tableData,
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
    });
}

$(document).on("click","#data-table-rank-paymentBehavior tbody tr td a",function() {
    detail = true;
    accountParam = $(this).attr("data-info");
    $("#account-detail").text(accountParam+" 帐号详情");
    loadRankAccountDetail(accountParam);
    $("#data-first").slideToggle();
    $("#data-second").slideToggle();
});

$("#whaleDetailback").click(function(){
    detail = false;
    $("#data-first").slideToggle();
    $("#data-second").slideToggle();
});

$("ul.nav.nav-tabs.rank-detail-payment-tab > li").click(function(){
    configDetailChart(queryData, $(this).children("a").attr("data-info"));
});