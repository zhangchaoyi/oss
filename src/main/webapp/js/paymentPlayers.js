var paymentPlayerListTable = "#data-table-paymentPlayers-list";
var paymentPlayerTable = "#data-table-paymentPlayer";

$(function(){
    loadData();
})

function loadData(){
    loadPlayersList();
    loadPlayerByAccount();
}

function loadPlayersList(){
    $.post("/oss/api/payment/players", {
        icon:getIcons(),
        startDate:$("input#startDate").attr("value"),
        endDate:$("input#endDate").attr("value")
    },
    function(data, status) {
        configTable(data, paymentPlayerListTable);
    });
}

function loadPlayerByAccount(){
    var account = $("#account").val();
    if(account == ""){
        configTable(null, paymentPlayerTable);
        return;
    }
    $.post("/oss/api/payment/player", {
        account:account
    },
    function(data, status) {
        configTable(data, paymentPlayerTable);
    });
}

function configTable(data,dataTable) {
    $(dataTable).dataTable().fnClearTable();  
    $(dataTable).dataTable({
        "destroy": true,
        // retrive:true,
        "data": data==null?null:data.tableData,
        "dom": '<"top"f>rt<"left"lip>',
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

$("#btn-queryAccount").click(function(){
    loadPlayerByAccount();
});