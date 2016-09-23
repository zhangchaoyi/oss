var dataPaymentChart = echarts.init(document.getElementById('data-payment-chart'));

$(function(){
    loadData();
});

function loadData(){
    loadDataPayment($("ul.nav.nav-tabs.payment-tab > li.active").children("a").attr("data-info"));
};

function loadDataPayment(tag) {

    $.post("/oss/api/payment/data", {
        tag:tag,
        icon:getIcons(),
        startDate:$("input#startDate").attr("value"),
        endDate:$("input#endDate").attr("value")
    },
    function(data, status) {
        configDataPaymentChart(data);
        // configDataPaymentTable(data);   
    });
}

function configDataPaymentChart(data) {
    var recData = data.data;
    dataPaymentChart.clear();
    dataPaymentChart.setOption({
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
        if(value <= -720){
            $("#btn-explain-down").addClass("disabled");
        }
        if($("#btn-explain-up").hasClass("disabled")){ 
            $("#btn-explain-up").removeClass("disabled");
        }
        return value;
    });
});

//付费分析选择tab
$("ul.nav.nav-tabs.payment-tab > li").click(function(){
    var info = $(this).children("a").attr("data-info");
    if(info=="analyze-payment-times"){
        $("div.nav-tab.paid-analyze-tab").hide();
        $("div.arpu-block").show();
        return;
    }
    $("div.nav-tab.paid-analyze-tab").show();
    $("div.arpu-block").hide();
});

//detail tag
$("ul.nav.nav-tabs.paid-details > li").click(function(){
    var info = $(this).children("a").attr("data-info");
    switch(info){
        case "area":
        case "country":
        case "channel":
        $("div.nav-tab.paid-detail-subtab").show();
        $("div.nav-tab.paid-detail-consumepackage").hide();
        break;
        case "mobileoperator":
        case "paid-way":
        case "currency-type":
        $("div.nav-tab.paid-detail-subtab").hide();
        $("div.nav-tab.paid-detail-consumepackage").hide();
        break;
        case "comsume-package":
        $("div.nav-tab.paid-detail-subtab").hide();
        $("div.nav-tab.paid-detail-consumepackage").show();
        break;
    }
});

//details sub tag
$("div.nav-tab.paid-detail-subtab > ul > li, div.nav-tab.paid-detail-consumepackage > ul > li, div.nav-tab.paid-analyze-arp-tab > ul > li, div.nav-tab.paid-analyze-tab > ul > li").click(function(){
    $(this).siblings("li.active").toggleClass("active");
    $(this).addClass("active");
});

$("ul.nav.nav-tabs.payment-tab > li").click(function(){
    loadDataPayment($(this).children("a").attr("data-info"));
});
