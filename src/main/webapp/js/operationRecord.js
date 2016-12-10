$(function(){
	$("#btn-type").attr("data-info","mail");
	$("#btn-type").text("邮件");
	loadData();
})

function loadData(){
	loadRecordData();
}

function loadRecordData(){
	$.post("/oss/api/operation/record/list", {
        startDate:$("input#startDate").attr("value"),
        endDate:$("input#endDate").attr("value"),
        icons:$("#btn-db").attr("data-info"),
        type:$("#btn-type").attr("data-info")
    },
    function(data, status) {
        configTable(data);
    });
}

function configTable(data) {
    $("#table-operation-record").dataTable().fnClearTable();  
    $("#table-operation-record").dataTable({
        "destroy": true,
        // retrive:true,
        "data": data,
        "dom": '<"top"f>rt<"left"lip>',
        "order": [[ 1, 'asc' ]],
        "lengthMenu": [[10,30,-1 ],[10,30,'全部']],
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

$("#type-list > li").click(function(){
	var type = $(this).children("a").attr("data-info");
	var txt = $(this).children("a").text();
	$("#btn-type").attr("data-info",type);
	$("#btn-type").text(txt);
});