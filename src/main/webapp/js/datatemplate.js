//get today date for dateselector
function getNowFormatDate() {
    var date = new Date();
    var seperator1 = "-";
    var year = date.getFullYear();
    var month = date.getMonth() + 1;
    var strDate = date.getDate();
    if (month >= 1 && month <= 9) {
        month = "0" + month;
    }
    if (strDate >= 0 && strDate <= 9) {
        strDate = "0" + strDate;
    }
    var currentdate = year + seperator1 + month + seperator1 + strDate;
    return currentdate;
}

$("#btn-explain-switch").click(function() {
    $("#explain-panel").toggleClass("toggle-switch");
    var txt = $(this).text();
    if (txt == "打开") {
        $(this).text("关闭");
        $("#btn-explain-up").show();
        $("#btn-explain-down").show();
    } else {
        $(this).text("打开");
        $("#btn-explain-up").hide();
        $("#btn-explain-down").hide();
    }
});

$("#btn-adduser-switch").click(function(){
    $("div.table-zoom-first").toggleClass("toggle-switch");
    var txt = $(this).text();
    if (txt == "打开") {
        $(this).text("关闭");
    } else {
        $(this).text("打开");
    }
});

$('#btn-gamedetail-switch').click(function(){
    $("div.table-zoom-second").toggleClass("toggle-switch");
    var txt = $(this).text();
    if (txt == "打开") {
        $(this).text("关闭");
    } else {
        $(this).text("打开");
    }
});
//change the css when mouse cover date
function dateSelected(obj) {
    var nodes = $(obj).siblings("a");
    for(var i=0;i<nodes.length;i++) {
        $(nodes[i]).removeClass("selected");
    }
    $(obj).addClass("selected");
}

//control the filter button
$("#btn-selall").click(function(){
    $("table.tab-pane.fade.active.in").find("div").iCheck("check");
    $("table[class='tab-pane fade']").find("div").iCheck("uncheck");
});

$("#btn-selreverse").click(function(){
    $("table.tab-pane.fade.active.in").find("div").iCheck("toggle");
});

//save the filter chioce
$("#btn-filtersave").click(function(){
    var filterCheckBoxs = $("table.tab-pane.fade.active.in").find("div");
    for(var i=0;i<filterCheckBoxs.length;i++) { 
        if($(filterCheckBoxs[i]).hasClass("checked")){
            //extend
            // console.log($(filterCheckBoxs[i]).siblings("span").text());
        }
    }
});

//explain up and down button 
$("#btn-explain-up").click(function(){
    $("div.explain-content-box").css("margin-top", function(index,value){
        value = parseFloat(value) + 155;
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
        value = parseFloat(value) - 155;
        if(value <= -310){
            $("#btn-explain-down").addClass("disabled");
        }
        if($("#btn-explain-up").hasClass("disabled")){ 
            $("#btn-explain-up").removeClass("disabled");
        }
        return value;
    });
});

//onload initial
//dateSelector
$(function() {
    var dateRange = new pickerDateRange('date_seletor', {
        isTodayValid: true,
        startDate: getNowFormatDate(),
        endDate: getNowFormatDate(),
        //needCompare : true,
        //isSingleDay : true,
        //shortOpr : true,
        defaultText: ' 至 ',
        inputTrigger: 'input_trigger',
        theme: 'ta',

    });
});

