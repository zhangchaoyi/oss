$(function(){
	withoutIcon();
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
        icons:getCookie("server"),
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
        "order": [[ 1, 'desc' ]],
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
        },
        "columnDefs": [ {
           "targets": 0,
           "render": function ( data, type, full, meta ) {
            if(data.length>15){
                return "<span title="+ data +">"+data.substr(0,15) + '......'+"</span>";
            }else{
                return "<span>"+data+"</span>";
            }
          }
         }],
        "scrollX": true
    });
}

$("#type-list > li").click(function(){
	var type = $(this).children("a").attr("data-info");
	var txt = $(this).children("a").text();
	$("#btn-type").attr("data-info",type);
	$("#btn-type").text(txt);
});

//锁死图标选择下拉菜单 清除按钮
$("button.btn.btn-default.btn-circle").attr('disabled',"true");
$("ul.dropdown-menu.iconBar > li").addClass("disabled");
$("li.btn-icons").unbind("click");
$("li.disabled > button.btn.btn-primary").attr('disabled',"true");
$("#btn-dropdownIcon").one("click", function(){
    $('.btn-icons > a > div').iCheck('disable');
});