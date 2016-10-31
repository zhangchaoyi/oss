var lcchart = echarts.init(document.getElementById('loss-count-chart'));

$(function(){
    loadData();
})

function loadData(){
	loadLossData($("div.nav-tab.loss > ul > li.active > a").attr("data-info"), $("ul.nav.nav-tabs.loss-count-tab > li.active > a").attr("data-info"));
}

function loadLossData(playerTag,tag){
	$.post("/oss/api/loss", {
        playerTag:playerTag,
        tag:tag,
        icon:getIcons(),
        startDate:$("input#startDate").attr("value"),
        endDate:$("input#endDate").attr("value")
    },
    function(data, status) {
        configChart(data, lcchart, "lcchart");
    });
}

function configChart(data, chart, chartName) {
    var recData = data.data;
    var categoryName = "";
    for(var key in data.category){
        categoryName = key;
    }
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
            type:'value',
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
                    type: "line",
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

$("div.nav-tab.loss > ul.nav.nav-pills > li").click(function(){
	$(this).siblings("li.active").toggleClass("active");
    $(this).addClass("active");
    loadLossData($(this).children("a").attr("data-info"), $("ul.nav.nav-tabs.loss-count-tab > li.active > a").attr("data-info"));
});

$("ul.nav.nav-tabs.loss-count-tab > li").click(function(){
    loadLossData($("div.nav-tab.loss > ul > li.active > a").attr("data-info"), $(this).children("a").attr("data-info"));
});