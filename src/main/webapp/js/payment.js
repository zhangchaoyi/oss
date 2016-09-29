var dataPaymentChart = echarts.init(document.getElementById('data-payment-chart'));
var analysePaymentChart = echarts.init(document.getElementById('analyse-payment-chart'));

$(function(){
    loadData();
});

function loadData(){
    loadDataPayment($("ul.nav.nav-tabs.payment-tab > li.active").children("a").attr("data-info"));
    loadDataPaymentTable();
    loadAnalyzePayment($("ul.nav.nav-tabs.analyze-payment-tab > li.active > a").attr("data-info"),$("div.nav-tab.paid-analyze-tab > ul > li.active > a > span").attr("data-info"));
    loadAnalyzePaymentTable($("ul.nav.nav-tabs.analyze-payment-tab > li.active > a").attr("data-info"),"");
};

function loadDataPayment(tag) {

    $.post("/oss/api/payment/data", {
        tag:tag,
        icon:getIcons(),
        startDate:$("input#startDate").attr("value"),
        endDate:$("input#endDate").attr("value")
    },
    function(data, status) {
        showPaidNote(data);
        configDataPaymentChart(data);
    });
}

function loadDataPaymentTable() {
    $.post("/oss/api/payment/data/table", {
        icon:getIcons(),
        startDate:$("input#startDate").attr("value"),
        endDate:$("input#endDate").attr("value")
    },
    function(data, status) {
        configDataPaymentTable(data);
    });
}

function loadAnalyzePayment(tag,subTag) {

    $.post("/oss/api/payment/analyze", {
        tag:tag,
        subTag:subTag,
        icon:getIcons(),
        startDate:$("input#startDate").attr("value"),
        endDate:$("input#endDate").attr("value")
    },
    function(data, status) {
        configAnalyzePaymentChart(data);
    });
}

function loadAnalyzePaymentTable(tag, subTag){
    $.post("/oss/api/payment/analyze/table", {
        icon:getIcons(),
        startDate:$("input#startDate").attr("value"),
        endDate:$("input#endDate").attr("value"),
        tag:tag,
        subTag:subTag
    },
    function(data, status) {
        configAnalyzePaymentTable(data);
    });
}

jQuery.extend(jQuery.fn.dataTableExt.oSort, {
            "num-html-pre": function(a) {
                var num = String(a).split("~")[0];
                return parseInt(num);
            },

            "num-html-asc": function(a, b) {
                return ((a < b) ? -1 : ((a > b) ? 1 : 0));
            },

            "num-html-desc": function(a, b) {
                return ((a < b) ? 1 : ((a > b) ? -1 : 0));
            }
});

function configAnalyzePaymentTable(data){
    appendAnalyzeTableHeader(data.header);
    $('#data-table-analyze-payment').dataTable().fnClearTable();  
    $('#data-table-analyze-payment').dataTable({
        "destroy": true,
        // retrive:true,
        "data": data.data,
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

function appendAnalyzeTableHeader(header) {
    var txt = "";
    for (var key in header) {
        txt += "<th><span>" + header[key] + "</span></th>";
    }
    
    if ($("table#data-table-analyze-payment > thead").length != 0) {
        $("table#data-table-analyze-payment > thead").empty();
        $("#data-table-analyze-payment").prepend("<thead><tr>" + txt + "</tr></thead>");
        return;
    }

    $("#data-table-analyze-payment").append("<thead><tr>" + txt + "</tr></thead>");
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

function configAnalyzePaymentChart(data) {
    var recData = data.data;
    var chartType = data.chartType;
    analysePaymentChart.clear();
    analysePaymentChart.setOption({
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
        yAxis: function() {
            if(chartType=='line'){
                var item = {
                    type:'value',
                    axisLabel: {
                        formatter: '{value} '
                    }
                };
                return item;
            }
            var item = {
                type: 'category',
                data: function() {
                    for (var key in data.category) {
                        return data.category[key];
                    }
                } ()
            };
            return item;
        } (),
        xAxis: function() {
            if(chartType=='line') {
                var item = {
                    type: 'category',
                    data: function() {
                    for (var key in data.category) {
                        return data.category[key];
                        }
                    } ()
                };
                return item;
            }
            var item = {
                    type:'value',
                    axisLabel: {
                        formatter: '{value} '
                    }
            };
            return item;
        } (),
        series: function() {
            var serie = [];
            for (var key in recData) {
                var item = {
                    name: key,
                    type: chartType,
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

function showPaidNote(data){
    var sum = data.sum;
    var htmlStr = "";
    var noteText = "SUM ";
    var noteValue = "";
    for(var key in sum){
        noteText += key + ' | ';
        noteValue += sum[key] + ' | ';
    }
    noteText = String(noteText).substring(0,noteText.length-2);
    noteText += ' : ';
    noteValue = String(noteValue).substring(0,noteValue.length-2);
   
    htmlStr += "<span>" + noteText + "<font class='sum-note'>" + noteValue + "</font></span>"
    $("div#payment-note").text("");
    $("div#payment-note").append(htmlStr);
}

function configDataPaymentTable(data) {
    $('#data-table-data-payment').dataTable().fnClearTable();  
    $('#data-table-data-payment').dataTable({
        "destroy": true,
        // retrive:true,
        "data": data,
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
$("ul.nav.nav-tabs.analyze-payment-tab > li").click(function(){
    var info = $(this).children("a").attr("data-info");
    if(info=="analyze-payment-arpu"){
        $("div.nav-tab.paid-analyze-tab").hide();
        $("div.arpu-block").show();
        loadAnalyzePayment(info,$("div.nav-tab.paid-analyze-arp-tab > ul > li.active > a > span").attr("data-info"));
        loadAnalyzePaymentTable(info,$("div.nav-tab.paid-analyze-arp-tab > ul > li.active > a > span").attr("data-info"));
        return;
    }
    $("div.nav-tab.paid-analyze-tab").show();
    $("div.arpu-block").hide();
    loadAnalyzePayment(info,$("div.nav-tab.paid-analyze-tab > ul > li.active > a > span").attr("data-info"));
    loadAnalyzePaymentTable(info, "");
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

$("div.nav-tab.paid-analyze-tab > ul > li").click(function(){
    loadAnalyzePayment($("ul.nav.nav-tabs.analyze-payment-tab > li.active > a").attr("data-info"),$(this).find("span").attr("data-info"));
});

$("div.nav-tab.paid-analyze-arp-tab > ul > li").click(function(){
    loadAnalyzePayment("analyze-payment-arpu",$(this).find("span").attr("data-info"));
    loadAnalyzePaymentTable("analyze-payment-arpu", $(this).find("span").attr("data-info"));
});