//E-chart
$(function() {
    // 基于准备好的dom，初始化echarts实例
    var userIncreaseLeft = echarts.init(document.getElementById('user-increase-chart-left'));
    var userIncreaseRight = echarts.init(document.getElementById('user-increase-chart-right'));
    var userChange = echarts.init(document.getElementById('user-change-chart'));

    // 指定图表的配置项和数据
    var userIncreaseOption = {
        tooltip: {
            trigger: 'axis'

        },
        legend: {
            // show:false,
            zlevel:-1,
            left:20,
            // padding:30,
            // left:"left",
            // align:"left",
            // selectedMode:false,
            // inactiveColor:"black"
            // orient:"vertical",
            data: ['设备激活', '新增用户', '新增设备'],
            shadowColor:'rgba(1, 0, 1, 1.1)',
            shadowBlur: 10,
            shadowOffsetX:10,
            shadowOffsetX:10,
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
        dataZoom: [
        {   // 这个dataZoom组件，默认控制x轴。
            type: 'slider', // 这个 dataZoom 组件是 slider 型 dataZoom 组件
            start: 10,      // 左边在 10% 的位置。
            end: 60         // 右边在 60% 的位置。
        },
        {   // 这个dataZoom组件，也控制x轴。
            type: 'inside', // 这个 dataZoom 组件是 inside 型 dataZoom 组件
            start: 10,      // 左边在 10% 的位置。
            end: 60         // 右边在 60% 的位置。
        }
    ],
        xAxis: {
            type: 'category',
            boundaryGap: true,
            name:"test",
            data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
        },
        yAxis: {
            type: 'value',
            axisLabel: {
                formatter: '{value}'
            }
        },
        series: [{
            name: '设备激活',
            type: 'line',
            smooth: 'true',
            data: [30, 48, 13, 8, 3, 4, 48, 30]
        },
        {
            name: '新增用户',
            type: 'line',
            smooth: 'true',
            data: [2, 5, 20, 5, 2, 51, 5, 2]
        
        },
        {
            name: '新增设备',
            type: 'line',
            smooth: 'true',
            data: [0, 5, 4, 5, 4, 50, 50, 40]
        
        }]
    };

        var userChangeOption = {
            tooltip: {
                trigger: 'axis'
            },
            legend: {
                zlevel:-1,
                left:20,
                data: ['玩家转化率'],
                shadowColor:'rgba(1, 0, 1, 1.1)',
                shadowBlur: 10,
                shadowOffsetX:10,
                shadowOffsetX:10,
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
            dataZoom: [
            {   // 这个dataZoom组件，默认控制x轴。
                type: 'slider', // 这个 dataZoom 组件是 slider 型 dataZoom 组件
                start: 10,      // 左边在 10% 的位置。
                end: 80         // 右边在 60% 的位置。
            },
            {   // 这个dataZoom组件，也控制x轴。
                type: 'inside', // 这个 dataZoom 组件是 inside 型 dataZoom 组件
                start: 10,      // 左边在 10% 的位置。
                end: 50         // 右边在 60% 的位置。
            }
        ],
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
                name: '玩家转化率',
                type: 'line',
                smooth: 'true',
                data: ["10", "20", "30", "40", "50", "40", "30", "20"]
            }]
        };
    // 使用刚指定的配置项和数据显示图表。
    userIncreaseLeft.setOption(userIncreaseOption);
    userIncreaseRight.setOption(userIncreaseOption);
    userChange.setOption(userChangeOption);
});

//Echart 首次游戏 + 小号分析 + 地区 + 国家 + 性别 + 年龄 + 账户类型
$(function(){
    var firstGamePeriodBar = echarts.init(document.getElementById('first-game-period-chart-bar'));
    var smallAccountBar = echarts.init(document.getElementById('small-account-chart-bar'));
    var areaBar = echarts.init(document.getElementById('area-chart-bar'));
    var countryBar = echarts.init(document.getElementById('country-chart-bar'));
    var sexBar = echarts.init(document.getElementById('sex-chart-pie'));
    var ageBar = echarts.init(document.getElementById('age-chart-bar'))

    var firstGamePeriodOption = {
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'shadow'
            }
        },
        legend: {
            data: ['玩家数']
        },

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
            data: ['>60 min','30-60 min','10-30 min','3-10 min','1-3 min','31-60 s','11-30 s','5-10 s','1-4 s']
        },
        series: [
            {
                name: '玩家数',
                type: 'bar',
                itemStyle: {normal:{color:'rgb(87, 139, 187)'}},
                data: [10, 20, 30, 40, 0, 40, 30, 20, 10]
            },
            
        ]
    };  
    var smallAccountOption = {
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'shadow'
            }
        },
        legend: {
            data: ['设备数']
        },

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
            data: ['>10', '8-10', '7', '6', '5', '4', '3', '2', '1', '未统计']
        },
        series: [
            {
                name: '设备数',
                type: 'bar',
                itemStyle: {normal:{color:'rgb(87, 139, 187)'}},
                data: [10, 10, 10, 20, 4, 16, 8, 16, 6, 0]
            },
            
        ]
    };
    
    var areaBarOption = {
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'shadow'
            }
        },
        legend: {
            data: ['新增人数']
        },

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
            data: ['广东省', '-']
        },
        series: [
            {
                name: '新增人数',
                type: 'bar',
                barWidth: '20%',
                itemStyle: {normal:{color:'rgb(87, 139, 187)'}},
                data: [9,1]
            },
            
        ]
    };

    var countryBarOption = {
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'shadow'
            }
        },
        legend: {
            data: ['新增人数']
        },

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
            data: ['中国', '-']
        },
        series: [
            {
                name: '新增人数',
                type: 'bar',
                barWidth: '20%',
                itemStyle: {normal:{color:'rgb(87, 139, 187)'}},
                data: [90,10]
            },
            
        ]
    };

    var sexPieOption = {
        tooltip : {
            trigger: 'item',
            formatter: "{a} <br/>{b} : {c} ({d}%)"
        },
        legend: {
            orient: 'vertical',
            left: 'left',
            data: ['男','女','未知']
        },
        series : [
            {
                name: '新增人数',
                type: 'pie',
                radius : '55%',
                center: ['50%', '60%'],
                data:[
                    {value:50, name:'男'},
                    {value:40, name:'女'},
                    {value:10, name:'未知'}
                ],
                itemStyle: {
                    emphasis: {
                        shadowBlur: 10,
                        shadowOffsetX: 0,
                        shadowColor: 'rgba(0, 0, 0, 0.5)'
                    }
                }
            }
        ]
    };

    var ageBarOption = {
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'shadow'
            }
        },
        legend: {
            data: ['新增玩家']
        },

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
            data: ['未知', '>60', '56-60', '51-55', '46-50', '41-45', '36-40', '31-35', '26-30', '21-25', '16-20','1-15']
        },
        series: [
            {
                name: '新增玩家',
                type: 'bar',
                itemStyle: {normal:{color:'rgb(87, 139, 187)'}},
                data: [10,0,0,20,30,40,0,40,30,20,10,0]
            },
            
        ]
    };

    firstGamePeriodBar.setOption(firstGamePeriodOption);
    smallAccountBar.setOption(smallAccountOption);
    areaBar.setOption(areaBarOption);
    countryBar.setOption(countryBarOption);
    sexBar.setOption(sexPieOption);
    ageBar.setOption(ageBarOption);
})

