var rankChart = echarts.init(document.getElementById('rank-paymentBehavior-chart'));

$(function(){
    loadData();
})

function loadData(){
    loadRankData($("ul.nav.nav-tabs.payment-tab > li.active > a").attr("data-info"));
}

function loadRankData(tag){
    $.post("/oss/api/payment/behavior/rank", {
        tag:tag,
        icon:getIcons(),
        startDate:$("input#startDate").attr("value"),
        endDate:$("input#endDate").attr("value")
    },
    function(data, status) {
        configRankChart(data);
        configRankTable(data);
    });
}

function configRankChart(data) {
    var recData = data.data;
    rankChart.clear();
    rankChart.setOption({
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
            boundaryGap: [0, 0.01],
        },
        series: function() {
            var serie = [];
            for (var key in recData) {
                var item = {
                    name: key,
                    type: "bar",
                    smooth:true,
                    barWidth:"30%",
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

function configRankTable(data) {
    appendTableHeader(data.header);
    var tableData = dealTableData(data);
    console.log(tableData);
    $('#data-table-rank-paymentBehavior').dataTable().fnClearTable();  
    $('#data-table-rank-paymentBehavior').dataTable({
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

function appendTableHeader(header){
    var txt = "";

    for (var i = 0; i < header.length; i++) {
        txt = txt + "<th><span>" + header[i] + "</span></th>"
    }

    if ($("table#data-table-rank-paymentBehavior > thead").length != 0) {
        $("table#data-table-rank-paymentBehavior > thead").empty();
        $("#data-table-rank-paymentBehavior").prepend("<thead><tr>" + txt + "</tr></thead>");
        return;
    }
    $("#data-table-rank-paymentBehavior").append("<thead><tr>" + txt + "</tr></thead>");
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

//explain up and down button 
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

//付费等级选择栏
$("ul.nav.nav-tabs.payment-tab > li").click(function(){
    loadRankData($(this).children("a").attr("data-info"));
});

//首付选择区
$("ul.nav.nav-tabs.paid-details > li").click(function(){
    var info = $(this).children("a").attr("data-info");
    switch(info){
        case "fp-cycle":
        $("div.nav-tab.paid-detail-subtab").show();
        break;
        case "fp-rank":
        case "fp-money":
        $("div.nav-tab.paid-detail-subtab").hide();
        break;
    }
});

//首付选择区域的子选择栏
$("div.nav-tab.paid-detail-subtab > ul > li").click(function(){
    $(this).siblings("li.active").toggleClass("active");
    $(this).addClass("active");
});
