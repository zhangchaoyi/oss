//E-chart--activePlayers--DAU + DAU|WAU|MAU + DAU/MAU
$(function() {
    var dauChart = echarts.init(document.getElementById('dau-chart'));
    var dauWauMauChart = echarts.init(document.getElementById('dau-wau-mau-chart'));
    var dauMauChart = echarts.init(document.getElementById('dau-mau-chart'));

    var dauOption = {
        tooltip: {
            trigger: 'axis'
        },
        legend: {
            zlevel: -1,
            left: 20,
            data: ['新增玩家', '付费玩家', '非付费玩家', 'DAU'],
            shadowColor: 'rgba(1, 0, 1, 1.1)',
            shadowBlur: 10,
            shadowOffsetX: 10,
            shadowOffsetX: 10,
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
            boundaryGap: true,
            data: ['2016-08-11', '2016-08-12', '2016-08-13', '2016-08-14', '2016-08-15', '2016-08-16', '2016-08-17', '2016-08-18']
        },
        yAxis: {
            type: 'value',
            axisLabel: {
                formatter: '{value}'
            }
        },
        series: [{
            name: '新增玩家',
            type: 'line',
            smooth: 'true',
            data: ["0", "0", "0", "0", "0", "0", "0", "49"]
        },
        {
            name: '付费玩家',
            type: 'line',
            smooth: 'true',
            data: ["0", "0", "0", "0", "1", "0", "1", "2"]
        },
        {
            name: '非付费玩家',
            type: 'line',
            smooth: 'true',
            data: ["0", "0", "0", "0", "0", "0", "0", "1"]
        },
        {
            name: 'DAU',
            type: 'line',
            smooth: 'true',
            data: ["0", "0", "0", "0", "1", "0", "1", "3"]
        }]
    };

    var dauWauMauOption = {
        tooltip: {
            trigger: 'axis'
        },
        legend: {
            zlevel: -1,
            left: 20,
            data: ['DAU', 'WAU', 'MAU'],
            shadowColor: 'rgba(1, 0, 1, 1.1)',
            shadowBlur: 10,
            shadowOffsetX: 10,
            shadowOffsetX: 10,
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
            boundaryGap: true,
            data: ['2016-08-11', '2016-08-12', '2016-08-13', '2016-08-14', '2016-08-15', '2016-08-16', '2016-08-17', '2016-08-18']
        },
        yAxis: {
            type: 'value',
            axisLabel: {
                formatter: '{value}'
            }
        },
        series: [{
            name: 'DAU',
            type: 'line',
            smooth: 'true',
            data: ["0", "1", "0", "0", "0", "0", "0", "2"]
        },
        {
            name: 'WAU',
            type: 'line',
            smooth: 'true',
            data: ["2", "1", "1", "1", "1", "0", "0", "2"]
        },
        {
            name: 'MAU',
            type: 'line',
            smooth: 'true',
            data: ["61", "61", "61", "60", "60", "60", "60", "2"]
        }]
    };

    var dauMauOption = {
        tooltip: {
            trigger: 'axis'
        },
        legend: {
            zlevel: -1,
            left: 20,
            data: ['DAU/MAU'],
            shadowColor: 'rgba(1, 0, 1, 1.1)',
            shadowBlur: 10,
            shadowOffsetX: 10,
            shadowOffsetX: 10,
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
            boundaryGap: true,
            data: ['2016-08-11', '2016-08-12', '2016-08-13', '2016-08-14', '2016-08-15', '2016-08-16', '2016-08-17', '2016-08-18']
        },
        yAxis: {
            type: 'value',
            axisLabel: {
                formatter: '{value}'
            }
        },
        series: [{
            name: 'DAU/MAU',
            type: 'line',
            smooth: 'true',
            data: ["0", "0.02", "0", "0", "0", "0", "0", "1"]
        }]
    };

    dauChart.setOption(dauOption);
    dauWauMauChart.setOption(dauWauMauOption);
    dauMauChart.setOption(dauMauOption);
});