//filter
$(function(){
    jQuery(document).ready(function(){
            jQuery("#filter").jcOnPageFilter({
                animateHideNShow: true,
                focusOnLoad:true,
                highlightColor:'#FFFF00',
                textColorForHighlights:'#000000',
                caseSensitive:false,
                hideNegatives:true,
                parentLookupClass:'jcorgFilterTextParent',
                childBlockClass:'jcorgFilterTextChild'
            });
        });    
})



//dataTable 首次游戏 + 小号分析 + 地区 + 国家 + 性别 + 年龄 + 账户类型
$(function(){
    var firstGamePeriodData=[
        ["1-4 s", "10", "5%"],
        ["5-10 s", "20", "10%"],
        ["11-30 s", "30", "15%"],
        ["31-60 s", "40", "20%"],
        ["1-3 min", "0", "0"],
        ["3-10 min", "40", "20%"],
        ["10-30 min", "30", "15%"],
        ["30-60 min", "20", "10%"],
        [">60 min", "10", "5%"],
    ];
    var smallAccount=[
        ["未统计", 0, 0],
        ["1", 6, "6%"],
        ["2", 16, "16%"],
        ["3", 8, "8%"],
        ["4", 16, "16%"],
        ["5", 4, "4%"],
        ["6", 20, "20%"],
        ["7", 10, "10%"],
        ["8-10", 10, "10%"],
        [">10", 10, "10%"]
    ];

    var areaData = [
        ["广东省", 9, "90%"],
        ["-", 1, "10%"]
    ];

    var countryData = [
        ["中国", 90, "90%"],
        ["-", 10, "10%"]
    ];

    var sexData = [
        ["男", 50, "50%"],
        ["女", 40, "40%"],
        ["未知", 10, "10%"]
    ];

    var ageData = [
        ["1-15", 10, "10%"],
        ["16-20", 0, 0],
        ["21-25", 0, 0],
        ["26-30", 20, "20%"],
        ["31-35", 30 , "30%"],
        ["36-40", 40 ,"40%"],
        ["41-45", 0 ,0],
        ["46-50", 40, "40%"],
        ["51-55", 30 ,"30%"],
        ["56-60", 20, "20%"],
        [">60", 10, "10%"],
        ["未知", 0, 0]
    ];

    $(document).ready( function () {
        //define sort
        jQuery.extend( jQuery.fn.dataTableExt.oSort, {
            "num-html-pre": function ( a ) {
                    var time = String(a).split(" ")[1]; 
                    var num = String(a).split(" ")[0].split("-")[0];
                    if(num==">60"){
                        num = 60;
                    }
                    if(time=="min"){
                        num *= 60;
                    }
                    return parseFloat(num);
                },
             
                "num-html-asc": function ( a, b ) {
                    return ((a < b) ? -1 : ((a > b) ? 1 : 0));               
                },
             
                "num-html-desc": function ( a, b ) {
                    return ((a < b) ? 1 : ((a > b) ? -1 : 0));
                }
        });

        $('#data-table-first-game-time').DataTable({
            data:firstGamePeriodData,
            //use self-define sort
            columnDefs: [
           { type: 'num-html', targets: 0 }
            ],
            "dom":'<"top"f>rt',
            'language': {  
                'emptyTable': '没有数据',  
                'loadingRecords': '加载中...',  
                'processing': '查询中...',  
                'search': '查询:',  
                'lengthMenu': '每页显示 _MENU_ 条记录',  
                'zeroRecords': '没有数据',                
                "sInfo": "(共 _TOTAL_ 条记录)",
                'infoEmpty': '没有数据', 
            }
        });
        $('#data-table-small-account').DataTable({
            data:smallAccount,
            "dom":'<"top"f>rt<"left">',
             'language': {  
                    'emptyTable': '没有数据',  
                    'loadingRecords': '加载中...',  
                    'processing': '查询中...',  
                    'search': '查询:',  
                    'lengthMenu': '每页显示 _MENU_ 条记录',  
                    'zeroRecords': '没有数据',                
                    "sInfo": "(共 _TOTAL_ 条记录)",
                    'infoEmpty': '没有数据', 
            } 
        });
        $('#data-table-area').DataTable({
            data:areaData,
            "dom":'<"top"f>rt<"left">',
             'language': {  
                    'emptyTable': '没有数据',  
                    'loadingRecords': '加载中...',  
                    'processing': '查询中...',  
                    'search': '查询:',  
                    'lengthMenu': '每页显示 _MENU_ 条记录',  
                    'zeroRecords': '没有数据',                
                    "sInfo": "(共 _TOTAL_ 条记录)",
                    'infoEmpty': '没有数据', 
            } 
        });
        $('#data-table-country').DataTable({
            data:countryData,
            "dom":'<"top"f>rt<"left">',
             'language': {  
                    'emptyTable': '没有数据',  
                    'loadingRecords': '加载中...',  
                    'processing': '查询中...',  
                    'search': '查询:',  
                    'lengthMenu': '每页显示 _MENU_ 条记录',  
                    'zeroRecords': '没有数据',                
                    "sInfo": "(共 _TOTAL_ 条记录)",
                    'infoEmpty': '没有数据', 
            } 
        });
        $("#data-table-sex").DataTable({
            data:sexData,
            "dom":'<"top"f>rt<"left">',
             'language': {  
                    'emptyTable': '没有数据',  
                    'loadingRecords': '加载中...',  
                    'processing': '查询中...',  
                    'search': '查询:',  
                    'lengthMenu': '每页显示 _MENU_ 条记录',  
                    'zeroRecords': '没有数据',                
                    "sInfo": "(共 _TOTAL_ 条记录)",
                    'infoEmpty': '没有数据', 
            }
        });
        $("#data-table-age").DataTable({
            data:ageData,
            "dom":'<"top"f>rt<"left">',
            'iDisplayLength':12,
             'language': {  
                    'emptyTable': '没有数据',  
                    'loadingRecords': '加载中...',  
                    'processing': '查询中...',  
                    'search': '查询:',  
                    'lengthMenu': '每页显示 _MENU_ 条记录',  
                    'zeroRecords': '没有数据',                
                    "sInfo": "(共 _TOTAL_ 条记录)",
                    'infoEmpty': '没有数据',                    
            }
        });
    })
})

