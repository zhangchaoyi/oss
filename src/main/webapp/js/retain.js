//E-chart ---retain 
function loadRetainChart(){	
	var retainChart = echarts.init(document.getElementById('newPlayers-retain-chart'));

	var retainOption = {
        tooltip: {
            trigger: 'axis'
        },
        legend: {
            zlevel: -1,
            left: 20,
            data: ['次日留存率', '7日留存率', '30日留存率'],
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
                formatter: '{value} %'
            }
        },
        series: [{
            name: '次日留存率',
            type: 'line',
            smooth: 'true',

            data: ["0", "0", "0", "0", "0", "0", "0", "0"]
        },
        {
            name: '7日留存率',
            type: 'line',
            smooth: 'true',
            data: ["0", "0", "0", "0", "0", "0", "0", "0"]
        },
        {
            name: '30日留存率',
            type: 'line',
            smooth: 'true',
            data: ["0", "0", "0", "0", "0", "0", "0", "0"]
        }]
    };

	retainChart.setOption(retainOption);
};

//dataTable --retain
function loadRetainDataTable(){
	var retainData = [["2016-08-11", 0, '0%', '0%', '0%'], ["2016-08-12", 0, '0%', '0%', '0%'], ["2016-08-13", 0, '0%', '0%', '0%'], ["2016-08-14", 0, '0%', '0%', '0%'], ["2016-08-15", 0, '0%', '0%', '0%'], ["2016-08-16", 0, '0%', '0%', '0%'], ["2016-08-17", 0, '0%', '0%', '0%'], ["2016-08-18", 49, '0%', '0%', '0%']];
	$('#data-table-newPlayers-retain').DataTable({
        data: retainData,
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
//E-chart --retain equipment
function loadRetainEquipmentChart(){
	var retainEquipmentChart = echarts.init(document.getElementById('equipment-retain-chart'));

	var retainEquipmentOption = {
        tooltip: {
        	// formatter: '{b0}: {c0}<br />{b1}: {c1}',
            trigger: 'axis'
        },
        legend: {
   
            zlevel: -1,
            left: 20,
            data: ['次日留存率', '7日留存率', '30日留存率'],
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
                formatter: '{value} %'
            }
        },
        series: [{
            name: '次日留存率',
            type: 'line',
            smooth: 'true',
            data: ["0", "0", "0", "0", "0", "0", "0", "2"]
        },
        {
            name: '7日留存率',
            type: 'line',
            smooth: 'true',
            data: ["0", "0", "0", "0", "0", "0", "0", "0"]
        },
        {
            name: '30日留存率',
            type: 'line',
            smooth: 'true',
            data: ["0", "0", "0", "0", "0", "0", "0", "0"]
        }]
    };

	retainEquipmentChart.setOption(retainEquipmentOption);

}

function loadRetainEquipmentDataTable() {
	var retainEquipmentData = [
	  ["2016-08-03", 0, '1%', '0%', '0%', '0%', '0%','0%', '0%', '0%', '0%'],
	  ["2016-08-04", 0, '0%', '0%', '0%', '0%', '0%','0%', '0%', '0%', '0%'],
	  ["2016-08-05", 0, '0%', '0%', '0%', '0%', '0%','0%', '0%', '0%', '0%'],
	  ["2016-08-06", 0, '0%', '0%', '0%', '0%', '0%','0%', '0%', '0%', '0%'],
	  ["2016-08-07", 0, '0%', '0%', '0%', '0%', '0%','0%', '0%', '0%', '0%'],
	  ["2016-08-08", 0, '0%', '0%', '0%', '0%', '0%','0%', '0%', '0%', '0%'],
	  ["2016-08-09", 0, '0%', '0%', '0%', '0%', '0%','0%', '0%', '0%', '0%'],
	  ["2016-08-10", 0, '0%', '0%', '0%', '0%', '0%','0%', '0%', '0%', '0%'],
	  ["2016-08-11", 0, '0%', '0%', '0%', '0%', '0%','0%', '0%', '0%', '0%'],
	  ["2016-08-12", 0, '0%', '0%', '0%', '0%', '0%','0%', '0%', '0%', '0%'],
	  ["2016-08-13", 0, '0%', '0%', '0%', '0%', '0%','0%', '0%', '0%', '0%'],
	  ["2016-08-14", 0, '0%', '0%', '0%', '0%', '0%','0%', '0%', '0%', '0%'],
	  ["2016-08-15", 0, '0%', '0%', '0%', '0%', '0%','0%', '0%', '0%', '0%'],
	  ["2016-08-16", 0, '0%', '0%', '0%', '0%', '0%','0%', '0%', '0%', '0%'],
	  ["2016-08-17", 0, '0%', '0%', '0%', '0%', '0%','0%', '0%', '0%', '0%'],
	  ["2016-08-18", 0, '0%', '0%', '0%', '0%', '0%','0%', '0%', '0%', '0%'],
	];

	$('#data-table-equipment-retain').DataTable({
        data: retainEquipmentData,
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

//E-chart customize-retained
function loadCustomizeRetainTable() {
	var customizeRetainData = [
	  ["2016-08-03", 0, '1%', '0%', '0%', '0%', '0%','0%', '0%', '0%', '0%'],
	  ["2016-08-04", 0, '0%', '0%', '0%', '0%', '0%','0%', '0%', '0%', '0%'],
	  ["2016-08-05", 0, '0%', '0%', '0%', '0%', '0%','0%', '0%', '0%', '0%'],
	  ["2016-08-06", 0, '0%', '0%', '0%', '0%', '0%','0%', '0%', '0%', '0%'],
	  ["2016-08-07", 0, '0%', '0%', '0%', '0%', '0%','0%', '0%', '0%', '0%'],
	  ["2016-08-08", 0, '0%', '0%', '0%', '0%', '0%','0%', '0%', '0%', '0%'],
	  ["2016-08-09", 0, '0%', '0%', '0%', '0%', '0%','0%', '0%', '0%', '0%'],
	  ["2016-08-10", 0, '0%', '0%', '0%', '0%', '0%','0%', '0%', '0%', '0%'],
	  ["2016-08-11", 0, '0%', '0%', '0%', '0%', '0%','0%', '0%', '0%', '0%'],
	  ["2016-08-12", 0, '0%', '0%', '0%', '0%', '0%','0%', '0%', '0%', '0%'],
	  ["2016-08-13", 0, '0%', '0%', '0%', '0%', '0%','0%', '0%', '0%', '0%'],
	  ["2016-08-14", 0, '0%', '0%', '0%', '0%', '0%','0%', '0%', '0%', '0%'],
	  ["2016-08-15", 0, '0%', '0%', '0%', '0%', '0%','0%', '0%', '0%', '0%'],
	  ["2016-08-16", 0, '0%', '0%', '0%', '0%', '0%','0%', '0%', '0%', '0%'],
	  ["2016-08-17", 0, '0%', '0%', '0%', '0%', '0%','0%', '0%', '0%', '0%'],
	  ["2016-08-18", 0, '0%', '0%', '0%', '0%', '0%','0%', '0%', '0%', '0%'],
	];

	$('#data-table-customize-retained-queryDay').DataTable({
        data: customizeRetainData,
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

    $('#data-table-customize-retained-queryWeek').DataTable({
        data: customizeRetainData,
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

    $('#data-table-customize-retained-queryMonth').DataTable({
        data: customizeRetainData,
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


$('.dropdown-menu > li').click(function(){
	$(this).parent().siblings("button").html($(this).text()+ "<span class='caret'></span>");
});