//E-chart--activePlayers--已玩天数 + 等级 + 地区 + 国家 + 性别 + 年龄 + 账户类型
$(function(){
    var playedDaysBar = echarts.init(document.getElementById('played-days-chart-bar'));
    var rankBar = echarts.init(document.getElementById('rank-chart-bar'));
    var areaBar = echarts.init(document.getElementById('area-chart-bar'));
    var countryBar = echarts.init(document.getElementById('country-chart-bar'));
    var sexBar = echarts.init(document.getElementById('sex-chart-pie'));
    var ageBar = echarts.init(document.getElementById('age-chart-bar'))
    
    var playedDaysBarOption = {
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'shadow'
            }
        },
        legend: {
            data: ['活跃玩家']
        },
        backgroundColor:"white",
        grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
        },
        xAxis: {
            type: 'value',
            boundaryGap: [0, 0.01]
        },
        yAxis: {
            type: 'category',
            axisLabel: {
                formatter: '{value} 天'
            },
            data: ['365+', '181-365', '91-180', '31-90', '15-30', '8-14', '4-7', '2-3', '1']
        },
        series: [{
            name: '活跃玩家',
            type: 'bar',
            itemStyle: {
                normal: {
                    color: 'rgb(87, 139, 187)'
                }
            },
            data: [0, 0, 0, 1, 0, 2, 0, 13, 48]
        },

        ]
    };
    
    var rankBarOption = {
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'shadow'
            }
        },
        legend: {
            data: ['活跃玩家']
        },
        backgroundColor:"white",
        grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
        },
        xAxis: {
            type: 'value',
            boundaryGap: [0, 0.01]
        },
        yAxis: {
            type: 'category',
            axisLabel: {
                formatter: '{value} 天'
            },
            data: ['10','1']
        },
        series: [{
            name: '活跃玩家',
            type: 'bar',
            barWidth: '20%',
            itemStyle: {
                normal: {
                    color: 'rgb(87, 139, 187)'
                }
            },
            data: [5,1]
        }]
    };

    var areaBarOption = {
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'shadow'
            }
        },
        legend: {
            data: ['活跃玩家']
        },
        backgroundColor:"white",
        grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
        },
        xAxis: {
            type: 'value',
            boundaryGap: [0, 0.01]
        },
        yAxis: {
            type: 'category',
            axisLabel: {
                formatter: '{value} 天'
            },
            data: ['广东省','-']
        },
        series: [{
            name: '活跃玩家',
            type: 'bar',
            barWidth: '20%',
            itemStyle: {
                normal: {
                    color: 'rgb(87, 139, 187)'
                }
            },
            data: [4,5]
        }]
    };

    var countryBarOption = {
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'shadow'
            }
        },
        legend: {
            data: ['活跃玩家']
        },
        backgroundColor:"white",
        grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
        },
        xAxis: {
            type: 'value',
            boundaryGap: [0, 0.01]
        },
        yAxis: {
            type: 'category',
            axisLabel: {
                formatter: '{value} 天'
            },
            data: ['中国','-']
        },
        series: [{
            name: '活跃玩家',
            type: 'bar',
            barWidth: '20%',
            itemStyle: {
                normal: {
                    color: 'rgb(87, 139, 187)'
                }
            },
            data: [4,5]
        }]
    };

    var sexPieOption = {
        tooltip: {
            trigger: 'item',
            formatter: "{a} <br/>{b} : {c} ({d}%)"
        },
        legend: {
            orient: 'vertical',
            left: 'left',
            // right:"right",

            data: ['男', '女', '未知']
        },
        backgroundColor:"white",
        series: [{
            name: '新增人数',
            type: 'pie',
            radius: '65%',
            center: ['50%', '50%'],
            data: [{
                value: 4,
                name: '男'
            },
            {
                value: 0,
                name: '女'
            },
            {
                value: 50,
                name: '未知'
            }],
            itemStyle: {
                emphasis: {
                    shadowBlur: 10,
                    shadowOffsetX: 0,
                    shadowColor: 'rgba(0, 0, 0, 0.5)'
                }
            }
        }]
    };

    var ageBarOption = {
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'shadow'
            }
        },
        legend: {
            data: ['活跃玩家']
        },
        backgroundColor:"white",
        grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
        },
        xAxis: {
            type: 'value',
            boundaryGap: [0, 0.01]
        },
        yAxis: {
            type: 'category',
            axisLabel: {
                formatter: '{value} 天'
            },
            data: ['未知', '>60', '56-60', '51-55', '46-50', '41-45', '36-40', '31-35', '26-30', '21-25', '16-20', '1-15']
        },
        series: [{
            name: '活跃玩家',
            type: 'bar',
            itemStyle: {
                normal: {
                    color: 'rgb(87, 139, 187)'
                }
            },
            data: [5,0,0,0,0,0,0,0,0,0,0,4]
        }]
    };

    

    playedDaysBar.setOption(playedDaysBarOption);
    rankBar.setOption(rankBarOption);
    areaBar.setOption(areaBarOption);
    countryBar.setOption(countryBarOption);
    sexBar.setOption(sexPieOption);
    ageBar.setOption(ageBarOption);

})