$(function(){
    var data = [
      ["2016-08-11",
       30,
       2,
       0
      ],
      ["2016-08-12",
       48,
       5,
       5
      ],
      ["2016-08-13",
       13,
       20,
       4
      ],
      ["2016-08-14",
       8,
       5,
       5
      ],
      ["2016-08-15",
       3,
       2,
       4
      ],
      ["2016-08-16",
       4,
       51,
       50
      ],
      ["2016-08-17",
       48,
       5,
       50
      ],
      ["2016-08-18",
       30,
       2,
       40
      ]
    ];
    
    var userChangeData = [
        ["2016-08-11",
           "10%"
        ],
        ["2016-08-12",
           "20%"
        ],
        ["2016-08-13",
            "30%"   
        ],
        ["2016-08-14",
           "40%"
        ],
        ["2016-08-15",
            "50%"   
        ],
        ["2016-08-16",
           "40%"
        ],
        ["2016-08-17",
           "30%"
        ],
        ["2016-08-18",
           "20%"
        ]
    ];

    $(document).ready( function () {
      $('#data-table-user-increase').DataTable({
        data:data,
        "dom":'<"top"f>rt<"left"lip>',
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
      $('#data-table-user-change').DataTable({
        data:userChangeData,
        "dom":'<"top"f>rt<"left"lip>',
         'language': {  
                'emptyTable': '没有数据',  
                'loadingRecords': '加载中...',  
                'processing': '查询中...',  
                'search': '查询:',  
                'lengthMenu': '每页显示 _MENU_ 条记录',  
                'zeroRecords': '没有数据',                
                "sInfo": "(共 _TOTAL_ 条记录)",
                'infoEmpty': '没有数据', 
            } 
      });
      
  });
})

//icheck  checkbox
$(function(){
    
    $('input').iCheck({
        checkboxClass: 'icheckbox_polaris'
        // increaseArea: '-10%' // optional
    });

});