//dataTables--activePlayers--DAU + DAU|WAU|MAU + DAU/MAU
$(function() {
    var dauData = [["2016-08-11", 0, 0, 0, 0], ["2016-08-12", 0, 0, 0, 0], ["2016-08-13", 0, 0, 0, 0], ["2016-08-14", 0, 0, 0, 0], ["2016-08-15", 0, 1, 0, 1], ["2016-08-16", 0, 0, 0, 0], ["2016-08-17", 0, 1, 0, 1], ["2016-08-18", 49, 2, 1, 3]];
    var dauWauMauData = [["2016-08-11", 0, 2, 61], ["2016-08-12", 1, 1, 61], ["2016-08-13", 0, 1, 61], ["2016-08-14", 0, 1, 60], ["2016-08-15", 0, 1, 60], ["2016-08-16", 0, 0, 60], ["2016-08-17", 0, 1, 60], ["2016-08-18", 2, 2, 2]];
    var dauMauData = [["2016-08-11", 0], ["2016-08-12", 0.02], ["2016-08-13", 0], ["2016-08-14", 0], ["2016-08-15", 0], ["2016-08-16", 0], ["2016-08-17", 0], ["2016-08-18", 1]];

    $('#data-table-dau').DataTable({
        data: dauData,
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

    $('#data-table-dau-wau-mau').DataTable({
        data: dauWauMauData,
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

    $('#data-table-dau-mau').DataTable({
        data: dauMauData,
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
})

$(function(){
        var playedDaysData = [["1",48,"75%"],["2-3",12,"20.31%"],["4-7",0,0],["8-14",2,"3.13%"],["15-30",0,0],["31-90",1,"1.56%"],["91-180",0,0],["181-365",0,0],["365+",0,0]];
        var rankData = [["1",5,"83.33%"],["10",1,"16.67%"]];
        var areaData = [["-",5,"55.56%"],["广东省",4,"44.44%"]];
        var countryData = [["-",5,"55,56%"],["中国",4,"44.44%"]];
        var sexData = [["男",4,"44.44%"],["女",0,0],["未知",5,"55.56%"]]
        var ageData = [["1-15", 4, "44.44%"], ["16-20", 0, 0], ["21-25", 0, 0], ["26-30", 0, 0], ["31-35", 0, 0], ["36-40", 0, 0], ["41-45", 0, 0], ["46-50", 0, 0], ["51-55", 0, 0], ["56-60", 0, 0], [">60", 0, 0], ["未知", 5, "55.56%"]];

        jQuery.extend(jQuery.fn.dataTableExt.oSort, {
            "num-html-pre": function(a) {
                var time = String(a).split(" ")[1];
                var num = String(a).split(" ")[0].split("-")[0];
                if (num == ">60") {
                    num = 60;
                }
                if (time == "min") {
                    num *= 60;
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

        $('#data-table-played-days').DataTable({
            data: playedDaysData,
            "dom": '<"top"f>rt<"left">',
            columnDefs: [{
                type: 'num-html',
                targets: 0
            }],
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
        $('#data-table-rank').DataTable({
            data: rankData,
            "dom": '<"top"f>rt<"left">',
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
        $('#data-table-area').DataTable({
            data: areaData,
            "dom": '<"top"f>rt<"left">',
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
        $('#data-table-country').DataTable({
            data: countryData,
            "dom": '<"top"f>rt<"left">',
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
        $('#data-table-sex').DataTable({
            data: sexData,
            "dom": '<"top"f>rt<"left">',
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
        $('#data-table-age').DataTable({
            data: ageData,
            "dom": '<"top"f>rt<"left">',
            'iDisplayLength': 12,
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

})